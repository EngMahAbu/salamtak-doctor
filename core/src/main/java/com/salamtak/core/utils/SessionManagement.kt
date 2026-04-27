package com.salamtak.core.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManagement(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {

    companion object {
        const val KEY_IS_LOGIN: String = "is_login"
        const val STATUS_KEY: String = "status"
        const val USER_ID = "id"
        const val KEY_USER_TOKEN: String = "user_token"
        const val FIREBASE_TOKEN: String = "firebase_token"
        const val KEY_REFRESH_TOKEN: String = "refresh_token"
        const val KEY_USER_NAME: String = "user_name"
        const val KEY_USER_NAME_AR: String = "user_name_ar"
        const val KEY_USER_EMAIL: String = "user_email"
        const val KEY_SORT_TYPE: String = "sort_type"
        const val KEY_IMAGE_URL: String = "image_url"
        const val KEY_SOCIAL_AUTH: String = "social_auth"
        const val KEY_LANGUAGE: String = "language"
        const val KEY_LANGUAGE_AR: String = "ar"
        const val KEY_LANGUAGE_EN: String = "en"
        const val KEY_SAVED_IMAGE: String = "saved_image"
        const val KEY_USER_PHONE: String = "user_phone"
        const val REFRESH_TIME = "refresh_time"
        const val KEY_profile_status: String = "setup_profile_status"
        const val VERIFICATION_CODE: String = "code"
        const val VERIFICATION_PASSWORD_CODE: String = "password_code"
        const val KEY_BIRTH_DATE: String = "birth_date"
        const val SPECIALIST_ID: String = "specialist_id"
        const val COUNTRY_ID: String = "country"
        const val CLINIC_ID: String = "clinic_id"
        const val PREF_USER_TOKEN: String = "new_user_token"
        const val DOCTOR_ID: String = "doctor_id"
        const val USER_JSON : String = "userJson"
        const val USER_NAME : String = "nameDr"

    }


    lateinit var context: Context

    fun setup(context: Context) {
        this.context = context
    }

    fun createSession(id: String, access_token: String, phone: String) {
        editor.putString(USER_ID, id)
        editor.putString(KEY_USER_TOKEN, access_token)
        editor.putString(KEY_USER_PHONE, phone)
        setIsProfileFinished("1")
        editor.commit()
    }

    fun saveUserData(json : String){
        editor.putString(USER_JSON, json)
        editor.commit()
    }

    fun hasUserData() : Boolean {
        return sharedPreferences.contains(USER_JSON)
    }
    fun getUserData() : String? {
        return sharedPreferences.getString(USER_JSON, "")
    }

    fun setUserSelectedData(countryId: String, specialistId: String) {

        editor.putString(SPECIALIST_ID, specialistId)
        editor.putString(COUNTRY_ID, countryId)
        editor.commit()

    }

    fun setUserData(name: String, imageUrl: String, birthDate: String, nameAr: String) {
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_BIRTH_DATE, birthDate)
        editor.putString(KEY_IMAGE_URL, imageUrl)
        editor.putString(KEY_USER_NAME_AR, nameAr)
        setLoggedIn()
        editor.commit()
    }

    fun refreshToken(access_token: String, refresh_token: String, refresh_time: Long) {
        editor.putLong(REFRESH_TIME, refresh_time)
        editor.putString(KEY_USER_TOKEN, access_token)
        editor.putString(KEY_REFRESH_TOKEN, refresh_token)
        editor.commit()

    }

    fun setLang(lang: String) {
        editor.putString(KEY_LANGUAGE, lang)
        editor.commit()
    }

    fun setClinicId(clinicId: String) {
        editor.putString(CLINIC_ID, clinicId)
        editor.commit()
    }

    fun getClinicId(): String? {
        return sharedPreferences.getString(CLINIC_ID, "")
    }

    fun getCountry(): String? {
        return sharedPreferences.getString(COUNTRY_ID, "1")
    }

    fun getSpecialistId(): String? {
        return sharedPreferences.getString(SPECIALIST_ID, "0")
    }

    fun getLang(): String? {

        return sharedPreferences.getString(KEY_LANGUAGE, "")

    }

    //Don't forget to redirect the user to the login screen
    fun clearUser() {
        editor.clear()
        editor.commit()
        setSignIn("")
    }

    fun getValue(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun setLoggedIn() {
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.commit()
    }

    fun getRefreshTime(): Long {
        return sharedPreferences.getLong(REFRESH_TIME, 0L)!!
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGIN, false)
    }

    fun getStatus(): String {
        return sharedPreferences.getString(STATUS_KEY, "")!!
    }

    fun setStatus(status: String) {
        editor.putString(STATUS_KEY, status)
        editor.commit()
    }

    fun setImage(imageUrl: String) {

        editor.putString(KEY_IMAGE_URL, imageUrl)
        editor.commit()

    }
    fun getImage():String? {

        return sharedPreferences.getString(KEY_IMAGE_URL, "")


    }

    fun getCode(): String? {
        return sharedPreferences.getString(VERIFICATION_CODE, "")
    }

    fun getPasswordCode(): String? {
        return sharedPreferences.getString(VERIFICATION_PASSWORD_CODE, "")
    }

    fun getFireBaseToken(): String? {
        return sharedPreferences.getString(FIREBASE_TOKEN, "")
    }

    fun isProfileFinished(): String? {
        return sharedPreferences.getString(KEY_profile_status, "st")
    }

    fun setCode(code: String, userId: String) {
        editor.putString(VERIFICATION_CODE, code)
        editor.putString(USER_ID, userId)
        editor.commit()
    }

    fun setPasswordCode(code: String) {
        editor.putString(VERIFICATION_PASSWORD_CODE, code)
        editor.commit()
    }

    fun setFireBaseToken(code: String) {
        editor.putString(VERIFICATION_CODE, code)
        editor.commit()
    }


    fun setIsProfileFinished(status: String) {
        editor.putString(KEY_profile_status, status)
        editor.commit()
    }


    fun getToken(): String? {
        return sharedPreferences.getString(PREF_USER_TOKEN, "")
    }

    fun setToken(token: String) {
        editor.putString(PREF_USER_TOKEN, token)
    }

    fun setString(name: String?) {
        editor.putString(USER_NAME, name)
    }

    fun getString(): String? {
        return sharedPreferences.getString(USER_NAME, "")
    }

    fun getSignIn():String?{
        return sharedPreferences.getString(KEY_USER_TOKEN, "")
    }

    fun setSignIn(token: String) {
        editor.putString(KEY_USER_TOKEN, token)
    }

    fun setDoctorId(doctorId: String) {
        editor.putString(DOCTOR_ID, doctorId)
        editor.commit()
    }

    fun getDoctorId(): String? {
        return sharedPreferences.getString(DOCTOR_ID, "")
    }

    fun getString(key: String,default: String) = sharedPreferences.getString(key,default)
    fun setString(key: String,value: String) = editor.putString(key,value).commit()


}