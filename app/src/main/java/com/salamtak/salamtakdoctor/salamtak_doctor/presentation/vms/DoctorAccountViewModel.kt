package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.domain.entities.AnswersQuestionsResponse
import com.salamtak.domain.entities.response.doctoraccount.DoctorInvoiceRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.AddInvoiceItemRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.DoctorInvoiceResponse
import com.salamtak.domain.entities.response.doctoraccount.invoice.EditInvoiceRequest
import com.salamtak.domain.entities.response.symtemos.SyemtomsResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn.NewLoginRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.NewClinicRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class DoctorAccountViewModel : ViewModel() {

    val Repo = NewLoginRepository()
    val repo = NewClinicRepository()
    var doctorsCountResponse = MutableLiveData<Resource<AnswersQuestionsResponse>>()
    var patientCountResponse = MutableLiveData<Resource<DoctorInvoiceResponse>>()
    var patientsyemtomsResponse = MutableLiveData<Resource<SyemtomsResponse>>()
    var doctorFeedbackResponse = MutableLiveData<Resource<SyemtomsResponse>>()



    fun createInvoice(doctorInvoiceRequest: DoctorInvoiceRequest)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorsCountResponse.postValue(Resource.Loading())
        val response=Repo.createInvoice(doctorInvoiceRequest)
        doctorsCountResponse.postValue(response?.let { handleCreateInvoice(it) })
    }
    private fun handleCreateInvoice(response: Response<AnswersQuestionsResponse>): Resource<AnswersQuestionsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<AnswersQuestionsResponse>(response.errorBody()!!.string(), AnswersQuestionsResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun addInvoiceItems(addInvoiceItemRequest: AddInvoiceItemRequest)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorsCountResponse.postValue(Resource.Loading())
        val response=Repo.addInvoiceItems(addInvoiceItemRequest)
        doctorsCountResponse.postValue(response?.let { handleAddInvoiceItems(it) })
    }
    private fun handleAddInvoiceItems(response: Response<AnswersQuestionsResponse>): Resource<AnswersQuestionsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<AnswersQuestionsResponse>(response.errorBody()!!.string(), AnswersQuestionsResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun editInvoiceItem(editInvoiceRequest: EditInvoiceRequest)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorsCountResponse.postValue(Resource.Loading())
        val response=Repo.editInvoiceItem(editInvoiceRequest)
        doctorsCountResponse.postValue(response?.let { handleEditInvoiceItem(it) })
    }
    private fun handleEditInvoiceItem(response: Response<AnswersQuestionsResponse>): Resource<AnswersQuestionsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<AnswersQuestionsResponse>(response.errorBody()!!.string(), AnswersQuestionsResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun deleteInvoiceItem(id: Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorsCountResponse.postValue(Resource.Loading())
        val response=Repo.deleteInvoiceItem(id)
        doctorsCountResponse.postValue(response?.let { handleDeleteInvoiceItem(it) })
    }
    private fun handleDeleteInvoiceItem(response: Response<AnswersQuestionsResponse>): Resource<AnswersQuestionsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<AnswersQuestionsResponse>(response.errorBody()!!.string(), AnswersQuestionsResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getInvoicesByPatientId(patientId: Int?)=viewModelScope.launch(Dispatchers.IO+handler) {
        patientCountResponse.postValue(Resource.Loading())
        val response=Repo.getInvoicesByPatientId(patientId)
        patientCountResponse.postValue(response?.let { handleGetInvoicesByPatientId(it) })
    }
    private fun handleGetInvoicesByPatientId(response: Response<DoctorInvoiceResponse>): Resource<DoctorInvoiceResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<DoctorInvoiceResponse>(response.errorBody()!!.string(), DoctorInvoiceResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getSymptomsByAppointmentId(appointmentId: Int?)=viewModelScope.launch(Dispatchers.IO+handler) {
        patientsyemtomsResponse.postValue(Resource.Loading())
        val response=Repo.getSymptomsByAppointmentId(appointmentId)
        patientsyemtomsResponse.postValue(response?.let { handleGetSymptomsByAppointmentId(it) })
    }
    private fun handleGetSymptomsByAppointmentId(response: Response<SyemtomsResponse>): Resource<SyemtomsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<SyemtomsResponse>(response.errorBody()!!.string(), SyemtomsResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun createDoctorResponse(request: HashMap<String, String>)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorFeedbackResponse.postValue(Resource.Loading())
        val response=Repo.createDoctorResponse(request)
        doctorFeedbackResponse.postValue(response?.let { handleCreateDoctorResponse(it) })
    }
    private fun handleCreateDoctorResponse(response: Response<SyemtomsResponse>): Resource<SyemtomsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<SyemtomsResponse>(response.errorBody()!!.string(), SyemtomsResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }



    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("exception","exception: ${exception.message.toString()}")
    }

}