package com.salamtak.salamtakdoctor.salamtak_doctor.utils


//base
//const val BASE_URL = "https://admin.salam-tech.com/doctors/"

/*
const val BASE_URL = "https://salamtech.azurewebsites.net/api/User/"
const val BASE_URL_UPLOAD_PROFILE_ASSETS = "${BASE_URL}doctor/upload"
const val BASE_URL_UPLOAD_CLINIC_ASSETS = "${BASE_URL}doctor/clinics/upload"
const val BASE_URL_DELETE_ASSETS = "${BASE_URL}attachment/{id}"
const val BASE_URL_UPLOAD_CLINIC_DATA = "${BASE_URL}doctor/clinics"
*/


const val PREF_NAME = "Salamtak-Doctor-App"


//service keys
const val LOGIN_KEY = "login"
const val REGISTER_KEY = "register"
const val GET_SPECIALITIES = "specialties"
const val GET_LOCATIONS = "locations"
const val UPLOAD_CLINIC_ASSETS = "doctor/clinics/upload"
const val UPLOAD_PROFILE_ASSETS = "doctor/upload"
const val UPLOAD_CLINIC_DATA = "doctor/clinics"
const val UPDATE_CLINIC_DATA = "doctor/clinics/{id}"
const val UPLOAD_PROFILE_DATA = "doctor/account/edit"
const val PROFILE_FINISH = "doctor/profile/finish"
const val SETTINGS = "settings"
const val SHOW_PARAMETERS = "show_parameters"
const val GET_PROFILE_DATA = "doctor/data"
const val GET_CLINIC_DATA = "doctor/clinics/{id}/edit"
const val DELETE_CLINIC_IMAGES = "attachment/{id}"
const val PASSWORD_VERIFY = "password/verify"
const val PASSWORD_RESET = "password/reset"
const val REFRESH_TOKEN = "login/refresh"




const val ACCOUNT_TYPE = "2"
const val DEVICE_TYPE = "android"
const val ASSET_AVATAR = "avatar"
const val ASSET_DOCUMENTS = "documents"
const val ASSET_LOGO = "logo"
const val ASSET_GALLERY = "gallery"


const val PHONE = "phone"
const val EMAIL = "email"
const val PASSWORD = "password"
const val TODAY_DATE = "today_date"
const val TO_DATE = "to_date"

var SELECTED_TAB = ""

const val DOCTOR_NAME = "doctor_name"
const val APPOINTMENT_FOR_DAY = "appointments"
const val PATIENT_PROFILE = "EMR"
const val PATIENT_NAME = "patient_name"
const val PATIENT_IMAGE= "patient_image"
const val MY_PROFILE = "profile"

const val CREATE_EMR = "createEMR"
const val UPDATE_EMR = "updateEMR"
const val UPDATE_USER_INFO = "updateInfo"
const val DELETE_KEY = "DELETE"
const val UPLOAD_UPDATE_DOCS = "https://admin.salam-tech.com/doctors/update-upload-documents"
const val UPDATE_WORK_INFO = "updateWork"
const val MESSAGE = "message"
const val CANCEL_APPOINTMENT_KEY = "cancelAppointments"
const val UPDATE_USER_INFO_FULL_URL = "https://admin.salam-tech.com/doctors/updateInfo"
const val UPDATE_EMR_FULL_URL = "https://admin.salam-tech.com/doctors/updateEMR"
const val FIRS_SETUP = "https://admin.salam-tech.com/doctors/setUpProfileFirstStep"
const val SECOND_SETUP = "https://admin.salam-tech.com/doctors/setUpProfileSecondStep"
const val UPDATE_CLINIC = "https://admin.salam-tech.com/doctors/update-doctor-clinic"
const val UPLOAD_DOCS = "https://admin.salam-tech.com/doctors/upload-documents"
const val DOCTOR_SPECIALISTS = "https://admin.salam-tech.com/doctors/specialists"
const val PROFILE_UPDATED = "Info Updated Succefully"
const val DELETE_EMR_DOCUMENT = "deleteEMRDocument"
const val SEND_VERIFY_CODE = "sendVerifyCode"
const val DELETE_PROFILE_DOCUMENT = "deleteProfileDocument"
const val ADD_CLINIC_BRANCH = "https://admin.salam-tech.com/doctors/addBranche"
const val DELETE_CLINIC_BRANCH = "delete-clinic"

//keys
const val SOMETHING_WRONG = "something went wrong"
const val NO_INTERNET = "No Internet Connection"
const val EMAIL_OR_PHONE_IS_TAKEN = "email or phone is taken"
const val EMAIL_IS_INVALID = "The Entered Value Is Not Registered With Us"
const val EMAIL_IS_TAKEN = "email is taken"
const val PHONE_IS_TAKEN = "phone is taken"
const val INVALID_CREDENTIALS = "Invalid email or password"

const val DOCTOR_LOCATION = "doctor_location"
const val APPOINTMENT_ID = "appointmentId"
const val PATIENT_ID = "patienttId"
const val OTP_CODE = "otpCode"
const val CLINIC_ID = "clinicId"
const val USER_ID = "userId"
const val NEW_OTP_CODE = "newotpCode"
const val TypeId ="TypeId"

const val CLINIC_NAME = "clinic_name"
const val Patient_NAME = "patient_name"
const val Doctor_NAME = "Doctor_name"
const val Patient_Phone = "patient_Phone"
const val CLINIC_TYPE = "clinic_type"
const val CLINIC_DATE = "clinic_date"
const val AGORA_TOKEN = "agora_token"
const val AGORA_CHANNEL = "agora_channel"
const val AGORA_APPOINTMENT_ID = "appointment_id"
const val VIDEO_CALL = 3
const val VOICE_CALL = 4


object RequestCode {
    const val USER_IMAGE_REQUEST_CODE = 12
    const val MEDIA_REQUEST_CODE = 233
    const val MEDIA_EDIT_REQUEST_CODE = 111
    const val PDF_REQ_CODE = 1243
    const val CLINIC_MEDIA_REQUEST_CODE = 235
    const val LOCATION_REQUEST_CODE = 23
    const val IRRIGATION_SALTS_RC = 14330
    const val DRINKING_SALTS_RC = 13220
    const val SOIL_SALTS_RC = 12110
    const val CLINIC_LOCATION_RC = 140
    const val USERS_REQUEST_CODE = 121
    const val INTERNAL_PRODUCT_IMAGE_REQUEST_CODE = 141
    const val EXTERNAL_PRODUCT_IMAGE_REQUEST_CODE = 142
    const val CAMERA_REQUEST_CODE = 300
    const val UNAUTHORIZED = 401
}

object ZegoConstants{
    const val appId : Long = 670388595
    const val appSignIN = "1d6e91dfa82391cf45e129d5a099659cd293c36296887782870d3b9adbf5f39a"
}