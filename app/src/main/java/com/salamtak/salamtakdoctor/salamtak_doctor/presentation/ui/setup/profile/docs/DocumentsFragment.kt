package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.DocumentsLayoutBinding
import com.salamtak.databinding.FragmentDocumentsBinding
import com.salamtak.domain.entities.response.LegalDocumentsData
import com.salamtak.domain.entities.resquest.auth.CreateDoctorDocumentModel
import com.salamtak.domain.entities.resquest.auth.DeleteDocumentModel
import com.salamtak.domain.entities.resquest.auth.Document
import com.salamtak.domain.entities.resquest.auth.DocumentModel
import com.salamtak.salamtakdoctor.adapter.LegalDocumentsAdapter
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs.getDocs.EditDocumentViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.profile.SetupProfileFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.DoctorProfileDocumentViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.io.File
import java.io.FileOutputStream

class DocumentsFragment : BaseFragment<SetupProfileViewModel>(R.layout.fragment_documents), RecyclerViewOnClickListener,
    DialogsListener,LegalDocumentsAdapter.Action{

    private lateinit var binding: FragmentDocumentsBinding
    private lateinit var mContext: Context
    private val sessionManagement: SessionManagement by inject()
    private lateinit var polyViewModel: PolyClinicsViewModel
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var noInternetDialog: NoInternetDialog
    private val STORAGE_PERMISSION_CODE = 26
    override val screenViewModel: SetupProfileViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_VIEW_MODEL))
    private lateinit var adapter: LegalDocumentAdapterNew
    var finalList = mutableListOf<LegalDocumentsData>()
    private var partMapImage: Map<String, Any> = mutableMapOf()
    private lateinit var doctorDoctorDocumentViewModel: DoctorProfileDocumentViewModel
    private lateinit var documentsBinding: DocumentsLayoutBinding
    private lateinit var cameraOpening: ActivityResultLauncher<Intent>
    private var itemId = 0
    private var imageFileId = 0
    private var imagePosition = 0
    private var documentOrder = 0
   // private lateinit var imageFile: File
    private var isFilled = false
    var isFinal = false
    var polyType=0
    private var documentId = 0
    var editLabel: String = "new"
    private lateinit var viewModel: EditDocumentViewModel
    private val TAG = DocumentsFragment::class.java.simpleName
    lateinit var imageType: ImageTypeEnum
    var counter: Int = 0
    var secCounter: Int = 0
    var thirdCounter: Int = 0
    val nationalIdSet = mutableSetOf<Int>()
    var syndicateIdIsEntered = false
    var medicalLicenseIsEntered = false

    private lateinit var documentsAdapter: LegalDocumentsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {

            editLabel = it.getString("edit", "new")
          //  editLabel = SetupProfileFragment.documentEdit
        }
        editLabel = SetupProfileFragment.documentEdit
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_documents, container, false)
        viewModel = ViewModelProvider(this)[EditDocumentViewModel::class.java]
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        doctorDoctorDocumentViewModel = ViewModelProvider(this).get(DoctorProfileDocumentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layout.setOnClickListener {
            return@setOnClickListener
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                broadcastReceiver,
                IntentFilter("legalDocumentFragment")
            )
        mContext = binding.root.context

        noInternetDialog = NoInternetDialog(mContext, this)
        loadingDialog = LoadingDialog(mContext)
      //  adapter = LegalDocumentAdapterNew(this, childFragmentManager, editLabel)
        documentsRecylerview()
        setUi()
        if (editLabel.equals("edit")) {
            initEdit()
                if (DateUtils.managesDoctors()){
                    binding.profileBottomToolbar.submitBtn.visibility=View.GONE
                }
          //  binding.profileBottomToolbar.submitBtn.text = "Save"
            binding.profileBottomToolbar.root.visibility= GONE
            binding.profileBottomToolbar.submitBtn.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.new_themeBlue_color
                )
            )
        }

        cameraRequestCode = RequestCode.CAMERA_REQUEST_CODE
        galleryRequestCode = RequestCode.MEDIA_REQUEST_CODE
        editGalleryRequestCode = RequestCode.MEDIA_EDIT_REQUEST_CODE

        //getDocuments()
        subscribeToObserver()

    }
    private fun documentsRecylerview() {
        documentsAdapter = LegalDocumentsAdapter(this)
        binding.docsItemsRv.apply {
            adapter = documentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initEdit() {

        getDocuments()
    }

    override fun onImageDelete(position: Int, binding: DocumentsLayoutBinding) {

        //TODO 29/11/22
        if (isOnline(mContext)) {
            if (documentId != 0) {
                screenViewModel.deleteDocument(
                    DeleteDocumentModel(
                        "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
                        Document(
                            documentId.toString()
                        )
                    )
                )

                documentsBinding = binding

                screenViewModel.deleteDocumentResponse.observe(viewLifecycleOwner) {
                    if (it.Success) {
                    }
                }

            }
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }


    }


    override fun onGetImageDelete(
        position: Int,
        binding: DocumentsLayoutBinding,
        documentsId: Int,
        typeId: Int,
        imageType: ImageTypeEnum
    ) {
        //TODO 29/11/22
        if (isOnline(mContext)) {
            if (documentsId != 0) {
                screenViewModel.deleteDocument(
                    DeleteDocumentModel(
                        "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
                        Document(
                            documentsId.toString()
                        )
                    )
                )


                if (typeId == 1) {
                    counter--
                    Log.d(TAG, "subrre: " + counter)
                }

                if (typeId == 2) {
                    secCounter--
                    Log.d(TAG, "subrsre: " + secCounter)
                }

                if (typeId == 3) {
                    thirdCounter--
                    Log.d(TAG, "subrtre: " + thirdCounter)
                }

                documentsBinding = binding

                screenViewModel.deleteDocumentResponse.observe(viewLifecycleOwner) {
                    if (it.Success) {

                        if (imageType == ImageTypeEnum.BACk) {
                            documentsBinding.backView.ivAddPhoto.visibility = VISIBLE
                            documentsBinding.frontView.ivAddDoc.setImageDrawable(null)
                            documentsBinding.backView.ivAddDoc.setBackgroundDrawable(
                                resources.getDrawable(
                                    R.drawable.otp_border
                                )
                            )

                        } else if (imageType == ImageTypeEnum.FRONT) {
                            documentsBinding.frontView.ivAddPhoto.visibility = VISIBLE
                            documentsBinding.frontView.ivAddDoc.setImageDrawable(null)
                            documentsBinding.frontView.ivAddDoc.setBackgroundDrawable(
                                resources.getDrawable(
                                    R.drawable.otp_border
                                )
                            )
                        }

                    }
                }

            }
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }


    }

    private fun setUi() {
        // handle bottom bar
        if (DateUtils.managesDoctors()){
            binding.profileBottomToolbar.submitBtn.visibility=View.VISIBLE
            binding.profileBottomToolbar.submitBtn.text = getString(R.string.save)
        }else{
            binding.profileBottomToolbar.submitBtn.text = getString(R.string.save)
            binding.profileBottomToolbar.submitBtn.visibility = View.VISIBLE
        }
        binding.profileBottomToolbar.submitBtn.background.setTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.new_themeBlue_color
            )
        )

//        binding.docsItemsRv.layoutManager = LinearLayoutManager(mContext)
//        binding.docsItemsRv.adapter = adapter

        // TODO 02/01/2023
        binding.profileBottomToolbar.submitBtn.setOnClickListener {
            isFinal = true
            if (!DateUtils.managesDoctors()){
               // getDocuments()
                if (nationalIdSet.size == 2 && syndicateIdIsEntered && medicalLicenseIsEntered){
                    findNavController().navigate(R.id.thankYouFragment)
                }else if (nationalIdSet.size != 2){
                    toast(requireContext(), getString(R.string.national_Id_required_photo))
                }
                else if (!syndicateIdIsEntered){
                    toast(requireContext(), getString(R.string.syndicate_Id_required_photo))
                }
                else {
                    toast(requireContext(), getString(R.string.license_required_photo))
                }
            }else{
                if (nationalIdSet.size == 2 && syndicateIdIsEntered && medicalLicenseIsEntered){
                    if (DateUtils.isHospital()) {
                        // Back to the Doctors tab, which reloads the list
                        findNavController().popBackStack(R.id.hospitalDoctorsFragment, false)
                    } else {
                        findNavController().navigate(R.id.clinicsFragment)
                    }
                }else if (nationalIdSet.size != 2){
                    toast(requireContext(), getString(R.string.national_Id_required_photo))
                }
                else if (!syndicateIdIsEntered){
                    toast(requireContext(), getString(R.string.syndicate_Id_required_photo))
                }
                else {
                    toast(requireContext(), getString(R.string.license_required_photo))
                }
            }



        }

    }

    private fun subscribeToObserver() {
        //TODO 29/11/22
        if (isOnline(mContext)) {
           // loadingDialog.show()
            if (!DateUtils.managesDoctors()){
                callGetDoctorLegelDocuments()
            }else{
                callGetPolyLegelDocuments()
            }

        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        screenViewModel.legalDocumentsItemsResponse.observe(viewLifecycleOwner) {

            Log.e(TAG, "subscribeToObserver: $it")

            if (it.Success) {
                loadingDialog.dismiss()
                finalList.clear()

                it.Data.forEachIndexed { index, legalDocumentsData ->

                    finalList.add(legalDocumentsData)
                }

                adapter.submitData(finalList.toCollection(mutableListOf()) as MutableList<LegalDocumentsData>)

                // adapter.submitData(modifiyDocumentList as ArrayList<ModifiyDocumentModel>)

            } else {
                loadingDialog.dismiss()
                Log.e(TAG, "subscribeToObserver: ${it.Message}")
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mediaIntent()
//        } else {
//            toast(requireActivity(), getString(R.string.image_per))
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        if (resultCode == Activity.RESULT_OK) {
//
//            if (requestCode == RequestCode.MEDIA_REQUEST_CODE) {
//                val selectedMediaUri: Uri = data!!.data!!
//
//                       val file =uriToFile(selectedMediaUri,requireContext())
//                        file?.let {
//                            val fileSizeMB = it.length() / (1024.0 * 1024.0)
//
//
//                if (fileSizeMB <= 5.0) {
//                    imageFile = it
//
//                    Log.e(TAG, "onActivityResult: $imageFile")
//
//                    isFilled = true
//
//                    if (imageType == ImageTypeEnum.BACk) {
//                        documentsBinding.backView.ivAddPhoto.visibility = GONE
//                        documentsBinding.backView.editFrontIv.visibility = VISIBLE
//
//                    } else if (imageType == ImageTypeEnum.FRONT) {
//                        documentsBinding.frontView.ivAddPhoto.visibility = GONE
//                        documentsBinding.frontView.editFrontIv.visibility = VISIBLE
//
//                    }
//                    Log.e(TAG, "onActivityResult:ItemId $itemId ")
//
//
//                } else {
//                    showError(getString(R.string.large_size_error))
//                }
//                        }
//            } else if (requestCode == cameraRequestCode) {
//                if (data != null) {
//                    val bitmap = data.extras!!.get("data") as Bitmap
//
//                    imageFile = getFileFromBitmap(bitmap, requireActivity())
//                    isFilled = true
//                    if (imageType == ImageTypeEnum.BACk) {
//                        documentsBinding.backView.ivAddPhoto.visibility = GONE
//                        documentsBinding.backView.editFrontIv.visibility = VISIBLE
//
//                    } else if (imageType == ImageTypeEnum.FRONT) {
//                        documentsBinding.frontView.ivAddPhoto.visibility = GONE
//                        documentsBinding.frontView.editFrontIv.visibility = VISIBLE
//
//                    }
//                }
//            }
//            if (imageFileId != 0) {
//                if (isOnline(mContext)) {
//                    loadingDialog.show()
//
//                    screenViewModel.updateDoctorDocument(
//                        UpdateDoctorDocumentResponse(
//                            "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
//                            UpdateDocumentModel(
//                                imageFileId, imageFile
//                            )
//                        )
//                    )
//                } else {
//                    noConnectivityDialog.noInternetDialogFragment(requireContext())
//                }
//            } else {
//                if (isOnline(mContext)) {
//                    loadingDialog.show()
//                  if (!DateUtils.managesDoctors()){
//                      polyType+=1
//                        screenViewModel.createDoctorDocument(
//                            CreateDoctorDocumentModel(
//                                "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
//                                DocumentModel(
//                                    itemId, documentOrder, imageFile
//                                )
//
//                            )
//                        )
//                    }else{
//                        polyType+=1
//                        screenViewModel.createPolyClinicDoctorDocument(
//                            CreateDoctorDocumentModel(
//                                "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
//                                DocumentModel(
//                                    itemId, documentOrder, imageFile
//                                )
//
//                            ),DateUtils.getDoctorId().toString()
//                        )
//                    }
//
//                } else {
//                    noConnectivityDialog.noInternetDialogFragment(requireContext())
//                }
//            }
//
//
//
//
//            if (itemId == 1) {
//                counter++
//            } else if (itemId == 2) {
//                secCounter++
//            } else if (itemId == 3) {
//                thirdCounter++
//            }
//
//
//            screenViewModel.updateDocumentResponse.observe(viewLifecycleOwner) {
//                when (it) {
//                    is Resource.Success -> {
//                        Log.e(TAG, "onActivityResult: success")
//                        loadingDialog.dismiss()
//                        if(it.data.MessageCode == 200){
//                            documentId = it.data.Data.Id
//                            toast(mContext, it.data.Message)
//                            loadingDialog.dismiss()
//                            imageFileId = 0
//                            getDocuments()
//                            isFilled = true
//                        }else if(it.data.MessageCode == 201){
//                            val sucess = UpdateDialog(findNavController(), "")
//                            sucess.show(childFragmentManager, "0")
//                        }
//
//                    }
//                    is Resource.Error -> {
//                        loadingDialog.dismiss()
//                        Log.e(TAG, "onActivityResult: ${it.message}")
//
//                    }
//                }
//            }
//
//
//            screenViewModel.createDocumentResponse.observe(viewLifecycleOwner) {
//
//                when (it) {
//                    is Resource.Success -> {
//                        Log.e(TAG, "onActivityResult: success")
//                        Log.d(TAG, "onActivityResult: ${it.data}")
//                        if(it.data.MessageCode == 200){
//                            getDocuments()
//                            if(it.data.Data != null){
//                                documentId = it.data.Data?.LegalDocumentTypeId!!
//                            }
//                        }else if(it.data.MessageCode == 201){
//                            val sucess = UpdateDialog(findNavController(), "more")
//                            sucess.show(childFragmentManager, "0")
//                        }
//
//
////                        toast(mContext, it.data.Message)
//
//                        isFilled = true
//                        loadingDialog.dismiss()
//
//                    }
//
//                    is Resource.Error -> {
//                        loadingDialog.dismiss()
//                        Log.e(TAG, "onActivityResult: ${it.message}")
//                    }
//                }
//
//            }
//        }
//
//    }


    private fun getDocuments() {
        if (isOnline(mContext)) {
           // loadingDialog.show()
            if (!DateUtils.managesDoctors()) {
               // screenViewModel.getLegalDocumentsItems()

                callGetDoctorLegelDocuments()
            }else{
                callGetPolyLegelDocuments()
            }
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }


//        screenViewModel.legalDocumentsItemsResponse.observe(viewLifecycleOwner) {
//
//            if (isFinal) {
//                if (it.Data.isNotEmpty()) {
//
//                    Log.e("response","${it}")
//
//                    var success = true
//                    for (item in it.Data) {
//                        if (item.URLs.size < item.RequiredImages) {
//                            success = false
//                        }
//                    }
//
//                    if (success) {
//
////                        if (binding.profileBottomToolbar.submitBtn.text.toString()
////                                .toLowerCase() == "save") {
////                            if (!DateUtils.managesDoctors()){
////                                findNavController().navigate(R.id.thankYouFragment)
////                            }else{
////                                findNavController().navigate(R.id.clinicsFragment)
////                            }
////
////                        } else {
////                            if (!DateUtils.managesDoctors()){
////                                findNavController().navigate(R.id.thankYouFragment)
////                            }else{
////                                findNavController().navigate(R.id.clinicsFragment)
////                            }
////                        }
//                    } else {
//                     //  toast(requireContext(), "Please fill data properly")
//                    }
//                } else {
//                    isFinal = false
//                //    toast(requireContext(), "Please fill data properly")
//                }
//            }
//
//                         // finalList.clear()
//            it.Data.forEachIndexed { index, legalDocumentsData ->
//
//                finalList.add(legalDocumentsData)
//
//            }
//
//            adapter.legalDocumentItemsList =
//                finalList.toCollection(ArrayList()) as MutableList<LegalDocumentsData>
//
////            }
//
//            loadingDialog.dismiss()
//
//        }

    }

    private fun callGetDoctorLegelDocuments() {
        doctorDoctorDocumentViewModel.getDoctorDocumentsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    loadingDialog.dismiss()
                    documentsAdapter.submitList(it.data?.Data)
                    documentsAdapter.currentList
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    loadingDialog.dismiss()
                }

            }
        })
       doctorDoctorDocumentViewModel.getDoctorLegalDocuments2()
    }


    private fun callGetPolyLegelDocuments() {
        polyViewModel.getLegelDocumentsPolyclinicResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    loadingDialog.dismiss()

//                    finalList.clear()
//
//                    it.data?.Data?.forEachIndexed { index, legalDocumentsData ->
//
//                        finalList.add(legalDocumentsData)
//                    }
//
//                    adapter.submitData(finalList.toCollection(mutableListOf()) as MutableList<LegalDocumentsData>)
//                    adapter.notifyDataSetChanged()
                    documentsAdapter.submitList(it.data?.Data)

                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    loadingDialog.dismiss()
                }

            }
        })
        DateUtils.getDoctorId()?.toInt()?.let { polyViewModel.getDoctorDocumentsPolyClinic(it) }
    }

    //TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isFinal = true
            getDocuments()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }


    fun uriToFile(uri: Uri, context: Context): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = getFileName(uri, context) ?: "temp_file"
        val tempFile = File(context.cacheDir, fileName)

        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    fun getFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }
    fun sendData(){
        if (imageFileId != 0) {
            if (isOnline(mContext)) {
                checkDataForUpdateDoctorDocuments(imageFileId)
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
            }
        } else {
            if (isOnline(mContext)) {
                loadingDialog.show()
                if (!DateUtils.managesDoctors()){
                    checkDataForDoctorDocuments(itemId,documentOrder,0)
                }else{
                    screenViewModel.createPolyClinicDoctorDocument(
                        CreateDoctorDocumentModel(
                            "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
                            DocumentModel(
                                itemId, documentOrder, imageFile
                            )

                        ),DateUtils.getDoctorId().toString()
                    )

                    checkDataForDoctorDocuments(itemId,documentOrder,DateUtils.getDoctorId().toString().toInt())
                }

            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
            }
        }

        if (imageFileId != 0) {
            sendUpdateDocumentsData()
        }else{
            sendDocumentsData()
        }

    }

    private fun sendDocumentsData() {

        doctorDoctorDocumentViewModel.doctorDocumentsResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    if(it.data?.MessageCode == 200){
                        polyType+=1
                        getDocuments()
                        if(it.data.Data != null){
                            documentId = it.data.Data?.LegalDocumentTypeId!!
                            if (it.data.Data?.LegalDocumentTypeId == 1){
                                nationalIdSet.add(it.data.Data?.Id!!)
                            }
                            else if(it.data.Data?.LegalDocumentTypeId == 2){
                                syndicateIdIsEntered = true
                            }
                            else if(it.data.Data?.LegalDocumentTypeId == 3){
                                medicalLicenseIsEntered = true
                            }
                        }
                    }else if(it.data?.MessageCode == 201){
                        val sucess = UpdateDialog(findNavController(), "more")
                        sucess.show(childFragmentManager, "0")
                    }
                    isFilled = true
                    loadingDialog.dismiss()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
            }
        })
    }
    private fun sendUpdateDocumentsData() {
        doctorDoctorDocumentViewModel.updatedoctorDocumentsResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {

                    if(it.data?.MessageCode == 200){
                        callGetDoctorLegelDocuments()
                        documentId = it.data.Data?.Id!!
                        toast(mContext, it.data.Message)
                        imageFileId = 0
                        isFilled = true
                    }else if(it.data?.MessageCode == 201){
                        val sucess = UpdateDialog(findNavController(), "")
                        sucess.show(childFragmentManager, "0")
                    }
                    loadingDialog.dismiss()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
            }
        })
    }

    private fun checkDataForDoctorDocuments(itemId:Int, documentOrder:Int,doctorId:Int) {

        DateUtils.getDoctorId()?.toInt()?.let {
            partMapImage = partMapImage + mapOf("DoctorID" to it)
        }
            partMapImage = partMapImage + mapOf("LegalDocumentTypeId" to itemId)


            partMapImage = partMapImage + mapOf("DocumentOrder" to documentOrder)

        imageFile?.let {
            partMapImage = partMapImage + mapOf("document" to it)
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMapImage) {
            val key = item.key
            if (key.contains("document")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

            doctorDoctorDocumentViewModel.createDoctorDocuments(builder.build(), doctorId)
    }

    private fun checkDataForUpdateDoctorDocuments(imageFileId:Int) {

        partMapImage = partMapImage + mapOf("Id" to imageFileId)

        imageFile?.let {
            partMapImage = partMapImage + mapOf("documentUrl" to it)
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMapImage) {
            val key = item.key
            if (key.contains("documentUrl")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

            doctorDoctorDocumentViewModel.updateDoctorDocumentS(builder.build())

        sendUpdateDocumentsData()
    }

     override fun onDestroy() {
        super.onDestroy()
    }

    override fun onItemClick(documentOrder: Int,legalDocumentTypeId:Int) {
        Log.e(TAG, "onItemClick:$legalDocumentTypeId ", )
        itemId=legalDocumentTypeId
        this.documentOrder=documentOrder
        selectPic()

    }

    override fun onItemClickUpdateDocuments(id: Int) {
        this.imageFileId=id
        selectPic()
    }
    override fun onImageSelected(file: File) {
        sendData()
    }
}
