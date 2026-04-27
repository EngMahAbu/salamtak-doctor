package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentEmrBinding
import com.salamtak.domain.entities.response.AttachmentEmrData
import com.salamtak.domain.entities.response.EmrTitleData
import com.salamtak.domain.entities.resquest.auth.EmrDescriptionModel
import com.salamtak.domain.entities.resquest.auth.EmrInstructionModel
import com.salamtak.domain.entities.resquest.auth.SendAttathmentEmrModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.adapter.ErmDocumentAdatper
import com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.addEmr.AttachmentEmrAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.DoctorProfileUpdateViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class EmrFragment : Fragment(), RecyclerViewOnClickListener {
    private lateinit var binding: FragmentEmrBinding
    private lateinit var viewModel: PatientProfileViewModel
    private val sessionManagement: SessionManagement by inject()
    private val sharedDataViewModel: SharedDataViewModel by activityViewModels()
    private lateinit var attachmentEmrAdapter: AttachmentEmrAdapter
    private lateinit var doctorProfileViewModel: DoctorProfileUpdateViewModel
    private var partMapImage: Map<String, Any> = mutableMapOf()
    private lateinit var mContext: Context
    private lateinit var noInternetDialog: NoInternetDialog
    var appointmentId: Long = -1
    var emrTitleData: EmrTitleData? = null
    var patientId: Long = -1
    var cameraRequestCode = 0
    var galleryRequestCode = 0
    var editGalleryRequestCode = 0
    private lateinit var loadingDialog: LoadingDialog
    private var imageFile: File?=null
    var isFilled = false
    var documentAdapter: ErmDocumentAdatper? = null
    var addemr=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            appointmentId = it.getLong(APPOINTMENT_ID, 0)
            patientId = it.getLong(PATIENT_ID, 0)
            emrTitleData = Gson().fromJson(it.getString("emr_data"), EmrTitleData::class.java)
        }
        loadingDialog = LoadingDialog(requireContext())
        // Inflate the layout for this fragment
        binding = FragmentEmrBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[PatientProfileViewModel::class.java]
        doctorProfileViewModel =
            ViewModelProvider(this).get(DoctorProfileUpdateViewModel::class.java)
        attachmentEmrAdapter =
            AttachmentEmrAdapter(requireContext(), loadingDialog, this, childFragmentManager)
        initListner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!emrTitleData?.PatientEmrdetails.isNullOrEmpty()) {
            if (!emrTitleData?.PatientEmrdetails?.get(0)?.Description.isNullOrEmpty()) {
                binding.emrSendLayout.rxDescriptionEt.setText(
                    emrTitleData?.PatientEmrdetails?.get(
                        0
                    )?.Description
                )
            }
            binding.emrSendLayout.instructionsEmrEt.setText(emrTitleData?.Instructions)
            binding.emrSendLayout.rxTitleEt.setText(
                emrTitleData?.PatientEmrdetails?.get(0)?.Title ?: ""
            )
        }
        binding.emrSendLayout.etSpecialistEn.setText(DateUtils.getSpecDoctor())
        if (Localization.getLanguageName() == "ar") {
            UserData.getUserNameAR()?.let {
                binding.emrSendLayout.etFullNameEn.setText(it)
            }
        } else {
            UserData.getUserName()?.let {
                binding.emrSendLayout.etFullNameEn.setText(it)
            }
        }
        sharedDataViewModel.patientProfile.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.emrSendLayout.etFullNamePatient.setText(it.name)
            binding.emrSendLayout.etNumberPatient.setText(it.phone)
            binding.emrSendLayout.etAddressClinic.setText(it.clinicAddress)
            binding.emrSendLayout.etNumberClinic.setText(it.clinicPhone.toString()?.removePrefix("[")?.removeSuffix("]"))
            binding.emrSendLayout.etNameClinic.setText(it.clinicName)
        })

        mContext = binding.root.context
        cameraRequestCode = RequestCode.CAMERA_REQUEST_CODE
        galleryRequestCode = RequestCode.MEDIA_REQUEST_CODE
        editGalleryRequestCode = RequestCode.MEDIA_EDIT_REQUEST_CODE
        binding.emrSendLayout.attachmentRv.layoutManager = LinearLayoutManager(requireContext())
        if (patientId.toInt() == 0) {
            binding.emrSendLayout.attachmentRv.visibility=View.VISIBLE
            documentAdapter = ErmDocumentAdatper(emrTitleData!!.PatientEmrdocuments)
            binding.emrSendLayout.attachmentRv.adapter = documentAdapter
        } else {
            initialEmrObserve()
        }
    }

    private fun initListner() {


        // show attachment recyclerview
//        binding.emrSendLayout.attachmentRv.adapter = attachmentEmrAdapter




        binding.confirmBtn.setOnClickListener {
            if (binding.emrSendLayout.rxTitleEt.text.toString().isNotEmpty()
                || binding.emrSendLayout.rxTitleEt.text.toString().isNotEmpty()
                || binding.emrSendLayout.rxDescriptionEt.text.toString().isNotEmpty()
            ) {
                //TODO
                if (isOnline(mContext)) {
                    viewModel.setInstructions(fillEmrInstructionRequest(EmrInstructionModel()))
                    viewModel.setEmrDescription(fillEmrDescriptionRequest(EmrDescriptionModel()))
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }

            } else {
                errorMessage("Please Fill All Field")
            }
        }


        binding.emrSendLayout.resetButton.setOnClickListener {
            binding.emrSendLayout.rxTitleEt.setText("")
            binding.emrSendLayout.rxDescriptionEt.setText("")
        }

        //add image
        binding.emrSendLayout.addDocumentIv.setOnClickListener {
//            CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE)
//                .setFixAspectRatio(false).setMinCropWindowSize(800, 800).start(
//                    requireContext(), this
//                )
            checkMediaPermissions()
//            showDialog(cameraRequestCode, galleryRequestCode)
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.emrSendLayout.ivDelete.setOnClickListener {
            binding.emrSendLayout.ivDelete.visibility = View.GONE
            binding.emrSendLayout.attachmentIv.visibility = View.GONE
        }

    }

    fun fillEmrInstructionRequest(emrInstructionModel: EmrInstructionModel): EmrInstructionModel {

        emrInstructionModel.Instructions = binding.emrSendLayout.instructionsEmrEt.text.toString()
        emrInstructionModel.AppointmentId = appointmentId.toInt()

        return emrInstructionModel
    }

    fun fillEmrDescriptionRequest(emrDescriptionModel: EmrDescriptionModel): EmrDescriptionModel {

        emrDescriptionModel.Title = binding.emrSendLayout.rxTitleEt.text.toString()
        emrDescriptionModel.Description = binding.emrSendLayout.rxDescriptionEt.text.toString()
        emrDescriptionModel.AppointmentId = appointmentId.toInt()
        return emrDescriptionModel
    }

    fun checkMediaPermissions() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.e("TAG", "checkMediaPermissions: ")
//
//            if (ActivityCompat.checkSelfPermission(
//                    requireActivity(),
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ),
//                    RequestCode.MEDIA_REQUEST_CODE
//                )
//
//            } else {
                mediaIntent()

//            }
//
//        } else {
//            Log.e("TAG", "checkMediaPermissions: sdsds")
//            mediaIntent()
//        }

    }

    private fun mediaIntent() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, RequestCode.MEDIA_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mediaIntent()
        } else {
            toast(requireActivity(), getString(R.string.image_per))
        }
    }



    private fun initialEmrObserve() {

        viewModel.emrAttachmentModel.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(R.id.appointmentsFragment)
            }
        }

        viewModel.createAttachmentEmrResponse.observe(viewLifecycleOwner) {

            when (it) {

                is Resource.Success -> {
                    loadingDialog.dismiss()
                    var documentUrl = it.data.Data.DocumentUrl
                    var documentId = it.data.Data.Id
                    val list=ArrayList<AttachmentEmrData>()
                    list.add(it.data.Data)
            //        attachmentEmrAdapter.submitData(list)
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    Log.e(TAG, "onActivityResult: ${it.message}")
                }

                Resource.Loading -> TODO()
                is Resource.SigUpError<*> -> TODO()
            }

        }
//        binding.emrSendLayout.addEmr.setOnClickListener {
//            if (binding.emrSendLayout.rxTitleEt.text.toString().isNotEmpty()
//            ) {
//                if (isOnline(mContext)) {
//                    viewModel.setInstructions(fillEmrInstructionRequest(EmrInstructionModel()))
//                    viewModel.setEmrDescription(fillEmrDescriptionRequest(EmrDescriptionModel()))
//
//                } else {
//                    noConnectivityDialog.noInternetDialogFragment(mContext)
//
//                }
//
//
//                viewModel.descriptionModel.observe(viewLifecycleOwner) {
////                    binding.emrSendLayout.showRxTitleEt.setText(it.Data.Title)
//                    if (it.Success){
//
//                        Snackbar.make(requireView(),"data sent Successfully",Snackbar.LENGTH_SHORT).show()
//                    }
//                    binding.emrSendLayout.instructionsEmrEt.setText(it.Data.Description)
//                }
//            }
//        }
//
        binding.emrSendLayout.addEmr.setOnClickListener{
            sendUpdateData()
        }
    }

    private fun sendUpdateData() {
        checkDataForDoctorProfile()
        doctorProfileViewModel.doctorUpdateProfileResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    loadingDialog.dismiss()
                    Snackbar.make(requireView(),"data sent Successfully",Snackbar.LENGTH_SHORT).show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()

                }
            }

        })

    }

    private fun checkDataForDoctorProfile( ){
        if (appointmentId.toInt()!= 0) {
            partMapImage = partMapImage + mapOf("AppointmentId" to appointmentId.toInt())
        }
        if (!binding.emrSendLayout.instructionsEmrEt.text.toString().isNullOrEmpty()) {
            partMapImage = partMapImage + mapOf("Instructions" to binding.emrSendLayout.instructionsEmrEt.text.toString())
        }
        if (!binding.emrSendLayout.rxTitleEt.text.toString().isNullOrEmpty()) {
            partMapImage = partMapImage + mapOf("MedicationTitle" to binding.emrSendLayout.rxTitleEt.text.toString())
        }
        if (!binding.emrSendLayout.rxDescriptionEt.text.toString().isNullOrEmpty()) {
            partMapImage = partMapImage + mapOf("MedicationDescription" to binding.emrSendLayout.rxDescriptionEt.text.toString())
            partMapImage = partMapImage + mapOf("MedicationTitle" to binding.emrSendLayout.rxDescriptionEt.text.toString())
        }
        imageFile?.let {
            partMapImage = partMapImage + mapOf("files" to it)
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMapImage) {
            val key = item.key
            if (key.contains("files")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }
        doctorProfileViewModel.createPatientEmrAl(builder.build())
    }



}
