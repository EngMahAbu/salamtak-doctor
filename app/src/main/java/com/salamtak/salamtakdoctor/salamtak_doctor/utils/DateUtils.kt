package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.content.Context
import com.salamtak.core.localDataBase.SharedPreferencesKeys
import com.salamtak.core.localDataBase.SharedPreferencesTool
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    lateinit var context: Context
    fun setup(context: Context) {
        DateUtils.context = context
    }

    fun convertLongToDate(time:Long):String{
        val date= Date(time)
        val sdf= SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        return sdf.format(date)
    }

    fun Long.toTimeDateString(): String {
        val dateTime = Date(this)
        val format = SimpleDateFormat(" yyyy/MM/dd", Locale.US)
        return format.format(dateTime)
    }
    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy.MM.dd")
        return df.parse(date).time
    }

    fun setLanguageFromMore(lang: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.LanguageMore.name, lang)
    }
    fun getLanguageFromMore(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.LanguageMore.name)
    }
    fun getFcmToken(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.PREF_USER_FCM_TOKEN.name)
    }

    fun setFcmToken(token: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.PREF_USER_FCM_TOKEN.name, token)
    }

    fun setStatus(lang: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.Language.name, lang)
    }
    fun getStatus(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.Language.name)
    }

    fun setPhone(lang: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.Phone.name, lang)
    }
    fun getPhone(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.Phone.name)
    }
    fun setYear(lang: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.Year.name, lang)
    }
    fun getYear(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.Year.name)
    }
    fun setLang(lang: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.Lang.name, lang)
    }
    fun getLang(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.Lang.name)
    }

    fun setAdminOwnerId(ownerId: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.AdminStep.name, ownerId)
    }
    fun getAdminOwnerId(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.AdminStep.name)
    }
    fun setAdminInfoId(ownerId: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.InfoID.name, ownerId)
    }
    fun getAdminInfoId(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.InfoID.name)
    }

    fun setUserId(step: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.UserId.name, step)
    }
    fun getUserId(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.UserId.name)
    }

    fun setDoctorId(step: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.DoctorId.name, step)
    }
    fun getDoctorId(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.DoctorId.name)
    }

    fun setClinicId(step: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.ClinicId.name, step)
    }
    fun getClinicId(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.ClinicId.name)
    }

    fun setClinicName(step: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.ClinicName.name, step)
    }
    fun getClinicName(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.ClinicName.name)
    }

    fun setSpecDoctor(spec: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.SpecDoctor.name, spec)
    }
    fun getSpecDoctor(): String? {
        return SharedPreferencesTool.getString(context,SharedPreferencesKeys.SpecDoctor.name)
    }




}