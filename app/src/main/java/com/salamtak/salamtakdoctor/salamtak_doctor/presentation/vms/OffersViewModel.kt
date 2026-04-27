package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.domain.entities.response.data.ClinicsInfoResponse
import com.salamtak.domain.entities.response.offers.ClincsResponse
import com.salamtak.domain.entities.response.offers.DoctorOffersResponse
import com.salamtak.domain.entities.response.offers.GetGallaryByIDResponse
import com.salamtak.domain.entities.response.offers.GetOffersByOfferIDResponse
import com.salamtak.domain.entities.response.offers.MainCategoryResponse
import com.salamtak.domain.entities.response.offers.OfferGallaryResponse
import com.salamtak.domain.entities.response.offers.OfferResponse
import com.salamtak.domain.entities.response.offers.OfferServicesResponse
import com.salamtak.domain.entities.response.offers.SubCategoryResponse
import com.salamtak.domain.entities.response.poly.PolyClinicAdminResponse
import com.salamtak.domain.entities.response.poly.PolyClinicInfoResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn.NewLoginRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.NewClinicRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response


class OffersViewModel : ViewModel() {

    val Repo = NewLoginRepository()
    val repo = NewClinicRepository()
    var clinicsResponse = MutableLiveData<Resource<ClincsResponse>>()
    var categoryResponse = MutableLiveData<Resource<MainCategoryResponse>>()
    var subCategoryResponse = MutableLiveData<Resource<SubCategoryResponse>>()
    var subCategory2Response = MutableLiveData<Resource<OfferServicesResponse>>()
    var subCategory3Response = MutableLiveData<Resource<OfferServicesResponse>>()
    var offerResponse = MutableLiveData<Resource<OfferResponse>>()
    var offerUpdateResponse = MutableLiveData<Resource<OfferResponse>>()
    var offerGallaryResponse = MutableLiveData<Resource<OfferGallaryResponse>>()
    var offersResponse = MutableLiveData<Resource<DoctorOffersResponse>>()
    var getofferGallaryResponse = MutableLiveData<Resource<GetGallaryByIDResponse>>()
    var getOfferResponse = MutableLiveData<Resource<GetOffersByOfferIDResponse>>()
    var deleteGalleryResponse = MutableLiveData<Resource<GetOffersByOfferIDResponse>>()
    var data = MutableLiveData<Resource<ClinicsInfoResponse>>()
    var polyClinicAdminResponse = MutableLiveData<Resource<PolyClinicAdminResponse>>()
    var polyClinicInfoResponse = MutableLiveData<Resource<PolyClinicInfoResponse>>()
    var polyClinicAdminBackImgResponse = MutableLiveData<Resource<OfferGallaryResponse>>()
    var polyClinicInfoBackImgResponse = MutableLiveData<Resource<OfferGallaryResponse>>()


    fun getDoctorClinics()=viewModelScope.launch(Dispatchers.IO+handler) {
        clinicsResponse.postValue(Resource.Loading())
        val response=Repo.getDoctorClinics()
        clinicsResponse.postValue(response?.let { handleGetDoctorClinics(it) })
    }

    private fun handleGetDoctorClinics(response: Response<ClincsResponse>): Resource<ClincsResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<ClincsResponse>(response.errorBody()!!.string(), ClincsResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getOfferMainCategory()=viewModelScope.launch(Dispatchers.IO+handler) {
        categoryResponse.postValue(Resource.Loading())
        val response=Repo.getOfferMainCategory()
        categoryResponse.postValue(response?.let { handleGetOfferMainCategory(it) })
    }

    private fun handleGetOfferMainCategory(response: Response<MainCategoryResponse>): Resource<MainCategoryResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<MainCategoryResponse>(response.errorBody()!!.string(), MainCategoryResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getOfferSubCategoryL1(ParentID: Int?)=viewModelScope.launch(Dispatchers.IO+handler) {
        subCategoryResponse.postValue(Resource.Loading())
        val response=Repo.getOfferSubCategoryL1(ParentID)
        subCategoryResponse.postValue(response?.let { handleGetOfferSubCategoryL1(it) })
    }

    private fun handleGetOfferSubCategoryL1(response: Response<SubCategoryResponse>): Resource<SubCategoryResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<SubCategoryResponse>(response.errorBody()!!.string(), SubCategoryResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getOfferSubCategoryL2(ParentID:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        subCategory2Response.postValue(Resource.Loading())
        val response=Repo.getOfferSubCategoryL2(ParentID)
        subCategory2Response.postValue(response?.let { handleGetOfferSubCategoryL2(it) })
    }

    private fun handleGetOfferSubCategoryL2(response: Response<OfferServicesResponse>): Resource<OfferServicesResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferServicesResponse>(response.errorBody()!!.string(), OfferServicesResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getOfferSubCategoryL3(ParentID:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        subCategory3Response.postValue(Resource.Loading())
        val response=Repo.getOfferSubCategoryL3(ParentID)
        subCategory3Response.postValue(response?.let { handleGetOfferSubCategoryL3(it) })
    }

    private fun handleGetOfferSubCategoryL3(response: Response<OfferServicesResponse>): Resource<OfferServicesResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferServicesResponse>(response.errorBody()!!.string(), OfferServicesResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun createClinicOffer(offerRequest: HashMap<String, String>)=viewModelScope.launch(Dispatchers.IO+handler) {
        offerResponse.postValue(Resource.Loading())
        val response=Repo.createClinicOffer(offerRequest)
        offerResponse.postValue(response?.let { handleCreateClinicOffer(it) })
    }

    private fun handleCreateClinicOffer(response: Response<OfferResponse>): Resource<OfferResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferResponse>(response.errorBody()!!.string(), OfferResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun updateHealthEntityOfferByOfferId(offerRequest: HashMap<String, String>)=viewModelScope.launch(Dispatchers.IO+handler) {
        offerUpdateResponse.postValue(Resource.Loading())
        val response=Repo.updateHealthEntityOfferByOfferId(offerRequest)
        offerUpdateResponse.postValue(response?.let { handleUpdateHealthEntityOfferByOfferId(it) })
    }

    private fun handleUpdateHealthEntityOfferByOfferId(response: Response<OfferResponse>): Resource<OfferResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferResponse>(response.errorBody()!!.string(), OfferResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun createOfferGallery(offerRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        offerGallaryResponse.postValue(Resource.Loading())
        val response=Repo.createOfferGallery(offerRequest)
        offerGallaryResponse.postValue(response?.let { handleCreateOfferGallery(it) })
    }

    private fun handleCreateOfferGallery(response: Response<OfferGallaryResponse>): Resource<OfferGallaryResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferGallaryResponse>(response.errorBody()!!.string(), OfferGallaryResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getDoctorswithOffers()=viewModelScope.launch(Dispatchers.IO+handler) {
        offersResponse.postValue(Resource.Loading())
        val response=Repo.getDoctorswithOffers()
        offersResponse.postValue(response?.let { handleGetDoctorswithOffers(it) })
    }

    private fun handleGetDoctorswithOffers(response: Response<DoctorOffersResponse>): Resource<DoctorOffersResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<DoctorOffersResponse>(response.errorBody()!!.string(), DoctorOffersResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getClinicOfferGalleryByOfferId(offerId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        getofferGallaryResponse.postValue(Resource.Loading())
        val response=Repo.getClinicOfferGalleryByOfferId(offerId)
        getofferGallaryResponse.postValue(response?.let { handleGetClinicOfferGalleryByOfferId(it) })
    }

    private fun handleGetClinicOfferGalleryByOfferId(response: Response<GetGallaryByIDResponse>): Resource<GetGallaryByIDResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<GetGallaryByIDResponse>(response.errorBody()!!.string(), GetGallaryByIDResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun getHealthEntityOfferById(offerId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        getOfferResponse.postValue(Resource.Loading())
        val response=Repo.getHealthEntityOfferById(offerId)
        getOfferResponse.postValue(response?.let { handleGetHealthEntityOfferById(it) })
    }

    private fun handleGetHealthEntityOfferById(response: Response<GetOffersByOfferIDResponse>): Resource<GetOffersByOfferIDResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<GetOffersByOfferIDResponse>(response.errorBody()!!.string(), GetOffersByOfferIDResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }


    fun deleteOfferGallery(galleryOfferId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        deleteGalleryResponse.postValue(Resource.Loading())
        val response=Repo.deleteOfferGallery(galleryOfferId)
        deleteGalleryResponse.postValue(response?.let { handleDeleteOfferGallery(it) })
    }

    private fun handleDeleteOfferGallery(response: Response<GetOffersByOfferIDResponse>): Resource<GetOffersByOfferIDResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<GetOffersByOfferIDResponse>(response.errorBody()!!.string(), GetOffersByOfferIDResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }


    fun getdata(clinicId:Int)=viewModelScope.launch(Dispatchers.IO+handler) {
        data.postValue(Resource.Loading())
        val response=repo.getInfo(clinicId)
        data.postValue(response?.let { handlegetdata(it) })
    }

    private fun handlegetdata(response: Response<ClinicsInfoResponse>): Resource<ClinicsInfoResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<ClinicsInfoResponse>(response.errorBody()!!.string(), ClinicsInfoResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }


    fun createPolyClinicOwner(offerRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        polyClinicAdminResponse.postValue(Resource.Loading())
        val response=Repo.createPolyClinicOwner(offerRequest)
        polyClinicAdminResponse.postValue(response?.let { handleCreatePolyClinicOwner(it) })
    }

    private fun handleCreatePolyClinicOwner(response: Response<PolyClinicAdminResponse>): Resource<PolyClinicAdminResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferGallaryResponse>(response.errorBody()!!.string(), OfferGallaryResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun createPolyClinicOwnerBackImg(offerRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        polyClinicAdminBackImgResponse.postValue(Resource.Loading())
        val response=Repo.createPolyClinicOwnerBackImg(offerRequest)
        polyClinicAdminBackImgResponse.postValue(response?.let { handleCreatePolyClinicOwnerBackImg(it) })
    }

    private fun handleCreatePolyClinicOwnerBackImg(response: Response<OfferGallaryResponse>): Resource<OfferGallaryResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferGallaryResponse>(response.errorBody()!!.string(), OfferGallaryResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun createPolyClinicInfo(offerRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        polyClinicInfoResponse.postValue(Resource.Loading())
        val response=Repo.createPolyClinicInfo(offerRequest)
        polyClinicInfoResponse.postValue(response?.let { handleCreatePolyClinicInfo(it) })
    }

    private fun handleCreatePolyClinicInfo(response: Response<PolyClinicInfoResponse>): Resource<PolyClinicInfoResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<PolyClinicInfoResponse>(response.errorBody()!!.string(), PolyClinicInfoResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }

    fun createPolyClinicInfoImg2(offerRequest: MultipartBody)=viewModelScope.launch(Dispatchers.IO+handler) {
        polyClinicInfoBackImgResponse.postValue(Resource.Loading())
        val response=Repo.createPolyClinicInfoImg2(offerRequest)
        polyClinicInfoBackImgResponse.postValue(response?.let { handleCreatePolyClinicInfoImg2(it) })
    }

    private fun handleCreatePolyClinicInfoImg2(response: Response<OfferGallaryResponse>): Resource<OfferGallaryResponse>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = Gson().fromJson<OfferGallaryResponse>(response.errorBody()!!.string(), OfferGallaryResponse::class.java)
        return error.message?.let { Resource.Error(it) }
    }





    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("exception","exception: ${exception.message.toString()}")
    }

}