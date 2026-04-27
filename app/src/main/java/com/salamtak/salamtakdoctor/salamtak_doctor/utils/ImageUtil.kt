package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.salamtak.R
import com.salamtak.core.usecase.UseCaseModule
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


object ImageUtil {

    private const val TAG = "ImageUtil"

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadPicture(imageView: ImageView, url: String?) {
        Glide.with(imageView.context).load("${url}").placeholder(R.drawable.image_placeholder).fitCenter().into(imageView)
    }

    fun convertImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)

    }

    fun convertImageToMultiPart(imageUri: Uri): MultipartBody.Part {
        val file = File(imageUri.path)
        var requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }

    private fun encodeImages(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun convertUriToBase64(context: Context, uri: Uri): String {
        val imageStream = context.getContentResolver().openInputStream(uri)
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        return encodeImages(selectedImage)
    }

//    fun bitmapToBase64(bitmap: Bitmap, imageSource: ImageSourceEnum): String? {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        if (imageSource.equals(ImageSourceEnum.GALLERY)) {
//            val newBitmap = Bitmap.createScaledBitmap(
//                bitmap,
//                (bitmap.width * 0.4).toInt(), (bitmap.height * 0.4).toInt(), false
//            )
//            newBitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
//        } else if (imageSource.equals(ImageSourceEnum.CAMERA)) {
//            val newBitmap = Bitmap.createScaledBitmap(
//                bitmap,
//                (bitmap.width * 2), (bitmap.height * 2), true
//            )
//            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//        }
//        val byteArray = byteArrayOutputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


}