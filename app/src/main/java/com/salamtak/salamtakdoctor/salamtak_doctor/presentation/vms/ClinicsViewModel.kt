package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.domain.entities.response.CreateClinicResponse
import com.salamtak.domain.entities.response.UpdateClinicResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn.NewLoginRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.NewClinicRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response


class ClinicsViewModel : ViewModel() {

    val Repo = NewLoginRepository()
    val repo = NewClinicRepository()
    var doctorClinicResponse = MutableLiveData<Resource<CreateClinicResponse>>()
    var updateDoctorClinicResponse = MutableLiveData<Resource<UpdateClinicResponse>>()



    fun createDoctorClinic(Request: MultipartBody,isPolyOrDoctorClinic:Boolean)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorClinicResponse.postValue(Resource.Loading())
        val response = if (isPolyOrDoctorClinic){
           Repo.createDoctorClinic(Request)
        }else{
            Repo.createHealthEntity(Request)
        }

        doctorClinicResponse.postValue(response?.let { handlecreateDoctorClinic(it) })
    }

    private fun handlecreateDoctorClinic(response: Response<CreateClinicResponse>): Resource<CreateClinicResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<CreateClinicResponse>(response.errorBody()!!.string(), CreateClinicResponse::class.java)
        return error.Message.let { Resource.Error(it) }
    }

    fun updateDoctorClinic2(Request: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        updateDoctorClinicResponse.postValue(Resource.Loading())
        val response=Repo.updateDoctorClinic2(Request)
        updateDoctorClinicResponse.postValue(response?.let { handleupdateDoctorClinic2(it) })
    }

    private fun handleupdateDoctorClinic2(response: Response<UpdateClinicResponse>): Resource<UpdateClinicResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<UpdateClinicResponse>(response.errorBody()!!.string(), UpdateClinicResponse::class.java)
        return error.Message.let { Resource.Error(it) }
    }

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("exception","exception: ${exception.message.toString()}")
    }

}