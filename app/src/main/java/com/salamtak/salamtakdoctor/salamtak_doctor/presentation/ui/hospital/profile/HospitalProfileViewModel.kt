package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.hospital.HospitalActionResponse
import com.salamtak.domain.entities.response.hospital.HospitalBranch
import com.salamtak.domain.entities.response.hospital.HospitalBranchRequest
import com.salamtak.domain.entities.response.hospital.HospitalInfoData
import com.salamtak.domain.entities.response.hospital.HospitalLookup
import com.salamtak.domain.entities.response.hospital.HospitalOwner
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.HospitalRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response

/** Everything the Hospital profile screen shows, loaded together so names can be mapped to ids. */
data class HospitalProfileData(
    val owner: HospitalOwner?,
    /** Info in the app language (names of city, area and services match the lookups below). */
    val info: HospitalInfoData?,
    val bioEn: String?,
    val bioAr: String?,
    val branches: List<HospitalBranch>,
    val services: List<HospitalLookup>,
    val features: List<HospitalLookup>,
    val cities: List<PickerItem>
)

class HospitalProfileViewModel : ViewModel() {

    private val repo = HospitalRepository()
    val profile = MutableLiveData<Resource<HospitalProfileData>>()
    val areas = MutableLiveData<Resource<List<PickerItem>>>()
    val branchAreas = MutableLiveData<Resource<List<PickerItem>>>()
    val branches = MutableLiveData<Resource<List<HospitalBranch>>>()
    val saveOwnerResponse = MutableLiveData<Resource<HospitalActionResponse>>()
    val saveInfoResponse = MutableLiveData<Resource<HospitalActionResponse>>()
    val branchActionResponse = MutableLiveData<Resource<HospitalActionResponse>>()

    fun loadProfile(hospitalId: Int, infoId: Int) =
        viewModelScope.launch(Dispatchers.IO + handler(profile)) {
            profile.postValue(Resource.Loading())
            val owner = async { repo.getHospitalOwner(hospitalId) }
            val info = async { repo.getHospitalInfoById(infoId) }
            val infoEn = async { repo.getHospitalInfoById(infoId, "en") }
            val infoAr = async { repo.getHospitalInfoById(infoId, "ar") }
            val branchList = async { repo.getHospitalBranches(hospitalId) }
            val services = async { repo.getAllServices() }
            val features = async { repo.getAllFeatures() }
            val cities = async { repo.getAllCities() }

            val ownerBody = owner.await().body()
            val infoBody = info.await().body()
            if (ownerBody?.Data == null && infoBody?.Data == null) {
                profile.postValue(Resource.Error("Error"))
                return@launch
            }
            val cityItems = when (val response = cities.await()) {
                is NetworkResponse.Success -> response.body.data.map {
                    PickerItem(it.id, if (Localization.getLanguageName() == "ar") it.nameAr else it.name)
                }
                else -> emptyList()
            }
            profile.postValue(
                Resource.sucess(
                    HospitalProfileData(
                        owner = ownerBody?.Data,
                        info = infoBody?.Data,
                        bioEn = infoEn.await().body()?.Data?.Bio,
                        bioAr = infoAr.await().body()?.Data?.Bio,
                        branches = branchList.await().body()?.Data.orEmpty(),
                        services = services.await().body()?.Data.orEmpty(),
                        features = features.await().body()?.Data.orEmpty(),
                        cities = cityItems
                    )
                )
            )
        }

    /** Areas for the Hospital Info tab ([areas]) or for the branch sheet ([branchAreas]). */
    fun loadAreas(cityId: Int, forBranch: Boolean = false) {
        val target = if (forBranch) branchAreas else areas
        viewModelScope.launch(Dispatchers.IO + handler(target)) {
            target.postValue(Resource.Loading())
            when (val response = repo.getAreasByCityId(cityId)) {
                is NetworkResponse.Success ->
                    target.postValue(Resource.sucess(response.body.data.map { PickerItem(it.id, it.name) }))
                else -> target.postValue(Resource.Error("Error"))
            }
        }
    }

    fun loadBranches(hospitalId: Int) = viewModelScope.launch(Dispatchers.IO + handler(branches)) {
        branches.postValue(Resource.Loading())
        val response = repo.getHospitalBranches(hospitalId)
        val body = response.body()
        branches.postValue(
            if (response.isSuccessful && body != null) Resource.sucess(body.Data.orEmpty())
            else Resource.Error(response.message().ifBlank { "Error ${response.code()}" })
        )
    }

    fun saveOwner(body: RequestBody) = viewModelScope.launch(Dispatchers.IO + handler(saveOwnerResponse)) {
        saveOwnerResponse.postValue(Resource.Loading())
        saveOwnerResponse.postValue(handle(repo.updateHospitalOwner(body)))
    }

    fun saveInfo(body: RequestBody) = viewModelScope.launch(Dispatchers.IO + handler(saveInfoResponse)) {
        saveInfoResponse.postValue(Resource.Loading())
        saveInfoResponse.postValue(handle(repo.updateHospitalInfo(body)))
    }

    fun createBranch(request: HospitalBranchRequest) =
        branchAction { repo.createHospitalBranch(request) }

    fun updateBranch(request: HospitalBranchRequest) =
        branchAction { repo.updateHospitalBranch(request) }

    fun deleteBranch(branchId: Int) = branchAction { repo.deleteHospitalBranch(branchId) }

    private fun branchAction(call: suspend () -> Response<HospitalActionResponse>) =
        viewModelScope.launch(Dispatchers.IO + handler(branchActionResponse)) {
            branchActionResponse.postValue(Resource.Loading())
            branchActionResponse.postValue(handle(call()))
        }

    // The save calls report failures in the body too, so check Success as well as the HTTP code.
    private fun handle(response: Response<HospitalActionResponse>): Resource<HospitalActionResponse> {
        val body = response.body()
        if (response.isSuccessful && body != null && body.Success != false) {
            return Resource.sucess(body)
        }
        val message = body?.Message ?: response.message()
        return Resource.Error(message.ifBlank { "Error ${response.code()}" })
    }

    private fun <T> handler(target: MutableLiveData<Resource<T>>) =
        CoroutineExceptionHandler { _, exception ->
            Log.e("HospitalProfileVM", "exception: ${exception.message}")
            target.postValue(Resource.Error(exception.message ?: "Error"))
        }
}
