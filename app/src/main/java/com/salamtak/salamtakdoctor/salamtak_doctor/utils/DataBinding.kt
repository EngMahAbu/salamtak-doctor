package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

object DataBinding {

    @JvmStatic
    @BindingAdapter("convertDate")
    fun convertDate(view: TextView, date1: String?) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(date1)
        val dateForm = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        view.text=dateForm.format(date)
    }

    @JvmStatic
    @BindingAdapter("convertDateAnthor")
    fun convertDateAnthor(view: TextView, date1: String?) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(date1)
        val dateForm = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        view.text=dateForm.format(date)
    }
    fun convertDateFormat(date1: String?,format:String):String{
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val date = sdf.parse(date1)
        val dateForm = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
       return dateForm.format(date)
    }
    fun convertDateFormat(date1: String?,format:String,formatParsing:String):String{
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val date = sdf.parse(date1)
        val dateForm = SimpleDateFormat(formatParsing, Locale.getDefault())
        return dateForm.format(date)
    }
    fun convertDateFormat(date1: String?,format:String,formatParsing:String,locale: Locale):String{
        val sdf = SimpleDateFormat(format, locale)
        val date = sdf.parse(date1)
        val dateForm = SimpleDateFormat(formatParsing, locale)
        return dateForm.format(date)
    }

    @JvmStatic
    @BindingAdapter("convertTime")
    fun convertTime(view: TextView, T: String?) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(T)
        val dateForm = SimpleDateFormat("hh:mm a", Locale.getDefault())
        view.text=dateForm.format(date)
    }
    fun convertTime( T: String?,format:String,formatParsing:String):String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val date = sdf.parse(T)
        val dateForm = SimpleDateFormat(formatParsing, Locale.getDefault())
       return dateForm.format(date)
    }

    @JvmStatic
    @BindingAdapter("convertAnthorTime")
    fun convertAnthorTime(view: TextView, date1: String?) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(date1)
        val dateForm = SimpleDateFormat("hh:mm a", Locale.getDefault())
        view.text=dateForm.format(date)
    }


    @JvmStatic
    @BindingAdapter("imageFile")
    fun imageFile(iv: ShapeableImageView, file: File?) {
        file?.let {
            iv.setImageURI(it.toUri())
        }
    }


     suspend fun getImageBitmap(url: String): Bitmap? {
        var bm: Bitmap? = null
        try {
            val aURL = URL(url)
            val conn: URLConnection = aURL.openConnection()
            withContext(Dispatchers.IO) {
                conn.connect()

                val `is` = conn.getInputStream()
                val bis = BufferedInputStream(`is`)
                bm = BitmapFactory.decodeStream(bis)
                bis.close()
                `is`.close()
            }
        } catch (e: IOException) {
            Log.e("TAG", "Error getting bitmap", e)
        }
        return bm
    }

     fun getImageFile(bitmap: Bitmap,context: Context): File {

        val wrapper = ContextWrapper(context)
        var file = wrapper.getDir("image", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpeg")
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream)
        stream.flush()
        stream.close()
        return file

    }

    fun File.toMultipartBodyPart(name: String): MultipartBody.Part {
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), this)
        return MultipartBody.Part.createFormData(name, this.name, requestFile)
    }

    fun File.changeExtension(newExtension: String): File {
        // Ensure the new extension starts with a dot
        val formattedExtension = if (newExtension.startsWith(".")) newExtension else ".$newExtension"

        // Get the file's parent directory
        val parentDir = this.parentFile

        // Create a new file with the new extension
        val newFile = File(parentDir, this.nameWithoutExtension + formattedExtension)

        // Rename the file
        if (this.renameTo(newFile)) {
            return newFile
        } else {
            throw RuntimeException("Failed to rename file")
        }
    }
}