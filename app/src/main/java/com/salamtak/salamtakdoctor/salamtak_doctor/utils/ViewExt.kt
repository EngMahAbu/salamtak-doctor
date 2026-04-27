package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.salamtak.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


fun Dialog.showLoadingDialog() = run {
    Dialog(context).requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    this.window?.setGravity(Gravity.CENTER)
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    this.setContentView(R.layout.dialog_loading)
    this.setCancelable(false)
    this.show()
}

fun Dialog.hideLoadingDialog() = run { if (this.isShowing) this.dismiss() }

fun EditText.clear() = this.setText("")

fun Fragment.errorMessage(message: String) = run {
    // create an instance of the snackbar
    val snackbar = Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG)

    val view:View=snackbar.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP

    view.layoutParams=params

    // inflate the custom_snackbar_view created previously
    val customSnackView: View = layoutInflater.inflate(R.layout.layout_error_snackbar, null)

    // set the background of the default snackbar as transparent
    snackbar.view.setBackgroundColor(Color.TRANSPARENT)

    // now change the layout of the snackbar
    val snackbarLayout = snackbar.view as SnackbarLayout

    // set padding of the all corners as 0
    snackbarLayout.setPadding(0, 0, 0, 0)

    // register the button from the custom_snackbar_view layout file
    val messageTv: TextView = customSnackView.findViewById(R.id.message_tv)

    messageTv.text = message

    // add the custom snack bar layout to snackbar layout
    snackbarLayout.addView(customSnackView, 0)

    snackbar.show()

}

fun Activity.errorMessage(message: String) = run {
    // create an instance of the snackbar
    val snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)


    val view:View=snackbar.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP

    view.layoutParams=params


    // inflate the custom_snackbar_view created previously
    val customSnackView: View = layoutInflater.inflate(R.layout.layout_error_snackbar, null)

    // set the background of the default snackbar as transparent
    snackbar.view.setBackgroundColor(Color.TRANSPARENT)

    // now change the layout of the snackbar
    val snackbarLayout = snackbar.view as SnackbarLayout



    // register the text from the snackbar view layout file
    val messageTv: TextView = customSnackView.findViewById(R.id.message_tv)

    messageTv.text = message

    // add the custom snack bar layout to snackbar layout
    snackbarLayout.addView(customSnackView, 0)

    snackbar.show()

}

fun Context.openKeyboard() {
    val inputMethodManager: InputMethodManager =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, 0)
}

 fun View.closeKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Bitmap.resizePic(maxSize: Int): Bitmap {
    var width = this.width
    var height = this.height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(this, width, height, true)
}

fun Bitmap.toFile(context: Context, fileName: String): File {
    //create a file to write bitmap data
    val file = File(context.cacheDir, fileName)
    file.createNewFile()
    //Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bitMapData = bos.toByteArray()
    //write the bytes in file
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    try {
        fos?.write(bitMapData)
        fos?.flush()
        fos?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

fun Int.getRandomString(): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..this)
        .map { allowedChars.random() }
        .joinToString("")
}

fun Activity.showRationalePermission() {
    AlertDialog.Builder(this)
        .setMessage("it looks like to have turened of permission" + "required for this feature")
        .setPositiveButton("Go to setting") { _, _ ->

            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel") { dialog, _ ->

            dialog.dismiss()
        }.show()
}





