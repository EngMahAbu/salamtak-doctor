package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentPatientProfileBinding
import com.salamtak.domain.entities.response.EmrTitleData
import com.salamtak.domain.entities.resquest.auth.*
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.addEmr.AttachmentEmrAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.getEmrPatient.EmrPatientAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.patientAccount.PatientAccountFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.showImage.ShowImageActivity
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import org.koin.android.ext.android.inject
import java.io.File

//import kotlinx.android.synthetic.main.emr_patient_layout.*

class PatientProfileFragment : Fragment(), View.OnClickListener,
    EmrPatientAdapter.OnEmrClickListener, RecyclerViewOnClickListener, DialogsListener,
    EmrPatientAdapter.EMRPatientDocuments {

    private lateinit var binding: FragmentPatientProfileBinding
    private lateinit var viewModel: PatientProfileViewModel
    lateinit var loading: Dialog
    var appointmentId: Long = -1
    var patientId: Long = -1
    private lateinit var adapter: EmrPatientAdapter
    var isCollapsed = false
    private lateinit var imageFile: File
    var isFilled = false
    private lateinit var loadingDialog: LoadingDialog
    private  var emrTitleData: EmrTitleData?=null
    private val sessionManagement: SessionManagement by inject()
    private lateinit var attachmentEmrAdapter: AttachmentEmrAdapter
    private lateinit var mContext: Context
    private lateinit var noInternetDialog: NoInternetDialog
    private var accountFragment = PatientAccountFragment()
    var documentPosition = -1

    var clinicName: String = ""
    var clinicType: String = ""
    var clinicDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            appointmentId = it.getLong(APPOINTMENT_ID, 0)
            patientId = it.getLong(PATIENT_ID, 0)
            clinicName = it.getString(CLINIC_NAME, "")
            clinicType = it.getString(CLINIC_TYPE, "")
            clinicDate = it.getString(CLINIC_DATE, "")
        }

        Log.d("getData", "onCreateView: $patientId")

        loading = Dialog(requireContext())
        loadingDialog = LoadingDialog(requireContext())
        binding = FragmentPatientProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[PatientProfileViewModel::class.java]

        attachmentEmrAdapter =
            AttachmentEmrAdapter(requireContext(), loadingDialog, this, childFragmentManager)

        adapter = EmrPatientAdapter(listOf(),
            this,
            requireContext(),
            listener,
            this,
            this)
        binding.patientEmrLayout.emrRecyclerView.adapter = adapter

        initListner()
        initObserve()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)

//        cameraRequestCode = RequestCode.CAMERA_REQUEST_CODE
//        galleryRequestCode = RequestCode.MEDIA_REQUEST_CODE
//        editGalleryRequestCode = RequestCode.MEDIA_EDIT_REQUEST_CODE

        binding.emrAccountRb.setOnCheckedChangeListener { compoundButton, b ->
            binding.patientProfileLayout.root.visibility = View.GONE
            binding.patientEmrLayout.root.visibility = View.GONE
//            binding.emrSendLayout.root.visibility = View.GONE
            binding.mainFrameLayout.visibility = View.VISIBLE
            replaceFragment(requireActivity(), accountFragment, patientId)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.appointmentsFragment)
            }
        }
        initButton()

    }
    private fun initButton(){
        binding.emrSyemtomsRb.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(APPOINTMENT_ID, appointmentId)
            bundle.putLong(PATIENT_ID, patientId)
            findNavController().navigate(R.id.patientSyemtomsFragment, bundle)
        }
    }

    private val listener = object : RecyclerViewOnClickListener {
        override fun onImageEmrClicked(position: Int, image: String) {

            val intent = Intent(requireContext(), ShowImageActivity::class.java)
            intent.putExtra("Image", image)
            startActivity(intent)

//            val bundle = Bundle()
//            bundle.putString("Image", image)
//            findNavController().navigate(R.id.showImageFragment, bundle)
        }
    }

    fun replaceFragment(activity: FragmentActivity, fragment: Fragment, patientId: Long) {
        try {
            val transition: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            transition.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            val arguments = Bundle()
            arguments.putLong(PATIENT_ID, patientId)
            fragment.arguments = arguments
            transition.replace(R.id.mainFrameLayout, fragment)
            transition.commitAllowingStateLoss()
            transition.commit()
            activity.supportFragmentManager.executePendingTransactions()
        } catch (e: Exception) {
            Log.e("TAG", "changeFragment: ${e.message}")
        }
    }

    private fun initObserve() {

        mContext = requireContext()
        if (isOnline(mContext)) {
            viewModel.getPatientProfile(appointmentId.toInt())
            viewModel.getPatientMedical(patientId.toInt())

        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        viewModel.patientProfileModel.observe(viewLifecycleOwner, Observer {

            it?.let {
             //   ImageUtil.loadPicture(binding.patientProfileLayout.uploadPhotoBg, it.Data.Image)
                binding.patientProfileLayout.patientNameEt.setText(it.Data.PatientName)
                binding.patientProfileLayout.patientAgeEt.setText(it.Data.PatientAge.toString())
                binding.patientProfileLayout.patientNumberEt.setText(it.Data.PatientPhone)
                binding.patientProfileLayout.patientLocationEt.setText(it.Data.PatientAddress)
                binding.patientProfileLayout.patientComplainEt.setText(it.Data.PatientComplain)
                if (it.Data.CityName!=null){
                    binding.patientProfileLayout.citynameEt.text=it.Data.CityName.toString()
                }else{
                    binding.patientProfileLayout.citynameEt.visibility=View.GONE
                }
                if (it.Data.AreaName!=null){
                    binding.patientProfileLayout.areaEt.text=it.Data.AreaName.toString()
                }else{
                    binding.patientProfileLayout.areaEt.visibility=View.GONE
                }

                if (it.Data.BlockNo!=null){
                    binding.patientProfileLayout.BlockNoEt.text=it.Data.BlockNo.toString()
                }else{
                    binding.patientProfileLayout.BlockNoEt.visibility=View.GONE
                }
                if (it.Data.FloorNo!=null){
                    binding.patientProfileLayout.FloorNoEt.text=it.Data.FloorNo.toString()
                }else{
                    binding.patientProfileLayout.FloorNoEt.visibility=View.GONE
                }


            }
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        viewModel.updateMedicalModel.observe(viewLifecycleOwner, Observer {
            if (it.Data != null) {
                if (!it.Data.BloodName.isNullOrEmpty()){
                    binding.patientProfileLayout.patientBloodType.text=it.Data.BloodName
                }else{
                    binding.patientProfileLayout.patientBloodType.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientBloodType.visibility=View.GONE
                }
                if (it.Data.Height!=null){
                    binding.patientProfileLayout.patientHight.text=it.Data.Height.toString()
                }else{
                    binding.patientProfileLayout.patientHight.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientHight.visibility=View.GONE
                }
                if (it.Data.Weight!=null){
                    binding.patientProfileLayout.patientWeight.text=it.Data.Weight.toString()
                }else{
                    binding.patientProfileLayout.patientWeight.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientWeight.visibility=View.GONE
                }


                if (!it.Data.SugarLevel.isNullOrBlank()){
                    binding.patientProfileLayout.patientSugarLevel.text=it.Data.SugarLevel
                    binding.patientProfileLayout.patientSugarLevel.visibility=View.VISIBLE
                    binding.patientProfileLayout.tvPatientSugarLevel.visibility=View.VISIBLE
                }else{
                    binding.patientProfileLayout.patientSugarLevel.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientSugarLevel.visibility=View.GONE
                }
                if (!it.Data.Pressure.isNullOrBlank()){
                    binding.patientProfileLayout.patientPressure.text=it.Data.Pressure
                    binding.patientProfileLayout.patientPressure.visibility=View.VISIBLE
                    binding.patientProfileLayout.tvPatientPressure.visibility=View.VISIBLE
                }else{
                    binding.patientProfileLayout.patientPressure.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientPressure.visibility=View.GONE
                }
                if (!it.Data.OtherAllergies.isNullOrEmpty()){
                    binding.patientProfileLayout.patientOtherAllergies.text=it.Data.OtherAllergies
                }else{
                    binding.patientProfileLayout.patientOtherAllergies.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientOtherAllergies.visibility=View.GONE
                }
                if (!it.Data.Prescriptions.isNullOrEmpty()){
                    binding.patientProfileLayout.patientPrescriptions.text=it.Data.Prescriptions
                }else{
                    binding.patientProfileLayout.patientPrescriptions.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientPrescriptions.visibility=View.GONE
                }
                if (!it.Data.ChronicDiseases.isNullOrEmpty()){
                    binding.patientProfileLayout.patientChronicDiseases.text=it.Data.ChronicDiseases
                }else{
                    binding.patientProfileLayout.patientChronicDiseases.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientChronicDiseases.visibility=View.GONE
                }
                if (!it.Data.CurrentMedication.isNullOrEmpty()){
                    binding.patientProfileLayout.patientCurrentMedication.text=it.Data.CurrentMedication
                }else{
                    binding.patientProfileLayout.patientCurrentMedication.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientCurrentMedication.visibility=View.GONE
                }
                if (!it.Data.Iinjuries.isNullOrEmpty()){
                    binding.patientProfileLayout.patientIinjuries.text=it.Data.Iinjuries
                }else{
                    binding.patientProfileLayout.patientIinjuries.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientIinjuries.visibility=View.GONE
                }
                if (!it.Data.Surgeries.isNullOrEmpty()){
                    binding.patientProfileLayout.patientSurgeries.text=it.Data.Surgeries
                }else{
                    binding.patientProfileLayout.patientSurgeries.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientSurgeries.visibility=View.GONE
                }
                if (!it.Data.PatientFoodAllergiesName.isNullOrEmpty()){
                    binding.patientProfileLayout.patientFoodAllergiesName.text=it.Data.PatientFoodAllergiesName.first().toString()
                }else{
                    binding.patientProfileLayout.patientFoodAllergiesName.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientFoodAllergiesName.visibility=View.GONE
                }
                if (!it.Data.PatientMedicineAllergiesName.isNullOrEmpty()){
                    binding.patientProfileLayout.patientMedicineAllergiesName.text=it.Data.PatientMedicineAllergiesName.first().toString()
                }else{
                    binding.patientProfileLayout.patientMedicineAllergiesName.visibility=View.GONE
                    binding.patientProfileLayout.tvPatientMedicineAllergiesName.visibility=View.GONE
                }
                binding.patientProfileLayout.foodAllergiesEt.setText(it.Data.PatientFoodAllergiesName.toString())
                if (!it.Data.OtherAllergies.isNullOrEmpty()) {
                    binding.patientProfileLayout.otherAllergiesEt.setText(it.Data.OtherAllergies.toString())
                }else{
                    binding.patientProfileLayout.otherAllergiesEt.visibility=View.GONE
                }
                if (!it.Data.CurrentMedication.isNullOrEmpty()) {
                    binding.patientProfileLayout.currentMedicationEt.setText(it.Data.CurrentMedication.toString())
                }else{
                    binding.patientProfileLayout.currentMedicationEt.visibility=View.GONE
                }
                if (!it.Data.PastMedication.isNullOrEmpty()) {
                    binding.patientProfileLayout.pastMedicationEt.setText(it.Data.PastMedication.toString())
                }else{
                    binding.patientProfileLayout.pastMedicationEt.visibility=View.GONE
                }
                if (!it.Data.ChronicDiseases.isNullOrEmpty()) {
                    binding.patientProfileLayout.chronicDiseasesEt.setText(it.Data.ChronicDiseases.toString())
                }else{
                    binding.patientProfileLayout.chronicDiseasesEt.visibility=View.GONE
                }
                if (!it.Data.Iinjuries.isNullOrEmpty()) {
                    binding.patientProfileLayout.injuriesEt.setText(it.Data.Iinjuries.toString())
                }else{
                    binding.patientProfileLayout.injuriesEt.visibility=View.GONE
                }
                if (!it.Data.Surgeries.isNullOrEmpty()) {
                    binding.patientProfileLayout.surgeriesEt.setText(it.Data.Surgeries.toString())
                }else{
                    binding.patientProfileLayout.surgeriesEt.visibility=View.GONE
                }
            }
        })
    }

    private fun initListner() {
        binding.patientProfileRb.setOnClickListener(this)
        binding.emrProfileRb.setOnClickListener(this)
        binding.patientEmrLayout.tvAddEmr.setOnClickListener(this)
        binding.toolbar.toolbarHeader.text = getString(R.string.patient_profile)

        // show attachment recyclerview
//        binding.emrSendLayout.attachmentRv.layoutManager = LinearLayoutManager(requireContext())
//        binding.emrSendLayout.attachmentRv.adapter = attachmentEmrAdapter
//
//        binding.emrSendLayout.clinicNameTv.setText(clinicName)
//        binding.emrSendLayout.emrTypeTv.setText(clinicType)
//        binding.emrSendLayout.dateTv.setText(clinicDate)
//

//
//        binding.emrSendLayout.confirmBtn.setOnClickListener {
//            if (binding.emrSendLayout.rxTitleEt.text.toString().isNotEmpty()
//                || binding.emrSendLayout.rxTitleEt.text.toString().isNotEmpty()
//                || binding.emrSendLayout.rxDescriptionEt.text.toString().isNotEmpty()
//            ) {
//                //TODO
//                if (isOnline(mContext)) {
//                    viewModel.setInstructions(fillEmrInstructionRequest(EmrInstructionModel()))
//                    viewModel.setEmrDescription(fillEmrDescriptionRequest(EmrDescriptionModel()))
//                } else {
//                    noConnectivityDialog.noInternetDialogFragment(requireContext())
//
//
//                }
//
//            } else {
//                errorMessage("Please Fill All Field")
//            }
//        }

//        binding.confirmBtn.setOnClickListener {
//            viewModel.setInstructions(fillEmrInstructionRequest(EmrInstructionModel()))
////            viewModel.appointmentId.value = appointmentId.toString()
////            viewModel.saveClicked()
//
//        }

//        binding.emrSendLayout.resetButton.setOnClickListener {
//            binding.emrSendLayout.rxTitleEt.setText("")
//            binding.emrSendLayout.rxDescriptionEt.setText("")
//        }

        //add image
        /*binding.emrSendLayout.addDocumentIv.setOnClickListener {
//            CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE)
//                .setFixAspectRatio(false).setMinCropWindowSize(800, 800).start(
//                    requireContext(), this
//                )
            checkMediaPermissions()
//            showDialog(cameraRequestCode, galleryRequestCode)
        }
*/
        binding.ivBack.setOnClickListener {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.appointmentsFragment)
            }
        }

        binding.statusRadio.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {

                R.id.profile_medicalRecord -> {
                    binding.patientProfileLayout.root.visibility = View.VISIBLE
                    binding.patientProfileLayout.layoutProfile.visibility = View.GONE
                    binding.patientProfileLayout.medicalRecord.visibility = View.VISIBLE
                    binding.patientEmrLayout.root.visibility = View.GONE
                    binding.mainFrameLayout.visibility = View.GONE
                }

            }
        }

//        binding.emrSendLayout.ivCollapse.setOnClickListener {
//
//            if (isCollapsed) {
//                animateExpand(binding.emrSendLayout.ivCollapse, requireContext())
//                binding.emrSendLayout.instructionsEmrEt.visibility = View.VISIBLE
//                binding.emrSendLayout.rxTitleEt.visibility = View.VISIBLE
////                binding.emrSendLayout.showRxTitleEt.visibility = View.VISIBLE
//                binding.emrSendLayout.rxDescriptionEt.visibility = View.VISIBLE
//                binding.emrSendLayout.linearBtn.visibility = View.VISIBLE
//                binding.emrSendLayout.instructionsTv.visibility = View.VISIBLE
//                binding.emrSendLayout.addDocumentIv.visibility = View.VISIBLE
//                binding.emrSendLayout.attachmentRv.visibility = View.VISIBLE
//
//                binding.emrSendLayout.pescriptionsTv.visibility = View.VISIBLE
//
//                binding.emrSendLayout.confirmBtn.visibility = View.VISIBLE
//
//            } else {
//                animateCollapse(binding.emrSendLayout.ivCollapse, requireContext())
//                binding.emrSendLayout.instructionsEmrEt.visibility = View.GONE
//                binding.emrSendLayout.rxTitleEt.visibility = View.GONE
//                binding.emrSendLayout.rxDescriptionEt.visibility = View.GONE
//                binding.emrSendLayout.linearBtn.visibility = View.GONE
//                binding.emrSendLayout.instructionsTv.visibility = View.GONE
//
//                binding.emrSendLayout.addDocumentIv.visibility = View.GONE
//                binding.emrSendLayout.attachmentRv.visibility = View.GONE
//                binding.emrSendLayout.attachmentIv.visibility = View.GONE
//                binding.emrSendLayout.ivDelete.visibility = View.GONE
////                binding.emrSendLayout.showRxTitleEt.visibility = View.GONE
//                binding.emrSendLayout.showRxDescriptionEt.visibility = View.GONE
//                binding.emrSendLayout.pescriptionTv.visibility = View.GONE
//                binding.emrSendLayout.pescriptionsTv.visibility = View.GONE
//
//                binding.emrSendLayout.confirmBtn.visibility = View.GONE
//
//            }
//
//        }

//        binding.emrSendLayout.ivDelete.setOnClickListener {
//            binding.emrSendLayout.ivDelete.visibility = View.GONE
//            binding.emrSendLayout.attachmentIv.visibility = View.GONE
//        }

    }

    private fun animateExpand(binding: ImageView, mContext: Context) {
        isCollapsed = false
        val rotate = RotateAnimation(
            360F,
            180F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        binding.setImageDrawable(mContext.getDrawable(R.drawable.ic_up))
    }

    private fun animateCollapse(binding: ImageView, mContext: Context) {
        isCollapsed = true
        val rotate = RotateAnimation(
            180F,
            360F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        binding.setImageDrawable(mContext.getDrawable(R.drawable.ic_down))
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.patientProfile_rb -> {
                binding.patientProfileLayout.root.visibility = View.VISIBLE
                binding.patientProfileLayout.layoutProfile.visibility = View.VISIBLE
                binding.patientProfileLayout.medicalRecord.visibility = View.GONE
                binding.patientEmrLayout.root.visibility = View.GONE
//                binding.emrSendLayout.root.visibility = View.GONE
                binding.mainFrameLayout.visibility = View.GONE
//                binding.confirmBtn.visibility = View.GONE
//                viewModel.getPatientProfile(appointmentId.toInt())
            }
            R.id.emr_profile_rb -> {

                binding.patientProfileLayout.root.visibility = View.GONE
                binding.patientEmrLayout.root.visibility = View.VISIBLE
//                binding.emrSendLayout.root.visibility = View.VISIBLE
                binding.mainFrameLayout.visibility = View.GONE
//                binding.confirmBtn.visibility = View.VISIBLE
                initialEmrObserve()
//                toast(this.requireContext(), "Login successfully!")
            }
            R.id.emr_account_rb -> {
//                binding.patientProfileLayout.root.visibility = View.GONE
//                binding.patientEmrLayout.root.visibility = View.GONE
//                binding.emrSendLayout.root.visibility = View.GONE
//                binding.mainFrameLayout.visibility = View.VISIBLE
//
//                replaceFragment(requireActivity(),accountFragment)
            }
            R.id.tvAddEmr -> {
                val bundle = Bundle()
                bundle.putLong(APPOINTMENT_ID, appointmentId)
                bundle.putLong(PATIENT_ID, patientId)
                findNavController().navigate(R.id.emrFragment, bundle)
            }
            R.id.emr_syemtoms_rb -> {
                val bundle = Bundle()
                bundle.putLong(APPOINTMENT_ID, appointmentId)
                bundle.putLong(PATIENT_ID, patientId)
                findNavController().navigate(R.id.patientSyemtomsFragment, bundle)
            }

        }

    }


    private fun initialEmrObserve() {
        binding.patientEmrLayout.emrRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        viewModel.patientEmrModel.observe(viewLifecycleOwner, Observer {

            // hide add emr if doctor add before
            if (it.Data.isEmpty()) {
//                binding.emrSendLayout.containerPatientLobby.visibility = View.VISIBLE
            } else {
                emrTitleData=it.Data[0]
//            emr_recycler_view.adapter = EmrPatientAdapter(it.Data)
                binding.patientEmrLayout.emrRecyclerView.adapter =
                    EmrPatientAdapter(it.Data, this, requireContext(), listener, this, this)
             //   adapter.setResults(it.Data)
            }
        })

        viewModel.emrAttachmentModel.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(R.id.appointmentsFragment)
            }
        })

        //TODO
        if (isOnline(mContext)) {
            viewModel.getPatientEmr(patientId.toInt())
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())


        }



        viewModel.createAttachmentEmrResponse.observe(viewLifecycleOwner) {

            when (it) {

                is Resource.Success -> {
                    loadingDialog.dismiss()
                    var documentUrl = it.data.Data.DocumentUrl
                    var documentId = it.data.Data.Id
                    adapter.setDocumentData(documentPosition, documentUrl, documentId)

//                    attachmentEmrAdapter.submitData(it.data as ArrayList<AttachmentEmrData>)
//                    toast(mContext, it.data.Message)
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    Log.e(TAG, "onActivityResult: ${it.message}")
                }

                Resource.Loading -> TODO()
                is Resource.SigUpError<*> -> TODO()
            }

        }

//        viewModel.deleteGalleyResponse.observe(viewLifecycleOwner) {
//            if (it.Success) {
//                loadingDialog.dismiss()
//                clinicGalleryAdapter.deleteAsset(assetPosition)
//            }
//        }

    }

    override fun onEmrClicked(emrTitleData: EmrTitleData) {
        val bundle = Bundle()
        bundle.putString("emr_data", Gson().toJson(emrTitleData))
        bundle.putString("Instructions", emrTitleData.Instructions);
        findNavController().navigate(R.id.emrFragment, bundle)
    }

    fun fillEmrDescriptionRequest(emrDescriptionModel: EmrDescriptionModel): EmrDescriptionModel {

//        emrDescriptionModel.Title = binding.emrSendLayout.rxTitleEt.text.toString()
//        emrDescriptionModel.Title = "Title"
//        emrDescriptionModel.Description = binding.emrSendLayout.rxDescriptionEt.text.toString()
        emrDescriptionModel.AppointmentId = appointmentId.toInt()

        return emrDescriptionModel
    }


    fun fillEmrInstructionRequest(emrInstructionModel: EmrInstructionModel): EmrInstructionModel {

//        emrInstructionModel.Instructions = binding.emrSendLayout.instructionsEmrEt.text.toString()
        emrInstructionModel.AppointmentId = appointmentId.toInt()

        return emrInstructionModel
    }


    fun checkMediaPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                    RequestCode.MEDIA_REQUEST_CODE
                )

            } else {
                mediaIntent()

            }

        } else {
            Log.e("TAG", "checkMediaPermissions: sdsds")
            mediaIntent()
        }

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


    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    override fun onEmrPatientDocuments(position: Int) {
        documentPosition = position
        checkMediaPermissions()
    }

    override fun onResume() {
        binding.patientProfileRb.isChecked = true
        super.onResume()
    }

}