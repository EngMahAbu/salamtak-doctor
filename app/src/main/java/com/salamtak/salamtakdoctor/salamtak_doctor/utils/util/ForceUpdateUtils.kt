package com.salamtak.salamtakdoctor.salamtak_doctor.utils.util

import android.content.Context
import android.content.pm.PackageManager

object ForceUpdateUtils {
    fun getAppVersion(context: Context): String? {
        var result: String? = ""
        try {
            result = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            result = result?.replace("[a-zA-Z]|-".toRegex(), "")
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return result
    }
}