package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.salamtak.domain.entities.response.hospital.HospitalActionResponse
import com.salamtak.domain.entities.response.hospital.HospitalClinic
import com.salamtak.domain.entities.response.hospital.HospitalCreateClinicRequest
import com.salamtak.domain.entities.response.hospital.HospitalDoctor
import com.salamtak.domain.entities.response.hospital.HospitalDoctorProfile
import com.salamtak.domain.entities.response.hospital.HospitalLookupResponse
import com.salamtak.domain.entities.response.hospital.HospitalScheduleRequest
import com.salamtak.domain.entities.response.hospital.HospitalScheduleSlot
import com.salamtak.domain.entities.response.hospital.HospitalUpdateClinicRequest
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response

/** A doctor of the hospital and the one clinic they work in (null: no clinic yet, shows "+"). */
data class HospitalDoctorRow(val doctor: HospitalDoctor, val clinic: HospitalClinic?)

enum class HospitalLookupKind {
    SERVICES, FEATURES, SPECIALISTS, SENIORITY, COUNTRIES, DAYS, DURATIONS, BOOKING_TYPES
}

/** Shared by the Doctors tab screens; each screen gets its own instance. */
class HospitalDoctorsViewModel : ViewModel() {

    private val repo = HospitalDoctorsRepository()

    val doctors = MutableLiveData<Resource<List<HospitalDoctorRow>>>()
    val clinics = MutableLiveData<Resource<List<HospitalClinic>>>()
    val schedule = MutableLiveData<Resource<List<HospitalScheduleSlot>>>()
    val profile = MutableLiveData<Resource<HospitalDoctorProfile>>()
    val lookups = MutableLiveData<Map<HospitalLookupKind, List<PickerItem>>>(emptyMap())
    val subSpecialists = MutableLiveData<List<PickerItem>>()
    /** Result of a save / delete on the current screen. */
    val action = MutableLiveData<Resource<HospitalActionResponse>>()

    fun loadDoctors(hospitalId: Int) = viewModelScope.launch(Dispatchers.IO + handler(doctors)) {
        doctors.postValue(Resource.Loading())
        val response = repo.getDoctors(hospitalId)
        val list = response.body()?.Data
        if (!response.isSuccessful || list == null) {
            doctors.postValue(Resource.Error(errorOf(response)))
            return@launch
        }
        // A doctor has at most one clinic; an empty list means the "+" (add clinic) icon
        val rows = list.map { doctor ->
            async {
                val clinic = runCatching { repo.getDoctorClinics(doctor.DoctorId).body()?.Data?.firstOrNull() }
                    .getOrNull()
                HospitalDoctorRow(doctor, clinic)
            }
        }.awaitAll()
        doctors.postValue(Resource.sucess(rows))
    }

    fun deleteDoctor(hospitalId: Int, doctorId: Int) = runAction { repo.deleteDoctor(hospitalId, doctorId) }

    fun loadClinics(doctorId: Int) = viewModelScope.launch(Dispatchers.IO + handler(clinics)) {
        clinics.postValue(Resource.Loading())
        val response = repo.getDoctorClinics(doctorId)
        clinics.postValue(
            if (response.isSuccessful) Resource.sucess(response.body()?.Data.orEmpty())
            else Resource.Error(errorOf(response))
        )
    }

    fun removeDoctorFromClinic(clinicId: Int, doctorId: Int) =
        runAction { repo.removeDoctorFromClinic(clinicId, doctorId) }

    fun updateClinic(request: HospitalUpdateClinicRequest) = runAction { repo.updateClinic(request) }

    /** Creates the clinic for the doctor, then uploads its logo when one was picked. */
    fun createClinic(request: HospitalCreateClinicRequest, logo: RequestBody?) =
        viewModelScope.launch(Dispatchers.IO + handler(action)) {
            action.postValue(Resource.Loading())
            val created = repo.createClinic(request)
            val body = created.body()
            if (!created.isSuccessful || body == null || body.Success == false) {
                action.postValue(Resource.Error(body?.Message ?: errorOf(created)))
                return@launch
            }
            if (logo != null) {
                // The create response isn't documented: read ClinicId from it, else from the doctor's clinics
                val clinicId = clinicIdOf(body.Data)
                    ?: repo.getDoctorClinics(request.DoctorId).body()?.Data?.firstOrNull()?.ClinicId
                if (clinicId != null) {
                    val upload = repo.uploadClinicLogo(clinicId, logo)
                    if (!upload.isSuccessful) Log.e(TAG, "logo upload failed: ${upload.code()}")
                }
            }
            action.postValue(Resource.sucess(HospitalActionResponse(body.Message, body.MessageCode, true)))
        }

    /** Slots of [doctorId] in [clinicId]: the schedule is per clinic, filtered by the doctor's id there. */
    fun loadSchedule(doctorId: Int, clinicId: Int) = viewModelScope.launch(Dispatchers.IO + handler(schedule)) {
        schedule.postValue(Resource.Loading())
        val inClinic = repo.getClinicDoctors(clinicId).body()?.Data.orEmpty()
            .firstOrNull { it.DoctorId == doctorId }?.HealthEntityDoctorId
        val response = repo.getSchedule(clinicId)
        if (!response.isSuccessful) {
            schedule.postValue(Resource.Error(errorOf(response)))
            return@launch
        }
        val slots = response.body()?.Data.orEmpty()
            .filter { inClinic == null || it.HealthEntityDoctorId == inClinic }
        schedule.postValue(Resource.sucess(slots))
    }

    fun addSlot(doctorId: Int, request: HospitalScheduleRequest) = runAction { repo.addSlot(doctorId, request) }

    fun deleteSlot(workingDayTimeId: Int) = runAction { repo.deleteSlot(workingDayTimeId) }

    fun loadProfile(doctorId: Int) = viewModelScope.launch(Dispatchers.IO + handler(profile)) {
        profile.postValue(Resource.Loading())
        val response = repo.getDoctorProfile(doctorId)
        val data = response.body()?.Data
        profile.postValue(if (data != null) Resource.sucess(data) else Resource.Error(errorOf(response)))
    }

    fun updateDoctor(body: RequestBody) = runAction { repo.updateDoctor(body) }

    fun loadLookups(vararg kinds: HospitalLookupKind) = viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
        Log.e(TAG, "lookups: ${e.message}")
    }) {
        val loaded = kinds.map { kind ->
            async {
                val response = runCatching { fetch(kind) }.getOrNull()
                kind to response?.body()?.Data.orEmpty()
                    // Day 0 is "Exceptional Day", not a weekday
                    .filter { kind != HospitalLookupKind.DAYS || it.Id != 0 }
                    .map { PickerItem(it.Id, it.Duration?.toString() ?: it.Name.orEmpty()) }
            }
        }.awaitAll()
        lookups.postValue(lookups.value.orEmpty() + loaded)
    }

    fun loadSubSpecialists(specialistId: Int) = viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
        Log.e(TAG, "sub specialists: ${e.message}")
    }) {
        subSpecialists.postValue(
            repo.getSubSpecialists(specialistId).body()?.Data.orEmpty().map { PickerItem(it.Id, it.Name.orEmpty()) }
        )
    }

    private suspend fun fetch(kind: HospitalLookupKind): Response<HospitalLookupResponse> = when (kind) {
        HospitalLookupKind.SERVICES -> repo.getServices()
        HospitalLookupKind.FEATURES -> repo.getFeatures()
        HospitalLookupKind.SPECIALISTS -> repo.getSpecialists()
        HospitalLookupKind.SENIORITY -> repo.getSeniorityLevels()
        HospitalLookupKind.COUNTRIES -> repo.getCountries()
        HospitalLookupKind.DAYS -> repo.getDays()
        HospitalLookupKind.DURATIONS -> repo.getDurations()
        HospitalLookupKind.BOOKING_TYPES -> repo.getBookingTypes()
    }

    private fun runAction(call: suspend () -> Response<HospitalActionResponse>) =
        viewModelScope.launch(Dispatchers.IO + handler(action)) {
            action.postValue(Resource.Loading())
            val response = call()
            val body = response.body()
            action.postValue(
                if (response.isSuccessful && body?.Success != false) Resource.sucess(body ?: HospitalActionResponse(null, response.code(), true))
                else Resource.Error(body?.Message ?: errorOf(response))
            )
        }

    private fun clinicIdOf(data: JsonElement?): Int? = runCatching {
        when {
            data == null || data.isJsonNull -> null
            data.isJsonPrimitive -> data.asInt
            data.isJsonObject -> data.asJsonObject.let { o ->
                (o.get("ClinicId") ?: o.get("clinicId") ?: o.get("Id"))?.asInt
            }
            else -> null
        }
    }.getOrNull()

    private fun errorOf(response: Response<*>) =
        response.message().ifBlank { "Error ${response.code()}" }

    private fun <T> handler(target: MutableLiveData<Resource<T>>) =
        CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "exception: ${exception.message}")
            target.postValue(Resource.Error(exception.message ?: "Error"))
        }

    companion object {
        private const val TAG = "HospitalDoctorsVM"
    }
}
