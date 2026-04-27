package com.salamtak.core.network

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun requestBody(txt: String): RequestBody =
    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), txt)


fun createBody(file: File): MultipartBody.Part {
    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    val builder = MultipartBody.Part
    return createFormData("imagePartFile", file.name, requestFile)
}

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
    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(
        if (key.isNullOrEmpty()) "photo" else key,
        file.name,
        requestFile
    )
}