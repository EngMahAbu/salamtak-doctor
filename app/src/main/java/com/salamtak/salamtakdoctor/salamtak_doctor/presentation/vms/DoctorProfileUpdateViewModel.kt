package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.domain.entities.AnswersQuestionsResponse
import com.salamtak.domain.repositories.update.UpdateDoctorProfileResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn.NewLoginRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.NewClinicRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response


class DoctorProfileUpdateViewModel : ViewModel() {

    val Repo = NewLoginRepository()
    var doctorUpdateProfileResponse = MutableLiveData<Resource<UpdateDoctorProfileResponse>>()


    fun updateDoctorProfileFirstStep(profileRequest: MultipartBody,type:Int,clinicId:Int,userId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorUpdateProfileResponse.postValue(Resource.Loading())
        val response= when(type){
            1->{
                Repo.updateDoctorProfileFirstStep(profileRequest)
            }
            2->{
                Repo.createProfile(profileRequest)
            }
            else->{
                Repo.createDoctorToPolyclinic(profileRequest,clinicId,userId)
            }
        }
        doctorUpdateProfileResponse.postValue(response?.let { handleUpdateDoctorProfileFirstStep(it) })
    }
    private fun handleUpdateDoctorProfileFirstStep(response: Response<UpdateDoctorProfileResponse>): Resource<UpdateDoctorProfileResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<UpdateDoctorProfileResponse>(response.errorBody()!!.string(), UpdateDoctorProfileResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }


    fun createPatientEmrAl(profileRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorUpdateProfileResponse.postValue(Resource.Loading())
        val response=Repo.createPatientEmrAl(profileRequest)
        doctorUpdateProfileResponse.postValue( response?.let { handleCreatePatientEmrAl(it) })
    }
    private fun handleCreatePatientEmrAl(response: Response<UpdateDoctorProfileResponse>): Resource<UpdateDoctorProfileResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<UpdateDoctorProfileResponse>(response.errorBody()!!.string(), UpdateDoctorProfileResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }


    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("exception","exception: ${exception.message.toString()}")
    }

}