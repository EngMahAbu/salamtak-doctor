package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.certificates


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentCertificatesBinding
import com.salamtak.databinding.ItemCertificateBinding
import com.salamtak.databinding.NewItemCertificateTableBinding
import com.salamtak.domain.entities.response.CertificatesData
import com.salamtak.domain.entities.resquest.auth.Certificate
import com.salamtak.domain.entities.resquest.auth.CreateCertificateModel
import com.salamtak.domain.entities.resquest.auth.DeleteCertificateModel
import com.salamtak.domain.entities.resquest.auth.EditCertificateModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.more.MoreFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.profile.SetupProfileFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RequestCode
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RequestCode.MEDIA_EDIT_REQUEST_CODE
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.Resource
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.UpdateDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isArabicValid
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isEnglishValid
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale


class CertificatesFragment :
    com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment<SetupProfileViewModel>(R.layout.fragment_certificates),
    DialogsListener,
    RecyclerViewOnClickListener, NewCertificatesAdapter.OnImageClickListener,
    NewCertificatesAdapter.OnEditClickListener, NewCertificatesAdapter.OnSaveClickListener,
    YearAdapter.AdapterOnClick {
    private lateinit var polyViewModel: PolyClinicsViewModel
    lateinit var binding: FragmentCertificatesBinding
    private lateinit var mContext: Context
    private val sessionManagement: SessionManagement by inject()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var noInternetDialog: NoInternetDialog
    var selectedPosition = 0
    var selectedYear = ""
    private lateinit var adapter: NewCertificatesAdapter
    override val screenViewModel: SetupProfileViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_VIEW_MODEL))

    //  private var imageFile: File? = null
    private var isFilled = false
    private lateinit var certificateBinding: ItemCertificateBinding
    private val TAG = CertificatesFragment::class.java.simpleName
    var assetPosition = 0
    var editLabel: String = "new"
    var certificateId: Int = 0
    var doctorId: String = ""
    var isFinal = false
    var addCertificateClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        arguments?.let {
            editLabel = it.getString("edit", "new")
        }
        editLabel = SetupProfileFragment.certificatesEdit

        Log.d(TAG, "onCreateView: ${MoreFragment.editClick} $editLabel")

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_certificates, container, false)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        doctorId = sessionManagement.getDoctorId()!!
        Log.d(TAG, "checkValidity1: " + doctorId)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addCertificate.llCertification.visibility = GONE
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                broadcastReceiver,
                IntentFilter("CertificateFragment")
            )
        //   navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        loadingDialog = LoadingDialog(mContext)
        noInternetDialog = NoInternetDialog(mContext, this)
        adapter =
            NewCertificatesAdapter(
                mContext,
                loadingDialog,
                this,
                childFragmentManager,
                this,
                this,
                this
            )
        setUi()
        subscribeToObserver()

        if (editLabel == "edit") {
            initEdit()
        } else {
            binding.profileBottomToolbar.submitBtn.visibility = VISIBLE
            binding.profileBottomToolbar.root.visibility = VISIBLE
        }
        cameraRequestCode = RequestCode.CAMERA_REQUEST_CODE
        galleryRequestCode = RequestCode.MEDIA_REQUEST_CODE
        editGalleryRequestCode = RequestCode.MEDIA_EDIT_REQUEST_CODE
    }

    private fun initEdit() {

        binding.profileBottomToolbar.submitBtn.setText("Save")
        binding.profileBottomToolbar.root.visibility = GONE
        binding.profileBottomToolbar.submitBtn.background.setTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.new_themeBlue_color
            )
        )

    }

    fun setUi() {
        binding.profileBottomToolbar.submitBtn.visibility = View.VISIBLE

        binding.certificatesRv.layoutManager = LinearLayoutManager(mContext)
        binding.certificatesRv.adapter = adapter

        binding.addCertificate.ivCertification.setOnClickListener {
            if (binding.addCertificate.llCertification.visibility == View.VISIBLE) {
                binding.addCertificate.llCertification.visibility = GONE
            } else {
                binding.addCertificate.llCertification.visibility = VISIBLE
            }
        }

        binding.addCertificate.cardCertificate.setOnClickListener {

            addCertificateClicked = true
            selectPic()

        }


        binding.addCertificate.cardCalender.setOnClickListener {
            val y = mutableListOf<String>()
            val nf: NumberFormat = NumberFormat.getInstance(Locale.getDefault())
            val yearList = mContext.resources.getStringArray(R.array.year_list)
            yearList.forEach {
                y.add(nf.parse(it).toString())
            }

            val arrayAdapter = ArrayAdapter(mContext, R.layout.drop_down_item, yearList)
//                binding.cardCalender.setAdapter(arrayAdapter)

            showYearBottomSheet(mContext, y)

        }
        binding.addCertificate.addCertificate.setOnClickListener {
            checkValidationForAddCertificate()

        }


        binding.profileBottomToolbar.submitBtn.setOnClickListener {
            isFinal = true
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                if (adapter.list.isNotEmpty()) {
                    val intent = Intent(".Fragment.SetupProfile")
                    intent.putExtra("stepStatus", "3")
                    requireContext().sendBroadcast(intent)
//                uploadCertificates()
                } else {

                    toast(
                        requireContext(),
                        getString(R.string.add_atleastcertificate_before_continuing)
                    )
                }
            } else {
                val intent = Intent(".Fragment.SetupProfile")
                intent.putExtra("stepStatus", "3")
                requireContext().sendBroadcast(intent)
            }


        }


    }

    fun clearAddCertificateData() {
        binding.addCertificate.tvTittleEn.setText("")
        binding.addCertificate.tvTittleAr.setText("")
        binding.addCertificate.tvDescriptionEn.setText("")
        binding.addCertificate.tvDescriptionAr.setText("")
        imageFile = null
        addCertificateClicked = false
        Glide.with(mContext).load(R.drawable.iv_addimage).into(binding.addCertificate.ivCertificate)
    }

    fun checkValidationForAddCertificate() {
        if (!binding.addCertificate.tvTittleEn.text.isEmpty()
//            && !binding.addCertificate.tvDescriptionEn.text.isEmpty()
            && !binding.addCertificate.tvTittleAr.text.isEmpty()
            && !binding.addCertificate.tvYear.text.isEmpty()
            && imageFile != null &&
            binding.addCertificate.tvYear.text.toString().toInt() > DateUtils.getYear()?.toInt()!!
        ) {
            binding.addCertificate.tvYearError.visibility = View.GONE
            var createCertificateData = CreateCertificateModel(
                binding.addCertificate.tvTittleEn.text.toString(),
                binding.addCertificate.tvDescriptionEn.text.toString(),
                binding.addCertificate.tvTittleAr.text.toString(),
                binding.addCertificate.tvDescriptionAr.text.toString(),
                binding.addCertificate.tvYear.text.toString().toInt(),
                ensureValidImageFormatAndSize(imageFile!!)
            )

            if (isOnline(mContext)) {
                loadingDialog.show()
                if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                    screenViewModel.createDoctorCertificate(
                        createCertificateData
                    )
                } else {
                    screenViewModel.createDoctorPolyClinicCertificate(
                        createCertificateData, DateUtils.getDoctorId().toString()
                    )
                }

            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
            }
        } else {
            if (!binding.addCertificate.tvYear.text.toString().isNullOrEmpty()) {
                val selectedYearInt = selectedYear.toIntOrNull()
                val currentYearInt = DateUtils.getYear()?.toIntOrNull()

                if (selectedYearInt == null || currentYearInt == null) {
                    binding.addCertificate.tvYearError.visibility = View.VISIBLE
                    binding.addCertificate.tvYearError.text =
                        getString(R.string.year_not_valid_required)
                } else if ((selectedYearInt - currentYearInt) < 23) {
                    binding.addCertificate.tvYearError.visibility = View.VISIBLE
                    binding.addCertificate.tvYearError.text =
                        getString(R.string.year_not_valid_required)
                }else {
                    binding.addCertificate.tvYearError.visibility = View.GONE
                    toast(mContext, getString(R.string.fields_required))
                }
            }
            else{
                toast(mContext, getString(R.string.fields_required))
            }


        }
    }

    fun showYearBottomSheet(
        context: Context,
        yearList: MutableList<String>,
    ) {
        val bottomSheetDialog = BottomSheetDialog(mContext)
        bottomSheetDialog.setContentView(R.layout.yearpicker)

        val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.btnDone)
        val pickerYear = bottomSheetDialog.findViewById<RecyclerView>(R.id.rvYear)

        val adapter = YearAdapter(context, yearList, this)
        pickerYear?.adapter = adapter

        btnDone?.setOnClickListener {
            binding.addCertificate.tvYear.text = selectedYear
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()

    }


    fun uploadCertificates() {
        val totalCerts = adapter.list.size
//        val currentCertificate = adapter.list[totalCerts - 1]
        val currentCertificate = adapter.list[totalCerts - 1]

        if (!currentCertificate.Title.isNullOrEmpty() &&
            !currentCertificate.TitleAr.isNullOrEmpty() &&
            imageFile != null
        ) {
            if (isOnline(mContext)) {
                loadingDialog.show()
                screenViewModel.createDoctorCertificate(
                    CreateCertificateModel(
                        currentCertificate.Title,
                        currentCertificate.Description,
                        currentCertificate.TitleAr,
                        currentCertificate.DescriptionAr,
                        currentCertificate.Year,
                        imageFile!!
                    )
                )
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
            }
        } else {
            val gson = Gson()
            val json = gson.toJson(currentCertificate)
            if (imageFile == null) {
                Log.d("certData", "setUi: $json && image file is null")
            } else {
                Log.d("certData", "setUi: $json && ${imageFile!!.absolutePath}")
            }
            toast(requireContext(), "Field is required")
        }
    }

    fun getYear(date: String): String {
        var year = ""
        var str = date.split("/")
        if (str.size > 2) {
            year = str[2]
        }

        return year
    }

    override fun onAssetDelete(id: Int, position: Int) {

        if (isOnline(mContext)) {
            loadingDialog.show()
            screenViewModel.deleteCertificate(
                DeleteCertificateModel(
                    "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
                    Certificate(id.toString())
                )
            )
            assetPosition = position
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }


    }

    override fun onImagePickAction(binding: ItemCertificateBinding, position: Int) {
        checkMediaPermissions(MEDIA_EDIT_REQUEST_CODE)
        certificateBinding = binding
    }

    private fun subscribeToObserver() {

        if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
            getCertificates()
        } else {
            callGetPolycertificate()
        }
        screenViewModel.createCertificateResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    binding.addCertificate.llCertification.visibility = GONE
                    imageFile = null
                    if (it.data.MessageCode == 200) {
                        loadingDialog.dismiss()
//                        if (isFinal) {
//                            binding.profileBottomToolbar.submitBtn.visibility= VISIBLE
//                            if (binding.profileBottomToolbar.submitBtn.text.toString()
//                                    .lowercase() != "save"
//                            ) {
//                                val intent = Intent(".Fragment.SetupProfile")
//                                intent.putExtra("stepStatus", "3")
//                                requireContext().sendBroadcast(intent)
//                            }
//
//                        } else {
                        if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                            getCertificates()
                        } else {
                            callGetPolycertificate()
                        }
                        //            }

                    } else if (it.data.MessageCode == 201) {
                        val sucess = UpdateDialog(findNavController(), "")
                        sucess.show(childFragmentManager, "0")
                    }


                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                }

                Resource.Loading -> TODO()
                is Resource.SigUpError<*> -> TODO()
            }

        }

        screenViewModel.editCertificateResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    imageFile = null

                    if (it.data.MessageCode == 200) {
                        if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                            getCertificates()

                        } else {
                            callGetPolycertificate()
                        }
                    } else if (it.data.MessageCode == 201) {
                        val sucess = UpdateDialog(findNavController(), "")
                        sucess.show(childFragmentManager, "0")
                    }

                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    Log.e(TAG, "subscribeToObserver: ${it.message} ")
                }

                else -> {
                    loadingDialog.show()
                }
            }

        }


        screenViewModel.deleteCertificateResponse.observe(viewLifecycleOwner) {
            if (it.Success) {
                loadingDialog.dismiss()
                adapter.deleteAsset(assetPosition)
            }
        }

    }

    private fun getCertificates() {
        //TODO 29/11/22
        if (isOnline(mContext)) {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                loadingDialog.show()
                screenViewModel.getCertificates()
            } else {
                callGetPolycertificate()
            }


        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }

        screenViewModel.certificatesResponse.observe(viewLifecycleOwner) {

            if (it.Success) {
                clearAddCertificateData()

                loadingDialog.dismiss()

                if (it.Data.isNotEmpty()) {

                    binding.addCertificateBtn.text = getString(R.string.add_another_certificate)
                } else {
                    binding.addCertificateBtn.text = getString(R.string.add_certificate)
                }

                adapter.list = it.Data as MutableList<CertificatesData>


                if (editLabel != MoreFragment.editClick) {


                }

            }

        }

    }

    private fun callGetPolycertificate() {
        polyViewModel.getcertificatePolyclinicResponse.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                when (it) {
                    is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                        //    loadingDialog.show()
                    }

                    is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                        loadingDialog.dismiss()

                        clearAddCertificateData()

                        if (it.data?.Data?.isNotEmpty() == true) {

                            binding.addCertificateBtn.text =
                                getString(R.string.add_another_certificate)
                        } else {
                            binding.addCertificateBtn.text = getString(R.string.add_certificate)
                        }

                        adapter.list = it.data?.Data as MutableList<CertificatesData>


                        if (editLabel != MoreFragment.editClick) {


                        }
                    }

                    is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                        loadingDialog.dismiss()
                    }

                }
            })
        DateUtils.getDoctorId()?.toInt()?.let { polyViewModel.getDoctorCertificatePolyClinic(it) }
    }


    override fun onImageClicked(
        binding: NewItemCertificateTableBinding,
        certificatesData: CertificatesData,
        position: Int
    ) {
        selectedPosition = position
        selectPic()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(3000)
                runBlocking {
                    loadingDialog.show()
                }
                delay(4000)
                loadingDialog.dismiss()
                Glide.with(requireContext())
                    .load(imageFile)
                    .into(binding.ivCertificate)
            }




    }

    override fun onEditClicked(certificatesData: CertificatesData) {
        binding.root.visibility = VISIBLE
        certificateId = certificatesData.Id
    }


    //TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    override fun onSaveClicked(certificatesData: CertificatesData) {
        //Need to update the certificate here

    }

    override fun onUpdateCertificate(
        binding: NewItemCertificateTableBinding,
        position: Int,
        id: Int,
        certificateUrl: String
    ) {

        if (binding.tvYear.text.toString().toInt() > DateUtils.getYear()?.toInt()!!
            && isEnglishValid(binding.tvTittleEn.text.toString())
            && isEnglishValid(binding.tvDescriptionEn.text.toString())
            && isArabicValid(binding.tvTittleAr.text.toString())
            && isArabicValid(binding.tvDescriptionAr.text.toString())
        ) {

            //   loadingDialog.show()
            if (imageFile != null) {

                val editCertificateModel =
                    EditCertificateModel(
                        id.toString(),
                        binding.tvTittleEn.text.toString(),
                        binding.tvDescriptionEn.text.toString(),
                        binding.tvTittleAr.text.toString(),
                        binding.tvDescriptionAr.text.toString(),
                        binding.tvYear.text.toString(),
                        ensureValidImageFormatAndSize(imageFile!!)
                    )

                //TODO 29/11/22
                if (isOnline(mContext)) {
                    screenViewModel.editDoctorCertificate(
                        editCertificateModel
                    )

                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }

            } else {
                val editCertificateModel =
                    EditCertificateModel(
                        id.toString(),
                        binding.tvTittleEn.text.toString(),
                        binding.tvDescriptionEn.text.toString(),
                        binding.tvTittleAr.text.toString(),
                        binding.tvDescriptionAr.text.toString(),
                        binding.tvYear.text.toString(),
                        ensureValidImageFormatAndSize(imageFile!!)
                    )
                //TODO 29/11/22
                if (isOnline(mContext)) {
                    screenViewModel.editDoctorCertificate(
                        editCertificateModel
                    )
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }


            }
        } else {

            if (binding.tvYear.text.toString().toInt() > 2024) {
                binding.tvYearError.visibility = View.VISIBLE
                binding.tvYearError.text = getString(R.string.year_not_valid_required)
            } else if (DateUtils.getYear()?.toInt()!! > binding.tvYear.text.toString().toInt()) {
                binding.tvYearError.visibility = View.VISIBLE
                binding.tvYearError.text = getString(R.string.year_not_valid_required)
            } else {
                binding.tvYearError.visibility = View.GONE
            }

            if (!isEnglishValid(binding.tvTittleEn.text.toString())) {
                binding.tvTittleEn.error = getString(R.string.english_name)
            }

            if (!isEnglishValid(binding.tvDescriptionEn.text.toString())) {
                binding.tvDescriptionEn.error = getString(R.string.english_name_Description)
            }

            if (!isArabicValid(binding.tvTittleAr.text.toString())) {
                binding.tvTittleAr.error =
                    getString(R.string.arabic_name)
            }

            if (!isArabicValid(binding.tvDescriptionAr.text.toString())) {
                binding.tvDescriptionAr.error = getString(R.string.arabic_name_Description)
            }

        }

    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isFinal = true
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                if (adapter.list.isNotEmpty()) {

                    if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                        uploadCertificates()
                    }

                } else {
                    isFinal = false
                    toast(
                        requireContext(),
                        getString(R.string.add_atleast_1_certificate_before_continuing)
                    )
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    override fun YearOnClick(year: String) {
        selectedYear = year
    }


    fun uriToFile(uri: Uri, context: Context): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        var fileName = getFileName(uri, context) ?: "temp_file"

        val validExtensions = listOf("jpg", "jpeg", "png")
        val hasValidExtension = validExtensions.any { fileName.lowercase().endsWith(".$it") }

        if (!hasValidExtension) {
            val mimeType = context.contentResolver.getType(uri)
            val extension = when (mimeType) {
                "image/jpeg" -> "jpg"
                "image/png" -> "png"
                else -> "jpg"
            }
            fileName += ".$extension"
        }

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

    override fun onImageSelected(file: File) {
            Glide.with(requireContext())
                .load(file)
                .into(binding.addCertificate.ivCertificate)
    }


    fun ensureValidImageFormat(inputFile: File): File {
        val validFormats = listOf("jpg", "jpeg", "png")
        val ext = inputFile.extension.lowercase()

        return if (ext !in validFormats) {
            val bitmap: Bitmap = BitmapFactory.decodeFile(inputFile.absolutePath)
            val newFile = File(inputFile.parent, inputFile.nameWithoutExtension + "_converted.jpg")
            FileOutputStream(newFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            println("✅ Converted to: ${newFile.absolutePath}")
            newFile
        } else {
            println("✅ Image format is already valid: .$ext")
            inputFile
        }
    }

    fun ensureValidImageFormatAndSize(inputFile: File): File {
        val validFormats = listOf("jpg", "jpeg", "png")
        val maxSizeBytes = 5 * 1024 * 1024 // 5 MB
        val ext = inputFile.extension.lowercase()

        // Decode bitmap
        var bitmap: Bitmap = BitmapFactory.decodeFile(inputFile.absolutePath)
            ?: throw IllegalArgumentException("Cannot read image: ${inputFile.absolutePath}")

        // If format not valid, convert to JPG
        var outputFile = if (ext !in validFormats) {
            val newFile = File(inputFile.parent, inputFile.nameWithoutExtension + "_converted.jpg")
            FileOutputStream(newFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            println("Converted to: ${newFile.absolutePath}")
            newFile
        } else inputFile

        // Check file size
        if (outputFile.length() > maxSizeBytes) {
            println("Compressing image because it’s larger than 5 MB...")
            outputFile = compressImage(outputFile, maxSizeBytes)
        }

        println("✅ Final image ready: ${outputFile.name} (size: ${outputFile.length() / 1024} KB)")
        return outputFile
    }

    fun compressImage(inputFile: File, maxSizeBytes: Int): File {
        val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath)
            ?: throw IllegalArgumentException("Cannot decode file for compression")

        val outputFile = File(inputFile.parent, inputFile.nameWithoutExtension + "_compressed.jpg")
        var quality = 100
        var byteArray: ByteArray
        do {
            val stream = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            byteArray = stream.toByteArray()
            quality -= 5
        } while (byteArray.size > maxSizeBytes && quality > 10)

        FileOutputStream(outputFile).use {
            it.write(byteArray)
        }

        return outputFile
    }


}