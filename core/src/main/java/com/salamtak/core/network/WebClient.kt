package com.salamtak.core.network

import android.util.Log
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.*
import okhttp3.RequestBody

fun createWebClient(
    endPoint: ApiEndpoints
): Lazy<WebClientCreator> = lazy {
    WebClient(
        endPoint
    )
}

class WebClient(
    private val contentEndpoint: ApiEndpoints
): WebClientCreator
{

    override suspend fun login(lang: String,loginModel: LoginModel): UserLoginResponse {
        return contentEndpoint.login(lang,loginModel)
    }

    override suspend fun register(lang: String,registerModel: RegisterModel): RegisterResponse {
        return contentEndpoint.register(lang,registerModel)
    }

    override suspend fun createUser(lang: String,registerModel: RegisterModel): UserLoginResponse {
        return contentEndpoint.createUSER(
            lang,registerModel
        )
    }

    override suspend fun getSubSpecialities(lang: String,specialList: GetSubSpecialitiesModel): SubSpecialitiesResponse {
        return contentEndpoint.getSubSpecialities(lang,specialList.token,specialList.id)
    }

    override suspend fun createSpecialist(
        lang: String,
        createSpecialistModel: CreateSpecialistModel
    ): CreateSubSpecialistResponse {
            return contentEndpoint.createSpecialist(lang,createSpecialistModel)
    }


    override suspend fun getSpecialities(lang: String,token: String): SubSpecialitiesResponse {
        return contentEndpoint.getSpecialities(lang,token)
    }

    override suspend fun getCountries(lang: String,token: String): CountriesResponse {
        return contentEndpoint.getCountries(
            lang,token
        )
    }

    override suspend fun getSeniorityLevels(lang: String,token: String): SeniorityLevelsResponse {
        return contentEndpoint.getSeniorityLevels(
            lang,token
        )
    }

    override suspend fun getLegalDocumentsItems(
        lang: String,
        token: String
    ): LegalDocumentsItemsResponse {
        return contentEndpoint.getLegalDocumentsItems(lang,token)
    }

    override suspend fun createDoctorProfile(lang: String,token: String,createDoctorProfileModel: DoctorProfileModel): CreateDoctorProfileResponse {

        Log.e("CreateProfileFunc", "createDoctorProfile: $createDoctorProfileModel" )


        /*
        *            createDoctorProfileModel.profileImage
        * */

        val list = ArrayList<RequestBody>()
        for (i in createDoctorProfileModel.DoctorSubSpecialist)
            list.add(requestBody(i.toString()))


        return contentEndpoint.createDoctorProfile(
            lang,token,
            requestBody(createDoctorProfileModel.UserId),
            requestBody(createDoctorProfileModel.Birthday),
//            requestBody(createDoctorProfileModel.BirthdayStr),
            requestBody(createDoctorProfileModel.FirstName),
            requestBody(createDoctorProfileModel.FirstNameAr),
            requestBody(createDoctorProfileModel.MiddelName),
            requestBody(createDoctorProfileModel.MiddelNameAr),
            requestBody(createDoctorProfileModel.LastName),
            requestBody(createDoctorProfileModel.LastNameAr),
            requestBody(createDoctorProfileModel.NickName),
            requestBody(createDoctorProfileModel.NickNameAr),
            requestBody(createDoctorProfileModel.GenderId),
            requestBody(createDoctorProfileModel.Image),
//            requestBody(createDoctorProfileModel.Website),
            requestBody(createDoctorProfileModel.DoctorInfo),
            requestBody(createDoctorProfileModel.DoctorInfoAr),
            requestBody(createDoctorProfileModel.NationalityId),
            list)
    }

    override suspend fun createExtraInfo(
        lang: String,
        token: String,
        createExtraInfoModel: CreateExtraInfoModel
    ): CreateExtraInfoResponse {
        return contentEndpoint.createExtraInfo(
            lang, token, createExtraInfoModel.data
        )
    }

    override suspend fun createDoctorDocument(
        lang: String,
        token: String,
        documentModel: DocumentModel
    ): CreateDoctorDocumentResponse {
        return contentEndpoint.createDoctorDocument(
            lang,token,documentModel
        )
    }

    override suspend fun deleteDocument(
        lang: String,
        token: String,
        DocumentId: Document
    ): DefaultResponse {
        return contentEndpoint.deleteDocument(
            lang,token,DocumentId.DocumentId
        )
    }

    override suspend fun getCertificates(lang: String, token: String): CertificatesResponse {
        return contentEndpoint.getCertificates(
            lang,token
        )
    }

    override suspend fun getCities(lang: String, token: String, CountryId: String): CitiesResponse {
        return contentEndpoint.getCities(
            lang,token,CountryId
        )
    }

    override suspend fun getAreas(lang: String, token: String, CityId: String): CitiesResponse {
        return contentEndpoint.getAreas(lang,token,CityId)
    }

    override suspend fun getServicesBySpecialist(
        lang: String,
        token: String,
        SpecialistId: String
    ): ServicesBySpecialistResponse {
        return contentEndpoint.getServicesBySpecialist(
            lang,token
        )
    }

    override suspend fun getServices(
        lang: String,
        token: String,
        SpecialistId: String
    ): ServicesBySpecialistResponse {
        return contentEndpoint.getServices(
            lang,token
        )
    }


    override suspend fun deleteClinicGallery(
        lang: String,
        token: String,
        galleryId: String
    ): DefaultResponse {
        return contentEndpoint.deleteClinicGallery(
            lang,token,galleryId
        )
    }

    override suspend fun getClinicGallery(
        lang: String,
        token: String,
        ClinicId: String
    ): GetClinicGalleryResponse {

        Log.e("TAG WebClient", "getClinicGallery: $ClinicId" )
        return contentEndpoint.getClinicGallery(
            lang,token,ClinicId
        )
    }

   override suspend fun setLocation(
        lang: String,
        token: String,
        locationModel: LocationModel

    ):LocationDataModel{
        return  contentEndpoint.setLocation(
            lang,token,locationModel.Street,locationModel.ClinicId,locationModel.CityId,locationModel.AreaId,locationModel.Latitude,locationModel.FloorNo,locationModel.BlockNo,locationModel.Longitude,locationModel.CountryId,locationModel.ApartmentNo,locationModel.Address,locationModel.CityName,locationModel.AreaName
        )
    }

    override suspend fun deleteCertificate(
        lang: String,
        token: String,
        CertificateId: Certificate
    ): DefaultResponse {
        return contentEndpoint.deleteCertificate(
            lang,token,CertificateId.CertificateId
        )
    }


    override suspend fun getClinicDaySchedule(
        lang: String,
        token: String,
        ClinicId: String,
        DayId: String
    ): ClinicDayScheduleResponse {
        return contentEndpoint.getClinicDayScheduleByClinicId(
            lang,token,ClinicId,DayId
        )
    }

    override suspend fun getClinicExaminationDuration(
        lang: String,
        token: String
    ): MedicalExaminationDurationResponse {
        return contentEndpoint.getClinicExaminationDuration(
            lang,token
        )
    }

    override suspend fun createClinicSchedule(
        lang: String,
        token: String,
        clinicScheduleModel: ClinicScheduleModel
    ): DefaultResponse {
        return contentEndpoint.createClinicSchedule(
            lang,token,clinicScheduleModel
        )
    }




    override suspend fun editClinicSchedule(
        lang: String,
        token: String,
        scheduleModel: ScheduleModel
    ): DefaultResponse {
        return contentEndpoint.editClinicSchedule(
            lang,token,scheduleModel
        )
    }

    override suspend fun getClinicSchedule(
        lang: String,
        token: String,
        ClinicId: String
    ): ClinicScheduleResponse {
        return contentEndpoint.getClinicScheduleByClinicId(
            lang,token,ClinicId
        )
    }

    override suspend fun getServicesTypes(lang: String, token: String): ServicesTypesResponse {
        return contentEndpoint.getServicesTypes(
            lang,token
        )
    }

    override suspend fun createServiceSchedule(
        lang: String,
        token: String,
        doctorServiceSchedule: DoctorServiceSchedule
    ): DefaultResponse {
        return contentEndpoint.createServiceSchedule(
            lang,token,doctorServiceSchedule
        )
    }

    override suspend fun createPolyClinicServiceSchedule(
        lang: String,
        token: String,
        doctorServiceSchedule: DoctorServiceSchedule
    ): DefaultResponse {
        return contentEndpoint.createPolyClinicServiceSchedule(
            lang,token,doctorServiceSchedule
        )
    }

    override suspend fun getServiceSchedule(
        lang: String,
        token: String,
        ServiceId: String,
        clinicId:String
    ): ServiceScheduleResponse {
        return contentEndpoint.getServiceScheduleByServiceId(
            lang,token,ServiceId,clinicId
        )
    }

    override suspend fun getPolyServiceSchedule(
        lang: String,
        token: String,
        doctorId: String,
        clinicId: String
    ): ServiceScheduleResponse {
        return contentEndpoint.getPolyClinicSchedualByClinicId(
            lang,token,doctorId,clinicId
        )
    }

    override suspend fun getServiceDaySchedule(
        lang: String,
        token: String,
        ServiceId: String,
        DayId: String,
        clinicId:String
    ): ServiceDayScheduleResponse {
        return contentEndpoint.getServiceDayScheduleByServiceDayId(
            lang,token,ServiceId,DayId,clinicId
        )
    }

    override suspend fun getClinics(lang: String, token: String): ClinicsResponse {
        return contentEndpoint.getClinics(
            lang,token
        )
    }

    override suspend fun getDocument(token: String, lang: String): LegalDocumentsItemsResponse {

        return contentEndpoint.getDocument(
            token,lang
        )
    }

}