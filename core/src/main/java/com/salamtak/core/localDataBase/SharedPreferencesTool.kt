package com.salamtak.core.localDataBase

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.salamtak.core.BuildConfig

object SharedPreferencesTool {

    val FILE_NAME = "com.salamtak.salamtakdoctor.salamtak_doctor"

    /**
     * The Editor to write in shared preference
     *
     * @param context app context
     */
    fun getEditor(context: Context): SharedPreferences.Editor {
        val preferences =
            getSharedPreferences(
                context
            )
        return preferences.edit()
    }

    /**
     * Reference from shared prefernce to read from it
     *
     * @param context app context
     */
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    fun saveObject(
        context: Context,
        key: String?,
        myObject: Any?
    ) {
        val editor =
            getEditor(
                context
            )
        val gson = Gson()
        val json = gson.toJson(myObject)
        editor.putString(key, json)
        editor.commit()
    }

    fun <M> getObject(
        context: Context,
        key: String?,
        className: Class<M>?
    ): M {
        val gson = Gson()
        val sharedPreferences =
            getSharedPreferences(
                context
            )
        val json = sharedPreferences.getString(key, "")
        return gson.fromJson(json, className)
    }

    fun setBoolean(
        context: Context,
        key: String?,
        value: Boolean
    ) {
        val editor =
            getEditor(
                context
            )
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String?): Boolean {
        val sharedPreferences =
            getSharedPreferences(
                context
            )
        return sharedPreferences.getBoolean(key, false)
    }

    fun getInt(context: Context, key: String?): Int {
        val sharedPreferences =
            getSharedPreferences(
                context
            )
        return sharedPreferences.getInt(key, -1)
    }

    fun setInt(
        context: Context,
        key: String?,
        value: Int
    ) {
        val editor =
            getEditor(
                context
            )
        editor.putInt(key, value)
        editor.apply()
    }

    fun setLong(
        context: Context,
        key: String?,
        value: Long
    ) {
        val editor =
            getEditor(
                context
            )
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(context: Context, key: String?): Long {
        val sharedPreferences =
            getSharedPreferences(
                context
            )
        return sharedPreferences.getLong(key, -1)
    }

    fun getString(context: Context, key: String?): String? {
        val sharedPreferences =
            getSharedPreferences(
                context
            )
        return sharedPreferences.getString(key, "")
    }

    fun setString(
        context: Context,
        key: String?,
        value: String?
    ) {
        val editor =
            getEditor(
                context
            )
        editor.putString(key, value)
        editor.apply()
    }

    /***
     * clear user data
     */
    fun clearUserData(context: Context) {
        setLong(
            context,
            SharedPreferencesKeys.PREF_USER_TOKEN.name,
            0
        ) // clear token
    }

    /***
     * clear all application data
     */
    fun clearAllData(context: Context) {
        val settings =
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
    }
}