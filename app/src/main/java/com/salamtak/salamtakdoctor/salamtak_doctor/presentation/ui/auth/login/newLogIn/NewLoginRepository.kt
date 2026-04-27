package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn


import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.response.DeleteSchduleRequest
import com.salamtak.domain.entities.response.doctoraccount.DoctorInvoiceRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.AddInvoiceItemRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.EditInvoiceRequest
import com.salamtak.domain.entities.resquest.auth.DoctorVideoRequest
import com.salamtak.domain.entities.resquest.auth.LoginModel
import com.salamtak.domain.entities.resquest.auth.RegisterModel
import okhttp3.MultipartBody
import retrofit2.http.Body

class NewLoginRepository {

    private val client = RetrofitBuilder().instant

    suspend fun newSignIn(signInRequest: LoginModel) =
        client.newSignIn(Localization.getLanguageName(), signInRequest)

    suspend fun getAllInsurance_Companies() =
        client.getAllInsurance_Companies(Localization.getLanguageName())
    suspend fun getFeatures() =
        client.getFeatures(Localization.getLanguageName())

    suspend fun getSalamtakGate() = client.getSalamtakGate(Localization.getLanguageName())
    suspend fun getBlogs(gateId: Int) = client.getBlogs(Localization.getLanguageName(), gateId)
    suspend fun getHealthEntityPagedList(
        CityId: Int, AreaId: Int, HealthEntityTypeId: Int,
        MaxResultCount: Int, SkipCount: Int
    ) = client.getHealthEntityPagedList(
        Localization.getLanguageName(),
        CityId,
        AreaId,
        HealthEntityTypeId,
        MaxResultCount,
        SkipCount
    )

    suspend fun getOtherPackagesByClinicID(clinicID: Int) =
        client.getOtherPackagesByClinicID(Localization.getLanguageName(), clinicID)

    suspend fun getClinicPackage(ClinicID: Int, Month: Int, Year: Int) =
        client.getClinicPackage(Localization.getLanguageName(), ClinicID, Month, Year)

    suspend fun getUserNotifications() = client.getUserNotifications(Localization.getLanguageName())
    suspend fun getAllCities() = client.getAllCities(Localization.getLanguageName())
    suspend fun getAreasByCityId(cityId: Int) =
        client.getAreasByCityId(Localization.getLanguageName(), cityId)

    suspend fun deleteUser() = client.deleteUser(Localization.getLanguageName())
    suspend fun deleteDoctorClinicSchedual(deleteSchduleRequest: DeleteSchduleRequest) = client.deleteDoctorClinicSchedual(Localization.getLanguageName(),deleteSchduleRequest)
    suspend fun getDoctorClinics() = client.getDoctorClinics(Localization.getLanguageName())
    suspend fun getOfferMainCategory() = client.getOfferMainCategory(Localization.getLanguageName())
    suspend fun getOfferSubCategoryL1(ParentID: Int?) = client.getOfferSubCategoryL1(Localization.getLanguageName(),ParentID)
    suspend fun getOfferSubCategoryL2(ParentID:Int) = client.getOfferSubCategoryL2(Localization.getLanguageName(),ParentID)
    suspend fun getOfferSubCategoryL3(ParentID:Int) = client.getOfferSubCategoryL3(Localization.getLanguageName(),ParentID)
    suspend fun createClinicOffer(offerRequest: HashMap<String, String>) = client.createClinicOffer(Localization.getLanguageName(),offerRequest)
    suspend fun createOfferGallery(offerRequest: MultipartBody) = client.createOfferGallery(Localization.getLanguageName(),offerRequest)
    suspend fun createDoctorClinic(Request: MultipartBody) = client.createDoctorClinic(Localization.getLanguageName(),Request)
    suspend fun createHealthEntity(Request: MultipartBody) = client.createHealthEntity(Localization.getLanguageName(),Request)
    suspend fun updateDoctorClinic2(Request: MultipartBody) = client.updateDoctorClinic2(Localization.getLanguageName(),Request)
    suspend fun getDoctorswithOffers() = client.getDoctorswithOffers(Localization.getLanguageName())
    suspend fun getClinicOfferGalleryByOfferId(offerId:Int) = client.getClinicOfferGalleryByOfferId(Localization.getLanguageName(),offerId)
    suspend fun getHealthEntityOfferById(offerId:Int) = client.getHealthEntityOfferById(Localization.getLanguageName(),offerId)
    suspend fun updateHealthEntityOfferByOfferId(offerUpdateRequest: HashMap<String, String>) = client.updateHealthEntityOfferByOfferId(Localization.getLanguageName(),offerUpdateRequest)
    suspend fun deleteOfferGallery(galleryOfferId:Int) = client.deleteOfferGallery(Localization.getLanguageName(),galleryOfferId)
    suspend fun createPolyClinicOwner(offerRequest: MultipartBody) = client.createPolyClinicOwner(Localization.getLanguageName(),offerRequest)
    suspend fun updatePolyClinicOwner(offerRequest: MultipartBody) = client.updatePolyClinicOwner(Localization.getLanguageName(),offerRequest)
    suspend fun updatePolyClinicInfo(offerRequest: MultipartBody) = client.updatePolyClinicInfo(Localization.getLanguageName(),offerRequest)
    suspend fun createPolyClinicOwnerBackImg(offerRequest: MultipartBody) = client.createPolyClinicOwnerBackImg(Localization.getLanguageName(),offerRequest)
    suspend fun createPolyClinicInfoImg2(offerRequest: MultipartBody) = client.createPolyClinicInfoImg2(Localization.getLanguageName(),offerRequest)
    suspend fun createPolyClinicInfo(offerRequest: MultipartBody) = client.createPolyClinicInfo(Localization.getLanguageName(),offerRequest)
    suspend fun getDoctorByClinicId(clinicId: Int) = client.getDoctorByClinicId(Localization.getLanguageName(),clinicId)
    suspend fun getpolyOwnerByID(ownerID: Int) = client.getpolyOwnerByID(Localization.getLanguageName(),ownerID)
    suspend fun getPolyClinicInfoByID(infoID: Int) = client.getPolyClinicInfoByID(Localization.getLanguageName(),infoID)
    suspend fun getDoctorCertificatePolyClinic(DoctorID: Int) = client.getDoctorCertificatePolyClinic(Localization.getLanguageName(),DoctorID)
    suspend fun getDoctorDocumentsPolyClinic(DoctorID: Int) = client.getDoctorDocumentsPolyClinic(Localization.getLanguageName(),DoctorID)
    suspend fun createDoctorClinicVideo(videoRequest: MultipartBody) = client.createDoctorClinicVideo(Localization.getLanguageName(),videoRequest)
    suspend fun createDoctorVideo(videoRequest: DoctorVideoRequest) = client.createDoctorVideo(Localization.getLanguageName(),videoRequest)
    suspend fun getDoctorVideos(DoctorID: Int) = client.getDoctorVideos(Localization.getLanguageName(),DoctorID)
    suspend fun deleteDoctorVideo(VideoID: Int) = client.deleteDoctorVideo(Localization.getLanguageName(),VideoID)
    suspend fun deleteHealthEntityVideo(VideoID: Int) = client.deleteHealthEntityVideo(Localization.getLanguageName(),VideoID)
    suspend fun getClinicVideoByClinicId(clinicId: Int) = client.getClinicVideoByClinicId(Localization.getLanguageName(),clinicId)
    suspend fun checkUser() = client.checkUser(Localization.getLanguageName())
    suspend fun sendOTP( registerUser: RegisterModel) = client.sendOTP(Localization.getLanguageName(),registerUser)
    suspend fun getAllQandAByDoctorId() = client.getAllQandAByDoctorSpecID(Localization.getLanguageName())
    suspend fun getAllQandA() = client.getAllQandA(Localization.getLanguageName())
    suspend fun getAnswersByDoctorId() = client.getAnswersByDoctorId(Localization.getLanguageName())
    suspend fun createAnswerpatientQ(answerRequest: HashMap<String, String>) = client.createAnswerpatientQ(Localization.getLanguageName(),answerRequest)
    suspend fun updateNotification() = client.updateNotification(Localization.getLanguageName())
    suspend fun cancelQuestion(questionID: Int) = client.cancelQuestion(Localization.getLanguageName(),questionID)
    suspend fun getAllPatientAccount(dateFrom: String?=null, dateTo: String?=null, patientName: String?=null) = client.getAllPatientAccount(Localization.getLanguageName(),dateFrom,dateTo,patientName)
    suspend fun createInvoice(doctorInvoiceRequest: DoctorInvoiceRequest) = client.createInvoice(Localization.getLanguageName(),doctorInvoiceRequest)
    suspend fun addInvoiceItems(addInvoiceItemRequest: AddInvoiceItemRequest) = client.addInvoiceItems(Localization.getLanguageName(),addInvoiceItemRequest)
    suspend fun editInvoiceItem(editInvoiceRequest: EditInvoiceRequest) = client.editInvoiceItem(Localization.getLanguageName(),editInvoiceRequest)
    suspend fun deleteInvoiceItem(id: Int) = client.deleteInvoiceItem(Localization.getLanguageName(),id)
    suspend fun getInvoicesByPatientId(patientId: Int?) = client.getInvoicesByPatientId(Localization.getLanguageName(),patientId)
    suspend fun getSymptomsByAppointmentId(appointmentId: Int?) = client.getSymptomsByAppointmentId(Localization.getLanguageName(),appointmentId)
    suspend fun createDoctorResponse(Request: HashMap<String, String>) = client.createDoctorResponse(Localization.getLanguageName(),Request)
    suspend fun updateDoctorProfileFirstStep(profileRequest: MultipartBody) = client.updateDoctorProfileFirstStep(Localization.getLanguageName(),profileRequest)
    suspend fun createProfile(profileRequest: MultipartBody) = client.createProfile(Localization.getLanguageName(),profileRequest)
    suspend fun createDoctorToPolyclinic(profileRequest: MultipartBody,clinicId: Int,userId: Int) = client.createDoctorToPolyclinic(Localization.getLanguageName(),clinicId,userId,profileRequest,)
    suspend fun createDoctorDocuments(documentRequest: MultipartBody) = client.CreateDoctorDocuments(Localization.getLanguageName(),documentRequest)
    suspend fun createDoctorDocumentsPolyClinic(documentRequest: MultipartBody) = client.createDoctorDocumentsPolyClinic(Localization.getLanguageName(),documentRequest)
    suspend fun updateDoctorDocument(documentRequest: MultipartBody) = client.updateDoctorDocument(Localization.getLanguageName(),documentRequest)
    suspend fun getDoctorLegalDocuments2() = client.getDoctorLegalDocuments2(Localization.getLanguageName())
    suspend fun createPatientEmrAl(emrRequest: MultipartBody) = client.createPatientEmrAl(Localization.getLanguageName(),emrRequest)

}