package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.response.hospital.HospitalCreateInfoRequest
import com.salamtak.domain.entities.response.hospital.HospitalInfoData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.RequestBody

/** Lookups for the Hospital Info sign-up step; insurance ids are strings, kept by list position. */
data class HospitalSignUpLookups(
    val cities: List<PickerItem>,
    val services: List<PickerItem>,
    val insuranceNames: List<PickerItem>,
    val insuranceIds: List<String>
)

/** Hospital sign-up: Hospital Admin (CreateHospitalOwner) → Hospital Info (CreateHospitalInfo) → session. */
class HospitalSignUpViewModel : ViewModel() {

    private val client = RetrofitBuilder().instant
    private val lang get() = Localization.getLanguageName()

    /** Created owner's id. */
    val ownerResult = MutableLiveData<Resource<Int>>()
    /** Created hospital info id. */
    val infoResult = MutableLiveData<Resource<Int>>()
    /** Hospital info used to open the session after "Let's Start". */
    val sessionInfo = MutableLiveData<Resource<HospitalInfoData>>()
    val lookups = MutableLiveData<HospitalSignUpLookups>()
    val areas = MutableLiveData<List<PickerItem>>()

    fun createOwner(body: RequestBody) = viewModelScope.launch(Dispatchers.IO + handler(ownerResult)) {
        ownerResult.postValue(Resource.Loading())
        val response = client.createHospitalOwner(lang, body)
        val data = response.body()
        val ownerId = idOf(data?.Data, "OwnerId", "OwnerID", "ownerId", "Id", "ID")
        ownerResult.postValue(
            when {
                !response.isSuccessful || data == null || data.Success == false ->
                    Resource.Error(data?.Message ?: errorOf(response.code(), response.message()))
                // The flow can't continue without the owner id
                ownerId == null -> Resource.Error(data.Message ?: "Error")
                else -> Resource.sucess(ownerId)
            }
        )
    }

    /** Creates the hospital info, then uploads the tax card image to it. */
    fun createInfo(request: HospitalCreateInfoRequest, taxCardImage: RequestBody?) =
        viewModelScope.launch(Dispatchers.IO + handler(infoResult)) {
            infoResult.postValue(Resource.Loading())
            val response = client.createHospitalInfo(lang, request)
            val data = response.body()
            if (!response.isSuccessful || data == null || data.Success == false) {
                infoResult.postValue(Resource.Error(data?.Message ?: errorOf(response.code(), response.message())))
                return@launch
            }
            val infoId = idOf(data.Data, "InfoID", "InfoId", "HospitalInfoId", "HospitalInfoID", "Id", "ID")
            if (infoId == null) {
                infoResult.postValue(Resource.Error(data.Message ?: "Error"))
                return@launch
            }
            if (taxCardImage != null) {
                val upload = client.uploadHospitalInfoImage(lang, infoId, taxCardImage)
                if (!upload.isSuccessful) Log.e(TAG, "tax card upload failed: ${upload.code()}")
            }
            infoResult.postValue(Resource.sucess(infoId))
        }

    fun loadSession(infoId: Int) = viewModelScope.launch(Dispatchers.IO + handler(sessionInfo)) {
        sessionInfo.postValue(Resource.Loading())
        val response = client.getHospitalInfoById(lang, infoId)
        val data = response.body()?.Data
        sessionInfo.postValue(
            if (data?.HospitalId != null) Resource.sucess(data)
            else Resource.Error(errorOf(response.code(), response.message()))
        )
    }

    fun loadLookups() = viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
        Log.e(TAG, "lookups: ${e.message}")
    }) {
        val cities = async { client.getAllCities(lang) }
        val services = async { runCatching { client.getAllServices(lang).body()?.Data }.getOrNull().orEmpty() }
        val insurance = async { runCatching { client.getInsuranceLookup(lang).body()?.Data }.getOrNull().orEmpty() }
        val cityItems = when (val response = cities.await()) {
            is NetworkResponse.Success -> response.body.data.map {
                PickerItem(it.id, if (lang == "ar") it.nameAr else it.name)
            }
            else -> emptyList()
        }
        val companies = insurance.await().filter { !it.ID.isNullOrBlank() }
        lookups.postValue(
            HospitalSignUpLookups(
                cities = cityItems,
                services = services.await().map { PickerItem(it.Id, it.Name.orEmpty()) },
                insuranceNames = companies.mapIndexed { index, company -> PickerItem(index, company.Name.orEmpty()) },
                insuranceIds = companies.map { it.ID.orEmpty() }
            )
        )
    }

    fun loadAreas(cityId: Int) = viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
        Log.e(TAG, "areas: ${e.message}")
    }) {
        when (val response = client.getAreasByCityId(lang, cityId)) {
            is NetworkResponse.Success -> areas.postValue(response.body.data.map { PickerItem(it.id, it.name) })
            else -> areas.postValue(emptyList())
        }
    }

    // The create responses aren't documented: Data may be the id itself or an object holding it
    private fun idOf(data: JsonElement?, vararg keys: String): Int? = runCatching {
        when {
            data == null || data.isJsonNull -> null
            data.isJsonPrimitive -> data.asInt
            data.isJsonObject -> keys.firstNotNullOfOrNull { key ->
                data.asJsonObject.get(key)?.takeIf { !it.isJsonNull }?.asInt
            }
            else -> null
        }
    }.getOrNull()

    private fun errorOf(code: Int, message: String) = message.ifBlank { "Error $code" }

    private fun <T> handler(target: MutableLiveData<Resource<T>>) =
        CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "exception: ${exception.message}")
            target.postValue(Resource.Error(exception.message ?: "Error"))
        }

    companion object {
        private const val TAG = "HospitalSignUpVM"
    }
}
