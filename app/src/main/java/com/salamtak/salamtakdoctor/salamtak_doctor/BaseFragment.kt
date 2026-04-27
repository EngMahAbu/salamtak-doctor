package com.salamtak.salamtakdoctor.salamtak_doctor

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.gson.JsonSyntaxException
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.domain.entities.GeneralResponse
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.BaseViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RequestCode
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.HttpException


abstract class BaseFragment<VM : BaseViewModel>(@LayoutRes contentLayoutId: Int) : NewBaseFragment(contentLayoutId),
    com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragmentDataObserver,
    KoinComponent {

    abstract val screenViewModel: VM
    var galleryRequestCode : Int = 0
    var cameraRequestCode : Int = 0
    var editGalleryRequestCode : Int = 0
    private val TAG = com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment::class.java.simpleName

    //    private var titleController: ITitle? = null
//    private var loader: Loading? = null
    val sharedStorage: SessionManagement by inject()

     var canShowNoNetworkDialog = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {

//            if (context is Loading)
//                loader = context

            if (screenTitle(context).isNotEmpty())
                setScreenTitle(screenTitle(context))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screenViewModel.failure.removeObservers(this)
        screenViewModel.failure.observe(this, Observer {
            when (it) {

                is GeneralResponse.Failure.Error -> {
                    when (it.ex) {
                        is HttpException -> if (canShowNoNetworkDialog) {

                            Log.e("$TAG Exception", "${ it.ex.localizedMessage } ")

//                            context?.showNoNetworkDialog { canShow ->
//                                canShowNoNetworkDialog = canShow
//                            }
                        }

                        is JsonSyntaxException ->{

                            Log.e("TAGJson BaseFragment123", "onCreate: ${it.ex.printStackTrace()}" )

                        }

                        else -> {
                            Log.e("TAG BaseFragment", "onCreate: ${it.ex.cause?.message}  == ${it.ex.message} ${it.errorCode} ${it.message}" )
                        }
                    }
                }

                is GeneralResponse.Failure.JSonServerError -> {
                    Log.e("TAG BaseFragment", "onCreate:" )
                }
            }
        })
        screenViewModel.loading.observe(this, Observer {
            showLoading(it)
        })
    }

    override fun onDetach() {
        super.onDetach()
        showLoading(false)
//        titleController = null
//         loader = null
    }

    override fun onDestroy() {
        super.onDestroy()
        showLoading(false)
    }

    override fun onStop() {
        super.onStop()
        showLoading(false)
    }

    protected fun showLoading(show: Boolean) {
//        loader?.showLoading(show)
    }

    protected fun setScreenTitle(title: String) {
        //   titleController?.changeTitle(title.trim())
    }

    open fun screenTitle(context: Context): String {
        return ""
    }


    //Camera code
    /** Show the dialog for choosing the images from camera and gallery */
    fun showDialog(galleryCode : Int , cameraCode : Int ){
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_image)

        val cameraImage : TextView = dialog.findViewById(R.id.ivCameraImage)
        val galleryImage : TextView = dialog.findViewById(R.id.ivGalleryImage)

        cameraImage.setOnClickListener{
            openCamera(cameraCode)
            dialog.dismiss()
        }

        galleryImage.setOnClickListener{
            checkMediaPermissions(galleryCode)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun openCamera(cameraCode : Int ){
        checkCameraPermission(cameraCode)
    }


    fun checkCameraPermission(cameraCode : Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("TAG", "checkMediaPermissions: ")

            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    cameraCode
                )

            } else {
                cameraIntent()
            }

        } else {
            Log.e("TAG", "checCameraPermissions: camera permission")
            cameraIntent()
        }
    }



    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun checkMediaPermissions(requestcode : Int) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            Log.e("TAG", "checkMediaPermissions: ")

            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    requestcode
                )

            } else {
                mediaIntent()
            }

        } else {
            Log.e("TAG", "checkMediaPermissions: sdsds")
            mediaIntent()
        }

    }

    fun cameraIntent(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, RequestCode.CAMERA_REQUEST_CODE)
    }

    fun mediaIntent() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, RequestCode.MEDIA_REQUEST_CODE)
    }


}