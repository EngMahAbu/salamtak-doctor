package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.response.hospital.HospitalCreateClinicRequest
import com.salamtak.domain.entities.response.hospital.HospitalScheduleRequest
import com.salamtak.domain.entities.response.hospital.HospitalUpdateClinicRequest
import okhttp3.RequestBody

class HospitalDoctorsRepository {

    private val client = RetrofitBuilder().instant
    private val lang get() = Localization.getLanguageName()

    suspend fun getDoctors(hospitalId: Int) = client.getHospitalDoctorList(lang, hospitalId)
    suspend fun getClinicDoctors(clinicId: Int) = client.getHospitalClinicDoctors(lang, clinicId)
    suspend fun deleteDoctor(hospitalId: Int, doctorId: Int) =
        client.deleteDoctorFromHospital(lang, hospitalId, doctorId)

    suspend fun getDoctorClinics(doctorId: Int) = client.getHospitalClinicsByDoctor(lang, doctorId)
    suspend fun removeDoctorFromClinic(clinicId: Int, doctorId: Int) =
        client.deleteDoctorFromHospitalClinic(lang, clinicId, doctorId)

    suspend fun createClinic(request: HospitalCreateClinicRequest) = client.createHospitalClinic(lang, request)
    suspend fun updateClinic(request: HospitalUpdateClinicRequest) = client.updateHospitalClinic(lang, request)
    suspend fun uploadClinicLogo(clinicId: Int, body: RequestBody) =
        client.uploadHospitalClinicLogo(lang, clinicId, body)

    suspend fun getSchedule(clinicId: Int) = client.getHospitalDoctorSchedule(lang, clinicId)
    suspend fun addSlot(doctorId: Int, request: HospitalScheduleRequest) =
        client.addHospitalDoctorSchedule(lang, doctorId, request)

    suspend fun deleteSlot(workingDayTimeId: Int) = client.deleteHospitalDoctorSchedule(lang, workingDayTimeId)

    suspend fun getDoctorProfile(doctorId: Int) = client.getHospitalDoctorProfile(lang, doctorId)
    suspend fun updateDoctor(body: RequestBody) = client.updateHospitalDoctor(lang, body)

    suspend fun getServices() = client.getAllServices(lang)
    suspend fun getFeatures() = client.getAllFeatures(lang)
    suspend fun getSpecialists() = client.getSpecialistLookup(lang)
    suspend fun getSubSpecialists(specialistId: Int) = client.getSubSpecialistLookup(lang, specialistId)
    suspend fun getSeniorityLevels() = client.getSeniorityLookup(lang)
    suspend fun getCountries() = client.getCountriesLookup(lang)
    suspend fun getDays() = client.getDaysLookup(lang)
    suspend fun getDurations() = client.getDurationsLookup(lang)
    suspend fun getBookingTypes() = client.getBookingTypesLookup(lang)
}
