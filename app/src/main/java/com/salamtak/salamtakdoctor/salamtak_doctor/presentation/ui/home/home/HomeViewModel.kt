package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RequestCode.UNAUTHORIZED
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    val repo = HomeRepository()
    var showLoading = MutableLiveData<Boolean>()
    var homeModel = MutableLiveData<HomeResponse>()
    val error = MutableLiveData<ErrorModel>()
    var clinicModel = MutableLiveData<ClinicsResponse>()
    var adsModel = MutableLiveData<WhatsUpAdsResponse>()
    var adsData = ArrayList<AdsData>()
    var homeAdsModel = MutableLiveData<HomeAdsResponse>()
    var doctorProfileModel = MutableLiveData<EditDoctorProfileResponse>()

    // images
    private val sliderCallMutable = MutableLiveData<Long>(0)
    fun sliderCall(): LiveData<Long> = sliderCallMutable


    fun getHome(fromDate: String? = null, toDate: String? = null, clinicID: Int? = null,doctorID: Int? = null,polyInfoID: Int?) {
        Log.d("getData", "getHome: $fromDate, $toDate, $clinicID")
        showLoading.value = true
        viewModelScope.launch {
            val home = if (polyInfoID==null){
                repo.getHome(fromDate, toDate, clinicID)
            }else{
                repo.getPolyClinicDashboard(fromDate,toDate,clinicID,doctorID,polyInfoID)
            }

            Log.d(TAG, "getHome: $home")

            when (home) {
                is NetworkResponse.Success -> {
                    homeModel.value = home.body
                    Log.d(ContentValues.TAG, "network mansour Success: " + home.body)
                }
                is NetworkResponse.ApiError -> {
                    if(home.code == UNAUTHORIZED){
                        home.body.code = UNAUTHORIZED
                    }
                    error.value = home.body
                    Log.d(ContentValues.TAG, "network mansour ApiError: " + home.code)
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(ContentValues.TAG, "network mansour NetworkError: " + home.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(ContentValues.TAG, "network mansour UnknownError: " + home.error)
                }
            }
            showLoading.value = false
        }
    }

    fun getHomeClinics() {

        showLoading.value = true
        viewModelScope.launch {
            val clinic = repo.getHomeClinic()

            when (clinic) {
                is NetworkResponse.Success -> {
                    clinicModel.value = clinic.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = clinic.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }



    fun getHomeAds() {

        viewModelScope.launch {
            val ads = repo.getHomeAds()

            when (ads) {
                is NetworkResponse.Success -> {
                    homeAdsModel.value = ads.body

                    Log.d(TAG, "getHomeAds: ${ads.body}")
                }
                is NetworkResponse.ApiError -> {
                    error.value = ads.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
        }
    }



    fun getWhatsUpAds() {

        viewModelScope.launch {
            val ads = repo.getWhatsUpAds()

            when (ads) {
                is NetworkResponse.Success -> {
                    adsModel.value = ads.body
                    Log.d(TAG, "getWhatsUpAds: ${ads.body}")
                }
                is NetworkResponse.ApiError -> {
                    error.value = ads.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
        }
    }
    fun getDoctorProfile() {
        showLoading.value = true
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
            showLoading.value = false
        }
    }


companion object{
    private const val TAG = "HomeViewModel"
}
}