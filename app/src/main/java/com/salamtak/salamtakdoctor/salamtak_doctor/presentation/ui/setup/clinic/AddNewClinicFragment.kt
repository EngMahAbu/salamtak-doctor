package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentAddNewClinicBinding
import com.salamtak.domain.entities.response.CityData
import com.salamtak.domain.entities.response.InsuranceCompany
import com.salamtak.domain.entities.response.ServiceData
import com.salamtak.domain.entities.resquest.auth.CreateClinicModel
import com.salamtak.domain.entities.resquest.auth.DoctorLocation
import com.salamtak.salamtakdoctor.ServiceTypeAdapter
import com.salamtak.salamtakdoctor.adapter.ClinicFeaturesAdapter
import com.salamtak.salamtakdoctor.adapter.InsuranceCompanyAdapter
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.AreasAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.CitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.ClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.OffersViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RequestCode.CLINIC_LOCATION_RC
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.UpdateDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.AreasBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.CitiesBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.ServicesBottomSheetDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.io.File
import java.util.*


class AddNewClinicFragment :
    com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment<SetupClinicViewModel>(R.layout.fragment_add_new_clinic),
    DialogsListener,
    RecyclerViewOnClickListener, View.OnClickListener,
    InsuranceCompanyAdapter.SelectInsuranceCompany, ClinicFeaturesAdapter.ISelectClinicFeatures {

    private lateinit var binding: FragmentAddNewClinicBinding
    lateinit var navController: NavController
    lateinit var mContext: Context
    private val STORAGE_PERMISSION_CODE = 25
    private val sessionManagement: SessionManagement by inject()
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    private lateinit var viewModelinsurance: InsuranceViewModel
    private lateinit var viewModels: OffersViewModel
    private lateinit var insuranceCompanyAdapter: InsuranceCompanyAdapter
    private lateinit var clinicFeaturesAdapter: ClinicFeaturesAdapter
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomsheetbeahavoirfeatures: BottomSheetBehavior<ConstraintLayout>
   // private var imageFile: File? = null
    private var polyInfoId = ""
    private var partMap: Map<String, Any> = mutableMapOf()

    //    private lateinit var imageFile: File
    private lateinit var servicesAdapter: ServiceTypeAdapter
    private lateinit var citiesAdapter: CitiesAdapter
    private lateinit var areasAdapter: AreasAdapter
    private var doctorLocation: DoctorLocation.Location? = null
    private val TAG = AddNewClinicFragment::class.java.simpleName
    var isFilled = false
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var loadingDialog: LoadingDialog
    var getClinicInfo: String = ""
    var clinictId: Int = 0
    var cityId: Int? = 0
    var areaId = 0
    private var services = ArrayList<String>()
    lateinit var loading: Dialog
    var clinicId: Int = 0
    var appartment: String = "0"
    var address: String = "0"
    var City: String = ""
    var area: String = ""
    var street: String = " "
    var name: String = " "
    var building_no = ""
    var floor_no: Int = 0
    var descriptionData = arrayOf("Clinic Info.", "Location", "Schedule", "Gallery")
    var descriptionDataAr = arrayOf("بيانات العيادة", "الموقع", "المواعيد", "الصور")
    var descriptionDataNew = arrayOf("Clinic Info.", "Location", "Gallery")
    var descriptionDataNewAr = arrayOf("بيانات العيادة", "الموقع", "الصور")
    var english_name: String = " "
    var arabicname: String = " "
    var emailID: String = " "
    var phoneno1: String = " "
    var phoneno2: String = " "
    var phoneno3: String = " "
    var service: String = " "
    var phone = ArrayList<String>()

    // var servicesList = ArrayList<ServicesTypesData>()
    var simpleServiceList = ArrayList<ServiceData>()
    var isButtonClicked = false
    val insuranceCompanyListID = mutableListOf<String>()
    val featureListID = mutableListOf<String>()
    val insuranceCompanyListName = mutableListOf<String>()
    val featureListListName = mutableListOf<String>()

    override val screenViewModel: SetupClinicViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_Clinic_VIEW_MODEL))

    private lateinit var viewModel: NewClinicViewModel
    private lateinit var clinicsviewModel: ClinicsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelinsurance = ViewModelProvider(this).get(InsuranceViewModel::class.java)
        clinicsviewModel = ViewModelProvider(this).get(ClinicsViewModel::class.java)
        arguments?.let {
            clinictId = sessionManagement.getClinicId().toString().toInt()
//            clinictId = it.getInt(CLINIC_ID, 0)
            getClinicInfo = it.getString("edit", "")
            name = it.getString("name", "")
        }


        clinicId = sessionManagement.getClinicId().toString().toInt()

        Log.d(TAG, "onCreateView: " + clinicId)
        loading = Dialog(requireContext())
        binding = FragmentAddNewClinicBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(NewClinicViewModel::class.java)
        viewModels = ViewModelProvider(this).get(OffersViewModel::class.java)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!DateUtils.getAdminOwnerId().isNullOrEmpty()) {
            // binding.servicesSelect.visibility=View.GONE
            binding.scheduleRb.visibility = View.GONE
            binding.vactionRb.visibility = View.GONE
            if (Localization.getLanguageName() == "ar") {
                binding.yourStateProgressBarId.setStateDescriptionData(descriptionDataNewAr)
                binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.backarabic,
                    0
                )
            } else {
                binding.yourStateProgressBarId.setStateDescriptionData(descriptionDataNew)
                binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.group_70328,
                    0,
                    0,
                    0
                )
            }

        } else {
            binding.servicesSelect.visibility = View.VISIBLE
            binding.scheduleRb.visibility = View.VISIBLE
            binding.vactionRb.visibility = View.VISIBLE
        }

        sharedDataViewmodel.healthEntityId.observe(viewLifecycleOwner, Observer {
            polyInfoId = it.toString()
        })
        bindViews()
        validity()
        initButton()
        insuranceCompanyRecylerview()
        clinicFeaturesRecylerview()
        SELECTED_TAB = "1"
        if (clinicId == 0) {
            binding.stepsRadio.visibility = INVISIBLE
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.visibility = VISIBLE
            } else {
                binding.polyYourStateProgressBarId.visibility = VISIBLE
            }
            binding.tvClinicprofile.visibility = VISIBLE
            binding.tvSaveedit.visibility = GONE
            // binding.help2.visibility = VISIBLE
            binding.tvEditClinicstxt.visibility = GONE
            binding.addBtnClinic.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.buttoncolor)
            binding.plus.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.new_themeBlue_color)
            binding.submitBtn.visibility = VISIBLE
        } else {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.visibility = GONE
            } else {
                binding.polyYourStateProgressBarId.visibility = GONE
            }
        }
//        else{
//            binding.tvEditClinicstxt.text = name
//        }

//        if (Locale.getDefault().language == "ar") {
//            supportActionBar?.setDisplayHomeAsUpEnabled(false)
//            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
//        } else {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
//        }

        if (Localization.getLanguageName() == "ar") {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.setStateDescriptionData(descriptionDataAr)
            } else {
                binding.polyYourStateProgressBarId.setStateDescriptionData(descriptionDataNewAr)
            }

            binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.backarabic,
                0
            )
        } else {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.setStateDescriptionData(descriptionData)
            } else {
                binding.polyYourStateProgressBarId.setStateDescriptionData(descriptionDataNew)
            }
            binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.group_70328,
                0,
                0,
                0
            )
        }
        binding.help2.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }
        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        loadingDialog = LoadingDialog(mContext)

        servicesAdapter = ServiceTypeAdapter(true, this, mContext)
        citiesAdapter = CitiesAdapter(false, this, mContext)
        areasAdapter = AreasAdapter(false, this, mContext)

        cameraRequestCode = RequestCode.CAMERA_REQUEST_CODE
        galleryRequestCode = RequestCode.MEDIA_REQUEST_CODE
        editGalleryRequestCode = RequestCode.MEDIA_EDIT_REQUEST_CODE
        binding.tvEditClinicstxt.setOnClickListener(this)
        binding.submitBtn.setOnClickListener(this)

        setUi()
        subscribeToObserver()
        if (getClinicInfo.equals("edit")) {
            getInfo()
        }

        binding.addBtnClinic.setOnClickListener {
            binding.mcardphoneLayout3.visibility = VISIBLE
            binding.addBtnClinic.visibility = GONE
        }

        binding.tvHelp.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }

//        binding.locationRb.setOnCheckedChangeListener { compoundButton, b ->
//            val bundle = Bundle()
//            bundle.putString("edit", "edit")
//            Toast.makeText(requireContext(), "vahhhation", Toast.LENGTH_SHORT).show()
//            findNavController().navigate(R.id.createClinicGalleryFragment, bundle)
//        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (getClinicInfo.equals("edit")) {
                findNavController().navigate(R.id.clinicsFragment)
            } else {
                if (!findNavController().popBackStack()) {
                    findNavController().navigate(R.id.clinicsFragment)
                }
            }
        }

    }

    private fun initButton() {
        binding.insuranceCompanySelect.setOnClickListener {
            bottomsheetbeahavoir.state =
                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
            getInsuranceCompanyCallback()
        }
        binding.featuresSelect.setOnClickListener {
            binding.layoutBottomsheetpersistantfeatures.tvSearch.text =
                getString(R.string.clinicsfeatures)
            bottomsheetbeahavoirfeatures.state =
                if (bottomsheetbeahavoirfeatures.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
            getFeaturesCallback()
        }
        binding.layoutBottomsheetpersistant.btnConfirm.setOnClickListener {
            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.layoutBottomsheetpersistantfeatures.btnConfirm.setOnClickListener {
            bottomsheetbeahavoirfeatures.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.tvClinicprofile.setOnClickListener {
            findNavController().navigate(R.id.clinicsFragment)
        }
    }

    override fun onServiceSelect(serviceId: Int, serviceTxt: String, position: Int) {
        binding.servicesSelect.text = servicesAdapter.getAdditionTexts()
        services = servicesAdapter.getAdditionIds()
    }

    override fun onAreSelect(areaId: Int, areaTxt: String, position: Int) {
        binding.areaSelect.text = areaTxt
        this.areaId = areaId
    }

    override fun onCitySelect(cityId: Int, cityTxt: String, position: Int) {
        binding.citySelect.text = cityTxt
        binding.areaSelect.text = ""
        this.cityId = cityId

        //TODO 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getAreas(cityId.toString())
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())


        }

        bindAreas()
    }

    fun bindAreas() {

        screenViewModel.areasResponse.observe(viewLifecycleOwner) {

            if (it.Success) {
                if (it.Data != null) {
                    val list = ArrayList<CityData>()
                    list.clear()
                    it.Data.forEach { item ->
                        list.add(item)
                    }
                    areasAdapter.submitData(list)
                } else {
                    toast(mContext, it.Message)
                }
            }

        }

        binding.areaSelect.setOnClickListener {
            val areaBottomSheetDialog = AreasBottomSheetDialog(areasAdapter)
            areaBottomSheetDialog.show(childFragmentManager, "")

//            if (areasAdapter != 0) {
            binding.cityErrorTxt.visibility = GONE
//            }
        }

    }


    private fun setUi() {

        binding.clinicToolbar.ivBackBg.setOnClickListener {
            if (getClinicInfo.equals("edit")) {
                findNavController().navigate(R.id.clinicsFragment)
            } else {
                findNavController().popBackStack()
            }
        }

        binding.toolbar.toolbarHeader.text = getString(R.string.new_clinic)
        binding.servicesSelect.setOnClickListener {
            val serviceBottomSheet = ServicesBottomSheetDialog(null, servicesAdapter, arrayListOf())
            serviceBottomSheet.show(childFragmentManager, "")
        }

        binding.citySelect.setOnClickListener {
            val cityBottomSheetDialog = CitiesBottomSheetDialog(citiesAdapter)
            cityBottomSheetDialog.show(childFragmentManager, "")
        }

        binding.pickLocation.setOnClickListener {
            pickLocation()
        }

        binding.uploadPhotoIv.setOnClickListener {
            // showDialog(cameraRequestCode, galleryRequestCode)
            selectPic()
        }

        binding.uploadPhotoBg.setOnClickListener {

            //  showDialog(cameraRequestCode, galleryRequestCode)
            selectPic()
        }
        //todo 28/12/22
        binding.tvSaveedit.setOnClickListener {
            if (getClinicInfo.equals("edit") || (clinicId != 0 && getClinicInfo.equals(""))) {
                checkValidationUpdateClinic()
            } else {
                checkValidation()
            }
        }


        binding.tvLogout.setOnClickListener {

            sessionManagement.clearUser()
            navController.navigate(R.id.action_logout_clinic)

        }

    }

    private fun checkValidation() {

        val phonesList = ArrayList<String>()
        var firstPhoneError = false
        var secondPhoneError = false
        var thirdPhoneError = false
        if (binding.clinicPhoneEt.text.toString().isNotEmpty()) {
            if (isClinicPhoneValid(binding.clinicPhoneEt.text.toString())) {
                phonesList.add(binding.clinicPhoneEt.text.toString())
                firstPhoneError = false
            } else {
                firstPhoneError = true
                binding.clinicPhoneEt.error = getString(R.string.phone_valid)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.phone_valid),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (binding.clinicPhone2Et.text.toString().isNotEmpty()) {
            if (isClinicPhoneValid(binding.clinicPhone2Et.text.toString())) {
                phonesList.add(binding.clinicPhone2Et.text.toString())
                secondPhoneError = false
            } else {
                secondPhoneError = true
                binding.clinicPhone2Et.error = getString(R.string.phone_valid)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.phone_valid),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        if (binding.clinicPhone3Et.text.toString().isNotEmpty()) {
            if (isClinicPhoneValid(binding.clinicPhone3Et.text.toString())) {
                phonesList.add(binding.clinicPhone3Et.text.toString())
                thirdPhoneError = false
            } else {
                thirdPhoneError = true
                binding.clinicPhone3Et.error = getString(R.string.phone_valid)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.phone_valid),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (isOnline(mContext)) {

            if (binding.clinicNameEt.text.toString().isNotEmpty() &&
                binding.clinicNameArEt.text.toString().isNotEmpty() &&
                isEnglishValid(binding.clinicNameEt.text.toString()) &&
                isArabicValid(binding.clinicNameArEt.text.toString()) &&
                imageFile != null &&
//                binding.servicesSelect.text.toString().isNotEmpty() &&
                phonesList.size > 0 && !firstPhoneError && !secondPhoneError && !thirdPhoneError
            ) {

                english_name = binding.clinicNameEt.text.toString()
                arabicname = binding.clinicNameArEt.text.toString()
                emailID = binding.emailEt.text.toString()
                phoneno1 = binding.clinicPhoneEt.toString()
                phoneno2 = binding.clinicPhone2Et.toString()
                phoneno3 = binding.clinicPhone3Et.toString()
                service = binding.servicesSelect.toString()


                //TODO 30/12/22 move Location fragment
                val createClinicModel = CreateClinicModel(
                    phonesList, services,
                    insuranceCompanyListID,
                    featureListID,
                    binding.clinicNameEt.text.toString(),
                    binding.clinicNameArEt.text.toString(),
                    binding.emailEt.text.toString(),
                    sessionManagement.getCountry()!!,
                    polyInfoId.toString(), areaId.toString(),
                    binding.addressEt.text.toString(),
                    doctorLocation?.latitude.toString(),
                    doctorLocation?.longitude.toString(),
                    binding.buildingEt.text.toString(),
                    binding.floorEt.text.toString(),
                    "0",
                    binding.apartmentEt.text.toString(),
                    false, binding.streetEt.text.toString(), imageFile
                )


                if (isOnline(mContext)) {
                    loadingDialog.show()
                    if (!DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                        binding.servicesSelect.visibility = View.GONE
                        binding.yourStateProgressBarId.visibility= GONE
                      // screenViewModel.createClinicHealtentity(createClinicModel)
                        sendData(createClinicModel,"create",false)
                    } else {
                        binding.servicesSelect.visibility = View.VISIBLE
                    //    screenViewModel.createClinic(createClinicModel)
                        sendData(createClinicModel,"create",true)
                    }

                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }


            } else {

                //     toast(mContext, getString(R.string.fields_required))


                if (imageFile == null) {
                    Toast.makeText(requireContext(), "You must upload images", Toast.LENGTH_SHORT)
                        .show()
                }
                if (binding.clinicNameEt.text.toString().isNullOrEmpty()) {
                    binding.tvClinicNameError.visibility = View.VISIBLE
                    binding.tvClinicNameError.text = getString(R.string.requiredclinicname)
                    binding.clinicNameEt.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_error,
                        0
                    )
                    binding.mCardClinicName.setBackgroundResource(R.drawable.bg_edittext_error)

                } else {
                    binding.tvClinicNameError.visibility = View.GONE
                    binding.mCardClinicName.setBackgroundResource(R.drawable.bg_edittext)
                    binding.clinicNameEt.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                }

                if (binding.clinicNameArEt.text.toString().isNullOrEmpty()) {
                    binding.tvClinicArabicNameError.visibility = View.VISIBLE
                    binding.tvClinicArabicNameError.text =
                        getString(R.string.requiredclinicnamearabic)
                    binding.clinicNameArEt.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_error,
                        0
                    )
                    binding.mcardClinicArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)

                } else {
                    binding.tvClinicArabicNameError.visibility = View.GONE
                    binding.mcardClinicArabicLayout.setBackgroundResource(R.drawable.bg_edittext)
                    binding.clinicNameArEt.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                }


                if (!isFilled)
                    binding.photoErrorTxt.visibility = VISIBLE
                else
                    binding.photoErrorTxt.visibility = GONE

//                if (binding.emailEt.text?.trim()!!.isEmpty()){
//                    binding.tvMcardEmailNameError.visibility=View.VISIBLE
//                    binding.tvMcardEmailNameError.text= getString(R.string.required)
//                    binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(
//                        0,
//                        0,
//                        R.drawable.ic_error,
//                        0
//                    )
//                    binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//
//                }else if (!isEmailValid(binding.emailEt.text.toString())) {
//                    binding.tvMcardEmailNameError.visibility=View.VISIBLE
//                    binding.tvMcardEmailNameError.text= getString(R.string.match_email)
//                    binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(
//                        0,
//                        0,
//                        R.drawable.ic_error,
//                        0
//                    )
//                    binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//
//                }else{
//                    binding.tvMcardEmailNameError.visibility = View.GONE
//                    binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext)
//                    binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
//                }
                if (binding.clinicPhoneEt.text?.trim()!!.isEmpty()) {
                    binding.tvMcardphoneNameError.visibility = View.VISIBLE
                    binding.tvMcardphoneNameError.text = getString(R.string.required)
                    binding.clinicPhoneEt.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_error,
                        0
                    )
                    binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)

                } else {
                    binding.tvMcardphoneNameError.visibility = View.GONE
                    binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext)
                    binding.clinicPhoneEt.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                }

                if (!isEnglishValid(binding.clinicNameEt.text.toString())) {
                    // binding.clinicNameEt.error = getString(R.string.english_name)
                }

                if (!isArabicValid(binding.clinicNameArEt.text.toString())) {
                    //  binding.clinicNameArEt.error = getString(R.string.arabic_name)
                }


            }
        } else {
            noInternetDialog.show(
                childFragmentManager, ""
            )
        }

    }

    private fun sendData(createClinicModel: CreateClinicModel,type:String,isPolyOrDoctorClinic:Boolean) {
        val mediaTypeText = "text/plain".toMediaTypeOrNull()
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (!DateUtils.getAdminOwnerId().isNullOrEmpty()) {
            builder.addFormDataPart("healthEntityTypeEnum", "6")
            builder.addFormDataPart("Poly_infoID", createClinicModel.CityId)
        }
        // List fields
        createClinicModel.HealthEntityPhoneDtos?.forEachIndexed { index, value ->
            builder.addFormDataPart("HealthEntityPhoneDtos[$index]", value)
        }

        createClinicModel.HealthEntityServiceDtos?.forEachIndexed { index, value ->
            builder.addFormDataPart("HealthEntityServiceDtos[$index]", value)
        }

        createClinicModel.insurance_CompaniesID?.forEachIndexed { index, value ->
            builder.addFormDataPart("insurance_CompaniesID[$index]", value.toString())
        }

        createClinicModel.featuresId?.forEachIndexed { index, value ->
            builder.addFormDataPart("HealthEntityFeaturesDtos[$index]", value.toString())
        }

        // Single value fields
        builder.addFormDataPart("NameAr", createClinicModel.NameAr)
        builder.addFormDataPart("Name", createClinicModel.Name)
        createClinicModel.Email?.let {
            builder.addFormDataPart("Email", it)
        }
        createClinicModel.ClinicId?.let {
            builder.addFormDataPart("ClinicId", it)
        }
        builder.addFormDataPart("MedicalExaminationTypeId", "1")
        builder.addFormDataPart("Inactive", "false")

        // File handling
        imageFile?.let { file ->
            partMap = partMap + mapOf("clinicLogo" to file)
        }
        for (item in partMap) {
            val key = item.key
            if (key.contains("clinicLogo")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

        // Send request
        if (type=="create"){
            createDoctorClinic(builder.build(),isPolyOrDoctorClinic)
        }else{
            updateDoctorClinic(builder.build())
        }

    }
    private fun createDoctorClinic(Request: MultipartBody,isPolyOrDoctorClinic:Boolean){
        clinicsviewModel.createDoctorClinic(Request,isPolyOrDoctorClinic)
        clinicsviewModel.doctorClinicResponse.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    // Show loading state
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    val messageCode = result.data?.MessageCode
                    val clinicId = result.data?.Data?.ClinicId.toString()
                    loadingDialog.dismiss()
                    sessionManagement.setClinicId(clinicId)

                    when (messageCode) {
                        200 -> {
                            toast(mContext, result.data.Message)
                            navController.navigate(R.id.locationFragment, Bundle().apply {
                                putInt("add_new_clinic", 0)
                            })
                        }

                        201 -> {
                            UpdateDialog(findNavController(), "").show(childFragmentManager, "0")
                            navController.navigate(R.id.locationFragment, Bundle().apply {
                                putInt("add_new_clinic", 0)
                            })
                        }
                    }
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error-> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun updateDoctorClinic(Request: MultipartBody){
        clinicsviewModel.updateDoctorClinic2(Request)
        clinicsviewModel.updateDoctorClinicResponse.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    // Show loading state
                    loadingDialog.show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    loadingDialog.dismiss()
//                    toast(mContext, it.data.Message)
                    if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                        binding.yourStateProgressBarId.visibility = GONE
                    } else {
                        binding.polyYourStateProgressBarId.visibility = GONE
                    }

                    if (result.data?.MessageCode == 200) {
                        toast(mContext, result.data.Message.toString())
                    } else if (result.data?.MessageCode == 201) {
                        if (isButtonClicked) {
                            val sucess = UpdateDialog(findNavController(), "")
                            sucess.show(childFragmentManager, "0")
                        }

                    }
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error-> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun checkValidationUpdateClinic() {

        val phonesList = ArrayList<String>()
        var firstPhoneError = false
        var secondPhoneError = false
        var thirdPhoneError = false

        if (binding.clinicPhoneEt.text.toString().isNotEmpty()) {
            if (isClinicPhoneValid(binding.clinicPhoneEt.text.toString())) {
                firstPhoneError = false
                phonesList.add(binding.clinicPhoneEt.text.toString())
            } else {
                firstPhoneError = true
                binding.clinicPhoneEt.error = getString(R.string.phone_valid)
            }
        }

        if (binding.clinicPhone2Et.text.toString().isNotEmpty()) {
            if (isClinicPhoneValid(binding.clinicPhone2Et.text.toString())) {
                secondPhoneError = false
                phonesList.add(binding.clinicPhone2Et.text.toString())
            } else {
                secondPhoneError = true
                binding.clinicPhone2Et.error = getString(R.string.phone_valid)
            }

        }

        if (binding.clinicPhone3Et.text.toString().isNotEmpty()) {
            if (isClinicPhoneValid(binding.clinicPhone3Et.text.toString())) {
                phonesList.add(binding.clinicPhone3Et.text.toString())
                thirdPhoneError = false
            } else {
                thirdPhoneError = true
                binding.clinicPhone3Et.error = getString(R.string.phone_valid)
            }
        }
        if (isOnline(mContext)) {

            if (binding.clinicNameEt.text.toString().isNotEmpty() &&
                binding.clinicNameArEt.text.toString().isNotEmpty()
                && isEnglishValid(binding.clinicNameEt.text.toString()) &&
                isArabicValid(binding.clinicNameArEt.text.toString()) &&
                phonesList.size > 0 && !firstPhoneError && !secondPhoneError && !thirdPhoneError

//                && (binding.clinicFeesEt.text.toString()
//                    .isNotEmpty() && binding.clinicFeesEt.text.toString().toDouble() > 0.0)
            ) {
//                if (isEmailValid(binding.emailEt.text.toString())) {

                if (imageFile != null) {
                    val createClinicModel = CreateClinicModel(
                        phonesList, services,
                        insuranceCompanyListID,
                        featureListID,
                        binding.clinicNameEt.text.toString(),
                        binding.clinicNameArEt.text.toString(),
                        binding.emailEt.text.toString(),
                        sessionManagement.getCountry()!!,
                        cityId.toString(), areaId.toString(),
                        binding.addressEt.text.toString(),
                        doctorLocation?.latitude.toString(),
                        doctorLocation?.longitude.toString(),
                        binding.buildingEt.text.toString(),
                        binding.floorEt.text.toString(),
                        "1",
                        binding.apartmentEt.text.toString(),
                        false, binding.streetEt.text.toString(), imageFile, clinicId.toString()

                    )
                    //TODO
                    if (isOnline(mContext)) {
                        if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                            binding.yourStateProgressBarId.visibility = GONE
                        } else {
                            binding.polyYourStateProgressBarId.visibility = GONE
                        }

                       // screenViewModel.updateClinicInfo(createClinicModel)
                        sendData(createClinicModel,"update",false)
                    } else {
                        noConnectivityDialog.noInternetDialogFragment(requireContext())


                    }

                } else {
                    val createClinicModel = CreateClinicModel(
                        phonesList, services,
                        insuranceCompanyListID,
                        featureListID,
                        binding.clinicNameEt.text.toString(),
                        binding.clinicNameArEt.text.toString(),
                        binding.emailEt.text.toString(),
                        sessionManagement.getCountry()!!,
                        cityId.toString(), areaId.toString(),
                        binding.addressEt.text.toString(),
                        doctorLocation?.latitude.toString(),
                        doctorLocation?.longitude.toString(),
                        binding.buildingEt.text.toString(),
                        binding.floorEt.text.toString(),
                        "1",
                        binding.apartmentEt.text.toString(),
                        false, binding.streetEt.text.toString(), null, clinicId.toString(),

                        )

                    //TODO
                    if (isOnline(mContext)) {
                        loadingDialog.show()
                       // screenViewModel.updateClinicInfo(createClinicModel)
                        sendData(createClinicModel,"update",false)
                    } else {
                        noConnectivityDialog.noInternetDialogFragment(requireContext())


                    }

                }
                if (getClinicInfo.equals("edit")) {
//                    val sucess = UpdateDialog(findNavController(), "")
//                    sucess.show(childFragmentManager, "0")
                } else {
                    findNavController().navigate(R.id.clinicScheduleFragment)
                }

//                } else {
//                    binding.emailEt.error = getString(R.string.email_valid)
//                }
            } else {

                toast(mContext, getString(R.string.fields_required))

//                if (binding.emailEt.text.toString().isEmpty()) {
//                    binding.emailEt.error = getString(R.string.email_required)
//                }

                if (binding.clinicNameEt.text.toString().isNullOrEmpty())
                    binding.clinicNameEt.error = getString(R.string.clinic_name_required)

                if (binding.clinicNameArEt.text.toString().isNullOrEmpty())
                    binding.clinicNameArEt.error = getString(R.string.clinic_name_required)

                if (cityId == 0) {
                    binding.cityErrorTxt.visibility = VISIBLE
                }
                if (areaId == 0) {
                    binding.cityErrorTxt.visibility = VISIBLE
                }
//                if (!isFilled)
//                    binding.photoErrorTxt.visibility = VISIBLE
//                else
//                    binding.photoErrorTxt.visibility = GONE

                if (phonesList.isNullOrEmpty() && (binding.clinicPhoneEt.text.toString()
                        .isEmpty() &&
                            binding.clinicPhone2Et.text.toString()
                                .isEmpty() && binding.clinicPhone3Et.text.toString().isEmpty())
                )
                    binding.clinicPhoneEt.error = getString(R.string.clinic_phone_required)

                if (binding.addressEt.text.toString().isNullOrEmpty())
                    binding.addressEt.error = getString(R.string.clinic_address_required)

//                if (binding.clinicFeesEt.text.toString().isNullOrEmpty())
//                    binding.clinicFeesEt.error = getString(R.string.clinic_fees_required)

//                if (binding.clinicFeesEt.text.toString()
//                        .isNotEmpty() && binding.clinicFeesEt.text.toString().toDouble() <= 0.0
//                )
//                    binding.clinicFeesEt.error = getString(R.string.clinic_fees_error)

                if (!isEnglishValid(binding.clinicNameEt.text.toString())) {
                    binding.clinicNameEt.error = getString(R.string.english_name)
                }

                if (!isArabicValid(binding.clinicNameArEt.text.toString())) {
                    binding.clinicNameArEt.error = getString(R.string.arabic_name)
                }

            }
        } else {
            noInternetDialog.show(
                childFragmentManager, ""
            )
        }


        Log.d(TAG, "checkValidationUpdateClinic: " + clinictId)


    }

    private fun pickLocation() {
        val intent = Intent(requireContext(), SelectLocationActivity::class.java)
        startActivityForResult(intent, CLINIC_LOCATION_RC)
        binding.errorLocation.visibility = GONE
    }

    private fun subscribeToObserver() {
        //todo 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getServices(
                sessionManagement.getSpecialistId()!!
            )
        } else {
            if (noInternetDialog != null && noInternetDialog.isAdded)
                noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        screenViewModel.servicesResponse.observe(viewLifecycleOwner) {
            simpleServiceList.clear()
            if (it.Success) {
                if (it.Data != null)
                    for (item in it.Data.indices) {
                        var data = it.Data.get(item)
                        simpleServiceList.add(
                            ServiceData(
                                data.Id,
                                data.Name,
                                data.Image,
                                data.isSelected
                            )
                        )
                    }

                servicesAdapter.submitData(simpleServiceList)

            } else
                showError(it.Message)
        }

        //todo 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getCities("1")
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())


        }

        screenViewModel.citiesResponse.observe(viewLifecycleOwner) {
            if (it.Success) {
                val list = ArrayList<CityData>()
                list.clear()
                it.Data.forEach { item ->
                    list.add(item)
                }
                citiesAdapter.submitData(list)
            }
        }

        screenViewModel.createClinicResponse.observe(viewLifecycleOwner) {

            when (it) {

                is Resource.Success -> {
//                    if (getClinicInfo.equals("edit")){
//                        val bundle = Bundle()
//                        bundle.putString("edit" , "edit")
//                        findNavController().navigate(R.id.clinicScheduleFragment,bundle)
//                    }
//                    else{
                    if (it.data.MessageCode == 200) {
                        loadingDialog.dismiss()
                        sessionManagement.setClinicId(it.data.Data?.ClinicId.toString())
                        toast(mContext, it.data.Message)
                        val bundle = Bundle()
                        bundle.putInt("add_new_clinic", 0)
                        navController.navigate(R.id.locationFragment, bundle)
                    } else if (it.data.MessageCode == 201) {
                        val sucess = UpdateDialog(findNavController(), "")
                        sucess.show(childFragmentManager, "0")
                        loadingDialog.dismiss()
                        sessionManagement.setClinicId(it.data.Data?.ClinicId.toString())
                        val bundle = Bundle()
                        bundle.putInt("add_new_clinic", 0)
//                        Handler(Looper.getMainLooper()).postDelayed(Runnable{
//                            navController.navigate(R.id.locationFragment,bundle)
//                        }, 2000)
                        navController.navigate(R.id.locationFragment, bundle)
                    }

//                    navController.navigate(R.id.clinicScheduleFragment)
//                    }
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    Log.e(TAG, "subscribeToObserver: ${it.message}")
                }

                Resource.Loading -> TODO()
                is Resource.SigUpError<*> -> TODO()
            }

        }

        screenViewModel.updateClinicResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
//                    toast(mContext, it.data.Message)
                    if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                        binding.yourStateProgressBarId.visibility = GONE
                    } else {
                        binding.polyYourStateProgressBarId.visibility = GONE
                    }

                    if (it.data.MessageCode == 200) {
                        toast(mContext, it.data.Message.toString())
                    } else if (it.data.MessageCode == 201) {
                        if (isButtonClicked) {
                            val sucess = UpdateDialog(findNavController(), "")
                            sucess.show(childFragmentManager, "0")
                        }

                    }
//


//                    val sucess = UpdateDialog(findNavController())
//                    sucess.show(childFragmentManager, "0")
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                        binding.yourStateProgressBarId.visibility = GONE
                    } else {
                        binding.polyYourStateProgressBarId.visibility = GONE
                    }
                    Log.e(TAG, "subscribeToObserver: ${it.message}")
                }

                Resource.Loading -> TODO()
                is Resource.SigUpError<*> -> TODO()
            }
        }
    }


    //todo
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    override fun onClick(p0: View) {
        if (p0 == binding.tvEditClinicstxt) {
            navController.navigate(R.id.clinicsFragment)
        }

        if (p0 == binding.submitBtn) {
            isButtonClicked = true

            if (getClinicInfo.equals("edit") || (clinicId != 0 && getClinicInfo.equals(""))) {

                binding.yourStateProgressBarId.visibility = GONE
                checkValidationUpdateClinic()
            } else {
                binding.yourStateProgressBarId.visibility = VISIBLE
                checkValidation()
            }

        }
    }

    private fun insuranceCompanyRecylerview() {
        insuranceCompanyAdapter = InsuranceCompanyAdapter(this)
        binding.layoutBottomsheetpersistant.rvInsuranceCompany.apply {
            adapter = insuranceCompanyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun clinicFeaturesRecylerview() {
        clinicFeaturesAdapter = ClinicFeaturesAdapter(this)
        binding.layoutBottomsheetpersistantfeatures.rvInsuranceCompany.apply {
            adapter = clinicFeaturesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun getInsuranceCompanyCallback() {
        viewModelinsurance.insurance.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                binding.layoutBottomsheetpersistant.rvInsuranceCompany.visibility = View.VISIBLE
                binding.layoutBottomsheetpersistant.layoutNoResult.visibility = View.GONE
                insuranceCompanyAdapter.submitList(it.data)
                insuranceCompanyAdapter.notifyDataSetChanged()
                getInsuranceCompany(it.data, true)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
            //loading.showLoadingDialog()
                showprogtessbar()
            else
            // loading.hideLoadingDialog()
                hideprogressbar()
        })
        viewModelinsurance.getAllInsurance_Companies()
    }

    private fun getFeaturesCallback() {
        viewModelinsurance.featuresresponse.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                binding.layoutBottomsheetpersistantfeatures.rvInsuranceCompany.visibility =
                    View.VISIBLE
                binding.layoutBottomsheetpersistantfeatures.layoutNoResult.visibility = View.GONE
                clinicFeaturesAdapter.submitList(it.data)
                clinicFeaturesAdapter.notifyDataSetChanged()
                getIfeatures(it.data, false)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
            //loading.showLoadingDialog()
                showprogtessbar()
            else
            // loading.hideLoadingDialog()
                hideprogressbar()
        })
        viewModelinsurance.getfeatures()
    }

    override fun onSelectInsuranceCompany(listofInsuranceCompanyt: MutableList<InsuranceCompany>) {
        var insuranceCompany = ""
        insuranceCompanyListID.clear()
        listofInsuranceCompanyt.forEach {
            insuranceCompanyListID.add(it.iD)
            insuranceCompany += "${it.name} "
            insuranceCompanyListName.add(it.name)
        }
        binding.insuranceCompanySelect.text = insuranceCompany
    }

    private fun showprogtessbar() {
        binding.layoutBottomsheetpersistant.progressBar.visibility = View.VISIBLE

    }

    private fun hideprogressbar() {
        binding.layoutBottomsheetpersistant.progressBar.visibility = View.GONE

    }

    private fun getInsuranceCompany(data: List<InsuranceCompany>, status: Boolean) {

        var job: Job? = null
        binding.layoutBottomsheetpersistant.etSearch.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                it?.let {
                    if (it.isNotEmpty()) {
                        val list = data.filter {
                            if (status == true) {
                                it.name.contains(binding.layoutBottomsheetpersistant.etSearch.text.toString())
                            } else {
                                it.name.contains(binding.layoutBottomsheetpersistantfeatures.etSearch.text.toString())
                            }
                        }
                        if (!list.isNullOrEmpty()) {
                            if (status == true) {
                                binding.layoutBottomsheetpersistant.rvInsuranceCompany.visibility =
                                    View.VISIBLE
                                binding.layoutBottomsheetpersistant.layoutNoResult.visibility =
                                    View.GONE
                                insuranceCompanyAdapter.submitList(list)
                                insuranceCompanyAdapter.notifyDataSetChanged()
                            } else {
                                binding.layoutBottomsheetpersistantfeatures.rvInsuranceCompany.visibility =
                                    View.VISIBLE
                                binding.layoutBottomsheetpersistantfeatures.layoutNoResult.visibility =
                                    View.GONE
                                clinicFeaturesAdapter.submitList(list)
                                clinicFeaturesAdapter.notifyDataSetChanged()
                            }


                        } else {
                            if (status == true) {
                                binding.layoutBottomsheetpersistant.rvInsuranceCompany.visibility =
                                    View.GONE
                                binding.layoutBottomsheetpersistant.layoutNoResult.visibility =
                                    View.VISIBLE
                            } else {
                                binding.layoutBottomsheetpersistantfeatures.rvInsuranceCompany.visibility =
                                    View.GONE
                                binding.layoutBottomsheetpersistantfeatures.layoutNoResult.visibility =
                                    View.VISIBLE
                            }
                        }

                    } else {
                        if (status == true) {
                            viewModelinsurance.getAllInsurance_Companies()
                        } else {
                            viewModelinsurance.getfeatures()
                        }
                    }

                }

            }
        }

    }

    private fun getIfeatures(data: List<InsuranceCompany>, status: Boolean) {

        var job: Job? = null
        binding.layoutBottomsheetpersistantfeatures.etSearch.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                it?.let {
                    if (it.isNotEmpty()) {
                        val list = data.filter {
                            it.name.contains(binding.layoutBottomsheetpersistantfeatures.etSearch.text.toString())
                        }
                        if (!list.isNullOrEmpty()) {
                            if (status == true) {
                                binding.layoutBottomsheetpersistant.rvInsuranceCompany.visibility =
                                    View.VISIBLE
                                binding.layoutBottomsheetpersistant.layoutNoResult.visibility =
                                    View.GONE
                                insuranceCompanyAdapter.submitList(list)
                                insuranceCompanyAdapter.notifyDataSetChanged()
                            } else {
                                binding.layoutBottomsheetpersistantfeatures.rvInsuranceCompany.visibility =
                                    View.VISIBLE
                                binding.layoutBottomsheetpersistantfeatures.layoutNoResult.visibility =
                                    View.GONE
                                clinicFeaturesAdapter.submitList(list)
                                clinicFeaturesAdapter.notifyDataSetChanged()
                            }


                        } else {
                            if (status == true) {
                                binding.layoutBottomsheetpersistant.rvInsuranceCompany.visibility =
                                    View.GONE
                                binding.layoutBottomsheetpersistant.layoutNoResult.visibility =
                                    View.VISIBLE
                            } else {
                                binding.layoutBottomsheetpersistantfeatures.rvInsuranceCompany.visibility =
                                    View.GONE
                                binding.layoutBottomsheetpersistantfeatures.layoutNoResult.visibility =
                                    View.VISIBLE
                            }
                        }

                    } else {
                        if (status == true) {
                            viewModelinsurance.getAllInsurance_Companies()
                        } else {
                            viewModelinsurance.getfeatures()
                        }
                    }

                }

            }
        }

    }

    private fun validity() {
        binding.clinicNameEt.doOnTextChanged { text, start, before, count ->
            if (binding.clinicNameEt.text.toString().isNullOrEmpty()) {
                binding.tvClinicNameError.visibility = View.VISIBLE
                binding.tvClinicNameError.text = getString(R.string.requiredclinicname)
                binding.clinicNameEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
                binding.mCardClinicName.setBackgroundResource(R.drawable.bg_edittext_error)

            } else {
                binding.tvClinicNameError.visibility = View.GONE
                binding.mCardClinicName.setBackgroundResource(R.drawable.bg_edittext)
                binding.clinicNameEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }


        }
        binding.clinicNameArEt.doOnTextChanged { text, start, before, count ->
            if (binding.clinicNameArEt.text.toString().isNullOrEmpty()) {
                binding.tvClinicArabicNameError.visibility = View.VISIBLE
                binding.tvClinicArabicNameError.text = getString(R.string.requiredclinicnamearabic)
                binding.clinicNameArEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
                binding.mcardClinicArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)

            } else {
                binding.tvClinicArabicNameError.visibility = View.GONE
                binding.mcardClinicArabicLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.clinicNameArEt.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }


        }
        binding.clinicPhoneEt.doOnTextChanged { text, start, before, count ->
            if (binding.clinicPhoneEt.text?.trim()!!.isEmpty()) {
                binding.tvMcardphoneNameError.visibility = View.VISIBLE
                binding.tvMcardphoneNameError.text = getString(R.string.requiredmobile)
                binding.clinicPhoneEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)

            } else {
                binding.tvMcardphoneNameError.visibility = View.GONE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.clinicPhoneEt.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }


        }


    }

    private fun getInfo() {
        binding.clinicToolbar.circularBackground.visibility = INVISIBLE
        binding.stepsRadio.visibility = View.VISIBLE
        binding.submitBtn.visibility = View.VISIBLE
        binding.uploadtxt.visibility = View.GONE
        binding.yourStateProgressBarId.visibility = View.GONE
        binding.submitBtn.setText(requireActivity().getString(R.string.update))
        val bundle = Bundle()
        viewModels.data.observe(viewLifecycleOwner, Observer {

            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    loadingDialog.dismiss()
                    Log.d("clinic", "initObserve: ${it.data}")
                    Log.d("clinicData", "initObserve: ${it.data}")
                    ImageUtil.loadPicture(binding.uploadPhotoBg, it.data?.data?.logo)
                    binding.clinicNameEt.setText(it.data?.data?.name)
                    binding.clinicNameArEt.setText(it.data?.data?.nameAr)
                    binding.emailEt.setText(it.data?.data?.email)
//                binding.streetEt.setText(it.Data.Address)
//                address = it.Data.Address
//                binding.apartmentEt.setText(it.Data.ApartmentNo)
                    if (it.data?.data?.apartmentNo != null) {
                        appartment = it.data.data?.apartmentNo.toString()
                    }

                    if (it.data?.data?.address != null) {
                        address = it.data?.data?.address.toString()
                    }
                    if (it.data?.data?.blockNo != null) {
                        building_no = it.data.data.blockNo
                    }
                    if (it.data?.data?.cityName != null) {
                        City = it.data?.data?.cityName.toString()
                    }
                    if (it.data?.data?.floorNo != null) {
                        floor_no = it.data.data.floorNo
                    }
                    if (it.data?.data?.areaName != null) {
                        area = it.data?.data?.areaName.toString()
                    }
                    if (it.data?.data?.street != null) {
                        street = it.data?.data?.street.toString()
                    }

                    sessionManagement.setClinicId(it.data?.data?.clinicId.toString())

                    areaId = it.data?.data?.areaId!!
                    cityId = it.data?.data?.cityId
                    if (it.data.data.healthEntityPhoneDtos.isNotEmpty()  ) {

                        if (it.data?.data?.healthEntityPhoneDtos?.size == 1) {
                            binding.clinicPhoneEt.setText(it.data.data.healthEntityPhoneDtos[0])

                        }

                        if (it.data?.data?.healthEntityPhoneDtos?.size == 2) {
                            binding.clinicPhoneEt.setText(it.data.data.healthEntityPhoneDtos[0])
                            binding.clinicPhone2Et.setText(it.data.data.healthEntityPhoneDtos[1])

                        }
                        if (it.data?.data?.healthEntityPhoneDtos?.size == 3) {
                            binding.clinicPhoneEt.setText(it.data.data.healthEntityPhoneDtos[0])
                            binding.clinicPhone2Et.setText(it.data.data.healthEntityPhoneDtos[1])
                            binding.clinicPhone3Et.setText(it.data.data.healthEntityPhoneDtos[2])
                        }
                    }
                    bundle.putString("edit", "edit")
                    bundle.putInt("key", clinicId)
                    bundle.putString("building", building_no)
                    bundle.putString("street", street)
                    bundle.putString("city", City)
                    bundle.putString("area", area)
                    bundle.putInt("areaId", areaId)
                    bundle.putInt("cityId", cityId!!)
                    bundle.putInt("floor", floor_no)
                    bundle.putString("appartment", appartment)
                    bundle.putString("name", name)
                    bundle.putInt("clinicId", clinicId)
                    bundle.putString("address", address)
                    if (it.data.data.healthEntityInsName != null) {
                        binding.insuranceCompanySelect.setText(
                            it.data?.data?.healthEntityInsName.toString().removePrefix("[")
                                .removeSuffix("]")
                        )
                        it.data.data.healthEntityInsID.forEach {
                            insuranceCompanyListID.add(it.toString())
                        }
                    }
                    if (it.data.data.HealthEntityFeaturesNamesDtos != null) {
                        binding.featuresSelect.setText(
                            it.data?.data?.HealthEntityFeaturesNamesDtos.toString()
                                .removePrefix("[").removeSuffix("]")
                        )
                        it.data.data.HealthEntityFeaturesDtos.forEach {
                            featureListID.add(it)

                        }
                    }
                    binding.servicesSelect.setText(
                        it.data.data.healthEntityServiceName.toString().removePrefix("[")
                            .removeSuffix("]")
                    )
                    it.data.data.healthEntityServices.forEach {
                        services.add(it)
                    }

                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    loadingDialog.dismiss()
                }
            }

        })

        viewModels.getdata(clinicId.toInt())

        binding.profileBottomToolbar.saveBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.locationRb.setOnClickListener {
            findNavController().navigate(R.id.locationFragment, bundle)
        }

        binding.scheduleRb.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("edit", "edit")
            bundle.putString("edit", "edit")
            bundle.putInt("key", clinicId)
            bundle.putString("building", building_no)
            bundle.putString("street", street)
            bundle.putString("city", City)
            bundle.putString("area", area)
            bundle.putInt("areaId", areaId)
            bundle.putInt("cityId", cityId!!)
            bundle.putInt("floor", floor_no)
            bundle.putString("appartment", appartment)
            bundle.putString("name", name)
            bundle.putInt("clinicId", clinicId)
            bundle.putString("address", address)
//            Toast.makeText(requireContext(), "schedule", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.clinicScheduleFragment, bundle)
        }

        binding.vactionRb.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("edit", "edit")
            bundle.putString("edit", "edit")
            bundle.putInt("key", clinicId)
            bundle.putString("building", building_no)
            bundle.putString("street", street)
            bundle.putString("city", City)
            bundle.putString("area", area)
            bundle.putInt("areaId", areaId)
            bundle.putInt("cityId", cityId!!)
            bundle.putInt("floor", floor_no)
            bundle.putString("appartment", appartment)
            bundle.putString("name", name)
            bundle.putInt("clinicId", clinicId)
            bundle.putString("address", address)
//            Toast.makeText(requireContext(), "vacation", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.vacationClinicFragment, bundle)

        }

        binding.galleryRb.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("edit", "edit")
            bundle.putString("edit", "edit")
            bundle.putInt("key", clinicId)
            bundle.putString("building", building_no)
            bundle.putString("street", street)
            bundle.putString("city", City)
            bundle.putString("area", area)
            bundle.putInt("areaId", areaId)
            bundle.putInt("cityId", cityId!!)
            bundle.putInt("floor", floor_no)
            bundle.putString("appartment", appartment)
            bundle.putString("name", name)
            bundle.putInt("clinicId", clinicId)
            bundle.putString("address", address)
//            Toast.makeText(requireContext(), "vahhhation", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.createClinicGalleryFragment, bundle)
        }
    }

    private fun bindViews() {
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.insuranceBottomsheet)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN

        bottomsheetbeahavoirfeatures =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistantfeatures.insuranceBottomsheet)
        bottomsheetbeahavoirfeatures.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onSelectClinicFeature(listOfClinicFeature: MutableList<InsuranceCompany>) {
        var insuranceCompany = ""
        featureListID.clear()
        listOfClinicFeature.forEach {
            featureListID.add(it.Id)
            insuranceCompany += "${it.name} "
            featureListListName.add(it.name)
        }
        binding.featuresSelect.text = insuranceCompany
    }


    override fun onImageSelected(file: File) {
        Glide.with(requireContext())
            .load(file)
            .into(binding.uploadPhotoBg)
        binding.uploadtxt.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.gray)
        )
    }


}