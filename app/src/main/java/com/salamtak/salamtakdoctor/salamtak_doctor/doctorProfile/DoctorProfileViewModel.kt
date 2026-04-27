package com.salamtak.salamtakdoctor.salamtak_doctor.doctorProfile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.DeleteResponse
import com.salamtak.domain.entities.response.EditDoctorProfileResponse
import kotlinx.coroutines.launch

class DoctorProfileViewModel : ViewModel() {

    val repo = DoctorProfileRepository()
    var showLoader = MutableLiveData<Boolean>()
    var doctorProfileModel = MutableLiveData<EditDoctorProfileResponse>()
    val error = MutableLiveData<ErrorModel>()
    var updateDoctorProfileModel = MutableLiveData<ErrorModel>()
    var deleteProfileModel = MutableLiveData<DeleteResponse>()

    fun getDoctorProfile() {
        showLoader.value = true
        viewModelScope.launch {
            val doctorProfile = repo.getDoctorProfile()

            when (doctorProfile) {
                is NetworkResponse.Success -> {
                    doctorProfileModel.value = doctorProfile.body
                    Log.d("getDoctorProfile", "getDoctorProfile: ${doctorProfile.body}")
                }
                is NetworkResponse.ApiError -> {
                    error.value = doctorProfile.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoader.value = false
        }
    }

    fun getDoctorProfileByID(doctorId : String) {
        showLoader.value = true
        viewModelScope.launch {
            val doctorProfile = repo.getDoctorProfileByID(doctorId.toInt())

            when (doctorProfile) {
                is NetworkResponse.Success -> {
                    doctorProfileModel.value = doctorProfile.body
                    Log.d("getDoctorProfile", "getDoctorProfile: ${doctorProfile.body}")
                }
                is NetworkResponse.ApiError -> {
                    error.value = doctorProfile.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoader.value = false
        }
    }

    fun deleteUser() {
        showLoader.value = true
        viewModelScope.launch {
            val delete = repo.deleteUser()

            when (delete) {
                is NetworkResponse.Success -> {
                    deleteProfileModel.value = delete.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = delete.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoader.value = false
        }
    }

//    fun editDoctorProfile(updateDoctorProfileModels: UpdateDoctorProfileModel) {
//        showLoader.value = true
//        viewModelScope.launch {
//            val updateDoctorProfile = repo.editDoctorProfile(updateDoctorProfileModels)
//
//            when (updateDoctorProfile) {
//                is NetworkResponse.Success -> {
//                    updateDoctorProfileModel.value = updateDoctorProfile.body
//                }
//                is NetworkResponse.ApiError -> {
//                    error.value = updateDoctorProfile.body
//                }
//                is NetworkResponse.NetworkError -> {
//                }
//                is NetworkResponse.UnknownError -> {
//                }
//            }
//            showLoader.value = false
//        }
//    }

}