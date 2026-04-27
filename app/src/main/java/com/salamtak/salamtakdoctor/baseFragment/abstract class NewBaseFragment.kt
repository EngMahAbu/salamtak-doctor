package com.salamtak.salamtakdoctor.baseFragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.getRandomString
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.resizePic
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toFile
import java.io.File

abstract class NewBaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected var imageFile: File? = null

    // === Camera ===
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val bitmap = data?.extras?.get("data") as? Bitmap ?: return@registerForActivityResult
                val resizedBitmap = bitmap.resizePic(2000)
                val file = resizedBitmap.toFile(requireContext(), 20.getRandomString())
                imageFile = file
                onImageSelected(file)
            }
        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera()
            else Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
        }

    protected fun checkPermissionFromCamera() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED -> openCamera()
            else -> cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    // === Photo Picker ===
    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val inputStream = requireActivity().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val resizedBitmap = bitmap.resizePic(1000)
                val file = resizedBitmap.toFile(requireContext(), 20.getRandomString())
                imageFile = file
                onImageSelected(file)
            }
        }

    protected fun selectImageFromGallery() {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    // === Dialog ===
    protected open fun selectPic() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Action")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkPermissionFromCamera()
                    1 -> selectImageFromGallery()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    // Callback child fragments override it
    protected open fun onImageSelected(file: File) {

    }
}
