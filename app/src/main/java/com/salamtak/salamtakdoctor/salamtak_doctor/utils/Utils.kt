package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.ErrorBottomSheetFragment
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.Locale.getDefault
import java.util.regex.Matcher
import java.util.regex.Pattern


const val TAG = "UtilsClass"

fun isPasswordValid(password: String): Boolean {
    val regex = Regex("^(?=.*[A-Z]).{8,}\$") // At least 1 uppercase and 8 characters
    return regex.matches(password)
}


fun isPhoneValid(phone: String?): Boolean {
//    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
//    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
//    val matcher: Matcher = pattern.matcher(email)
//    return matcher.matches()

    if (Pattern.matches("01[0-2-5]\\d{1,8}\$+", phone)) {
        return phone!!.length == 11;
    }
    return false;

}

fun isEmailValid(email: String?): Boolean {
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z ]{2,4}$"
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isEnglishValid(email: String?): Boolean {
    val expression = "^[A-Za-z 1234567890 .,/()@!&]+$"
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isArabicValid(email: String?): Boolean {
    val expression = "^[\\u0600-\\u06FF .,/()@!&]+$"
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isClinicPhoneValid(phone: String?): Boolean {

    return ((phone!!.length >= 5 && phone.length <= 11)
            && (Pattern.matches(".\\d{1,11}\$+", phone)))
}

fun toast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun requestBody(txt: String): RequestBody =
    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), txt)


@SuppressLint("Recycle")
fun imageBody(mContext: Context, uri: Uri, key: String? = null): MultipartBody.Part {
    val p: String
    val cursor = mContext.contentResolver.query(uri, null, null, null, null)

    p = if (cursor == null) {
        uri.path.toString()
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        cursor.getString(idx)
    }

    val file = File(p)
    val requestFile = file.asRequestBody("multipart/file".toMediaType())
    return MultipartBody.Part.createFormData(
        if (key.isNullOrEmpty()) "photo" else key,
        file.name,
        requestFile
    )
}

fun getImageRequestBody(sourceFile: File, uri: Uri, mContext: Context): RequestBody? {
    var requestBody: RequestBody? = null

    Thread {
        val mimeType = getMimeType(sourceFile);
        if (mimeType == null) {
            Log.e("file error", "Not able to get mime type")
            return@Thread
        }
        try {
            requestBody = sourceFile.path.toRequestBody("multipart/form-data".toMediaType())
            /*MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(serverImageKey, uploadedFileName,sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
                .build()*/
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("File upload", "failed")
        }
    }.start()

    return requestBody
}

// url = file path or whatever suitable URL you want.
private fun getMimeType(file: File): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

//fun getMimeType(uri: Uri, mContext: Context): String? {
//    var mimeType: String? = null
//    mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
//        val cr: ContentResolver = mContext.contentResolver
//        cr.getType(uri)
//    } else {
//        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
//            uri
//                .toString()
//        )
//        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//            lowercase(getDefault())
//        )
//    }
//
//    Log.e(TAG, "getMimeType: $mimeType")
//    return mimeType
//}

fun getMimeType(file: File, mContext: Context): String? {

    val uri = Uri.fromFile(file)
    val cR: ContentResolver = mContext.contentResolver
    val mime = cR.getType(uri)

    return mime
}

fun getDaysList(): ArrayList<String> {

    val arrayList = ArrayList<String>()
    arrayList.add("Saturday")
    arrayList.add("Sunday")
    arrayList.add("Monday")
    arrayList.add("Tuesday")
    arrayList.add("Wednesday")
    arrayList.add("Thursday")
    arrayList.add("Friday")
    arrayList.add("Clear selection")


    return arrayList

}

fun getDate(timestamp: Long): String {

    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    val dateString = formatter.format(Date(timestamp * 1000L))

    Log.e("TAG", "getDate: $timestamp")

    return dateString
}

fun getDateFromDate(timestamp: Long): String {

    val formatter = SimpleDateFormat("YYYY", Locale.ENGLISH)
    val dateString = formatter.format(Date(timestamp * 1000L))

    Log.e("TAG", "getDate: $dateString")

    return dateString
}

fun getTimeFromTimeStamp(timestamp: Long): String {

    val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)
    val dateString = formatter.format(Date(timestamp * 1000L))

    Log.e("TAG", "getDate: $dateString")

    return dateString
}


fun isOnline(mContext: Context): Boolean {
    val cm =
        mContext.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return cm!!.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting
}

fun getDateFromDatePicker(datePicker: DatePicker): Date {
    val day: Int = datePicker.dayOfMonth
    val month: Int = datePicker.month
    val year: Int = datePicker.year
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return calendar.time
}

fun setLocale(activity: Activity, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources: Resources = activity.resources
    val config: Configuration = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
    activity.recreate()
}

fun loadLocale(activity: Activity, languageCode: String) {

    Log.e("UTILS Lang", "loadLocale: $languageCode")
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources: Resources = activity.resources
    val config: Configuration = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun AppCompatActivity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.showError(message: String) {
    val errorBottomSheetFragment = ErrorBottomSheetFragment.newInstance(message)
    errorBottomSheetFragment.show(parentFragmentManager, null)
}

fun showError(message: String, parentFragmentManager: FragmentManager) {
    val errorBottomSheetFragment = ErrorBottomSheetFragment.newInstance(message)
    errorBottomSheetFragment.show(parentFragmentManager, null)
}


fun checkTiming(time: String, endTime: String): Boolean {
    val pattern = "HH:mm"
    val sdf = SimpleDateFormat(pattern)

    var isBefore = false

    Log.e(TAG, "checkTimingBefore: $isBefore  ")


    try {
        val date1 = sdf.parse(time)
        val date2 = sdf.parse(endTime)
        if (date1.before(date2)) isBefore = true
    } catch (e: ParseException) {
        Log.e(TAG, "checkTimingException: $isBefore  ${e.errorOffset}")
        e.printStackTrace()
    }
    Log.e(TAG, "checkTiming: $isBefore")
    return isBefore
}

fun checkSessionTiming(time: String, endTime: String): Boolean {
    val pattern = "dd-MM-yyyy HH:mm"
    val sdf = SimpleDateFormat(pattern)

    var isBefore = false

    Log.e(TAG, "checkTimingBefore: $isBefore  ")


    try {
        val date1 = sdf.parse(time)
        val date2 = sdf.parse(endTime)
        if (date1.before(date2)) isBefore = true
    } catch (e: ParseException) {
        Log.e(TAG, "checkTimingException: $isBefore  ${e.errorOffset}")
        e.printStackTrace()
    }
    Log.e(TAG, "checkTiming: $isBefore")
    return isBefore
}

fun doBirthDateValidate(date: String): Boolean {
    var result = false
    Log.e(TAG, "doBirthDateValidate: $date")
    val sdf = SimpleDateFormat("YYYY")
    try {
        val parseddate = sdf.parse(date)
        val c2 = Calendar.getInstance()
        c2.add(Calendar.YEAR, -21)
        Log.e(
            TAG,
            "doBirthDateValidatesa: ${sdf.format(c2.time)} ${getDateFromDate(c2.timeInMillis)} ${
                sdf.format(sdf.parse(date))
            }"
        )
        if (sdf.format(c2.time) > sdf.format(sdf.parse(date))) {
            result = true
        }
    } catch (e: ParseException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }

    Log.e(TAG, "doBirthDateValidate:Result -> $result ")
    return result
}