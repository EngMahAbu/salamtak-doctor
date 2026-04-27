package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.datatransport.cct.internal.LogEvent
import com.salamtak.core.network.ApiEndpoints
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.core.usecase.auth.*
import com.salamtak.core.usecase.home.GetClinicsUseCase
import com.salamtak.core.utils.SessionManagement
import com.salamtak.core.utils.SessionManagement.Companion.KEY_USER_TOKEN
import com.salamtak.core.utils.SingleLiveEvent
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.*
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters.PeriodsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.Resource
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.imageBody
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SetupClinicViewModel(
    val sessionManagement: SessionManagement,
    val contentEndpoints: ApiEndpoints,
    val getServicesBySpecialistUseCase: GetServicesBySpecialistUseCase,
    val getServicesUseCase: GetServicesUseCase,
    val getAreasUseCase: GetAreasUseCase,
    val getCitiesUseCase: GetCitiesUseCase,
    val getClinicGalleryUseCase: GetClinicGalleryUseCase,
    val deleteClinicGalleryUseCase: DeleteClinicGalleryUseCase,
    val getClinicScheduleUseCase: GetClinicScheduleUseCase,
    val getClinicDayScheduleUseCase: GetClinicDayScheduleUseCase,
    val getMedicalExaminationDurationUseCase: GetMedicalExaminationDurationUseCase,
    val createClinicScheduleUseCase: CreateClinicScheduleUseCase,
    val editScheduleUseCase: EditScheduleUseCase,
    val getClinicsUseCase: GetClinicsUseCase,
    val setLocationUseCase: LocationUseCase
) : BaseViewModel() {

    private val _servicesResponse: MutableLiveData<ServicesBySpecialistResponse> = MutableLiveData()
    var servicesResponse = _servicesResponse

    private val _areasResponse: MutableLiveData<CitiesResponse> = MutableLiveData()
    var areasResponse = _areasResponse
    private val _locationResponse: MutableLiveData<LocationDataModel> = MutableLiveData()
    var locationResponse = _locationResponse


    private val _citiesResponse: MutableLiveData<CitiesResponse> = MutableLiveData()
    var citiesResponse = _citiesResponse

    private val _clinicGalleyResponse: MutableLiveData<GetClinicGalleryResponse> = MutableLiveData()
    var clinicGalleyResponse = _clinicGalleyResponse

    private val _deleteGalleyResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
    var deleteGalleyResponse = _deleteGalleyResponse

    private val _createClinicResponse = SingleLiveEvent<Resource<CreateClinicResponse>>()
    var createClinicResponse = _createClinicResponse

    private val _createGalleryClinicResponse =
        MutableLiveData<Resource<CreateClinicGalleryResponse>>()
    var createGalleryClinicResponse = _createGalleryClinicResponse

    private val _clinicScheduleResponse: SingleLiveEvent<ClinicScheduleResponse> = SingleLiveEvent()
    var clinicScheduleResponse = _clinicScheduleResponse

    private val _clinicDayScheduleResponse: MutableLiveData<ClinicDayScheduleResponse> =
        MutableLiveData()
    var clinicDayScheduleResponse = _clinicDayScheduleResponse


    private val _clinicDurationScheduleResponse: MutableLiveData<MedicalExaminationDurationResponse> =
        MutableLiveData()
    var clinicDurationScheduleResponse = _clinicDurationScheduleResponse

    private val _createScheduleResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
    var createScheduleResponse = _createScheduleResponse

    private val _editScheduleResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
    var editScheduleResponse = _editScheduleResponse

    private val _clinicsResponse: MutableLiveData<ClinicsResponse> = MutableLiveData()
    var clinicsResponse = _clinicsResponse

    private val _updateClinicResponse: MutableLiveData<Resource<UpdateClinicResponse>> = MutableLiveData()
    var updateClinicResponse = _updateClinicResponse

    fun getServices(id: String) {

        callApi(_servicesResponse) {
            getServicesUseCase(
                DateUtils.getLang()!!,
                QueryModel("Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}", id)
            )
        }
    }

    fun getCities(id: String) {

        callApi(_citiesResponse) {
            getCitiesUseCase(
                QueryModel("Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}", id),
                DateUtils.getLang()!!
            )
        }
    }

    fun getAreas(id: String) {

        Log.e(TAG, "getAreas: $id")

        callApi(_areasResponse) {
            getAreasUseCase(
                QueryModel("Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}", id),
                DateUtils.getLang()!!
            )
        }
    }

    fun setLocation(
        street: String,
        clinicId: String,
        cityId: String,
        areaId: String,
        latitude: String,
        longitude: String,
        floorNumber: String,
        blockNumber: String,
        countryId: String,
        appartmeneNumber: String,
        address: String,
        cityName: String,
        areaName: String
    ) {
        callApi(_locationResponse) {
            setLocationUseCase(
                LocationModel(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    street,
                    clinicId.toInt(),
                    cityId.toInt(),
                    areaId.toInt(),
                    latitude,
                    floorNumber.toInt(),
                    blockNumber,
                    longitude,
                    countryId.toInt(),
                    appartmeneNumber,
                    address,
                    cityName,
                    areaName
                ),
                DateUtils.getLang()!!
            )
        }
    }


    fun createClinic(createClinicModel: CreateClinicModel) {


        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CREATE_CLINIC}")
//            .addHeaders("Content-Type", "multipart/form-data")
//            .addHeaders("accept", "text/plain")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")


        Log.e(TAG, "createDoctorProfile: ${createClinicModel.clinicLogo}")

        for (i in createClinicModel.HealthEntityPhoneDtos.indices) {
            upload.addMultipartParameter(
                "HealthEntityPhoneDtos[${i}]",
                createClinicModel.HealthEntityPhoneDtos[i]
            )

            Log.e(TAG, "createDoctorProfile: ${createClinicModel.HealthEntityPhoneDtos[i]}")


        }

        for (x in createClinicModel.HealthEntityServiceDtos.indices) {
            upload.addMultipartParameter(
                "HealthEntityServiceDtos[${x}]",
                createClinicModel.HealthEntityServiceDtos[x]
            )

            Log.e(TAG, "createDoctorProfile: ${createClinicModel.HealthEntityServiceDtos[x]}")


        }
        for (insuranceId in createClinicModel.insurance_CompaniesID.indices) {
            upload.addMultipartParameter(
                "insurance_CompaniesID[${insuranceId}]",
                createClinicModel.insurance_CompaniesID[insuranceId]
            )

            Log.e(TAG, "createDoctorProfile: ${createClinicModel.insurance_CompaniesID[insuranceId]}")
        }
        for (featuresId in createClinicModel.featuresId.indices) {
            upload.addMultipartParameter(
                "HealthEntityFeaturesDtos[${featuresId}]",
                createClinicModel.featuresId[featuresId]
            )

        }



        upload
//            .addMultipartParameter("UserId", createDoctorProfileModel.doctorProfileModel.UserId)
            .addMultipartParameter("Name", createClinicModel.Name)
            .addMultipartParameter("NameAr", createClinicModel.NameAr)
            .addMultipartParameter("MedicalExaminationTypeId", "1")
//            .addMultipartParameter("CountryId", createClinicModel.CountryId)
//            .addMultipartParameter("CityId", createClinicModel.CityId)
//            .addMultipartParameter("AreaId", createClinicModel.AreaId)
//            .addMultipartParameter("Address", "")
//            .addMultipartParameter("Latitude", createClinicModel.Latitude)
//            .addMultipartParameter("Longitude", createClinicModel.Longitude)
//            .addMultipartParameter("BlockNo", createClinicModel.BlockNo)
//            .addMultipartParameter("FloorNo", createClinicModel.FloorNo)
//            .addMultipartParameter("FixedFee", createClinicModel.FixedFee)
            .addMultipartParameter("Inactive", "false")
            .addMultipartFile("clinicLogo", createClinicModel.clinicLogo)
            .addMultipartParameter("Email", createClinicModel.Email)
//            .addMultipartParameter("ApartmentNo", createClinicModel.ApartmentNo)
//            .addMultipartParameter("Street", createClinicModel.Street)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: CreateClinicResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(CreateClinicResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

                    _createClinicResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createClinicResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })


    }
    fun createClinicHealtentity(createClinicModel: CreateClinicModel) {


        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CREATE_HealthEntity}")
//            .addHeaders("Content-Type", "multipart/form-data")
                .addQueryParameter("healthEntityTypeEnum","6")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")


        Log.e(TAG, "createDoctorProfile: ${createClinicModel.clinicLogo}")

        for (i in createClinicModel.HealthEntityPhoneDtos.indices) {
            upload.addMultipartParameter(
                "HealthEntityPhoneDtos[${i}]",
                createClinicModel.HealthEntityPhoneDtos[i]
            )

            Log.e(TAG, "createDoctorProfile: ${createClinicModel.HealthEntityPhoneDtos[i]}")


        }

        for (x in createClinicModel.HealthEntityServiceDtos.indices) {
            upload.addMultipartParameter(
                "HealthEntityServiceDtos[${x}]",
                createClinicModel.HealthEntityServiceDtos[x]
            )

            Log.e(TAG, "createDoctorProfile: ${createClinicModel.HealthEntityServiceDtos[x]}")


        }
        for (insuranceId in createClinicModel.insurance_CompaniesID.indices) {
            upload.addMultipartParameter(
                "insurance_CompaniesID[${insuranceId}]",
                createClinicModel.insurance_CompaniesID[insuranceId]
            )

            Log.e(TAG, "createDoctorProfile: ${createClinicModel.insurance_CompaniesID[insuranceId]}")
        }
        for (featuresId in createClinicModel.featuresId.indices) {
            upload.addMultipartParameter(
                "insurance_CompaniesNames[${featuresId}]",
                createClinicModel.featuresId[featuresId]
            )

        }



        upload
//            .addMultipartParameter("UserId", createDoctorProfileModel.doctorProfileModel.UserId)
            .addMultipartParameter("Name", createClinicModel.Name)
            .addMultipartParameter("NameAr", createClinicModel.NameAr)
            .addMultipartParameter("MedicalExaminationTypeId", "1")
            .addMultipartParameter("Poly_infoID", createClinicModel.CityId)
            .addMultipartParameter("Inactive", "false")
            .addMultipartFile("clinicLogo", createClinicModel.clinicLogo)
            .addMultipartParameter("Email", createClinicModel.Email)
//            .addMultipartParameter("ApartmentNo", createClinicModel.ApartmentNo)
//            .addMultipartParameter("Street", createClinicModel.Street)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: CreateClinicResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(CreateClinicResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

                    _createClinicResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createClinicResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })


    }

    fun createClinicGallery(createClinicGalleryModel: CreateClinicGalleryModel) {


        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.CREATE_CLINIC_GALLERY}")
//            .addHeaders("Content-Type", "multipart/form-data")
//            .addHeaders("accept", "text/plain")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")


        Log.e(TAG, "createDoctorProfile: ${createClinicGalleryModel.clinicGallery}")





        upload
//            .addMultipartParameter("UserId", createDoctorProfileModel.doctorProfileModel.UserId)
            .addMultipartParameter("ClinicId", createClinicGalleryModel.ClinicId)
            .addMultipartFile("clinicGallery", createClinicGalleryModel.clinicGallery)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {
                    Log.e("response", response.toString())
                    try {


                        val uploadResponse: CreateClinicGalleryResponse? =
                            Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                                .build().adapter(CreateClinicGalleryResponse::class.java)
                                .fromJson(response.toString())

                        Log.e("TAGWebClient", "onResponse: $uploadResponse")
                        _createGalleryClinicResponse.value = Resource.Success(uploadResponse!!)
                    }catch ( e:Exception){

                    }


                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _createGalleryClinicResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })


    }

    fun getClinicGallery(id: String) {

        callApi(_clinicGalleyResponse) {
            getClinicGalleryUseCase(
                DateUtils.getLang()!!,
                QueryModel("Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}", id)
            )
        }
    }

    fun deleteClinicGallery(id: String) {

        callApi(_deleteGalleyResponse) {
            deleteClinicGalleryUseCase(
                DateUtils.getLang()!!,
                QueryModel("Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}", id)
            )
        }

    }

    fun getClinicSchedule(id: String) {
        Log.e(TAG, "getClinicSchedule: $id")
        callApi(_clinicScheduleResponse) {
            getClinicScheduleUseCase(
                QueryModel("Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}", id),
                DateUtils.getLang()!!

            )
        }
    }

    fun getClinicDaySchedule(clinicId: String, id: String, periodsListener: PeriodsListener) {

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                contentEndpoints.getClinicDayScheduleByClinicId(
                    DateUtils.getLang()!!,
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    clinicId, id
                )
            }.onFailure { t ->
                Log.e(TAG, "pagination: false -> ${t.message}")
                withContext(Dispatchers.Main) {
                    periodsListener.result(
                        ClinicDayScheduleResponse(
                            listOf(), t.message!!, t.hashCode(), false
                        )
                    )
                }
            }.onSuccess {

                withContext(Dispatchers.Main) {
                    periodsListener.result(it)
                }

            }
        }

    }

    fun getClinicVisitDuration(clinicId: String) {
        callApi(_clinicDurationScheduleResponse) {
            getMedicalExaminationDurationUseCase(
                "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                DateUtils.getLang()!!
            )
        }
    }

    fun createClinicSchedule(clinicScheduleModel: ClinicScheduleModel) {

        Log.e(TAG, "createClinicSchedule: $clinicScheduleModel")

        callApi(_createScheduleResponse) {
            createClinicScheduleUseCase(
                CreateClinicSchedule(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    clinicScheduleModel
                ), DateUtils.getLang()!!
            )
        }

    }

    fun editClinicSchedule(scheduleModel: ScheduleModel) {

        Log.e(TAG, "createClinicSchedule: $scheduleModel")

        callApi(_editScheduleResponse) {
            editScheduleUseCase(
                EditScheduleModel(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    scheduleModel
                ), DateUtils.getLang()!!
            )
        }

    }

    fun getClinics() {

        callApi(_clinicsResponse) {
            getClinicsUseCase(
                "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
                DateUtils.getLang()!!
            )
        }

    }

    fun updateClinicInfo(createClinicModel: CreateClinicModel) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.UPDATE_CLINIC}")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")


        Log.e(TAG, "updateDoctorProfile: ${createClinicModel.clinicLogo}")

        for (i in createClinicModel.HealthEntityPhoneDtos.indices) {
            upload.addMultipartParameter(
                "HealthEntityPhoneDtos[${i}]",
                createClinicModel.HealthEntityPhoneDtos[i]
            )

            Log.e(TAG, "updateDoctorProfile: ${createClinicModel.HealthEntityPhoneDtos[i]}")
        }

        for (x in createClinicModel.HealthEntityServiceDtos.indices) {
            upload.addMultipartParameter(
                "HealthEntityServiceDtos[${x}]",
                createClinicModel.HealthEntityServiceDtos[x]
            )

            Log.e(TAG, "updateDoctorProfile: ${createClinicModel.HealthEntityServiceDtos[x]}")
        }

        for (insuranceId in createClinicModel.insurance_CompaniesID.indices) {
            upload.addMultipartParameter(
                "insurance_CompaniesDtos[${insuranceId}]",
                createClinicModel.insurance_CompaniesID[insuranceId]
            )

            Log.e(TAG, "createDoctorProfile: ${createClinicModel.insurance_CompaniesID[insuranceId]}")
        }
        for (insuranceName in createClinicModel.featuresId.indices) {
            upload.addMultipartParameter(
                "HealthEntityFeaturesDtos[${insuranceName}]",
                createClinicModel.featuresId[insuranceName]
            )

        }



        upload
            .addMultipartParameter("Name", createClinicModel.Name)
            .addMultipartParameter("NameAr", createClinicModel.NameAr)
            .addMultipartParameter("Inactive", createClinicModel.Inactive.toString())
            .addMultipartFile("clinicLogo", createClinicModel.clinicLogo)
            .addMultipartParameter("ClinicId", createClinicModel.ClinicId)
            .addMultipartParameter("MedicalExaminationType", "1")
            .addMultipartParameter("Email", createClinicModel.Email)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    Log.e(TAG, "onResponse: $response" )
                    val uploadResponse: UpdateClinicResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(UpdateClinicResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

//                    _createClinicResponse.value = Resource.Success(uploadResponse!!)

//                    Log.e("TAGWebClient", "onResponse: $uploadResponse")
//
                    _updateClinicResponse.value = Resource.Success(uploadResponse!!)
//                    _createClinicResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {

                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _updateClinicResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }


    fun updateClinic(createClinicModel: CreateClinicModel) {

        val lang = DateUtils.getLang()!!

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val upload =
            AndroidNetworking.upload("${UseCaseModule.BASE_URL}$lang${UseCaseModule.UPDATE_CLINIC}")
                .addHeaders("Authorization", "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)}")


        Log.e(TAG, "updateDoctorProfile: ${createClinicModel.clinicLogo}")

        for (i in createClinicModel.HealthEntityPhoneDtos.indices) {
            upload.addMultipartParameter(
                "HealthEntityPhoneDtos[${i}]",
                createClinicModel.HealthEntityPhoneDtos[i]
            )

            Log.e(TAG, "updateDoctorProfile: ${createClinicModel.HealthEntityPhoneDtos[i]}")
        }

        for (x in createClinicModel.HealthEntityServiceDtos.indices) {
            upload.addMultipartParameter(
                "HealthEntityServiceDtos[${x}]",
                createClinicModel.HealthEntityServiceDtos[x]
            )

            Log.e(TAG, "updateDoctorProfile: ${createClinicModel.HealthEntityServiceDtos[x]}")
        }

        upload
            .addMultipartParameter("Name", createClinicModel.Name)
            .addMultipartParameter("NameAr", createClinicModel.NameAr)
            .addMultipartParameter("CountryId", createClinicModel.CountryId)
            .addMultipartParameter("CityId", createClinicModel.CityId)
            .addMultipartParameter("AreaId", createClinicModel.AreaId)
            .addMultipartParameter("Address", createClinicModel.Address)
            .addMultipartParameter("Latitude", createClinicModel.Latitude)
            .addMultipartParameter("Longitude", createClinicModel.Longitude)
            .addMultipartParameter("BlockNo", createClinicModel.BlockNo)
            .addMultipartParameter("FloorNo", createClinicModel.FloorNo)
            .addMultipartParameter("FixedFee", createClinicModel.FixedFee)
            .addMultipartParameter("Inactive", createClinicModel.Inactive.toString())
            .addMultipartFile("clinicLogo", createClinicModel.clinicLogo)
            .addMultipartParameter("ClinicId", createClinicModel.ClinicId)
            .addMultipartParameter("Email", createClinicModel.Email)
            .addMultipartParameter("ApartmentNo", createClinicModel.ApartmentNo)
            .addMultipartParameter("Street", createClinicModel.Street)
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    val uploadResponse: UpdateClinicResponse? =
                        Moshi.Builder().addLast(KotlinJsonAdapterFactory())
                            .build().adapter(UpdateClinicResponse::class.java)
                            .fromJson(response.toString())

                    Log.e("TAGWebClient", "onResponse: $uploadResponse")

//                    _createClinicResponse.value = Resource.Success(uploadResponse!!)

//                    Log.e("TAGWebClient", "onResponse: $uploadResponse")
//
                    _updateClinicResponse.value = Resource.Success(uploadResponse!!)
//                    _createClinicResponse.value = Resource.Success(uploadResponse!!)

                }

                override fun onError(anError: ANError?) {


                    val apiError = anError?.toString().let {
                        val moshiAdapter = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build().adapter(CreateProfileErrorResponse::class.java)
                        Log.e("TAG", "convertErrorBody: ${it}")


                    }

                    _updateClinicResponse.value = Resource.Error(apiError)

                    Log.e(
                        TAG,
                        "onError: #${anError?.response} ${anError?.message} ->> ${anError?.errorBody} ${anError?.cause}"
                    )

                    Log.e(TAG, "onError2222: $apiError. ")

                }

            })

    }


}