package com.salamtak.core.network

import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.*

interface WebClientCreator
{

    suspend fun login (lang: String,loginModel: LoginModel): UserLoginResponse
    suspend fun register(lang: String,registerModel: RegisterModel): RegisterResponse
    suspend fun createUser(lang: String,registerModel: RegisterModel): UserLoginResponse
    suspend fun getSubSpecialities(lang: String,specialListId: GetSubSpecialitiesModel): SubSpecialitiesResponse
    suspend fun createSpecialist(lang: String,createSpecialistModel: CreateSpecialistModel): CreateSubSpecialistResponse
    suspend fun getSpecialities(lang: String,token: String): SubSpecialitiesResponse
    suspend fun getCountries(lang: String,token: String): CountriesResponse
    suspend fun getSeniorityLevels(lang: String,token: String): SeniorityLevelsResponse
    suspend fun getLegalDocumentsItems(lang: String,token: String): LegalDocumentsItemsResponse
    suspend fun createDoctorProfile(lang: String,token: String,createDoctorProfileModel: DoctorProfileModel): CreateDoctorProfileResponse
    suspend fun createExtraInfo(lang: String, token: String, createExtraInfoModel: CreateExtraInfoModel): CreateExtraInfoResponse
    suspend fun createDoctorDocument(lang: String,token: String,documentModel: DocumentModel): CreateDoctorDocumentResponse
    suspend fun deleteDocument(lang: String,token: String,DocumentId: Document): DefaultResponse
    suspend fun deleteCertificate(lang: String,token: String,CertificateId: Certificate): DefaultResponse
    suspend fun getCertificates(lang: String,token: String): CertificatesResponse
    suspend fun getCities(lang: String,token: String,CountryId: String): CitiesResponse
    suspend fun getAreas(lang: String,token: String, CityId: String): CitiesResponse
    suspend fun getServicesBySpecialist(lang: String, token: String, SpecialistId: String): ServicesBySpecialistResponse
    suspend fun getServices(lang: String, token: String, SpecialistId: String): ServicesBySpecialistResponse
    suspend fun deleteClinicGallery(lang: String, token: String, galleryId: String): DefaultResponse
    suspend fun getClinicGallery(lang: String,token: String, ClinicId: String): GetClinicGalleryResponse
    suspend fun getClinicSchedule(lang: String,token: String, ClinicId: String): ClinicScheduleResponse
    suspend fun getClinicDaySchedule(lang: String,token: String, ClinicId: String,DayId: String): ClinicDayScheduleResponse
    suspend fun getClinicExaminationDuration(lang: String,token: String): MedicalExaminationDurationResponse
    suspend fun createClinicSchedule(lang: String,token: String,clinicScheduleModel: ClinicScheduleModel): DefaultResponse
    suspend fun editClinicSchedule(lang: String,token: String,scheduleModel: ScheduleModel): DefaultResponse
    suspend fun getServicesTypes(lang: String,token: String): ServicesTypesResponse
    suspend fun createServiceSchedule(lang: String,token: String,doctorServiceSchedule: DoctorServiceSchedule): DefaultResponse
    suspend fun createPolyClinicServiceSchedule(lang: String,token: String,doctorServiceSchedule: DoctorServiceSchedule): DefaultResponse
    suspend fun getServiceSchedule(lang: String,token: String, ServiceId: String, ClinicId: String): ServiceScheduleResponse
    suspend fun getPolyServiceSchedule(lang: String,token: String, doctorId: String, ClinicId: String): ServiceScheduleResponse
    suspend fun getServiceDaySchedule(lang: String,token: String, ServiceId: String,DayId: String,ClinicId: String): ServiceDayScheduleResponse
    suspend fun getClinics(lang: String,token: String): ClinicsResponse
    suspend fun setLocation(lang: String,token: String,locationModel: LocationModel):LocationDataModel

    //add new
    suspend fun getDocument(token: String,lang: String): LegalDocumentsItemsResponse

}