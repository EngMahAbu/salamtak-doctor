package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.domain.entities.AnswersDoctorResponse
import com.salamtak.domain.entities.AnswersQuestionsResponse
import com.salamtak.domain.entities.QuestionsResponse
import com.salamtak.domain.entities.response.CertificatesResponse
import com.salamtak.domain.entities.response.RegisterResponse
import com.salamtak.domain.entities.response.checktoken.CheckTokenResponse
import com.salamtak.domain.entities.response.doctoraccount.invoice.DoctorInvoiceResponse
import com.salamtak.domain.entities.response.newdocuents.LegalDocumentsResponse
import com.salamtak.domain.entities.response.offers.OfferResponse
import com.salamtak.domain.entities.response.poly.PolyClinicsDoctorsResponse
import com.salamtak.domain.entities.response.poly.get.GetPolyInfoResponse
import com.salamtak.domain.entities.response.poly.get.GetPolyResponse
import com.salamtak.domain.entities.response.poly.get.update.UpdatePolyClinicInfo
import com.salamtak.domain.entities.response.videos.DoctorVideosResponse
import com.salamtak.domain.entities.resquest.auth.DoctorVideoRequest
import com.salamtak.domain.entities.resquest.auth.RegisterModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn.NewLoginRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.NewClinicRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response


class PolyClinicsViewModel : ViewModel() {

    val Repo = NewLoginRepository()
    val repo = NewClinicRepository()
    var polyClinicDoctorsResponse = MutableLiveData<Resource<PolyClinicsDoctorsResponse>>()
    var polyOwnerIdResponse = MutableLiveData<Resource<GetPolyResponse>>()
    var polyInfoIdResponse = MutableLiveData<Resource<GetPolyInfoResponse>>()
    var updatePolyOwnerIdResponse = MutableLiveData<Resource<UpdatePolyClinicInfo>>()
    var updatePolyInfoIdResponse = MutableLiveData<Resource<UpdatePolyClinicInfo>>()
    var getcertificatePolyclinicResponse = MutableLiveData<Resource<CertificatesResponse>>()
    var getLegelDocumentsPolyclinicResponse = MutableLiveData<Resource<LegalDocumentsResponse>>()
    var createVideoReponse = MutableLiveData<Resource<OfferResponse>>()
    var createDoctorVideoReponse = MutableLiveData<Resource<OfferResponse>>()
    var getDoctorVideoReponse = MutableLiveData<Resource<DoctorVideosResponse>>()
    var deleteDoctorVideoReponse = MutableLiveData<Resource<DoctorVideosResponse>>()
    var getClinicsVideoResponse = MutableLiveData<Resource<DoctorVideosResponse>>()
    var checkUserResponse = MutableLiveData<Resource<CheckTokenResponse>>()
    var otpResponse = MutableLiveData<Resource<RegisterResponse>>()
    var questionsUserResponse = MutableLiveData<Resource<QuestionsResponse>>()
    var answerUserResponse = MutableLiveData<Resource<AnswersQuestionsResponse>>()
    var updatenotificationResponse = MutableLiveData<Resource<QuestionsResponse>>()
    var cancelQuestionsResponse = MutableLiveData<Resource<AnswersQuestionsResponse>>()
    var answersDoctorResponse = MutableLiveData<Resource<AnswersDoctorResponse>>()
    var doctorAccountResponse = MutableLiveData<Resource<DoctorInvoiceResponse>>()


    fun getDoctorByClinicId(clinicId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        polyClinicDoctorsResponse.postValue(Resource.Loading())
        val response=Repo.getDoctorByClinicId(clinicId)
        polyClinicDoctorsResponse.postValue(response?.let { handleGetDoctorClinics(it) })
    }
    private fun handleGetDoctorClinics(response: Response<PolyClinicsDoctorsResponse>): Resource<PolyClinicsDoctorsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<PolyClinicsDoctorsResponse>(response.errorBody()!!.string(), PolyClinicsDoctorsResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getpolyOwnerByID(ownerId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        polyOwnerIdResponse.postValue(Resource.Loading())
        val response=Repo.getpolyOwnerByID(ownerId)
        polyOwnerIdResponse.postValue(response?.let { handlegetpolyOwnerByID(it) })
    }

    private fun handlegetpolyOwnerByID(response: Response<GetPolyResponse>): Resource<GetPolyResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<GetPolyResponse>(response.errorBody()!!.string(), GetPolyResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getPolyClinicInfoByID(infoId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        polyInfoIdResponse.postValue(Resource.Loading())
        val response=Repo.getPolyClinicInfoByID(infoId)
        polyInfoIdResponse.postValue(response?.let { handlegetPolyClinicInfoByID(it) })
    }

    private fun handlegetPolyClinicInfoByID(response: Response<GetPolyInfoResponse>): Resource<GetPolyInfoResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<GetPolyInfoResponse>(response.errorBody()!!.string(), GetPolyInfoResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun updatePolyClinicOwner(offerRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        updatePolyOwnerIdResponse.postValue(Resource.Loading())
        val response=Repo.updatePolyClinicOwner(offerRequest)
        updatePolyOwnerIdResponse.postValue(response?.let { handleupdatePolyClinicOwner(it) })
    }

    private fun handleupdatePolyClinicOwner(response: Response<UpdatePolyClinicInfo>): Resource<UpdatePolyClinicInfo>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<UpdatePolyClinicInfo>(response.errorBody()!!.string(), UpdatePolyClinicInfo::class.java)
        return error.message?.let { Resource.Error(it) }
    }


    fun updatePolyClinicInfo(offerRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        updatePolyInfoIdResponse.postValue(Resource.Loading())
        val response=Repo.updatePolyClinicInfo(offerRequest)
        updatePolyInfoIdResponse.postValue(response?.let { handleupdatePolyClinicInfo(it) })
    }

    private fun handleupdatePolyClinicInfo(response: Response<UpdatePolyClinicInfo>): Resource<UpdatePolyClinicInfo>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<UpdatePolyClinicInfo>(response.errorBody()!!.string(), UpdatePolyClinicInfo::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getDoctorCertificatePolyClinic(DoctorID: Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        getcertificatePolyclinicResponse.postValue(Resource.Loading())
        val response=Repo.getDoctorCertificatePolyClinic(DoctorID)
        getcertificatePolyclinicResponse.postValue(response?.let { handleGetDoctorCertificatePolyClinic(it) })
    }

    private fun handleGetDoctorCertificatePolyClinic(response: Response<CertificatesResponse>): Resource<CertificatesResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<CertificatesResponse>(response.errorBody()!!.string(), CertificatesResponse::class.java)
        return error.Message?.let { Resource.Error(it) }
    }


    fun getDoctorDocumentsPolyClinic(DoctorID: Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        getLegelDocumentsPolyclinicResponse.postValue(Resource.Loading())
        val response=Repo.getDoctorDocumentsPolyClinic(DoctorID)
        getLegelDocumentsPolyclinicResponse.postValue(response?.let { handleGetDoctorDocumentsPolyClinic(it) })
    }

    private fun handleGetDoctorDocumentsPolyClinic(response: Response<LegalDocumentsResponse>): Resource<LegalDocumentsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<LegalDocumentsResponse>(response.errorBody()!!.string(), LegalDocumentsResponse::class.java)
        return error.Message?.let { Resource.Error(it) }
    }

    fun createDoctorClinicVideo(videoRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        createVideoReponse.postValue(Resource.Loading())
        val response=Repo.createDoctorClinicVideo(videoRequest)
        createVideoReponse.postValue(response?.let { handlecreateDoctorClinicVideo(it) })
    }

    private fun handlecreateDoctorClinicVideo(response: Response<OfferResponse>): Resource<OfferResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferResponse>(response.errorBody()!!.string(), OfferResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun createDoctorVideo(videoRequest:DoctorVideoRequest)=viewModelScope.launch(Dispatchers.IO+handler) {
        createDoctorVideoReponse.postValue(Resource.Loading())
        val response=Repo.createDoctorVideo(videoRequest)
        createDoctorVideoReponse.postValue(response?.let { handlecreateDoctorVideo(it) })
    }

    private fun handlecreateDoctorVideo(response: Response<OfferResponse>): Resource<OfferResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferResponse>(response.errorBody()!!.string(), OfferResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getDoctorVideos(DoctorID: Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        getDoctorVideoReponse.postValue(Resource.Loading())
        val response=Repo.getDoctorVideos(DoctorID)
        getDoctorVideoReponse.postValue(response?.let { handlegetDoctorVideos(it) })
    }

    private fun handlegetDoctorVideos(response: Response<DoctorVideosResponse>): Resource<DoctorVideosResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<DoctorVideosResponse>(response.errorBody()!!.string(), DoctorVideosResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun deleteDoctorVideo(videoId: Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        deleteDoctorVideoReponse.postValue(Resource.Loading())
        val response=Repo.deleteDoctorVideo(videoId)
        deleteDoctorVideoReponse.postValue(response?.let { handledeleteDoctorVideo(it) })
    }

    private fun handledeleteDoctorVideo(response: Response<DoctorVideosResponse>): Resource<DoctorVideosResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<DoctorVideosResponse>(response.errorBody()!!.string(), DoctorVideosResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun deleteHealthEntityVideo(videoId: Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        deleteDoctorVideoReponse.postValue(Resource.Loading())
        val response=Repo.deleteHealthEntityVideo(videoId)
        deleteDoctorVideoReponse.postValue(response?.let { handleddeleteHealthEntityVideo(it) })
    }

    private fun handleddeleteHealthEntityVideo(response: Response<DoctorVideosResponse>): Resource<DoctorVideosResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<DoctorVideosResponse>(response.errorBody()!!.string(), DoctorVideosResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getClinicVideoByClinicId(clinicId: Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        getClinicsVideoResponse.postValue(Resource.Loading())
        val response=Repo.getClinicVideoByClinicId(clinicId)
        getClinicsVideoResponse.postValue(response?.let { handleGetClinicVideoByClinicId(it) })
    }

    private fun handleGetClinicVideoByClinicId(response: Response<DoctorVideosResponse>): Resource<DoctorVideosResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<DoctorVideosResponse>(response.errorBody()!!.string(), DoctorVideosResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }


    fun checkUser()=viewModelScope.launch(Dispatchers.IO+handler) {
        checkUserResponse.postValue(Resource.Loading())
        val response=Repo.checkUser()
        checkUserResponse.postValue(response?.let { handleCheckUser(it) })
    }

    private fun handleCheckUser(response: Response<CheckTokenResponse>): Resource<CheckTokenResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<CheckTokenResponse>(response.errorBody()!!.string(), CheckTokenResponse::class.java)
        return error.let { Resource.Error("") }
    }

    fun sendOTP(registerUser: RegisterModel)=viewModelScope.launch(Dispatchers.IO+handler) {
        otpResponse.postValue(Resource.Loading())
        val response=Repo.sendOTP(registerUser)
        otpResponse.postValue(response?.let { handleSendOTP(it) })
    }

    private fun handleSendOTP(response: Response<RegisterResponse>): Resource<RegisterResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<RegisterResponse>(response.errorBody()!!.string(), RegisterResponse::class.java)
        return error.let { Resource.Error("") }
    }

    fun getAllQandAByDoctorId(type:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        questionsUserResponse.postValue(Resource.Loading())
        val response=if(type==1){
            Repo.getAllQandAByDoctorId()
        }else{
            Repo.getAllQandA()
        }
        questionsUserResponse.postValue(response?.let { handleGetAllQandAByDoctorId(it) })
    }

    private fun handleGetAllQandAByDoctorId(response: Response<QuestionsResponse>): Resource<QuestionsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<QuestionsResponse>(response.errorBody()!!.string(), QuestionsResponse::class.java)
        return error.let { Resource.Error("") }
    }

    fun getAnswersByDoctorId()=viewModelScope.launch(Dispatchers.IO+handler) {
        answersDoctorResponse.postValue(Resource.Loading())
        val response=Repo.getAnswersByDoctorId()
        answersDoctorResponse.postValue(response?.let { handleGetAnswersByDoctorId(it) })
    }

    private fun handleGetAnswersByDoctorId(response: Response<AnswersDoctorResponse>): Resource<AnswersDoctorResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<AnswersDoctorResponse>(response.errorBody()!!.string(), AnswersDoctorResponse::class.java)
        return error.let { Resource.Error("") }
    }

    fun createAnswerPatientQ(answerRequest: HashMap<String, String>)=viewModelScope.launch(Dispatchers.IO+handler) {
        answerUserResponse.postValue(Resource.Loading())
        val response=Repo.createAnswerpatientQ(answerRequest)
        answerUserResponse.postValue(response?.let { handleCreateAnswerPatientQ(it) })
    }

    private fun handleCreateAnswerPatientQ(response: Response<AnswersQuestionsResponse>): Resource<AnswersQuestionsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<AnswersQuestionsResponse>(response.errorBody()!!.string(), AnswersQuestionsResponse::class.java)
        return error.let { Resource.Error("") }
    }

    fun updateNotification()=viewModelScope.launch(Dispatchers.IO+handler) {
        updatenotificationResponse.postValue(Resource.Loading())
        val response=Repo.updateNotification()
        updatenotificationResponse.postValue(response?.let { handleUpdateNotification(it) })
    }

    private fun handleUpdateNotification(response: Response<QuestionsResponse>): Resource<QuestionsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<QuestionsResponse>(response.errorBody()!!.string(), QuestionsResponse::class.java)
        return error.let { Resource.Error("") }
    }

    fun cancelQuestion(questionID: Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        cancelQuestionsResponse.postValue(Resource.Loading())
        val response=Repo.cancelQuestion(questionID)
        cancelQuestionsResponse.postValue(response?.let { handleCancelQuestion(it) })
    }

    private fun handleCancelQuestion(response: Response<AnswersQuestionsResponse>): Resource<AnswersQuestionsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<AnswersQuestionsResponse>(response.errorBody()!!.string(), AnswersQuestionsResponse::class.java)
        return error.let { Resource.Error("") }
    }


    fun getAllPatientAccount(dateFrom: String?=null, dateTo: String?=null, patientName: String?=null)=viewModelScope.launch(Dispatchers.IO+handler) {
        doctorAccountResponse.postValue(Resource.Loading())
        val response=Repo.getAllPatientAccount(dateFrom,dateTo,patientName)
        doctorAccountResponse.postValue(response?.let { handleGetAllPatientAccount(it) })
    }

    private fun handleGetAllPatientAccount(response: Response<DoctorInvoiceResponse>): Resource<DoctorInvoiceResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<DoctorInvoiceResponse>(response.errorBody()!!.string(), DoctorInvoiceResponse::class.java)
        return error.let { Resource.Error("") }
    }


    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("exception","exception: ${exception.message.toString()}")
    }

}