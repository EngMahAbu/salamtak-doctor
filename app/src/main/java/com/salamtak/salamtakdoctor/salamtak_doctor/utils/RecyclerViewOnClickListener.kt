package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.net.Uri
import com.salamtak.databinding.DocumentsLayoutBinding
import com.salamtak.databinding.ItemCertificateBinding
import com.salamtak.domain.entities.response.LegalDocumentsData
import com.salamtak.domain.entities.resquest.auth.ScheduleModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs.ImageTypeEnum


interface RecyclerViewOnClickListener {

    fun <UI, D> onRootClickListener(dataBinding: UI, data: D) {}

    fun <UI, D> onRootClickListener(dataBinding: UI, data: D, position: Int) {}

    //    fun onClinicClickListener(clinic: Clinic){}
//
//    fun onClinicDelete(clinic: Clinic,position: Int){}
    fun onImageDelete(position: Int, binding: DocumentsLayoutBinding) {}
    fun onImageDelete(image: Uri, position: Int) {}
    fun onAssetDelete(id: Int, position: Int) {}
    fun onCertificateEdit(binding: ItemCertificateBinding, position: Int, id: Int) {}
    fun onClinicGallerySelect(clinicId: Int, position: Int) {}
    fun onClinicGalleryURlSelect(imageUrl: String, position: Int) {}
    fun onCountrySelect(countryId: Int, countryTxt: String, position: Int) {}
    fun onSpecialitySelect(specialityId: Int, specialityTxt: String, position: Int) {}
    fun onSenioritySelect(seniorityId: Int, seniorityTxt: String, position: Int) {}
    fun onSubSpecialitySelect(subSpecialityId: Int, subSpecialityTxt: String, position: Int) {}
    fun onSubSpecialityDeselect(subSpecialityId: Int, subSpecialityTxt: String, position: Int) {}
    fun onServiceSelect(serviceId: Int, serviceTxt: String, position: Int) {}
    fun onCitySelect(cityId: Int, cityTxt: String, position: Int) {}
    fun onAreSelect(areaId: Int, areaTxt: String, position: Int) {}

    fun onDurationSelect(durationId: Int, durationTxt: String, position: Int) {}
    fun onScheduleSubmit(
        dayId: Int,
        durationId: Int,
        workingFrom: String,
        workingTo: String,
        fees: Double,
        position: Int,
        serviceId: Int,
        MaxNoOfPatients:Int,
        followUpFees: Double,
        periodSwitch: Boolean,
        overlapping: Boolean,
        WorkingDate:String?
    ) {
    }
    fun onUpdate(scheduleUpdate: ScheduleModel){}


    fun onCertificateImageEdit(binding: ItemCertificateBinding, position: Int, id: Int) {}
    fun onDaySelect(day_index: Int, from: String, to: String) {}
    fun onDayDeselect(day_index: Int, from: String, to: String) {}

    fun onImagePickAction(
        binding: DocumentsLayoutBinding,
        position: Int,
        data: LegalDocumentsData,
        imageType: ImageTypeEnum,
        addClicked:Boolean
    ) {
    }

    fun onImagePickAction(binding: ItemCertificateBinding, position: Int) {}
    fun onGetImageDelete(
        position: Int,
        binding: DocumentsLayoutBinding,
        id: Int,
        typeId: Int,
        imageType: ImageTypeEnum
    ) {
    }

    fun onClinicTypeSelect(clinicId: Int, clinicNameTxt: String, position: Int) {}
    fun onClinicSelect(clinicId: Int, clinicNameTxt: String, position: Int) {}

    fun onImageEmrClicked(position: Int, image: String) {}
    fun onTypeService(){}

}