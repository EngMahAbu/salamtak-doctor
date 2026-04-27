package com.salamtak.core.localDataBase

import android.content.Context
import java.util.*

object UserData {

    lateinit var context: Context

    fun setup(context: Context) {
        this.context = context
    }

    fun isLogin(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    fun isFirstUsed():Boolean {
        return SharedPreferencesTool.getBoolean(context, SharedPreferencesKeys.PREf_FIRST_USED.name)
    }

    fun setFirstUsed(isFirstUsed:Boolean) {
        SharedPreferencesTool.setBoolean(context, SharedPreferencesKeys.PREf_FIRST_USED.name, isFirstUsed)
    }

    fun getToken(): String? {
        return SharedPreferencesTool.getString(context, SharedPreferencesKeys.PREF_USER_TOKEN.name)
    }

    fun setToken(token: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.PREF_USER_TOKEN.name, token)
    }

    fun setFcmToken(token: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.PREF_FCM_TOKEN.name, token)
    }

    fun getFcmToken():String? {
        return SharedPreferencesTool.getString(context, SharedPreferencesKeys.PREF_FCM_TOKEN.name)
    }

    fun setLanguageID(languageId: Int) {
        SharedPreferencesTool.setInt(
            context,
            SharedPreferencesKeys.PREF_LANGUAGE_ID.name,
            languageId
        )
    }

    @JvmStatic
    fun getLanguageID(): Int {
        return SharedPreferencesTool.getInt(context, SharedPreferencesKeys.PREF_LANGUAGE_ID.name)
    }

    fun setProfileStatus(key : String ,profileStatus : Int) {
        SharedPreferencesTool.setInt(context,key, profileStatus)
    }

    fun getProfileStatus(key : String) : Int{
        return  SharedPreferencesTool.getInt(context,key)
    }

    fun logout() {

        setToken("")
        setUserName("")

        //SharedPreferencesTool.clearAllData(context)
    }

    fun getUserName(): String? {
        return SharedPreferencesTool.getString(context, SharedPreferencesKeys.PREF_USER_NAME.name)
    }
    fun getUserNameAR(): String? {
        return SharedPreferencesTool.getString(context, SharedPreferencesKeys.PREF_USER_NAMEAR.name)
    }

    fun getUserNameBirthDate(): String? {
        return SharedPreferencesTool.getString(context, SharedPreferencesKeys.PREF_USER_NAME_BearthDate.name)
    }
    fun setUserBirthDate(token: String){
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.PREF_USER_NAME_BearthDate.name, token)
    }

    fun setUserName(token: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.PREF_USER_NAME.name, token)
    }
    fun setUserNameAR(token: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.PREF_USER_NAMEAR.name,token)
    }

    fun getUserId(): Long? {
        return SharedPreferencesTool.getLong(context, SharedPreferencesKeys.PREF_USER_ID.name)
    }

    fun setUserId(id: Long) {
        SharedPreferencesTool.setLong(context, SharedPreferencesKeys.PREF_USER_ID.name, id)
    }

    fun getEmail(): String? {
        return SharedPreferencesTool.getString(context, SharedPreferencesKeys.PREF_USER_EMAIL.name)
    }

    fun setEmail(token: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.PREF_USER_EMAIL.name, token)
    }

    fun getImage(): String? {
        return SharedPreferencesTool.getString(context, SharedPreferencesKeys.PREF_USER_IMAGE.name)
    }

    fun setImage(image: String) {
        SharedPreferencesTool.setString(context, SharedPreferencesKeys.PREF_USER_IMAGE.name, image)
    }

    fun isSubscribeAsk():Boolean {
        return SharedPreferencesTool.getBoolean(context, SharedPreferencesKeys.PREF_SUBSCRIBE_ASK.name)
    }

    fun setSubsccribeAsk(isSubscribe:Boolean) {
        SharedPreferencesTool.setBoolean(context, SharedPreferencesKeys.PREF_SUBSCRIBE_ASK.name, isSubscribe)
    }

}