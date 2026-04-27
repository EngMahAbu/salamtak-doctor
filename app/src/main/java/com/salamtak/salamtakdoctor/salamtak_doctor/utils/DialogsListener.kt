package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.PickerDialog


interface DialogsListener {

    fun onDismiss(){}

    fun onDismiss(action:Int){}

    fun onFilesClick(requestCode: Int,pickerDialog: PickerDialog){}
    fun onImagesClick(requestCode: Int,pickerDialog: PickerDialog){}
    fun onPickDate(birthDate: String){}
    fun onInternetOKClick(noInternetDialog: NoInternetDialog){}

    fun onTimePick(fromTime: String,toTime: String){}

    fun onDurationConfirm(durationId: Int, durationTxt: String){}
    fun onServiceConfirm(clinicTypelId: Int, servTxt: String){}


}