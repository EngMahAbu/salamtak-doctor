package com.salamtak.core.network


import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.domain.UpdateMeetingResponse
import com.salamtak.domain.entities.AnswersDoctorResponse
import com.salamtak.domain.entities.AnswersQuestionsResponse
import com.salamtak.domain.entities.QuestionsResponse
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.response.checktoken.CheckTokenResponse
import com.salamtak.domain.entities.response.data.ClinicsInfoResponse
import com.salamtak.domain.entities.response.doctoraccount.DoctorInvoiceRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.AddInvoiceItemRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.DoctorInvoiceResponse
import com.salamtak.domain.entities.response.doctoraccount.invoice.EditInvoiceRequest
import com.salamtak.domain.entities.response.newdocuents.LegalDocumentsResponse
import com.salamtak.domain.entities.response.offers.ClincsResponse
import com.salamtak.domain.entities.response.offers.DoctorOffersResponse
import com.salamtak.domain.entities.response.offers.GetGallaryByIDResponse
import com.salamtak.domain.entities.response.offers.GetOffersByOfferIDResponse
import com.salamtak.domain.entities.response.offers.MainCategoryResponse
import com.salamtak.domain.entities.response.offers.OfferGallaryResponse
import com.salamtak.domain.entities.response.offers.OfferResponse
import com.salamtak.domain.entities.response.offers.OfferServicesResponse
import com.salamtak.domain.entities.response.offers.SubCategoryResponse
import com.salamtak.domain.entities.response.hospital.HospitalActionResponse
import com.salamtak.domain.entities.response.hospital.HospitalBranchRequest
import com.salamtak.domain.entities.response.hospital.HospitalBranchesResponse
import com.salamtak.domain.entities.response.hospital.HospitalClinicsResponse
import com.salamtak.domain.entities.response.hospital.HospitalCreateClinicRequest
import com.salamtak.domain.entities.response.hospital.HospitalCreateInfoRequest
import com.salamtak.domain.entities.response.hospital.HospitalInsuranceResponse
import com.salamtak.domain.entities.response.hospital.HospitalDataResponse
import com.salamtak.domain.entities.response.hospital.HospitalDoctorProfileResponse
import com.salamtak.domain.entities.response.hospital.HospitalScheduleRequest
import com.salamtak.domain.entities.response.hospital.HospitalScheduleResponse
import com.salamtak.domain.entities.response.hospital.HospitalUpdateClinicRequest
import com.salamtak.domain.entities.response.hospital.HospitalLookupResponse
import com.salamtak.domain.entities.response.hospital.HospitalOwnerResponse
import com.salamtak.domain.entities.response.hospital.HospitalDashboardResponse
import com.salamtak.domain.entities.response.hospital.HospitalDoctorsResponse
import com.salamtak.domain.entities.response.hospital.HospitalInfoResponse
import com.salamtak.domain.entities.response.poly.PolyClinicAdminResponse
import com.salamtak.domain.entities.response.poly.PolyClinicInfoResponse
import com.salamtak.domain.entities.response.poly.PolyClinicsDoctorsResponse
import com.salamtak.domain.entities.response.queue.CancelQueueTicketRequest
import com.salamtak.domain.entities.response.queue.QueueActionResponse
import com.salamtak.domain.entities.response.queue.QueueBoardResponse
import com.salamtak.domain.entities.response.queue.QueueTicketActionRequest
import com.salamtak.domain.entities.response.poly.get.GetPolyInfoResponse
import com.salamtak.domain.entities.response.poly.get.GetPolyResponse
import com.salamtak.domain.entities.response.poly.get.update.UpdatePolyClinicInfo
import com.salamtak.domain.entities.response.symtemos.SyemtomsResponse
import com.salamtak.domain.entities.response.videos.DoctorVideosResponse
import com.salamtak.domain.entities.resquest.auth.*
import com.salamtak.domain.repositories.update.UpdateDoctorProfileResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiEndpoints {

    @POST(UseCaseModule.LOGIN)
    suspend fun login(
        @Path("culture") culture: String,
        @Body loginModel: LoginModel
    ): UserLoginResponse

    @POST(UseCaseModule.REGISTER)
    suspend fun register(
        @Path("culture") culture: String,
        @Body registerModel: RegisterModel
    ): RegisterResponse


    @POST(UseCaseModule.CREATE_USER)
    suspend fun createUSER(
        @Path("culture") culture: String,
        @Body registerModel: RegisterModel
    ): UserLoginResponse

    @GET(UseCaseModule.GET_SUB_SPECIALTIES)
    suspend fun getSubSpecialities(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("specialListId") specialListId: String,
        @Query("UsertypeID") UsertypeID: Int=2
    ): SubSpecialitiesResponse

    @GET(UseCaseModule.GET_UPDATE_MEETING)
    suspend fun updateMeeting(
        @Path("culture") culture: String,
        @Query("AppointmentID") appointmentId:String,
    ): NetworkResponse<UpdateMeetingResponse, ErrorModel>

    @GET(UseCaseModule.GET_START_MEETING)
    suspend fun startMeeting(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("AppointmentID") appointmentId:String,
    ): NetworkResponse<MeetingInfoModel, ErrorModel>

    @GET(UseCaseModule.GET_SPECIALIST)
    suspend fun getSpecialities(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): SubSpecialitiesResponse
    @GET(UseCaseModule.GET_SENIORITY_LEVELS)
    suspend fun getSeniorityLevels(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): SeniorityLevelsResponse

    @GET(UseCaseModule.GET_COUNTRIES)
    suspend fun getCountries(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): CountriesResponse

    @GET(UseCaseModule.GET_LEGAL_DOCUMENTS_ITEMS)
    suspend fun getLegalDocumentsItems(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): LegalDocumentsItemsResponse

    @GET(UseCaseModule.GET_DOCTOR_CERTIFICATES)
    suspend fun getCertificates(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
    ): CertificatesResponse

    @GET(UseCaseModule.GET_CITIES)
    suspend fun getCities(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("CountryId") countryId: String
    ): CitiesResponse

    @GET(UseCaseModule.GET_AREAS)
    suspend fun getAreas(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("cityId") cityId: String
    ): CitiesResponse

    @GET(UseCaseModule.GET_CLINIC_GALLERY)
    suspend fun getClinicGallery(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("ClinicId") ClinicId: String
    ): GetClinicGalleryResponse

    @GET(UseCaseModule.GET_SERVICES_BY_SPECIALIST)
    suspend fun getServicesBySpecialist(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): ServicesBySpecialistResponse

    @GET(UseCaseModule.GET_SERVICES)
    suspend fun getServices(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): ServicesBySpecialistResponse

    @HTTP(method = "DELETE", path = UseCaseModule.DELETE_DOCUMENT, hasBody = true)
    suspend fun deleteDocument(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("DocumentId") DocumentId: String
    ): DefaultResponse


    @HTTP(method = "DELETE", path = UseCaseModule.DELETE_CERTIFICATE, hasBody = true)
    suspend fun deleteCertificate(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("CertificateId") CertificateId: String
    ): DefaultResponse


    @POST(UseCaseModule.CREATE_DOCTOR_DOCUMENT)
    @Headers("Accept:text/plain", "Content-Type: multipart/form-data")
    suspend fun createDoctorDocument(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Body createDoctorDocumentModel: DocumentModel
    ): CreateDoctorDocumentResponse

    @HTTP(method = "DELETE", path = UseCaseModule.DELETE_CLINIC_GALLERY, hasBody = true)
    suspend fun deleteClinicGallery(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("ClinicGalleryId") ClinicGalleryId: String
    ): DefaultResponse

    @GET(UseCaseModule.GET_CLINIC_SCHEDULE_BY_CLINIC)
    suspend fun getClinicScheduleByClinicId(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("ClinicId") ClinicId: String
    ): ClinicScheduleResponse

    @GET(UseCaseModule.GET_CLINIC_SCHEDULE_BY_DAY)
    suspend fun getClinicDayScheduleByClinicId(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("ClinicId") ClinicId: String,
        @Query("DayId") DayId: String
    ): ClinicDayScheduleResponse

    @GET(UseCaseModule.GET_MEDICAL_EXAMINATION_DURATION)
    suspend fun getClinicExaminationDuration(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): MedicalExaminationDurationResponse

    @POST(UseCaseModule.CREATE_CLINIC_SCHEDULE)
    suspend fun createClinicSchedule(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Body clinicScheduleModel: ClinicScheduleModel
    ): DefaultResponse

    @FormUrlEncoded
    @POST(UseCaseModule.SET_LOCATION)
    suspend fun setLocation(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Field ("Street") street:String,
        @Field ("ClinicId") ClinicId:Int,
        @Field ("CityId") CityId:Int,
        @Field ("AreaId") AreaId:Int,
        @Field ("Latitude") Latitude:String,
        @Field ("FloorNo") FloorNo:Int,
        @Field ("BlockNo") BlockNo:String,
        @Field ("Longitude") Longitude:String,
        @Field ("CountryId") CountryId:Int,
        @Field ("ApartmentNo") ApartmentNo:String,
        @Field ("Address") Address:String,
        @Field ("CityName") CityName:String,
        @Field ("AreaName") AreaName:String,
    ): LocationDataModel

    @POST(UseCaseModule.EDIT_SCHEDULE)
    suspend fun editClinicSchedule(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Body scheduleModel: ScheduleModel
    ): DefaultResponse

    @GET(UseCaseModule.GET_SERVICES_TYPES)
    suspend fun getServicesTypes(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): ServicesTypesResponse

    @POST(UseCaseModule.CREATE_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)
    suspend fun createServiceSchedule(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Body doctorServiceSchedule: DoctorServiceSchedule
    ): DefaultResponse

    @POST(UseCaseModule.CREATE_PolyClinic_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)
    suspend fun createPolyClinicServiceSchedule(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Body doctorServiceSchedule: DoctorServiceSchedule
    ): DefaultResponse

    @GET(UseCaseModule.GET_DOCTOR_SERVICE_DAY_SCHEDULE_BY_SERVICE_ID)
    suspend fun getServiceDayScheduleByServiceDayId(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
//        @Query("serviceId") serviceId: String,
        @Query("MedicalExaminationTypeId") serviceId: String,
        @Query("DayId") DayId: String,
        @Query("ClinicId") clinicId: String
    ): ServiceDayScheduleResponse

    @GET(UseCaseModule.GET_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)
    suspend fun getServiceScheduleByServiceId(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("MedicalExaminationTypeId") ClinicId: String,
        @Query("ClinicId") ClinicsId: String
    ): ServiceScheduleResponse

    @GET(UseCaseModule.GET_POlyClinic_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)
    suspend fun getPolyClinicSchedualByClinicId(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Query("DoctorID") DoctorID: String,
        @Query("ClinicId") ClinicsId: String
    ): ServiceScheduleResponse

    @Multipart
    @POST(UseCaseModule.CREATE_DOCTOR_PROFILE)
    @JvmSuppressWildcards
    suspend fun createDoctorProfile(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Part("UserId") UserId: RequestBody,
        @Part("Birthday") Birthday: RequestBody,
        @Part("FirstName") FirstName: RequestBody,
        @Part("FirstNameAr") FirstNameAr: RequestBody,
        @Part("MiddelName") MiddelName: RequestBody,
        @Part("MiddelNameAr") MiddelNameAr: RequestBody,
        @Part("LastName") LastName: RequestBody,
        @Part("LastNameAr") LastNameAr: RequestBody,
        @Part("NickName") nickName: RequestBody,
        @Part("NickNameAr") nickNameAr: RequestBody,
        @Part("GenderId") GenderId: RequestBody,
        @Part("Image") Image: RequestBody,
//        @Part("Website") Website: RequestBody,
        @Part("DoctorInfo") DoctorInfo: RequestBody,
        @Part("DoctorInfoAr") DoctorInfoAr: RequestBody,
        @Part("NationalityId") NationalityId: RequestBody,
        @Part("DoctorSubSpecialist") DoctorSubSpecialist: List<RequestBody>
    ): CreateDoctorProfileResponse

    @POST(UseCaseModule.CREATE_EXTRA_INFO)
    suspend fun createExtraInfo(
        @Path("culture") culture: String,
        @Header("Authorization") token: String,
        @Body createExtraInfoModel: ExtraInfoData
    ): CreateExtraInfoResponse

    @POST(UseCaseModule.CREATE_SUB_SPECIALIST)
    suspend fun createSpecialist(
        @Path("culture") culture: String,
        @Body createSpecialistModel: CreateSpecialistModel
    ): CreateSubSpecialistResponse

    @GET(UseCaseModule.GET_CLINICS)
    suspend fun getClinics(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): ClinicsResponse

    //new Artuchture
    @GET(UseCaseModule.GET_DOCTOR_PROFILE)
    suspend fun getDoctorProfile(
        @Path("culture") culture: String
    ): NetworkResponse<EditDoctorProfileResponse, ErrorModel>


    @GET(UseCaseModule.GET_DOCTOR_PROFILE_ID)
    suspend fun getDoctorProfileByID(
        @Path("culture") culture: String,
        @Query("DoctorId") DoctorId: Int? = null
    ): NetworkResponse<EditDoctorProfileResponse, ErrorModel>

    @GET(UseCaseModule.GET_CURRENT_APPOINTMENT)
    suspend fun getCurrentAppointment(
        @Path("culture") culture: String,
        @Query("DateFrom") DateFrom: String? = null,
        @Query("DateTo") DateTo: String? = null,
        @Query("clinicID") clinicID: Int? = null,
        @Query("medicalTypeId") medicalTypeId: Int? = null,
        @Query("SkipCount") skipCount: Int? = null,
        @Query("MaxResultCount") maxResultCount: Int? = null
    ): NetworkResponse<AppointmentResponse, ErrorModel>

    @GET("{culture}/DoctorAppointment/GetCurrentDoctorAppointmentPolyclinic")
    suspend fun getCurrentDoctorAppointmentPolyclinic(
        @Path("culture") culture: String,
        @Query("DateFrom") DateFrom: String? = null,
        @Query("DateTo") DateTo: String? = null,
        @Query("clinicID") clinicID: Int? = null,
        @Query("medicalTypeId") medicalTypeId: Int? = null,
        @Query("SkipCount") skipCount: Int? = null,
        @Query("MaxResultCount") maxResultCount: Int? = null,
        @Query("PolyInfoID") PolyInfoID: Int? = null,
        @Query("DoctorID") DoctorID: Int? = null,
        @Query("PatientName") patientName: String? = null,
    ): NetworkResponse<AppointmentResponse, ErrorModel>


    @GET(UseCaseModule.GET_AGORA_APP_ID)
    suspend fun getAgoraAppId(
        @Path("culture") culture: String,
    ): NetworkResponse<AppointmentResponse, ErrorModel>

@GET(UseCaseModule.GET_AGORA_MEETING_NOTIFICATION)
    suspend fun getAgoraMeeting(
        @Path("culture") culture: String,
        @Query("AppointmentID") appointmentId : Int
    ): NetworkResponse<AgoraMeetingResponse, ErrorModel>



    @GET(UseCaseModule.GET_HISTORY_APPOINTMENT)
    suspend fun getHistoryAppointment(
        @Path("culture") culture: String
    ): NetworkResponse<AppointmentResponse, ErrorModel>

    @GET(UseCaseModule.GET_PATIENTPROFILE)
    suspend fun getPatientProfile(
        @Path("culture") culture: String,
        @Query("appointmentId") appointmentId: Int
    ): NetworkResponse<PatientProfileResponse, ErrorModel>

    @GET(UseCaseModule.GET_EMR_TITLE)
    suspend fun getPatientEmr(
        @Path("culture") culture: String,
        @Query("patientId") patienttId: Int
    ): NetworkResponse<EmrTitleResponse, ErrorModel>

    @GET(UseCaseModule.GET_EMR_DETAILS)
    suspend fun getEmrDetails(
        @Path("culture") culture: String,
        @Query("appointmentId") appointmentId: Int
    ): NetworkResponse<EmrDetailsResponse, ErrorModel>

    @POST(UseCaseModule.SET_EMR_INSTRUCTION)
    suspend fun setEmrInstruction(
        @Path("culture") culture: String,
        @Body emrInstructionModel: EmrInstructionModel
    ): NetworkResponse<EmrInstructionResponse, ErrorModel>

    @POST(UseCaseModule.SET_EMR_DESCRIPTION)
    suspend fun setEmrDescription(
        @Path("culture") culture: String,
        @Body emrDescriptionModel: EmrDescriptionModel
    ): NetworkResponse<EmrDescriptionResponse, ErrorModel>

    @POST(UseCaseModule.SEND_EMAIl_FORGET_PASSWORD)
    suspend fun forgetPassword(
        @Path("culture") culture: String,
        @Body forgetPasswordModel: ForgetPasswordModel
    ): NetworkResponse<ResetPasswordResponse, ErrorModel>

    @POST(UseCaseModule.SEND_NEW_PASSWORD)
    suspend fun newPassword(
        @Path("culture") culture: String,
        @Body newPasswordModel: NewPasswordModel
    ): NetworkResponse<ResetPasswordResponse, ErrorModel>

    @POST(UseCaseModule.SEND_FIRST_NEW_PASSWORD)
    suspend fun firstNewPassword(
        @Path("culture") culture: String,
        @Body newPasswordModel: NewPasswordModel
    ): NetworkResponse<ResetPasswordResponse, ErrorModel>

    @GET(UseCaseModule.GET_CLINIC_INFO_WITH_ID)
    suspend fun getClinicInfo(
        @Path("culture") culture: String,
        @Query("ClinicId") clinictId: Int
    ): NetworkResponse<ClinicInfoResponse, ErrorModel>

    //    @GET(UseCaseModule.GET_DOCUMENT_DOCTOR)
//    suspend fun getDocumentDoctor(): NetworkResponse<EditDocumentResponse, ErrorModel>
    @GET(UseCaseModule.GET_NEW_DOCUMENT_DOCTOR)
    suspend fun getDocument(
        @Path("culture") culture: String,
        @Header("Authorization") token: String
    ): LegalDocumentsItemsResponse

//    @POST(UseCaseModule.UPDATE_DOCTOR_PROFILE)
//    suspend fun UpdateDoctorProfile(
//        @Body updateDoctorProfileModel: UpdateDoctorProfileModel
//    ): NetworkResponse<ErrorModel, ErrorModel>

//    @Multipart
//    @POST(UseCaseModule.SEND_ATTACHMENT_EMR)
//    suspend fun sendAttachmentEmr(
//        @Part document: MultipartBody.Part?,
//        @Part("AppointmentId") statusMultiPart: RequestBody
//    ): NetworkResponse<EmrAttachmentResponse, ErrorModel>

    @GET(UseCaseModule.CANCEL_APPOINTMENT)
    suspend fun cancelAppointment(
        @Path("culture") culture: String,
        @Query("AppointmentId") AppointmentId: Int,
        @Query("CancelReason") CancelReason: String?=null
    ): NetworkResponse<ErrorModel, ErrorModel>

    @GET(UseCaseModule.CHANGE_APPOINTMENT_STATUS)
    suspend fun changeAppointmentStatus(
        @Path("culture") culture: String,
        @Query("AppointmentId") AppointmentId: Int,
        @Query("StatusID") StatusID: Int?=null
    ): NetworkResponse<ErrorModel, ErrorModel>

    @GET(UseCaseModule.CHANGE_APPOINTMENT_STATUS)
    suspend fun agoraMeetingId(
        @Path("culture") culture: String,
        @Query("AppointmentId") AppointmentId: Int,
    ): NetworkResponse<ErrorModel, ErrorModel>

    @GET(UseCaseModule.HOME_DATA)
    suspend fun getHome(
        @Path("culture") culture: String,
        @Query("DateFrom") DateFrom: String? = null,
        @Query("DateTo") DateTo: String? = null,
        @Query("clinicID") clinicID: Int? = null
    ): NetworkResponse<HomeResponse, ErrorModel>

    @GET("{culture}/DoctorDashboard/GetPolyClinicDashboard")
    suspend fun getPolyClinicDashboard(
        @Path("culture") culture: String,
        @Query("DateFrom") DateFrom: String? = null,
        @Query("DateTo") DateTo: String? = null,
        @Query("clinicID") clinicID: Int? = null,
        @Query("DoctorID") DoctorID: Int?=null,
        @Query("PolyInfoID") PolyInfoID: Int?
    ): NetworkResponse<HomeResponse, ErrorModel>


    @POST(UseCaseModule.SEND_FILTER_DATA)
    suspend fun SendFilterData(
        @Path("culture") culture: String,
        @Body filterModel: FilterModel
    ): NetworkResponse<AppointmentResponse, ErrorModel>

    @GET(UseCaseModule.GET_CLINICS)
    suspend fun getHomeClinics(
        @Path("culture") culture: String
    ): NetworkResponse<ClinicsResponse, ErrorModel>

    @GET(UseCaseModule.DELETE_PROFILE)
    suspend fun DeleteProfile(
        @Path("culture") culture: String
    ): NetworkResponse<DeleteResponse, ErrorModel>

    @GET(UseCaseModule.GET_NOTIFICATION)
    suspend fun getNotification(
        @Path("culture") culture: String
    ): NetworkResponse<NotificationResponse, ErrorModel>

    @GET(UseCaseModule.GET_PATIENT_ACCOUNT)
    suspend fun getPatientAccount(
        @Path("culture") culture: String,
        @Query("patientId") patientId: Int
    ): NetworkResponse<GetPatientAccountResponse, ErrorModel>

    @POST(UseCaseModule.ADD_PATIENT_ACCOUNT)
    suspend fun addPatientAccount(
        @Path("culture") culture: String,
        @Body patientAccountModel: PatientAccountModel
    ): NetworkResponse<PatientAccountResponse, ErrorModel>

    @POST(UseCaseModule.EDIT_PATIENT_ACCOUNT)
    suspend fun editPatientAccount(
        @Path("culture") culture: String,
        @Body patientAccountModel: PatientAccountModel
    ): NetworkResponse<PatientAccountResponse, ErrorModel>

    @GET(UseCaseModule.DELETE_PATIENT_ACCOUNT)
    suspend fun deletePatientAccount(
        @Path("culture") culture: String,
        @Query("PatientAccountId") dateId: Int
    ): NetworkResponse<ErrorModel, ErrorModel>

    @GET(UseCaseModule.GET_WHATSUP_ADS)
    suspend fun getWhatsUpAds(
        @Path("culture") culture: String,
        @Query("DoctorApp") DoctorApp: Boolean
    ): NetworkResponse<WhatsUpAdsResponse, ErrorModel>


    @GET(UseCaseModule.GET_HOME_ADS)
    suspend fun getHomeAds(
        @Path("culture") culture: String,
        @Query("PageID") PageID: Int,
        @Query("DoctorApp") DoctorApp: Boolean
    ): NetworkResponse<HomeAdsResponse, ErrorModel>

    @GET(UseCaseModule.GET_VACTION)
    suspend fun getVaction(
        @Path("culture") culture: String,
        @Query("ClinicID") clinicID: Int
    ): NetworkResponse<VactionResponse, ErrorModel>

    @POST(UseCaseModule.ADD_VACTION)
    suspend fun addVaction(
        @Path("culture") culture: String,
        @Body vactionModel: VactionModel
    ): NetworkResponse<VactionResponse, ErrorModel>

    @POST(UseCaseModule.EDIT_VACTION)
    suspend fun editVaction(
        @Path("culture") culture: String,
        @Body vactionModel: VactionModel
    ): NetworkResponse<VactionResponse, ErrorModel>


    @DELETE(UseCaseModule.DELETE_VACTION)
    suspend fun deleteVaction(
        @Path("culture") culture: String,
        @Query("DoctorVacationId") doctorVacationId: Int
    ): NetworkResponse<ErrorModel, ErrorModel>

    @POST(UseCaseModule.LOGIN)
    suspend fun newSignIn(
        @Path("culture") culture: String, @Body signInRequest: LoginModel
    ): NetworkResponse<UserLoginResponse, ErrorModel>

//    @GET(UseCaseModule.GET_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)
//    suspend fun getScheduleByServiceId(
//        @Query("MedicalExaminationTypeId") MedicalId: Int
//    ): NetworkResponse<VactionResponse, ErrorModel>

    @GET(UseCaseModule.GET_PATIENT_MEDICAL_DATA)
    suspend fun getPatientMedicalData(
        @Path("culture") culture: String,
        @Query("PatientID") patientID: Int
    ): NetworkResponse<PatientMedicalResponse, ErrorModel>

    @GET(UseCaseModule.GetAllInsurance)
    suspend fun getAllInsurance_Companies(
        @Path("culture") culture: String,
    ): NetworkResponse<InsuranceCompanyResponse, ErrorModel>
    @GET("{culture}/Services/getFeatures")
    suspend fun getFeatures(
        @Path("culture") culture: String,
    ): NetworkResponse<InsuranceCompanyResponse, ErrorModel>

    @GET("{culture}/LookUp/GetSalamtakGate")
    suspend fun getSalamtakGate(
        @Path("culture") culture: String,
    ): NetworkResponse<SalamtakGateResponse, ErrorModel>

    @GET("{culture}/LookUp/GetBlogs")
    suspend fun getBlogs(
        @Path("culture") culture: String,
        @Query("salamtakGateEnum") salamtakGateEnum :Int,
    ): NetworkResponse<BlogsResponse, ErrorModel>

    @GET("{culture}/HealthEntity/GetHealthEntityPagedList")
    suspend fun getHealthEntityPagedList(
        @Path("culture") culture: String,
        @Query("CityId") CityId :Int,
        @Query("AreaId") AreaId :Int,
        @Query("HealthEntityTypeId") HealthEntityTypeId :Int,
        @Query("MaxResultCount") MaxResultCount :Int,
        @Query("SkipCount") SkipCount :Int,
    ): NetworkResponse<GetHealthEntityResponse, ErrorModel>

    @GET("{culture}/DoctorAccount/GetClinicPackage")
    suspend fun getClinicPackage(
        @Path("culture") culture: String,
        @Query("ClinicID") ClinicID: Int,
        @Query("Month") Month: Int,
        @Query("Year") Year: Int
    ): NetworkResponse<GetClincPacakgesResponse, ErrorModel>

    @GET("{culture}/DoctorAccount/getOtherPackagesByClinicID")
    suspend fun getOtherPackagesByClinicID(
        @Path("culture") culture: String,
        @Query("ClinicID") ClinicID: Int,
    ): NetworkResponse<OtherPackagesResponse, ErrorModel>

    @GET("{culture}/Notification/GetUserNotifications")
    suspend fun getUserNotifications(
        @Path("culture") culture: String,
    ): NetworkResponse<NotificationListResponse, ErrorModel>
    @POST("{culture}/User/UpdateNotification")
    suspend fun updateNotification(
        @Path("culture") culture: String,
    ): Response<QuestionsResponse>

    @GET("{culture}/City/GetAllCities")
    suspend fun getAllCities(
        @Path("culture") culture: String,
    ): NetworkResponse<GetAllCitiesResponse, ErrorModel>

    @GET("{culture}/Area/GetAreasByCityId")
    suspend fun getAreasByCityId(
        @Path("culture") culture: String,
        @Query("cityId") cityId:Int,
    ): NetworkResponse<GetAreasByCityIdResponse,ErrorModel>

    @DELETE("{culture}/User/DeleteUser")
    suspend fun deleteUser(
        @Path("culture") culture: String,
    ):NetworkResponse<DeleteUserResponse,ErrorModel>
    @POST("{culture}/DoctorClinic/DeleteDoctorClinicSchedual")
    suspend fun deleteDoctorClinicSchedual(
        @Path("culture") culture: String,
        @Body deleteSchduleRequest:DeleteSchduleRequest
    ):NetworkResponse<DeleteScheduleResponse,ErrorModel>

    @GET("{culture}/DoctorClinic/GetDoctorClinics")
    suspend fun getDoctorClinics(
        @Path("culture") culture: String,
    ):Response<ClincsResponse>
    @GET("{culture}/LookUp/GetOfferMainCategory")
    suspend fun getOfferMainCategory(
        @Path("culture") culture: String,
    ):Response<MainCategoryResponse>

    @GET("{culture}/LookUp/GetOfferSubCategoryL1")
    suspend fun getOfferSubCategoryL1(
        @Path("culture") culture: String,
        @Query("ParentID") ParentID: Int?
    ):Response<SubCategoryResponse>
    @GET("{culture}/LookUp/GetOfferSubCategoryL2")
    suspend fun getOfferSubCategoryL2(
        @Path("culture") culture: String,
        @Query("ParentID") ParentID:Int
    ):Response<OfferServicesResponse>

    @GET("{culture}/LookUp/GetOfferSubCategoryL3")
    suspend fun getOfferSubCategoryL3(
        @Path("culture") culture: String,
        @Query("ParentID") ParentID:Int
    ):Response<OfferServicesResponse>

    @POST("{culture}/DoctorClinic/CreateClinicOffer")
    suspend fun createClinicOffer(
        @Path("culture") culture: String,
        @Body offerRequest: Map<String, String>,
    ): Response<OfferResponse>
    @POST("{culture}/DoctorClinic/UpdateHealthEntityOfferByOfferId")
    suspend fun updateHealthEntityOfferByOfferId(
        @Path("culture") culture: String,
        @Body offerUpdateRequest: Map<String, String>,
    ): Response<OfferResponse>
    @POST("{culture}/DoctorClinic/CreateOfferGallery")
    suspend fun createOfferGallery(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<OfferGallaryResponse>

    @POST("{culture}/DoctorClinic/CreateDoctorClinic")
    suspend fun createDoctorClinic(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<CreateClinicResponse>
    @POST("{culture}/HealthEntity/CreateHealthEntity")
    suspend fun createHealthEntity(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<CreateClinicResponse>

    @POST("{culture}/DoctorClinic/UpdateDoctorClinic2")
    suspend fun updateDoctorClinic2(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<UpdateClinicResponse>

    @GET("{culture}/DoctorClinic/GetHealthEntityOffer")
    suspend fun getDoctorswithOffers(
        @Path("culture") culture: String,
    ):Response<DoctorOffersResponse>
    @GET("{culture}/DoctorClinic/GetClinicOfferGalleryByOfferId")
    suspend fun getClinicOfferGalleryByOfferId(
        @Path("culture") culture: String,
        @Query("offerId") offerId:Int
    ):Response<GetGallaryByIDResponse>

    @GET("{culture}/DoctorClinic/GetHealthEntityOfferById")
    suspend fun getHealthEntityOfferById(
        @Path("culture") culture: String,
        @Query("offerId") offerId:Int
    ):Response<GetOffersByOfferIDResponse>
    @DELETE("{culture}/DoctorClinic/DeleteOfferGallery")
    suspend fun deleteOfferGallery(
        @Path("culture") culture: String,
        @Query("ClinicOfferGalleryId") galleryOfferIndex:Int
    ):Response<GetOffersByOfferIDResponse>

    @GET(UseCaseModule.GET_CLINIC_INFO_WITH_ID)
    suspend fun getInfo(
        @Path("culture") culture: String,
        @Query("ClinicId") clinictId: Int
    ):Response<ClinicsInfoResponse>

    @POST("{culture}/HealthEntity/CreatePolyClinicOwner")
    suspend fun createPolyClinicOwner(
        @Path("culture") culture: String,
        @Body part: RequestBody,
    ): Response<PolyClinicAdminResponse>

    @POST("{culture}/HealthEntity/UpdatePolyClinicOwner")
    suspend fun updatePolyClinicOwner(
        @Path("culture") culture: String,
        @Body part: RequestBody,
    ): Response<UpdatePolyClinicInfo>
    @POST("{culture}/HealthEntity/UpdatePolyClinicInfo")
    suspend fun updatePolyClinicInfo(
        @Path("culture") culture: String,
        @Body part: RequestBody,
    ): Response<UpdatePolyClinicInfo>

    @POST("{culture}/HealthEntity/CreatePolyClinicOwnerBackImg")
    suspend fun createPolyClinicOwnerBackImg(
        @Path("culture") culture: String,
        @Body part: RequestBody,
    ): Response<OfferGallaryResponse>
    @POST("{culture}/HealthEntity/CreatePolyClinicInfoImg2")
    suspend fun createPolyClinicInfoImg2(
        @Path("culture") culture: String,
        @Body part: RequestBody,
    ): Response<OfferGallaryResponse>

    @POST("{culture}/HealthEntity/CreatePolyClinicInfo")
    suspend fun createPolyClinicInfo(
        @Path("culture") culture: String,
        @Body part: RequestBody,
    ): Response<PolyClinicInfoResponse>

    @GET("{culture}/Doctor/GetDoctorByClinicId")
    suspend fun getDoctorByClinicId(
        @Path("culture") culture: String,
        @Query("ClinicId") clinictId: Int
    ):Response<PolyClinicsDoctorsResponse>

    @GET("{culture}/HealthEntity/GetpolyOwnerByID")
    suspend fun getpolyOwnerByID(
        @Path("culture") culture: String,
        @Query("ownerID") ownerID: Int
    ):Response<GetPolyResponse>

    @GET("{culture}/HealthEntity/GetPolyClinicInfoByID")
    suspend fun getPolyClinicInfoByID(
        @Path("culture") culture: String,
        @Query("InfoID") InfoID: Int
    ):Response<GetPolyInfoResponse>

    @GET("{culture}/Doctor/getDoctorCertificatePolyClinic")
    suspend fun getDoctorCertificatePolyClinic(
        @Path("culture") culture: String,
        @Query("DoctorID") DoctorID: Int
    ):Response<CertificatesResponse>

    @GET("{culture}/Doctor/GetDoctorLegalDocumentsPolyClinic")
    suspend fun getDoctorDocumentsPolyClinic(
        @Path("culture") culture: String,
        @Query("DoctorID") DoctorID: Int
    ):Response<LegalDocumentsResponse>

    @POST("{culture}/DoctorClinic/CreateDoctorClinicVideo")
    suspend fun createDoctorClinicVideo(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<OfferResponse>
    @POST("{culture}/Doctor/CreateDoctorVideo")
    suspend fun createDoctorVideo(
        @Path("culture") culture: String,
        @Body videoRequest: DoctorVideoRequest
    ): Response<OfferResponse>
    @GET("{culture}/Doctor/GetDoctorVideos")
    suspend fun getDoctorVideos(
        @Path("culture") culture: String,
        @Query("DoctorID") DoctorID: Int
    ): Response<DoctorVideosResponse>

    @DELETE("{culture}/Doctor/DeleteDoctorVideo")
    suspend fun deleteDoctorVideo(
        @Path("culture") culture: String,
        @Query("VideoID") VideoID: Int
    ): Response<DoctorVideosResponse>

    @DELETE("{culture}/DoctorClinic/DeleteHealthEntityVideo")
    suspend fun deleteHealthEntityVideo(
        @Path("culture") culture: String,
        @Query("ClinicVideoId") videoID: Int
    ): Response<DoctorVideosResponse>

    @GET("{culture}/DoctorClinic/GetClinicVideoByClinicId")
    suspend fun getClinicVideoByClinicId(
        @Path("culture") culture: String,
        @Query("ClinicId") ClinicId: Int
    ): Response<DoctorVideosResponse>

    @GET("{culture}/User/CheckToken")
    suspend fun checkUser(
        @Path("culture") culture: String,
    ): Response<CheckTokenResponse>

    @POST("{culture}/User/SendOTP")
    suspend fun sendOTP(@Path("culture") culture: String,
                        @Body registerUser: RegisterModel):Response<RegisterResponse>

    @GET("{culture}/LookUp/GetAllQandABySpecID")
    suspend fun getAllQandAByDoctorSpecID(
        @Path("culture") culture: String,
    ): Response<QuestionsResponse>
    @GET("{culture}/LookUp/GetAllQandA")
    suspend fun getAllQandA(
        @Path("culture") culture: String,
    ): Response<QuestionsResponse>
    @GET("{culture}/Doctor/GetAllQandAByDoctorId")
    suspend fun getAnswersByDoctorId(
        @Path("culture") culture: String,
    ): Response<AnswersDoctorResponse>

    @POST("{culture}/Doctor/AnswerpatientQ")
    suspend fun createAnswerpatientQ(
        @Path("culture") culture: String,
        @Body answerRequest: Map<String, String>,
    ): Response<AnswersQuestionsResponse>

    @POST("{culture}/Doctor/CancelQuestion")
    suspend fun cancelQuestion(
        @Path("culture") culture: String,
        @Query("QuestionID") questionID: Int
    ): Response<AnswersQuestionsResponse>

    @GET("{culture}/Patient/GetAllInvoices")
    suspend fun getAllPatientAccount(
        @Path("culture") culture: String,
        @Query("DateFrom") dateFrom: String? = null,
        @Query("DateTo") dateTo: String? = null,
        @Query("PatientName") patientName: String? = null,
    ): Response<DoctorInvoiceResponse>

    @POST("{culture}/Patient/CreateInvoice")
    suspend fun createInvoice(
        @Path("culture") culture: String,
        @Body doctorInvoiceRequest: DoctorInvoiceRequest
    ): Response<AnswersQuestionsResponse>
    @POST("{culture}/Patient/AddInvoiceItems")
    suspend fun addInvoiceItems(
        @Path("culture") culture: String,
        @Body addInvoiceItemRequest: AddInvoiceItemRequest
    ): Response<AnswersQuestionsResponse>

    @POST("{culture}/Patient/EditInvoiceItem")
    suspend fun editInvoiceItem(
        @Path("culture") culture: String,
        @Body editInvoiceItemRequest: EditInvoiceRequest
    ): Response<AnswersQuestionsResponse>

    @DELETE("{culture}/Patient/DeleteInvoiceItem")
    suspend fun deleteInvoiceItem(
        @Path("culture") culture: String,
        @Query("Id") id: Int
    ): Response<AnswersQuestionsResponse>

    @GET("{culture}/Patient/GetInvoicesByPatientId")
    suspend fun getInvoicesByPatientId(
        @Path("culture") culture: String,
        @Query("patientId") patientId: Int? = null,
    ): Response<DoctorInvoiceResponse>

    @GET("{culture}/Patient/GetSymptomsByAppointmentId")
    suspend fun getSymptomsByAppointmentId(
        @Path("culture") culture: String,
        @Query("appointmentId") appointmentId: Int? = null,
    ): Response<SyemtomsResponse>

    @POST("{culture}/Patient/CreateDoctorResponse")
    suspend fun createDoctorResponse(
        @Path("culture") culture: String,
        @Body medicalInfoRequest: Map<String, String>
    ): Response<SyemtomsResponse>

    // Api profile update
    @POST("{culture}/Doctor/UpdateDoctorProfileFirstStep")
    suspend fun updateDoctorProfileFirstStep(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<UpdateDoctorProfileResponse>
    @POST("{culture}/Doctor/CreateProfile")
    suspend fun createProfile(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<UpdateDoctorProfileResponse>
    @POST("{culture}/Doctor/CreateDoctorToPolyclinic")
    suspend fun createDoctorToPolyclinic(
        @Path("culture") culture: String,
        @Query("clinicId") clinicId: Int? = null,
        @Query("userId") userId: Int? = null,
        @Body part: RequestBody
    ): Response<UpdateDoctorProfileResponse>

    @POST("{culture}/Doctor/CreateDoctorDocuments")
    suspend fun CreateDoctorDocuments(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<CreateDoctorDocumentResponse>
    @POST("{culture}/Doctor/CreateDoctorDocumentsPolyClinic")
    suspend fun createDoctorDocumentsPolyClinic(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<CreateDoctorDocumentResponse>
    @POST("{culture}/Doctor/UpdateDoctorDocument")
    suspend fun updateDoctorDocument(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<UpdateLegalDocumentModel>

    @GET("{culture}/Doctor/GetDoctorLegalDocuments2")
    suspend fun getDoctorLegalDocuments2(
        @Path("culture") culture: String,
    ):Response<LegalDocumentsResponse>
    @POST("{culture}/Patient/CreatePatientEmrAll")
    suspend fun createPatientEmrAl(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<UpdateDoctorProfileResponse>

    @GET("{culture}/Queue/GetQueueBoard")
    suspend fun getQueueBoard(
        @Path("culture") culture: String,
        @Query("HealthEntityId") healthEntityId: Int,
        @Query("QueueDate") queueDate: String
    ): Response<QueueBoardResponse>

    @POST("{culture}/Queue/Complete")
    suspend fun completeQueueTicket(
        @Path("culture") culture: String,
        @Body request: QueueTicketActionRequest
    ): Response<QueueActionResponse>

    @POST("{culture}/Queue/Cancel")
    suspend fun cancelQueueTicket(
        @Path("culture") culture: String,
        @Body request: CancelQueueTicketRequest
    ): Response<QueueActionResponse>

    @GET("{culture}/Hospital/GetHospitalDashboard")
    suspend fun getHospitalDashboard(
        @Path("culture") culture: String,
        @Query("HospitalInfoID") hospitalInfoId: Int,
        @Query("DateFrom") dateFrom: String? = null,
        @Query("DateTo") dateTo: String? = null,
        @Query("ClinicID") clinicId: Int? = null,
        @Query("DoctorID") doctorId: Int? = null
    ): Response<HospitalDashboardResponse>

    @GET("{culture}/Hospital/GetHospitalInfoByID")
    suspend fun getHospitalInfoById(
        @Path("culture") culture: String,
        @Query("InfoID") infoId: Int
    ): Response<HospitalInfoResponse>

    @GET("{culture}/Hospital/GetHospitalClinicList")
    suspend fun getHospitalClinicList(
        @Path("culture") culture: String,
        @Query("hospitalId") hospitalId: Int
    ): Response<HospitalClinicsResponse>

    @GET("{culture}/Hospital/GetHospitalDoctorList")
    suspend fun getHospitalDoctorList(
        @Path("culture") culture: String,
        @Query("hospitalId") hospitalId: Int
    ): Response<HospitalDoctorsResponse>

    @GET("{culture}/Hospital/GetClinicDoctors")
    suspend fun getHospitalClinicDoctors(
        @Path("culture") culture: String,
        @Query("clinicId") clinicId: Int
    ): Response<HospitalDoctorsResponse>

    @GET("{culture}/Hospital/GetHospitalOwner")
    suspend fun getHospitalOwner(
        @Path("culture") culture: String,
        @Query("hospitalId") hospitalId: Int
    ): Response<HospitalOwnerResponse>

    @POST("{culture}/Hospital/UpdateHospitalOwner")
    suspend fun updateHospitalOwner(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<HospitalActionResponse>

    @POST("{culture}/Hospital/UpdateHospitalInfo")
    suspend fun updateHospitalInfo(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<HospitalActionResponse>

    @GET("{culture}/Hospital/GetHospitalBranches")
    suspend fun getHospitalBranches(
        @Path("culture") culture: String,
        @Query("hospitalId") hospitalId: Int
    ): Response<HospitalBranchesResponse>

    @POST("{culture}/Hospital/CreateHospitalBranch")
    suspend fun createHospitalBranch(
        @Path("culture") culture: String,
        @Body request: HospitalBranchRequest
    ): Response<HospitalActionResponse>

    @POST("{culture}/Hospital/UpdateHospitalBranch")
    suspend fun updateHospitalBranch(
        @Path("culture") culture: String,
        @Body request: HospitalBranchRequest
    ): Response<HospitalActionResponse>

    @POST("{culture}/Hospital/DeleteHospitalBranch")
    suspend fun deleteHospitalBranch(
        @Path("culture") culture: String,
        @Query("branchId") branchId: Int
    ): Response<HospitalActionResponse>

    @GET("{culture}/Services/GetServices")
    suspend fun getAllServices(
        @Path("culture") culture: String
    ): Response<HospitalLookupResponse>

    @GET("{culture}/Services/GetFeatures")
    suspend fun getAllFeatures(
        @Path("culture") culture: String
    ): Response<HospitalLookupResponse>

    // Hospital Doctors tab

    @GET("{culture}/Hospital/GetClinicByDoctorId")
    suspend fun getHospitalClinicsByDoctor(
        @Path("culture") culture: String,
        @Query("doctorId") doctorId: Int
    ): Response<HospitalClinicsResponse>

    @POST("{culture}/Hospital/DeleteDoctorFromHospital")
    suspend fun deleteDoctorFromHospital(
        @Path("culture") culture: String,
        @Query("hospitalId") hospitalId: Int,
        @Query("doctorId") doctorId: Int
    ): Response<HospitalActionResponse>

    @DELETE("{culture}/Hospital/DeleteDoctorFromClinic")
    suspend fun deleteDoctorFromHospitalClinic(
        @Path("culture") culture: String,
        @Query("clinicId") clinicId: Int,
        @Query("doctorId") doctorId: Int
    ): Response<HospitalActionResponse>

    @POST("{culture}/Hospital/CreateHospitalClinic")
    suspend fun createHospitalClinic(
        @Path("culture") culture: String,
        @Body request: HospitalCreateClinicRequest
    ): Response<HospitalDataResponse>

    @PUT("{culture}/Hospital/UpdateHospitalClinic")
    suspend fun updateHospitalClinic(
        @Path("culture") culture: String,
        @Body request: HospitalUpdateClinicRequest
    ): Response<HospitalActionResponse>

    @POST("{culture}/Hospital/UploadHospitalClinicLogo")
    suspend fun uploadHospitalClinicLogo(
        @Path("culture") culture: String,
        @Query("ClinicId") clinicId: Int,
        @Body part: RequestBody
    ): Response<HospitalActionResponse>

    @GET("{culture}/Hospital/GetDoctorSchedule")
    suspend fun getHospitalDoctorSchedule(
        @Path("culture") culture: String,
        @Query("healthEntityId") clinicId: Int
    ): Response<HospitalScheduleResponse>

    @POST("{culture}/Hospital/AddDoctorSchedule")
    suspend fun addHospitalDoctorSchedule(
        @Path("culture") culture: String,
        @Query("doctorId") doctorId: Int,
        @Body request: HospitalScheduleRequest
    ): Response<HospitalActionResponse>

    @DELETE("{culture}/Hospital/DeleteDoctorSchedule")
    suspend fun deleteHospitalDoctorSchedule(
        @Path("culture") culture: String,
        @Query("workingDayTimeId") workingDayTimeId: Int
    ): Response<HospitalActionResponse>

    @GET("{culture}/PatientHospital/GetDoctorProfile")
    suspend fun getHospitalDoctorProfile(
        @Path("culture") culture: String,
        @Query("doctorId") doctorId: Int
    ): Response<HospitalDoctorProfileResponse>

    @POST("{culture}/Hospital/UpdateDoctor")
    suspend fun updateHospitalDoctor(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<HospitalActionResponse>

    /** Step 1 of Add Doctor in hospital mode; parsed like the other create-doctor calls. */
    @POST("{culture}/Hospital/AddDoctorToHospital")
    suspend fun addDoctorToHospital(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<UpdateDoctorProfileResponse>

    @GET("{culture}/Specialist/GetSpecialist")
    suspend fun getSpecialistLookup(@Path("culture") culture: String): Response<HospitalLookupResponse>

    @GET("{culture}/Specialist/GetSubSpecialist")
    suspend fun getSubSpecialistLookup(
        @Path("culture") culture: String,
        @Query("specialListId") specialistId: Int,
        @Query("UsertypeID") userTypeId: Int = 2
    ): Response<HospitalLookupResponse>

    @GET("{culture}/SeniorityLevel/GetSeniorityLevel")
    suspend fun getSeniorityLookup(@Path("culture") culture: String): Response<HospitalLookupResponse>

    @GET("{culture}/LookUp/GetCountries")
    suspend fun getCountriesLookup(@Path("culture") culture: String): Response<HospitalLookupResponse>

    @GET("{culture}/LookUp/GetDays")
    suspend fun getDaysLookup(@Path("culture") culture: String): Response<HospitalLookupResponse>

    @GET("{culture}/LookUp/GetDurationMedicalExamination")
    suspend fun getDurationsLookup(@Path("culture") culture: String): Response<HospitalLookupResponse>

    @GET("{culture}/LookUp/GetMedicalExaminationType")
    suspend fun getBookingTypesLookup(@Path("culture") culture: String): Response<HospitalLookupResponse>

    // Hospital sign-up

    @POST("{culture}/Hospital/CreateHospitalOwner")
    suspend fun createHospitalOwner(
        @Path("culture") culture: String,
        @Body part: RequestBody
    ): Response<HospitalDataResponse>

    @POST("{culture}/Hospital/CreateHospitalInfo")
    suspend fun createHospitalInfo(
        @Path("culture") culture: String,
        @Body request: HospitalCreateInfoRequest
    ): Response<HospitalDataResponse>

    @POST("{culture}/Hospital/UploadHospitalInfoImage")
    suspend fun uploadHospitalInfoImage(
        @Path("culture") culture: String,
        @Query("InfoID") infoId: Int,
        @Body part: RequestBody
    ): Response<HospitalActionResponse>

    @GET(UseCaseModule.GetAllInsurance)
    suspend fun getInsuranceLookup(@Path("culture") culture: String): Response<HospitalInsuranceResponse>


}