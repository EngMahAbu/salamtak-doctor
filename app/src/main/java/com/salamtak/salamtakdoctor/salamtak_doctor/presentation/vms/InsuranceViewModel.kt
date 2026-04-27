package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.*
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn.NewLoginRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG
import kotlinx.coroutines.launch

class InsuranceViewModel : ViewModel() {

    val Repo = NewLoginRepository()
    var showLoading = MutableLiveData<Boolean>()
    var insurance = MutableLiveData<InsuranceCompanyResponse>()
    var featuresresponse = MutableLiveData<InsuranceCompanyResponse>()
    var gateResponse = MutableLiveData<SalamtakGateResponse>()
    var getBlogsResponse = MutableLiveData<BlogsResponse>()
    var healthEntityResponse = MutableLiveData<GetHealthEntityResponse>()
    var packages = MutableLiveData<OtherPackagesResponse>()
    var packageModel = MutableLiveData<GetClincPacakgesResponse>()
    var notificationsResponse = MutableLiveData<NotificationListResponse>()
   // var examinationtypeId = MutableLiveData<MedicalExaminationResponse>()
    val error = MutableLiveData<ErrorModel>()
    val errorOtherPackages = MutableLiveData<ErrorModel>()

    fun getAllInsurance_Companies(){
        showLoading.value = true
        viewModelScope.launch {
            val Insurance = Repo.getAllInsurance_Companies()

            when (Insurance) {
                is NetworkResponse.Success -> {
                    insurance.value=Insurance.body
                    Log.d(TAG, "network mansour: " + Insurance.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + Insurance.code)
                    error.value = Insurance.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + Insurance.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + Insurance.error)
                }
            }
            showLoading.value = false
        }
    }

    fun getfeatures(){
        showLoading.value = true
        viewModelScope.launch {
            val features = Repo.getFeatures()

            when (features) {
                is NetworkResponse.Success -> {
                    featuresresponse.value=features.body
                    Log.d(TAG, "network mansour: " + features.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + features.code)
                    error.value = features.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + features.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + features.error)
                }
            }
            showLoading.value = false
        }
    }

    fun getSalamtakGate(){
        showLoading.value = true
        viewModelScope.launch {
            val gates = Repo.getSalamtakGate()

            when (gates) {
                is NetworkResponse.Success -> {
                    gateResponse.value=gates.body
                    Log.d(TAG, "network mansour: " + gates.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + gates.code)
                    error.value = gates.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + gates.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + gates.error)
                }
            }
            showLoading.value = false
        }
    }

    fun getBlogs(gateId: Int){
        showLoading.value = true
        viewModelScope.launch {
            val gates = Repo.getBlogs(gateId)

            when (gates) {
                is NetworkResponse.Success -> {
                    getBlogsResponse.value=gates.body
                    Log.d(TAG, "network mansour: " + gates.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + gates.code)
                    error.value = gates.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + gates.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + gates.error)
                }
            }
            showLoading.value = false
        }
    }

    fun getHealthEntityPagedList(CityId: Int, AreaId: Int, HealthEntityTypeId: Int,
                 MaxResultCount: Int, SkipCount: Int){
        showLoading.value = true
        viewModelScope.launch {
            val gates = Repo.getHealthEntityPagedList(CityId,AreaId,HealthEntityTypeId,MaxResultCount,SkipCount)

            when (gates) {
                is NetworkResponse.Success -> {
                    healthEntityResponse.value=gates.body
                    Log.d(TAG, "network mansour: " + gates.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + gates.code)
                    error.value = gates.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + gates.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + gates.error)
                }
            }
            showLoading.value = false
        }
    }



    fun  getOtherPackagesByClinicID(clinicId:Int){
        showLoading.value = true
        viewModelScope.launch {
            val otherPackages = Repo.getOtherPackagesByClinicID(clinicId)

            when (otherPackages) {
                is NetworkResponse.Success -> {
                    packages.value=otherPackages.body
                    Log.d(TAG, "network mansour: " + otherPackages.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + otherPackages.code)
                    errorOtherPackages.value = otherPackages.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + otherPackages.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + otherPackages.error)
                }
            }
            showLoading.value = false
        }
    }

    fun getClinicPackage(ClinicID:Int,Month:Int,Year:Int){
        showLoading.value = true
        viewModelScope.launch {
            val packages = Repo.getClinicPackage(ClinicID,Month,Year)

            when (packages) {
                is NetworkResponse.Success -> {
                    packageModel.value=packages.body
                    Log.d(ContentValues.TAG, "network mansour: " + packages.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(ContentValues.TAG, "network mansour: " + packages.code)
                    error.value = packages.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(ContentValues.TAG, "network mansour: " + packages.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(ContentValues.TAG, "network mansour: " + packages.error)
                }
            }
            showLoading.value = false
        }
    }



//    fun  getMedicalExamination(){
//        showLoading.value = true
//        viewModelScope.launch {
//            val typeId = Repo.getMedicalExamination()
//
//            when (typeId) {
//                is NetworkResponse.Success -> {
//                    examinationtypeId.value=typeId.body
//                    Log.d(TAG, "network mansour: " + typeId.body)
//                }
//                is NetworkResponse.ApiError -> {
//                    Log.d(TAG, "network mansour: " + typeId.code)
//                    errorOtherPackages.value = typeId.body
//                }
//                is NetworkResponse.NetworkError -> {
//                    Log.d(TAG, "network mansour: " + typeId.error)
//                }
//                is NetworkResponse.UnknownError -> {
//                    Log.d(TAG, "network mansour: " + typeId.error)
//                }
//            }
//            showLoading.value = false
//        }
//    }


//}
//
//class InsuranceViewModel : ViewModel() {
//
//    val Repo = NewLoginRepository()
//    var showLoading = MutableLiveData<Boolean>()
//    var insurance = MutableLiveData<InsuranceCompanyResponse>()
//    var packages = MutableLiveData<OtherPackagesResponse>()
//    var examinationtypeId = MutableLiveData<MedicalExaminationResponse>()
//    val error = MutableLiveData<ErrorModel>()
//    val errorOtherPackages = MutableLiveData<ErrorModel>()
//
//    fun getAllInsurance_Companies(){
//        showLoading.value = true
//        viewModelScope.launch {
//            val Insurance = Repo.getAllInsurance_Companies()
//
//            when (Insurance) {
//                is NetworkResponse.Success -> {
//                    insurance.value=Insurance.body
//                    Log.d(TAG, "network mansour: " + Insurance.body)
//                }
//                is NetworkResponse.ApiError -> {
//                    Log.d(TAG, "network mansour: " + Insurance.code)
//                    error.value = Insurance.body
//                }
//                is NetworkResponse.NetworkError -> {
//                    Log.d(TAG, "network mansour: " + Insurance.error)
//                }
//                is NetworkResponse.UnknownError -> {
//                    Log.d(TAG, "network mansour: " + Insurance.error)
//                }
//            }
//            showLoading.value = false
//        }
//    }
//
//
//
//    fun  getOtherPackagesByClinicID(clinicId:Int){
//        showLoading.value = true
//        viewModelScope.launch {
//            val otherPackages = Repo.getOtherPackagesByClinicID(clinicId)
//
//            when (otherPackages) {
//                is NetworkResponse.Success -> {
//                    packages.value=otherPackages.body
//                    Log.d(TAG, "network mansour: " + otherPackages.body)
//                }
//                is NetworkResponse.ApiError -> {
//                    Log.d(TAG, "network mansour: " + otherPackages.code)
//                    errorOtherPackages.value = otherPackages.body
//                }
//                is NetworkResponse.NetworkError -> {
//                    Log.d(TAG, "network mansour: " + otherPackages.error)
//                }
//                is NetworkResponse.UnknownError -> {
//                    Log.d(TAG, "network mansour: " + otherPackages.error)
//                }
//            }
//            showLoading.value = false
//        }
//    }
//
//    fun  getMedicalExamination(){
//        showLoading.value = true
//        viewModelScope.launch {
//            val typeId = Repo.getMedicalExamination()
//
//            when (typeId) {
//                is NetworkResponse.Success -> {
//                    examinationtypeId.value=typeId.body
//                    Log.d(TAG, "network mansour: " + typeId.body)
//                }
//                is NetworkResponse.ApiError -> {
//                    Log.d(TAG, "network mansour: " + typeId.code)
//                    errorOtherPackages.value = typeId.body
//                }
//                is NetworkResponse.NetworkError -> {
//                    Log.d(TAG, "network mansour: " + typeId.error)
//                }
//                is NetworkResponse.UnknownError -> {
//                    Log.d(TAG, "network mansour: " + typeId.error)
//                }
//            }
//            showLoading.value = false
//        }
//    }


    fun getUserNotifications(){
        showLoading.value = true
        viewModelScope.launch {
            val notifications = Repo.getUserNotifications()

            when (notifications) {
                is NetworkResponse.Success -> {
                    notificationsResponse.value=notifications.body
                    Log.d(ContentValues.TAG, "network mansour: " + notifications.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(ContentValues.TAG, "network mansour: " + notifications.code)
                     error.value = notifications.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(ContentValues.TAG, "network mansour: " + notifications.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(ContentValues.TAG, "network mansour: " + notifications.error)
                }
            }
            showLoading.value = false
        }
    }



}