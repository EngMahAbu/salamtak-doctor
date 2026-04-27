package com.salamtak.core.usecase

import android.content.Context
import com.google.gson.Gson
import com.salamtak.core.usecase.auth.*
import com.salamtak.core.usecase.home.GetClinicsUseCase
import org.koin.core.module.Module
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module

object UseCaseModule : (Context) -> Module {


    const val GSON = "GSon"
    const val APP_CONTEXT = "APP_CONTEXT"

    //live
      const val BASE_URL = "https://salamtakapi.salamtakgroup.com/api/"
      const val IMAGE_BASE_URL = "https://salamtechapi.azurewebsites.net/"
    //test
//     const val BASE_URL = "https://salamtakapi.salamtakgroup.com/api/"
//     const val IMAGE_BASE_URL = "https://salamtechapi-staging.azurewebsites.net/"


    const val REGISTER = "{culture}/User/Register"
    const val LOGIN = "{culture}/User/Login"
    const val GET_SUB_SPECIALTIES = "{culture}/Specialist/GetSubSpecialist"
    const val GET_START_MEETING = "{culture}/LookUp/StartMeeting"
    const val GET_UPDATE_MEETING = "{culture}/LookUp/UpdateMeeting"
    const val GET_SPECIALIST = "{culture}/Specialist/GetSpecialist"
    const val CREATE_SUB_SPECIALIST = "{culture}/Specialist/CreateSpecialist"
    const val CREATE_USER = "{culture}/User/CreateUser"
    const val PREF_NAME = "Salamtak-Doctor-App"
    const val GET_SENIORITY_LEVELS = "{culture}/SeniorityLevel/GetSeniorityLevel"
    const val CREATE_DOCTOR_PROFILE = "/Doctor/CreateProfile"
    const val CreateDoctorToPolyclinic = "/Doctor/CreateDoctorToPolyclinic"
    const val GET_COUNTRIES = "{culture}/LookUp/GetCountries"
    const val GET_LEGAL_DOCUMENTS_ITEMS = "{culture}/Doctor/GetDoctorLegalDocuments2"

    //    const val GET_LEGAL_DOCUMENTS_ITEMS = "{culture}/Doctor/GetDoctorDocuments"
    const val CREATE_DOCTOR_CERTIFICATE = "/Doctor/CreateDoctorCertificate"
    const val CREATE_DOCTOR_Poly_Clinic_CERTIFICATE = "/Doctor/CreateDoctorCertificatePolyclinic"
    const val EDIT_DOCTOR_CERTIFICATE = "/Doctor/UpdateDoctorCertificate"
    const val GET_DOCTOR_CERTIFICATES = "{culture}/Doctor/GetDoctorCertificate"
    const val CREATE_EXTRA_INFO = "{culture}/Doctor/CreateExtraInfo"
    const val CREATE_DOCTOR_DOCUMENT = "/Doctor/CreateDoctorDocuments"
    const val CREATE_Poly_Clinic_DOCTOR_DOCUMENT = "/Doctor/CreateDoctorDocumentsPolyClinic"
    const val UPDATE_DOCTOR_DOCUMENT = "/Doctor/UpdateDoctorDocument"
    const val DELETE_DOCUMENT = "{culture}/Doctor/DeleteDoctorDocument"
    const val DELETE_CERTIFICATE = "{culture}/Doctor/DeleteDoctorCertificates"
    const val GET_CITIES = "{culture}/City/GetCities"
    const val GET_AREAS = "{culture}/Area/GetAreasByCityId"
    const val GET_SERVICES_BY_SPECIALIST = "{culture}/Services/GetServicesBySpecialist"

    //    const val GET_SERVICES = "{culture}/Services/GetServices"
    const val GET_SERVICES = "{culture}/Services/GetServicesBySpecialityID"
    const val CREATE_CLINIC = "/DoctorClinic/CreateDoctorClinic"
    const val CREATE_HealthEntity = "/HealthEntity/CreateHealthEntity"
    const val CREATE_CLINIC_GALLERY = "/DoctorClinic/CreateClinicGallery"
    const val GET_CLINIC_GALLERY = "{culture}/DoctorClinic/GetClinicGalleryByClinicId"
    const val DELETE_CLINIC_GALLERY = "{culture}/DoctorClinic/DeleteClinicGallery"
    const val GET_CLINIC_SCHEDULE_BY_CLINIC = "{culture}/DoctorClinic/GetClinicSchedualByClinicId"
    const val GET_CLINIC_SCHEDULE_BY_DAY = "{culture}/DoctorClinic/GetClinicSchedualByClinicDayId"
    const val GET_MEDICAL_EXAMINATION_DURATION = "{culture}/LookUp/GetDurationMedicalExamination"
    const val CREATE_CLINIC_SCHEDULE = "{culture}/DoctorClinic/CreateDoctorClinicSchedual"
    const val SET_LOCATION = "{culture}/DoctorClinic/CreateExtraInfoInDoctorClinic"
    const val EDIT_SCHEDULE = "{culture}/DoctorClinic/UpdateDoctorClinicSchedual"
    const val GET_SERVICES_TYPES = "{culture}/LookUp/GetMedicalExaminationType"

    const val CREATE_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID =
        "{culture}/DoctorClinic/CreateDoctorClinicSchedual"
    const val CREATE_PolyClinic_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID =
        "{culture}/DoctorClinic/CreateDoctorClinicSchedualPolyClinic"
    const val GET_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID =
        "{culture}/DoctorClinic/GetClinicSchedualByClinicId"

    const val GET_POlyClinic_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID =
        "{culture}/DoctorClinic/GetDoctorPolyClinicByClinicId"

    const val GET_DOCTOR_SERVICE_DAY_SCHEDULE_BY_SERVICE_ID =
        "{culture}/DoctorClinic/GetClinicSchedualByClinicDayId"

    //        "{culture}/DoctorService/GetDoctorServiceSchedualByServiceDayId"
    const val GET_CLINICS = "{culture}/DoctorClinic/GetDoctorClinics"

    //new
    const val GET_DOCTOR_PROFILE = "{culture}/Doctor/GetDoctorProfile"
    const val GET_DOCTOR_PROFILE_ID = "{culture}/Doctor/GetDoctorProfileByDoctorId"
    const val GET_CURRENT_APPOINTMENT = "{culture}/DoctorAppointment/GetCurrentDoctorAppointment"
    const val GET_HISTORY_APPOINTMENT = "{culture}/DoctorAppointment/GetHistoryDoctorAppointment"
    const val GET_PATIENTPROFILE = "{culture}/Patient/GetPatientDetails"

    //    const val GET_EMR_TITLE = "{culture}/Patient/GetPatientEmrHistory"
    const val GET_EMR_TITLE = "{culture}/Patient/GetPatientEmrHistoryWithDetails"
    const val GET_EMR_DETAILS = "{culture}/Patient/GetPatientEmrDetails"
    const val SET_EMR_INSTRUCTION = "{culture}/Patient/CreatePatientEmr"
    const val SET_EMR_DESCRIPTION = "{culture}/Patient/CreatePatientEmrDetails"
    const val SEND_EMAIl_FORGET_PASSWORD = "{culture}/User/ResetPassword"
    const val GET_CLINIC_INFO_WITH_ID = "{culture}/DoctorClinic/GetDoctorClinicByClinicId"
    const val SEND_NEW_PASSWORD = "{culture}/User/ChangePassword"
    const val SEND_FIRST_NEW_PASSWORD = "{culture}/User/UpdatePassword"
    const val GET_DOCUMENT_DOCTOR = "{culture}/Doctor/GetDoctorDocuments"
    const val UPDATE_DOCTOR_PROFILE = "{culture}/Doctor/UpdateProfile"

    //    const val UPDATE_CLINIC = "/DoctorClinic/UpdateDoctorClinic"
    const val UPDATE_CLINIC = "/DoctorClinic/UpdateDoctorClinic2"
    const val SEND_ATTACHMENT_EMR = "{culture}/Patient/CreatePatientEmrDocument"
    const val CANCEL_APPOINTMENT = "{culture}/DoctorAppointment/CancelAppointment"
    const val CHANGE_APPOINTMENT_STATUS = "{culture}/DoctorAppointment/ChangeAppointmentStatus"
    const val AGORA_MEETING_ID = "{culture}/DoctorAppointment/GetAgoraMeeting"
    const val HOME_DATA = "{culture}/DoctorDashboard/GetDoctorDashboardByClinicID"
    const val GET_NEW_DOCUMENT_DOCTOR = "{culture}/Doctor/GetDoctorLegalDocuments"
    const val SEND_FILTER_DATA = "{culture}/DoctorAppointment/SearchForDoctorAppointment"
    const val DELETE_PROFILE = "{culture}/User/DeleteUser"
    const val GET_NOTIFICATION = "{culture}/PatientAppointment/GetNotifications"
    const val GET_PATIENT_ACCOUNT = "{culture}/Patient/GetPatientAccount"
    const val ADD_PATIENT_ACCOUNT = "{culture}/Patient/CreatePatientAccount"
    const val EDIT_PATIENT_ACCOUNT = "{culture}/Patient/EditPatientAccount"
    const val DELETE_PATIENT_ACCOUNT = "{culture}/Patient/DeletePatientAccount"
    const val GET_WHATSUP_ADS = "{culture}/Ads/GetWhatsAppAds"
    const val GET_HOME_ADS = "{culture}/Ads/GetVidoesAds"
    const val GET_VACTION = "{culture}/DoctorVacation/GetDoctorVacations"
    const val ADD_VACTION = "{culture}/DoctorVacation/CreateDoctorVacations"
    const val EDIT_VACTION = "{culture}/DoctorVacation/UpdateDoctorVacation"
    const val DELETE_VACTION = "{culture}/DoctorVacation/DeleteDoctorVacations"
    const val GET_PATIENT_MEDICAL_DATA = "{culture}/Patient/GetPatientMedicalInfo"
    const val GET_AGORA_APP_ID = "{culture}/LookUp/GetAgoraAppID"
    const val GET_AGORA_MEETING_NOTIFICATION = "{culture}/Notification/GetAgoraMeeting"
    const val GetAllInsurance = "{culture}/Insurance_Companies/GetAllInsurance_Companies"


    override fun invoke(context: Context) = module {

        single(StringQualifier(GSON)) {
            Gson()
        }

        single(StringQualifier(APP_CONTEXT)) {
            context
        }

        factory(StringQualifier(LOGIN)) {
            LoginUseCase()
        }

        factory(StringQualifier(REGISTER)) {
            RegisterUseCase()
        }

        factory(StringQualifier(CREATE_USER)) {
            CreateUserUseCase()
        }

        factory(StringQualifier(GET_SUB_SPECIALTIES)) {
            GetSubSpecialitiesUseCase()
        }

        factory(StringQualifier(CREATE_SUB_SPECIALIST)) {
            CreateSubSpecialitiesUseCase()
        }

        factory(StringQualifier(GET_SPECIALIST)) {
            GetSpecialitiesUseCase()
        }

        factory(StringQualifier(CREATE_DOCTOR_PROFILE)) {
            CreateDoctorProfileUseCase()
        }

        factory(StringQualifier(CREATE_DOCTOR_PROFILE)) {
            CreateExtraInfoUseCase()
        }

        factory(StringQualifier(GET_SENIORITY_LEVELS)) {
            GetSeniorityLevelsUseCase()
        }

        factory(StringQualifier(GET_COUNTRIES)) {
            GetNationalitiesUseCase()
        }

        factory(StringQualifier(GET_LEGAL_DOCUMENTS_ITEMS)) {
            GetLegalDocumentsItemsUseCase()
        }

        factory(StringQualifier(CREATE_DOCTOR_DOCUMENT)) {
            CreateDoctorDocumentUseCase()
        }

        factory(StringQualifier(DELETE_DOCUMENT)) {
            DeleteDocumentUseCase()
        }

        factory(StringQualifier(DELETE_CERTIFICATE)) {
            DeleteCertificateUseCase()
        }

        factory(StringQualifier(GET_DOCTOR_CERTIFICATES)) {
            GetDoctorCertificatesUseCase()
        }

        factory(StringQualifier(GET_AREAS)) {
            GetAreasUseCase()
        }

        factory(StringQualifier(GET_CITIES)) {
            GetCitiesUseCase()
        }

        factory(StringQualifier(GET_SERVICES_BY_SPECIALIST)) {
            GetServicesBySpecialistUseCase()
        }

        factory(StringQualifier(GET_SERVICES)) {
            GetServicesUseCase()
        }

        factory(StringQualifier(DELETE_CLINIC_GALLERY)) {
            DeleteClinicGalleryUseCase()
        }

        factory(StringQualifier(GET_CLINIC_GALLERY)) {
            GetClinicGalleryUseCase()
        }

        factory(StringQualifier(GET_CLINIC_SCHEDULE_BY_CLINIC)) {
            GetClinicScheduleUseCase()
        }

        factory(StringQualifier(GET_CLINIC_SCHEDULE_BY_DAY)) {
            GetClinicDayScheduleUseCase()
        }

        factory(StringQualifier(GET_MEDICAL_EXAMINATION_DURATION)) {
            GetMedicalExaminationDurationUseCase()
        }

        factory(StringQualifier(CREATE_CLINIC_SCHEDULE)) {
            CreateClinicScheduleUseCase()
        }

        factory(StringQualifier(EDIT_SCHEDULE)) {
            EditScheduleUseCase()
        }

        factory(StringQualifier(GET_SERVICES_TYPES)) {
            GetServicesTypesUseCase()
        }

        factory(StringQualifier(CREATE_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)) {
            CreateDoctorServiceScheduleUseCase()
        }

        factory(StringQualifier(GET_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)) {
            GetServiceScheduleUseCase()
        }
        factory(StringQualifier(GET_POlyClinic_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)) {
            PolyClinicGetServiceScheduleUseCase()
        }

        factory(StringQualifier(CREATE_PolyClinic_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)) {
            CreatePolyClinicsDoctorServiceScheduleUseCase()
        }

        factory(StringQualifier(GET_DOCTOR_SERVICE_DAY_SCHEDULE_BY_SERVICE_ID)) {
            GetServiceDayScheduleUseCase()
        }

        factory(StringQualifier(GET_CLINICS)) {
            GetClinicsUseCase()
        }

        factory(StringQualifier(GET_NEW_DOCUMENT_DOCTOR)) {
            GetDocumentUseCase()
        }

        factory(StringQualifier(CREATE_EXTRA_INFO)) {
            CreateExtraInfoUseCase()
        }
        factory(StringQualifier(SET_LOCATION)) {
            LocationUseCase()
        }
    }

}