package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.domain.entities.response.CreateDoctorDocumentResponse
import com.salamtak.domain.entities.response.UpdateLegalDocumentModel
import com.salamtak.domain.entities.response.newdocuents.LegalDocumentsResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn.NewLoginRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response


class DoctorProfileDocumentViewModel : ViewModel() {

    val Repo = NewLoginRepository()
    var doctorDocumentsResponse = MutableLiveData<Resource<CreateDoctorDocumentResponse>>()
    var updatedoctorDocumentsResponse = MutableLiveData<Resource<UpdateLegalDocumentModel>>()
    var getDoctorDocumentsResponse = MutableLiveData<Resource<LegalDocumentsResponse>>()


    fun createDoctorDocuments(profileRequest: MultipartBody,doctorId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorDocumentsResponse.postValue(Resource.Loading())
        val response= when(doctorId){
            0->{
                Repo.createDoctorDocuments(profileRequest)
            }
            else->{
                Repo.createDoctorDocumentsPolyClinic(profileRequest)
            }

        }
        doctorDocumentsResponse.postValue(response?.let { handlecreateDoctorDocuments(it) })
    }
    private fun handlecreateDoctorDocuments(response: Response<CreateDoctorDocumentResponse>): Resource<CreateDoctorDocumentResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<CreateDoctorDocumentResponse>(response.errorBody()!!.string(), CreateDoctorDocumentResponse::class.java)
        return error.Message?.let { Resource.Error(it) }
    }

    fun updateDoctorDocumentS(profileRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        updatedoctorDocumentsResponse.postValue(Resource.Loading())
        val response= Repo.updateDoctorDocument(profileRequest)
        updatedoctorDocumentsResponse.postValue(response?.let { handleupdateDoctorDocumentS(it) })
    }
    private fun handleupdateDoctorDocumentS(response: Response<UpdateLegalDocumentModel>): Resource<UpdateLegalDocumentModel>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<UpdateLegalDocumentModel>(response.errorBody()!!.string(), UpdateLegalDocumentModel::class.java)
        return error.Message?.let { Resource.Error(it) }
    }

    fun getDoctorLegalDocuments2()=viewModelScope.launch(Dispatchers.IO+handler) {
        getDoctorDocumentsResponse.postValue(Resource.Loading())
        val response= Repo.getDoctorLegalDocuments2()
        getDoctorDocumentsResponse.postValue(response?.let { handlegetDoctorLegalDocuments2(it) })
    }
    private fun handlegetDoctorLegalDocuments2(response: Response<LegalDocumentsResponse>): Resource<LegalDocumentsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<LegalDocumentsResponse>(response.errorBody()!!.string(), LegalDocumentsResponse::class.java)
        return error.Message.let { Resource.Error(it) }
    }



    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("exception","exception: ${exception.message.toString()}")
    }

}