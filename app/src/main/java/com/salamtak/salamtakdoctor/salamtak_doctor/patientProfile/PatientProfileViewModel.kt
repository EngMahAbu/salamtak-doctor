package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.core.utils.SessionManagement
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.Resource
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.Multipart
import java.util.concurrent.TimeUnit

class PatientProfileViewModel : ViewModel() {

    val repo = PatientProfileRepository()
    var showLoading = MutableLiveData<Boolean>()
    var patientProfileModel = MutableLiveData<PatientProfileResponse>()
    var patientEmrModel = MutableLiveData<EmrTitleResponse>()
    var emrDetailsModel = MutableLiveData<EmrDetailsResponse>()
    val error = MutableLiveData<ErrorModel>()
    var instructionModel = MutableLiveData<EmrInstructionResponse>()
    var descriptionModel = MutableLiveData<EmrDescriptionResponse>()
    var emrAttachmentModel = MutableLiveData<EmrAttachmentResponse>()

    var updateMedicalModel = MutableLiveData<PatientMedicalResponse>()

    val image = MutableLiveData<MultipartBody.Part?>()
    val appointmentId = MutableLiveData<String>("")


    private val _createAttachmentEmrResponse =
        MutableLiveData<Resource<EmrAttachmentResponse>>()
    var createAttachmentEmrResponse = _createAttachmentEmrResponse

    fun getPatientProfile(appointmentId: Int) {

        showLoading.value = true
        viewModelScope.launch {
            val patientProfile = repo.getPatientProfile(appointmentId)

            when (patientProfile) {
                is NetworkResponse.Success -> {
                    patientProfileModel.value = patientProfile.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = patientProfile.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun getPatientEmr(patientId: Int) {

        showLoading.value = true
        viewModelScope.launch {
            val patientEmr = repo.getPatientEmr(patientId)

            when (patientEmr) {
                is NetworkResponse.Success -> {
                    patientEmrModel.value = patientEmr.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = patientEmr.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun setInstructions(emrInstructionModel: EmrInstructionModel) {
        showLoading.value = true
        viewModelScope.launch {
            val emrInstruction = repo.setEmrInstruction(emrInstructionModel)

            when (emrInstruction) {
                is NetworkResponse.Success -> {
                    instructionModel.value = emrInstruction.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = emrInstruction.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun setEmrDescription(emrDescriptionModel: EmrDescriptionModel) {
        showLoading.value = true
        viewModelScope.launch {
            val emrDescription = repo.setEmrDescription(emrDescriptionModel)

            when (emrDescription) {
                is NetworkResponse.Success -> {
                    descriptionModel.value = emrDescription.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = emrDescription.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun getPatientMedical(patientId:Int) {
        showLoading.value = true
        viewModelScope.launch {
            val blood = repo.getPatientMedical(patientId)

            when (blood) {
                is NetworkResponse.Success -> {
                    updateMedicalModel.value = blood.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = blood.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

//    fun setEmrAttachment(summryMultiPart: RequestBody) {
//        showLoading.value = true
//        viewModelScope.launch {
//            val emrAttachment = repo.sendEmrAttachment(image.value, summryMultiPart)
//
//            when (emrAttachment) {
//                is NetworkResponse.Success -> {
//                    emrAttachmentModel.value = emrAttachment.body
//                }
//                is NetworkResponse.ApiError -> {
//                    error.value = emrAttachment.body
//                }
//                is NetworkResponse.NetworkError -> {
//                }
//                is NetworkResponse.UnknownError -> {
//                }
//            }
//            showLoading.value = false
//        }
//    }
//
//    fun saveClicked() {
//        val summryMultiPart =
//            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), appointmentId.value!!)
//
//        setEmrAttachment(summryMultiPart)
//    }

    fun createEmrAttachment(sendAttathmentEmrModel: SendAttathmentEmrModel , sessionManagement: SessionManagement) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.SEND_ATTACHMENT_EMR}")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}")

        Log.e(TAG, "createDoctorProfile: ${sendAttathmentEmrModel.document}")

        upload
            .addMultipartParameter("AppointmentId", sendAttathmentEmrModel.AppointmentId)
            .addMultipartFile("document", sendAttathmentEmrModel.document)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: EmrAttachmentResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(EmrAttachmentResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

                    _createAttachmentEmrResponse.value = Resource.Success(uploadResponse!!)
                }
                override fun onError(anError: ANError?) {
                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")

                    }
                    _createAttachmentEmrResponse.value = Resource.Error(apiError)
                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )
                    Log.e(TAG, "onError2222: $apiError. ")
                }
            })
    }


}