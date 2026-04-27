package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.core.usecase.auth.*
import com.salamtak.core.utils.SessionManagement
import com.salamtak.core.utils.SessionManagement.Companion.KEY_USER_TOKEN
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.*
import com.salamtak.salamtakdoctor.salamtak_doctor.LegalDocument.UpdateDoctorDocumentResponse
import com.salamtak.domain.entities.response.UpdateLegalDocumentModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding.changeExtension
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class SetupProfileViewModel(
    private val sessionManagement: SessionManagement,
    private val getSubSpecialitiesUseCase: GetSubSpecialitiesUseCase,
    private val createSubSpecialitiesUseCase: CreateSubSpecialitiesUseCase,
    private val getSpecialitiesUseCase: GetSpecialitiesUseCase,
    private val getSeniorityLevelsUseCase: GetSeniorityLevelsUseCase,
    private val createDoctorProfileUseCase: CreateDoctorProfileUseCase,
    private val createExtraInfoUseCase: CreateExtraInfoUseCase,
    private val getNationalitiesUseCase: GetNationalitiesUseCase,
    private val getLegalDocumentsItemsUseCase: GetLegalDocumentsItemsUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    private val deleteCertificateUseCase: DeleteCertificateUseCase,
    private val getDoctorCertificatesUseCase: GetDoctorCertificatesUseCase,
    private val getDocumentUseCase: GetDocumentUseCase
) : BaseViewModel() {

    private val _subSpecialties: MutableLiveData<SubSpecialitiesResponse> = MutableLiveData()
    var subSpecialties = _subSpecialties

    private val _specialties: MutableLiveData<SubSpecialitiesResponse> = MutableLiveData()
    var specialties = _specialties

    private val _seniorityLevelsResponse: MutableLiveData<SeniorityLevelsResponse> =
        MutableLiveData()
    var seniorityLevels = _seniorityLevelsResponse

    private val _createExtraInfoResponse: MutableLiveData<CreateExtraInfoResponse> = MutableLiveData()
    var createExtraInfoResponse = _createExtraInfoResponse

    private val _countriesResponse: MutableLiveData<CountriesResponse> = MutableLiveData()
    var countriesResponse = _countriesResponse

    private val _legalDocumentsItemsResponse: MutableLiveData<LegalDocumentsItemsResponse> =
        MutableLiveData()
    var legalDocumentsItemsResponse = _legalDocumentsItemsResponse

    private val _deleteDocumentResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
    var deleteDocumentResponse = _deleteDocumentResponse

    private val _deleteCertificateResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
    var deleteCertificateResponse = _deleteCertificateResponse

    private val _createDocumentResponse = MutableLiveData<Resource<CreateDoctorDocumentResponse>>()
    var createDocumentResponse = _createDocumentResponse

    private val _updateDocumentResponse = MutableLiveData<Resource<UpdateLegalDocumentModel>>()
    var updateDocumentResponse = _updateDocumentResponse

    private val _createCertificateResponse = MutableLiveData<Resource<CreateCertificateResponse>>()
    var createCertificateResponse = _createCertificateResponse

    private val _editCertificateResponse = MutableLiveData<Resource<CreateCertificateResponse>>()
    var editCertificateResponse = _editCertificateResponse


    private val _createSubSpecialties: MutableLiveData<CreateSubSpecialistResponse> =
        MutableLiveData()
    var createSubSpecialties = _createSubSpecialties

    private val _createDoctorProfileResponse =
        MutableLiveData<Resource<CreateDoctorProfileResponse>>()
    var createDoctorProfileResponse = _createDoctorProfileResponse


    private val _certificatesResponse: MutableLiveData<CertificatesResponse> = MutableLiveData()
    var certificatesResponse = _certificatesResponse


    //    private val _getDocumentResponse :MutableLiveData<EditDocumentResponse> = MutableLiveData()
    private val _getDocumentResponse: MutableLiveData<LegalDocumentsItemsResponse> =
        MutableLiveData()
    var getDocumentResponse = _getDocumentResponse


    fun getSubSpecialities(id: Int) {
        callApi(_subSpecialties) {
            getSubSpecialitiesUseCase(
                GetSubSpecialitiesModel(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}",
                    id.toString()
                ), DateUtils.getLang()!!
            )
        }
    }

    fun getSpecialities() {
        callApi(_specialties) {
            getSpecialitiesUseCase(
                "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}",
                DateUtils.getLang()!!
            )
        }
    }

    fun getSeniorityLevels() {
        callApi(_seniorityLevelsResponse) {
            getSeniorityLevelsUseCase(
                "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}",
                DateUtils.getLang()!!
            )
        }
    }

    fun createDoctorProfile(createDoctorProfileModel: CreateDoctorProfileModel) {

        /*callApi(_createDoctorProfileResponse) {createDoctorProfileUseCase(createDoctorProfileModel,
            "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}"
            )}*/

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CREATE_DOCTOR_PROFILE}")
//            .addHeaders("Content-Type", "multipart/form-data")
//            .addHeaders("accept", "text/plain")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")


        Log.e(
            TAG,
            "createDoctorProfile: ${createDoctorProfileModel.doctorProfileModel.profileImage}"
        )

        for (i in createDoctorProfileModel.doctorProfileModel.DoctorSubSpecialist.indices) {
            upload.addMultipartParameter(
                "DoctorSubSpecialist[${i}]",
                createDoctorProfileModel.doctorProfileModel.DoctorSubSpecialist[i].toString()
            )

            Log.e(
                TAG,
                "createDoctorProfile: ${createDoctorProfileModel.doctorProfileModel.DoctorSubSpecialist[i]}"
            )


        }



        upload
//            .addMultipartParameter("UserId", createDoctorProfileModel.doctorProfileModel.UserId)
//            .addMultipartParameter("Birthday", createDoctorProfileModel.doctorProfileModel.Birthday)
            .addMultipartParameter("Birthday", createDoctorProfileModel.doctorProfileModel.Birthday)
            .addMultipartParameter(
                "FirstName",
                createDoctorProfileModel.doctorProfileModel.FirstName
            )
            .addMultipartParameter(
                "FirstNameAr",
                createDoctorProfileModel.doctorProfileModel.FirstNameAr
            )
            .addMultipartParameter(
                "MiddelName",
                createDoctorProfileModel.doctorProfileModel.MiddelName
            )
            .addMultipartParameter(
                "MiddelNameAr",
                createDoctorProfileModel.doctorProfileModel.MiddelNameAr
            )
            .addMultipartParameter("LastName", createDoctorProfileModel.doctorProfileModel.LastName)
            .addMultipartParameter(
                "LastNameAr",
                createDoctorProfileModel.doctorProfileModel.LastNameAr
            )
            .addMultipartParameter("GenderId", createDoctorProfileModel.doctorProfileModel.GenderId)
            .addMultipartParameter("NickName", createDoctorProfileModel.doctorProfileModel.NickName)
            .addMultipartParameter("NickNameAr", createDoctorProfileModel.doctorProfileModel.NickNameAr)
            .addMultipartParameter("Image", createDoctorProfileModel.doctorProfileModel.Image)
//            .addMultipartParameter("Website", createDoctorProfileModel.doctorProfileModel.Website)
            .addMultipartParameter(
                "DoctorInfo",
                createDoctorProfileModel.doctorProfileModel.DoctorInfo
            )
            .addMultipartParameter(
                "DoctorInfoAr",
                createDoctorProfileModel.doctorProfileModel.DoctorInfoAr
            )
            .addMultipartParameter(
                "NationalityId",
                createDoctorProfileModel.doctorProfileModel.NationalityId
            )
            .addMultipartFile(
                "profileImage",
                createDoctorProfileModel.doctorProfileModel.profileImage?.changeExtension("jpg")
            //new national Id
            ).addMultipartParameter(
                "NationalId",
                createDoctorProfileModel.doctorProfileModel.NationalId
            ).addMultipartParameter(
                "SyndicateId",
                createDoctorProfileModel.doctorProfileModel.SyndicateId
            )
          .addMultipartParameter("LicenseNumber", createDoctorProfileModel.doctorProfileModel.LicenseNumber)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: CreateDoctorProfileResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(CreateDoctorProfileResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

                    _createDoctorProfileResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createDoctorProfileResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }
    fun createDoctorToPolyclinic(createDoctorProfileModel: CreateDoctorProfileModel ,clinicId:Int) {

        /*callApi(_createDoctorProfileResponse) {createDoctorProfileUseCase(createDoctorProfileModel,
            "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}"
            )}*/

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CreateDoctorToPolyclinic}")
//            .addHeaders("Content-Type", "multipart/form-data")
//            .addHeaders("accept", "text/plain")
                .addQueryParameter("clinicId",clinicId.toString())
                .addQueryParameter("userId",DateUtils.getUserId())
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")


        Log.e(
            TAG,
            "createDoctorProfile: ${createDoctorProfileModel.doctorProfileModel.profileImage}"
        )

        for (i in createDoctorProfileModel.doctorProfileModel.DoctorSubSpecialist.indices) {
            upload.addMultipartParameter(
                "DoctorSubSpecialist[${i}]",
                createDoctorProfileModel.doctorProfileModel.DoctorSubSpecialist[i].toString()
            )

            Log.e(
                TAG,
                "createDoctorProfile: ${createDoctorProfileModel.doctorProfileModel.DoctorSubSpecialist[i]}"
            )


        }



        upload
//            .addMultipartParameter("UserId", createDoctorProfileModel.doctorProfileModel.UserId)
//            .addMultipartParameter("Birthday", createDoctorProfileModel.doctorProfileModel.Birthday)
            .addMultipartParameter("Birthday", createDoctorProfileModel.doctorProfileModel.Birthday)
            .addMultipartParameter(
                "FirstName",
                createDoctorProfileModel.doctorProfileModel.FirstName
            )
            .addMultipartParameter(
                "FirstNameAr",
                createDoctorProfileModel.doctorProfileModel.FirstNameAr
            )
            .addMultipartParameter(
                "MiddelName",
                createDoctorProfileModel.doctorProfileModel.MiddelName
            )
            .addMultipartParameter(
                "MiddelNameAr",
                createDoctorProfileModel.doctorProfileModel.MiddelNameAr
            )
            .addMultipartParameter("LastName", createDoctorProfileModel.doctorProfileModel.LastName)
            .addMultipartParameter(
                "LastNameAr",
                createDoctorProfileModel.doctorProfileModel.LastNameAr
            )
            .addMultipartParameter("GenderId", createDoctorProfileModel.doctorProfileModel.GenderId)
            .addMultipartParameter("NickName", createDoctorProfileModel.doctorProfileModel.NickName)
            .addMultipartParameter("NickNameAr", createDoctorProfileModel.doctorProfileModel.NickNameAr)
            .addMultipartParameter("Image", createDoctorProfileModel.doctorProfileModel.Image)
//            .addMultipartParameter("Website", createDoctorProfileModel.doctorProfileModel.Website)
            .addMultipartParameter(
                "DoctorInfo",
                createDoctorProfileModel.doctorProfileModel.DoctorInfo
            )
            .addMultipartParameter(
                "DoctorInfoAr",
                createDoctorProfileModel.doctorProfileModel.DoctorInfoAr
            )
            .addMultipartParameter(
                "NationalityId",
                createDoctorProfileModel.doctorProfileModel.NationalityId
            )
            .addMultipartFile(
                "profileImage",
                createDoctorProfileModel.doctorProfileModel.profileImage?.changeExtension("jpg")
                //new national Id
            ).addMultipartParameter(
                "NationalId",
                createDoctorProfileModel.doctorProfileModel.NationalId
            ).addMultipartParameter(
                "SyndicateId",
                createDoctorProfileModel.doctorProfileModel.SyndicateId
            )
            .addMultipartParameter("LicenseNumber", createDoctorProfileModel.doctorProfileModel.LicenseNumber)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: CreateDoctorProfileResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(CreateDoctorProfileResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

                    _createDoctorProfileResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createDoctorProfileResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }




    fun createExtraInfo(createExtraInfoModel: CreateExtraInfoModel){

        callApi(_createExtraInfoResponse) { createExtraInfoUseCase(createExtraInfoModel,
            DateUtils.getLang()!!
        ) }
    }

    fun getCountries() {
        callApi(_countriesResponse) {
            getNationalitiesUseCase(
                "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}",
                DateUtils.getLang()!!
            )
        }
    }

    fun getLegalDocumentsItems() {

        callApi(_legalDocumentsItemsResponse) {
            getLegalDocumentsItemsUseCase(
                "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}",
                DateUtils.getLang()!!
            )
        }

    }

    fun updateDoctorDocument(doctorDocumentModel: UpdateDoctorDocumentResponse) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.UPDATE_DOCTOR_DOCUMENT}")
//            .addHeaders("Content-Type", "multipart/form-data")
//            .addHeaders("accept", "text/plain")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")

        Log.e(TAG, "createDoctorDocument: ${doctorDocumentModel.documentModel.documentUrl}")


        upload
            .addMultipartParameter(
                "Id",
                doctorDocumentModel.documentModel.Id.toString()
            )
            .addMultipartFile("documentUrl", doctorDocumentModel.documentModel.documentUrl)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: UpdateLegalDocumentModel? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(UpdateLegalDocumentModel::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

                    _updateDocumentResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(UpdateLegalDocumentModel::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _updateDocumentResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }



    fun createDoctorDocument(doctorDocumentModel: CreateDoctorDocumentModel) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CREATE_DOCTOR_DOCUMENT}")
//            .addHeaders("Content-Type", "multipart/form-data")
//            .addHeaders("accept", "text/plain")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")

        Log.e(TAG, "createDoctorDocument: ${doctorDocumentModel.documentModel.document}")


        upload
            .addMultipartParameter("LegalDocumentTypeId", doctorDocumentModel.documentModel.LegalDocumentTypeId.toString())
            .addMultipartFile("document", doctorDocumentModel.documentModel.document)
            .addMultipartParameter("DocumentOrder", doctorDocumentModel.documentModel.DocumentOrder.toString())
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: CreateDoctorDocumentResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(CreateDoctorDocumentResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: ${uploadResponse!!}")

                    _createDocumentResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createDocumentResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }
    fun createPolyClinicDoctorDocument(doctorDocumentModel: CreateDoctorDocumentModel,doctorId: String) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CREATE_Poly_Clinic_DOCTOR_DOCUMENT}")
//            .addHeaders("Content-Type", "multipart/form-data")
//            .addHeaders("accept", "text/plain")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")

        Log.e(TAG, "createDoctorDocument: ${doctorDocumentModel.documentModel.document}")


        upload
            .addMultipartParameter("LegalDocumentTypeId", doctorDocumentModel.documentModel.LegalDocumentTypeId.toString())
            .addMultipartFile("document", doctorDocumentModel.documentModel.document)
            .addMultipartParameter("DocumentOrder", doctorDocumentModel.documentModel.DocumentOrder.toString())
            .addMultipartParameter("DoctorID", doctorId)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: CreateDoctorDocumentResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(CreateDoctorDocumentResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: ${uploadResponse!!}")

                    _createDocumentResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createDocumentResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }

    fun createDoctorCertificate(createCertificateModel: CreateCertificateModel) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CREATE_DOCTOR_CERTIFICATE}")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")



        upload
            .addMultipartParameter("Title", createCertificateModel.Title)
            .addMultipartParameter("TitleAr", createCertificateModel.TitleAr)
            .addMultipartParameter("Description", createCertificateModel.Description)
            .addMultipartParameter("DescriptionAr", createCertificateModel.DescriptionAr)
            .addMultipartParameter("Year", createCertificateModel.Year.toString())
            .addMultipartFile("certificateImage", createCertificateModel.certificateImage)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val gson = Gson()
                    val uploadResponse: CreateCertificateResponse? = gson.fromJson(response.toString(), CreateCertificateResponse::class.java)
//                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
//                            .build().adapter(CreateCertificateResponse::class.java)
//                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

                    _createCertificateResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createCertificateResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }
    fun createDoctorPolyClinicCertificate(createCertificateModel: CreateCertificateModel,doctorId:String) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CREATE_DOCTOR_Poly_Clinic_CERTIFICATE}")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")


        upload
            .addMultipartParameter("Title", createCertificateModel.Title)
            .addMultipartParameter("TitleAr", createCertificateModel.TitleAr)
            .addMultipartParameter("Description", createCertificateModel.Description)
            .addMultipartParameter("DescriptionAr", createCertificateModel.DescriptionAr)
            .addMultipartParameter("Year", createCertificateModel.Year.toString())
            .addMultipartParameter("DoctorID", doctorId)
            .addMultipartFile("certificateImage",  createCertificateModel.certificateImage)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val gson = Gson()
                    val uploadResponse: CreateCertificateResponse? = gson.fromJson(response.toString(), CreateCertificateResponse::class.java)
//                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
//                            .build().adapter(CreateCertificateResponse::class.java)
//                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

                    _createCertificateResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createCertificateResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }

    fun deleteDocument(deleteDocumentModel: DeleteDocumentModel) {

        callApi(_deleteDocumentResponse) {
            deleteDocumentUseCase(
                deleteDocumentModel, DateUtils.getLang()!!
            )
        }

    }

    fun deleteCertificate(deleteCertificateModel: DeleteCertificateModel) {

        callApi(_deleteCertificateResponse) {
            deleteCertificateUseCase(
                deleteCertificateModel, DateUtils.getLang()!!
            )
        }

    }

    fun editDoctorCertificate(editCertificateModel: EditCertificateModel) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.EDIT_DOCTOR_CERTIFICATE}")
//            .addHeaders("Content-Type", "multipart/form-data")
//            .addHeaders("accept", "text/plain")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")

            upload
                .addMultipartParameter("CertificateId", editCertificateModel.CertificateId)
                .addMultipartParameter("Title", editCertificateModel.Title)
                .addMultipartParameter("TitleAr", editCertificateModel.TitleAr)
                .addMultipartParameter("Description", editCertificateModel.Description)
                .addMultipartParameter("DescriptionAr", editCertificateModel.DescriptionAr)
                .addMultipartParameter("Year", editCertificateModel.Year)
                .addMultipartFile("certificateImage",  editCertificateModel.certificateImage)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build().getAsJSONObject(object : JSONObjectRequestListener {

                    override fun onResponse(response: JSONObject?) {

                        var uploadResponse : CreateCertificateResponse ?= null
                        try{
                             uploadResponse = Gson().fromJson(response.toString(), CreateCertificateResponse::class.java)

                        }catch (e : JSONException){
                            e.printStackTrace()
                        }
//                        val uploadResponse: CreateCertificateResponse? =
//                            Moshi.Builder().add(KotlinJsonAdapterFactory())
//                                .build().adapter(CreateCertificateResponse::class.java)
//                                .fromJson(response.toString())

                        Log.e("TAGWebClient", "onResponse: $uploadResponse")

                        _editCertificateResponse.value = Resource.Success(uploadResponse!!)

                    }

                    override fun onError(anError: ANError?) {


                        val apiError = anError?.toString().let {
                            val moshiAdapter = Moshi.Builder()
                                .add(KotlinJsonAdapterFactory())
                                .build().adapter(CreateProfileErrorResponse::class.java)
                            Log.e("TAG", "convertErrorBody: ${it}")


                        }

                        _editCertificateResponse.value = Resource.Error(apiError)

                        Log.e(
                            TAG,
                            "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                        )

                        Log.e(TAG, "onError2222: $apiError. ")

                    }

                })

    }


    fun getCertificates() {
        callApi(_certificatesResponse) {
            getDoctorCertificatesUseCase(
                "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}",
                DateUtils.getLang()!!
            )
        }
    }

    fun createSubSpecialities() {
        callApi(_createSubSpecialties) {
            createSubSpecialitiesUseCase(
                CreateSpecialistModel(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}", SpecialistModel(
                        "speciality", "speciality22", 1, false
                    )
                ), DateUtils.getLang()!!
            )

        }
    }

    fun getDocuments() {
        callApi(_getDocumentResponse) {
            getDocumentUseCase(
                "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}",
                DateUtils.getLang()!!
            )
        }
    }

    fun updateDoctorProfile(createDoctorProfileModel: UpdateDoctorProfileModel) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.UPDATE_DOCTOR_PROFILE}")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")

        val gson = Gson()
        val jsonObj = gson.toJson(createDoctorProfileModel)
        Log.d(TAG, "updateDoctorProfile: $jsonObj")

        for (i in createDoctorProfileModel.DoctorSubSpecialist.indices) {
            upload.addMultipartParameter(
                "DoctorSubSpecialist[${i}]",
                createDoctorProfileModel.DoctorSubSpecialist[i].toString()
            )

            Log.e(TAG, "createDoctorProfile: ${createDoctorProfileModel.DoctorSubSpecialist[i]}")
            Log.e(TAG, "manslastDoctorSubSpecialist: ${createDoctorProfileModel.DoctorSubSpecialist[i]}")

        }

        upload
            .addMultipartParameter("FirstName", createDoctorProfileModel.FirstName)
            .addMultipartParameter("FirstNameAr", createDoctorProfileModel.FirstNameAr)
            .addMultipartParameter("MiddelName", createDoctorProfileModel.MiddelName)
            .addMultipartParameter("MiddelNameAr", createDoctorProfileModel.MiddelNameAr)
            .addMultipartParameter("LastName", createDoctorProfileModel.LastName)
            .addMultipartParameter("LastNameAr", createDoctorProfileModel.LastNameAr)
            .addMultipartParameter("GenderId", createDoctorProfileModel.GenderId)
            .addMultipartParameter("SpecialistId", createDoctorProfileModel.SpecialistId.toString())
//            .addMultipartParameter("Birthday", createDoctorProfileModel.Birthday)
            .addMultipartParameter("Birthday", createDoctorProfileModel.Birthday)
            .addMultipartParameter("SeniorityLevelId", createDoctorProfileModel.SeniorityLevelId.toString())
//            .addMultipartParameter("Website", createDoctorProfileModel.Website)
            .addMultipartParameter("DoctorInfo", createDoctorProfileModel.DoctorInfo)
            .addMultipartParameter("DoctorInfoAr", createDoctorProfileModel.DoctorInfoAr)
            .addMultipartParameter("NationalityId", createDoctorProfileModel.NationalityId)
            .addMultipartFile("profileImage", createDoctorProfileModel.profileImage?.changeExtension("jpg"))
            .addMultipartParameter("Email", createDoctorProfileModel.Email)
            .addMultipartParameter("NationalId", createDoctorProfileModel.NationalId)
            .addMultipartParameter("SyndicateId", createDoctorProfileModel.SyndicateId)
            .addMultipartParameter("LicenseNumber", createDoctorProfileModel.LicenseNumber)
            .addMultipartParameter("NickName", createDoctorProfileModel.NickName)
            .addMultipartParameter("NickNameAr", createDoctorProfileModel.NickNameAr)
//            .addMultipartParameter("FacebookAccount", createDoctorProfileModel.FacebookAccount)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

//                    val uploadResponse: CreateDoctorProfileResponse? =
//                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
//                            .build().adapter(CreateDoctorProfileResponse::class.java)
//                            .fromJson(response.toString())
//
//                    Log.e("TAGWebClient", "onResponse: $uploadResponse")
//
//                    _createDoctorProfileResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {

                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createDoctorProfileResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }

}