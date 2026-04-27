package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.salamtak.core.network.ApiEndpoints
import com.salamtak.core.usecase.auth.*
import com.salamtak.core.utils.SessionManagement
import com.salamtak.core.utils.SessionManagement.Companion.KEY_USER_TOKEN
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.*
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters.ServicesPeriodsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SetupServicesViewModel(
    private val sessionManagement: SessionManagement,
    private val apiEndpoints: ApiEndpoints,
    private val getServicesTypesUseCase: GetServicesTypesUseCase,
    private val getServiceScheduleUseCase: GetServiceScheduleUseCase,
    private val createDoctorServiceScheduleUseCase: CreateDoctorServiceScheduleUseCase,
    val getServiceDurationUseCase: GetMedicalExaminationDurationUseCase,
    val editScheduleUseCase: EditScheduleUseCase,
    private val getPolyServiceScheduleUseCase: PolyClinicGetServiceScheduleUseCase,
    private val createPolyClinicServiceScheduleUseCase:CreatePolyClinicsDoctorServiceScheduleUseCase,
    val createClinicScheduleUseCase: CreateClinicScheduleUseCase,
    ) : BaseViewModel() {

    private val _servicesTypesResponse: MutableLiveData<ServicesTypesResponse> = MutableLiveData()
    var servicesTypesResponse = _servicesTypesResponse

    private val _serviceScheduleResponse: MutableLiveData<ServiceScheduleResponse> =
        MutableLiveData()
    var serviceScheduleResponse = _serviceScheduleResponse


    private val _polyServiceScheduleResponse: MutableLiveData<ServiceScheduleResponse> =
        MutableLiveData()
    var polyServiceScheduleResponse = _polyServiceScheduleResponse

    private val _serviceDurationScheduleResponse: MutableLiveData<MedicalExaminationDurationResponse> =
        MutableLiveData()
    var serviceDurationScheduleResponse = _serviceDurationScheduleResponse

    private val _createScheduleResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
    var createScheduleResponse = _createScheduleResponse

    private val _createPolyclinicScheduleResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
    var createPolyClinicScheduleResponse = _createPolyclinicScheduleResponse

    private val _editScheduleResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
    var editScheduleResponse = _editScheduleResponse

    fun getServicesTypes() {

        callApi(_servicesTypesResponse) {
            getServicesTypesUseCase(
                "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
                DateUtils.getLang()!!
            )
        }

    }


//    fun getServiceSchedule(id: String) {
//        Log.e(TAG, "getClinicSchedule: $id")
//        callApi(_serviceScheduleResponse) {
//            getServiceScheduleUseCase(
//                QueryModel("Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}", id),
//                sessionManagement.getLang()!!
//
//            )
//        }
//    }

    fun getServiceSchedule(id: String, clinicId: String) {
        Log.e(TAG, "getClinicSchedule: $id")
        callApi(_serviceScheduleResponse) {
            getServiceScheduleUseCase(
                MultiQueryModel(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    id,
                    clinicId
                ),
                DateUtils.getLang()!!
            )
        }

    }

    fun getPolyServiceSchedule(id: String, clinicId: String) {
        Log.e(TAG, "getClinicSchedule: $id")
        callApi(_polyServiceScheduleResponse) {
            getPolyServiceScheduleUseCase(
                MultiQueryModel(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    id,
                    clinicId
                ),
                DateUtils.getLang()!!

            )
        }

    }

    fun getMedicalDurations(clinicId: String) {
        callApi(_serviceDurationScheduleResponse) {
            getServiceDurationUseCase(
                "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                DateUtils.getLang()!!
            )
        }
    }


    fun getServiceDaySchedule(
        serviceId: String,
        id: String,
        periodsListener: ServicesPeriodsListener,
        clinicId:String
    ) {
        Log.e(TAG, "getClinicScheduleDay: $id $clinicId $serviceId")

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                apiEndpoints.getServiceDayScheduleByServiceDayId(
                    DateUtils.getLang()!!,
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    serviceId,id,clinicId
                )
            }.onFailure { t ->
                Log.e(TAG, "pagination: false -> ${t.message}")
                withContext(Dispatchers.Main) {
                    periodsListener.result(
                        ServiceDayScheduleResponse(
                            listOf(), t.message!!, t.hashCode(), false
                        )
                    )
                }
            }.onSuccess {
//                Log.e(TAG, "pagination: true -> ${it.body()}")

                withContext(Dispatchers.Main) {
                    periodsListener.result(it)
                }

            }
        }

    }

    fun createServiceSchedule(doctorServiceSchedule: DoctorServiceSchedule) {

        Log.e(TAG, "createClinicSchedule: $doctorServiceSchedule")

        callApi(_createScheduleResponse) {
            createDoctorServiceScheduleUseCase(
                CreateDoctorServiceSchedule(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    doctorServiceSchedule
                ), DateUtils.getLang()!!
            )
        }

    }

    fun createPolyClinicServiceSchedule(doctorServiceSchedule: DoctorServiceSchedule) {

        Log.e(TAG, "createClinicSchedule: $doctorServiceSchedule")

        callApi(_createPolyclinicScheduleResponse) {
            createPolyClinicServiceScheduleUseCase(
                CreateDoctorServiceSchedule(
                    "Bearer ${sessionManagement.getValue(KEY_USER_TOKEN)!!}",
                    doctorServiceSchedule
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


}