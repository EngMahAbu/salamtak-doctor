package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.kofigyan.stateprogressbar.StateProgressBar
import com.salamtak.R
import com.salamtak.core.localDataBase.SharedPreferencesKeys
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.core.utils.SessionManagement.Companion.USER_ID
import com.salamtak.databinding.FragmentSetupProfileBinding
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.*
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule.SETUP_VIEW_MODEL
import com.salamtak.salamtakdoctor.salamtak_doctor.doctorProfile.DoctorProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.help.HelpBottomSheetFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.more.MoreFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.register.SignUpFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.CountriesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.SeniorityLevelsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.SpecialitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.SubSpecialitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.certificates.CertificatesFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs.DocumentsFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.DoctorProfileUpdateViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.NationalitiesBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SeniorityLevelsBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SpecialitiesBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SubSpecialitiesBottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class SetupProfileFragment : BaseFragment<SetupProfileViewModel>(R.layout.fragment_setup_profile), DialogsListener,
    RecyclerViewOnClickListener {
    companion object {
        var certificatesEdit: String = ""
        var documentEdit: String = ""
    }

    private var broadcastReceiver: BroadcastReceiver? = null
    private val STORAGE_PERMISSION_CODE = 23
    private lateinit var binding: FragmentSetupProfileBinding
    private val args: SetupProfileFragmentArgs by navArgs()
    private var partMapImage: Map<String, Any> = mutableMapOf()
    private lateinit var navController: NavController
    private lateinit var mContext: Context
    private val sessionManagement: SessionManagement by inject()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var specialitiesAdapter: SpecialitiesAdapter
    private lateinit var subSpecialitiesAdapter: SubSpecialitiesAdapter
    private lateinit var seniorityLevelsAdapter: SeniorityLevelsAdapter
    private lateinit var countriesAdapter: CountriesAdapter
    private var certificateFragment = CertificatesFragment()
    private var documentFragment = DocumentsFragment()
    var certificatesFragmentVisible = false
    var legalDocumentsFragmentVisible = false
    var specialityClicked = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics
   // private var imageFile: File? = null
    private var statusname = ""
    private val TAG = SetupProfileFragment::class.java.simpleName

    override val screenViewModel: SetupProfileViewModel
            by sharedViewModel(StringQualifier(SETUP_VIEW_MODEL))
    private lateinit var doctorProfileViewModel: DoctorProfileUpdateViewModel
    var specializationId = 0
    private var subSpecializations = ArrayList<Int>()
    var seniorityLevelId = 0
    var nationalityId = 0
    lateinit var loading: Dialog
    var birthEt = ""
    var gender = ""
    var birthDateTxt = ""
    var birthdateVaild = ""
    var yearOfBirth = ""
    var isFilled = false
    var getInfo: String = "new"
    private lateinit var viewModel: DoctorProfileViewModel
    var doctorId: Int? = null
    private var polyClinicType = 0
    var profileStatus = 0
    var editSpeciality = -1
    var descriptionData: Array<String>? = null
    var countriesList = ArrayList<CountriesData>()
    var specialityList = ArrayList<SubSpecialitiesData>()
    var subSpecialityList = ArrayList<SubSpecialitiesData>()
    var seniorityList = ArrayList<SeniorityLevelsData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        arguments?.let {
//            getInfo = it.getString("edit", "")
//        }
        getInfo = args.editProfile.toString()
        doctorId = sessionManagement.getDoctorId().toString().toInt()

        loading = Dialog(requireContext())
        sessionManagement.getImage()?.let { Log.e("uridata", it) }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_setup_profile, container, false
        )
        viewModel = ViewModelProvider(this)[DoctorProfileViewModel::class.java]
        doctorProfileViewModel =
            ViewModelProvider(this).get(DoctorProfileUpdateViewModel::class.java)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* requireActivity().onBackPressedDispatcher.addCallback {
            this@SetupProfileFragment
        }*/
        bindViews()
        if (!getInfo.equals("edit")) {
            binding.profileToolbar.backBtn.visibility = GONE
            validity()
        }
        initButton()
        isFilled = true
        if (getInfo == MoreFragment.editClick) {
            binding.profileBottomToolbar.contraint2.visibility = View.VISIBLE
            binding.profileBottomToolbar.submitBtn.visibility = View.VISIBLE
            binding.profileBottomToolbar.submitBtn.text = getString(R.string.update)
            binding.yourStateProgressBarId.visibility = View.GONE
            binding.horizontalScrollView.visibility = VISIBLE
            binding.profileToolbar.backBtn.visibility = GONE
            binding.profileToolbar.backBtn.setOnClickListener {
                navController.navigate(R.id.moreFragment)
            }

        }
        if (binding.horizontalScrollView.visibility == VISIBLE) {
            if (binding.llPersonalInfo.visibility == VISIBLE) {
                binding.profileBottomToolbar.submitBtn.visibility = View.VISIBLE

                binding.profileBottomToolbar.submitBtn.setOnClickListener {
                    binding.fgCertificate.visibility = GONE
                    binding.llSpecialty.visibility = VISIBLE
                    binding.llPersonalInfo.visibility = GONE
                    binding.profileBottomToolbar.root.visibility = VISIBLE
                }
            } else if (binding.llSpecialty.visibility == VISIBLE) {
                binding.profileBottomToolbar.submitBtn.setOnClickListener {
                    binding.fgCertificate.visibility = VISIBLE
                    binding.llSpecialty.visibility = GONE
                    binding.llPersonalInfo.visibility = GONE
                    binding.profileBottomToolbar.root.visibility = VISIBLE
                }
            } else if (binding.fgCertificate.visibility == VISIBLE) {
                binding.profileBottomToolbar.submitBtn.setOnClickListener {
                    binding.fgCertificate.visibility = GONE
                    binding.llSpecialty.visibility = GONE
                    binding.llPersonalInfo.visibility = GONE
                    binding.legalBtn.visibility = VISIBLE
                    binding.profileBottomToolbar.root.visibility = VISIBLE
                }
            }
        }

        if (sessionManagement.hasUserData()) {
            setupData()
        }
        if (!DateUtils.getAdminOwnerId().isNullOrEmpty()) {

            if (args.clinicid > 0) {
                binding.profileBottomToolbar.submitBtn.text = getString(R.string.next_)
                binding.horizontalScrollView.visibility = View.GONE
                binding.yourStateProgressBarId.visibility = View.VISIBLE
                binding.profileToolbar.backBtn.visibility = VISIBLE
                binding.yourStateProgressBarId.setStateDescriptionData(descriptionData)

            } else {
                binding.horizontalScrollView.visibility = VISIBLE
                binding.yourStateProgressBarId.visibility = View.GONE
                binding.profileToolbar.backBtn.visibility = VISIBLE

            }
        } else {
            if (getInfo.equals("edit")) {
                binding.horizontalScrollView.visibility = View.VISIBLE
                binding.profileToolbar.backBtn.visibility = VISIBLE

            } else {
                binding.horizontalScrollView.visibility = View.GONE
                binding.yourStateProgressBarId.visibility = View.VISIBLE
                binding.profileBottomToolbar.submitBtn.text = getString(R.string.next_)
                profileStatus =
                    UserData.getProfileStatus(SharedPreferencesKeys.PREF_PROFILE_STATUS.name)
                if (profileStatus == 0) {
                    binding.fgCertificate.visibility = GONE
                    binding.profileBottomToolbar.root.visibility = VISIBLE
                }
                if (profileStatus == 1) {
                    binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                    binding.fgCertificate.visibility = GONE
                    binding.llSpecialty.visibility = VISIBLE
                    binding.llPersonalInfo.visibility = GONE
                    binding.profileBottomToolbar.root.visibility = VISIBLE
                }
                if (profileStatus == 2) {
                    binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
                    CommonFunctions.replaceFragment(
                        requireActivity(),
                        certificateFragment,
                        R.id.fg_certificate
                    )
                    binding.profileBottomToolbar.root.visibility = GONE
                    binding.fgCertificate.visibility = VISIBLE

                }
                if (profileStatus == 3) {
                    binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
                    CommonFunctions.replaceFragment(
                        requireActivity(),
                        documentFragment,
                        R.id.fg_certificate
                    )
                    binding.fgCertificate.visibility = VISIBLE
                    binding.profileBottomToolbar.root.visibility = GONE
                }
            }
        }
//        val stateProgressBar = View.findViewById(R.id.your_state_progress_bar_id) as StateProgressBar
//        stateProgressBar.setStateDescriptionData(descriptionData)

        binding.yourStateProgressBarId.setStateDescriptionData(descriptionData)

        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        loadingDialog = LoadingDialog(mContext)
        noInternetDialog = NoInternetDialog(mContext, this)
        specialitiesAdapter = SpecialitiesAdapter(false, this, mContext)
        subSpecialitiesAdapter = SubSpecialitiesAdapter(true, this, mContext)
        countriesAdapter = CountriesAdapter(false, this, mContext)
        seniorityLevelsAdapter = SeniorityLevelsAdapter(false, this, mContext)

        binding.profileToolbar.help2.setOnClickListener {
            val bottomSheet = HelpBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, "ModalBottomSheet")
        }
        binding.profileToolbar.profileHeaderTv.setOnClickListener {
//            findNavController().navigate(R.id.Specaility)
        }
        if (getInfo.equals("edit")) {
            initEdit()
            binding.profileBottomToolbar.root.visibility = VISIBLE
            binding.profileBottomToolbar.submitBtn.text = getString(R.string.update)
            binding.uploadPhotoTxt.visibility = View.GONE
            binding.yourStateProgressBarId.visibility = View.INVISIBLE
            binding.profileToolbar.profileHeaderTv.text = getString(R.string.edit_profile)
//            binding.profileBottomToolbar.submitBtn.setText("Save")
            binding.profileBottomToolbar.submitBtn.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.new_themeBlue_color
                )
            )
            initEditViews()
        }
        cameraRequestCode = RequestCode.CAMERA_REQUEST_CODE
        galleryRequestCode = RequestCode.MEDIA_REQUEST_CODE
        editGalleryRequestCode = RequestCode.MEDIA_EDIT_REQUEST_CODE

        setUi()
        bindSubSpecializations()
    }

    private fun initButton() {
        binding.subSpecializationSelect.setOnClickListener {
            if (specializationId != 0) {
                screenViewModel.getSubSpecialities(specializationId)
            }
            val d = SubSpecialitiesBottomSheetDialog(subSpecialitiesAdapter, subSpecialityList)
            d.show(childFragmentManager, "")
        }
        binding.profileToolbar.tvHelp.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }
        binding.profileToolbar.backBtn.setOnClickListener {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                navController.navigate(R.id.moreFragment)
            } else {
                navController.navigate(R.id.clinicsFragment)
            }
        }
    }

    private fun initEditViews() {

        if (SignUpFragment.profileImage != null) {
//            imageFile = SignUpFragment.profileImage
        }
        setButtonColor(binding.personalInfoBtn)

        binding.personalInfoBtn.setOnClickListener {
            certificatesFragmentVisible = false
            legalDocumentsFragmentVisible = false
            specialityClicked = false
            setButtonColor(binding.personalInfoBtn)
            binding.llSpecialty.visibility = GONE
            binding.llPersonalInfo.visibility = VISIBLE
            binding.fgCertificate.visibility = GONE
            binding.profileBottomToolbar.root.visibility = VISIBLE
            if (getInfo.equals("edit")) {
                binding.profileBottomToolbar.root.visibility = VISIBLE
            }
        }
        binding.specialtyBtn.setOnClickListener {
            certificatesFragmentVisible = false
            legalDocumentsFragmentVisible = false
            specialityClicked = true
            setButtonColor(binding.specialtyBtn)
            binding.fgCertificate.visibility = GONE
            binding.llSpecialty.visibility = VISIBLE
            binding.llPersonalInfo.visibility = GONE
            binding.profileBottomToolbar.root.visibility = VISIBLE
            editSpeciality = 1
        }

        binding.certificatesBtn.setOnClickListener {
            certificatesFragmentVisible = true
            legalDocumentsFragmentVisible = false
            specialityClicked = false
            binding.yourStateProgressBarId.visibility = View.INVISIBLE
            binding.horizontalScrollView.visibility = VISIBLE
            setButtonColor(binding.certificatesBtn)
//            val bundle = Bundle()
//            bundle.putString("edit", "edit")
            certificatesEdit = "edit"
            CommonFunctions.replaceFragment(
                requireActivity(),
                certificateFragment, R.id.fg_certificate
            )
            binding.profileBottomToolbar.root.visibility = GONE
            binding.fgCertificate.visibility = VISIBLE
        }

        binding.legalBtn.setOnClickListener {
            certificatesFragmentVisible = false
            legalDocumentsFragmentVisible = true
            specialityClicked = false
            binding.yourStateProgressBarId.visibility = View.INVISIBLE
            binding.horizontalScrollView.visibility = VISIBLE
            setButtonColor(binding.legalBtn)
            CommonFunctions.replaceFragment(
                requireActivity(),
                documentFragment, R.id.fg_certificate
            )
            binding.fgCertificate.visibility = VISIBLE
            binding.profileBottomToolbar.root.visibility = VISIBLE

            documentEdit = "edit"
        }
    }

    private fun setButtonColor(textView: TextView) {
        binding.personalInfoBtn.setTextColor(Color.parseColor("#2B2979"))
        binding.specialtyBtn.setTextColor(Color.parseColor("#2B2979"))
        binding.certificatesBtn.setTextColor(Color.parseColor("#2B2979"))
        binding.legalBtn.setTextColor(Color.parseColor("#2B2979"))
        binding.personalInfoBtn.setBackgroundColor(Color.parseColor("#E8E8FD"))
        binding.specialtyBtn.setBackgroundColor(Color.parseColor("#E8E8FD"))
        binding.certificatesBtn.setBackgroundColor(Color.parseColor("#E8E8FD"))
        binding.legalBtn.setBackgroundColor(Color.parseColor("#E8E8FD"))
        textView.setTextColor(Color.parseColor("#FFFFFF"))
        textView.setBackgroundColor(Color.parseColor("#2B2979"))
    }

    private fun setupData() {
        val gson = Gson()
        val jsonData = sessionManagement.getUserData()
        val userLoginResponse = gson.fromJson(jsonData!!, UserDataModel::class.java)

        if (userLoginResponse != null) {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.enNameEt.setText(userLoginResponse.name)
                binding.nameEtArabic.setText(userLoginResponse.nameAr)
                binding.phoneEt.setText(userLoginResponse.phone)
                binding.emailEt.setText(userLoginResponse.email)
                binding.phoneEt.isEnabled = false
                binding.emailEt.isEnabled = true
            }
        }
    }

    private fun initEdit() {
        // TODO
        if (isOnline(mContext)) {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                viewModel.getDoctorProfile()
            } else {
                viewModel.getDoctorProfileByID(DateUtils.getDoctorId().toString())
            }
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        viewModel.doctorProfileModel.observe(viewLifecycleOwner, Observer { it ->
            it?.let { it ->

                Log.d("DoctorProfile", "initEdit: ${it.Data}")

                gender = it.Data.GenderId.toString()
                specializationId = it.Data.SpecialistId
                seniorityLevelId = it.Data.SeniorityLevelId
                it.Data.DoctorSubSpecialist.forEach { sub ->
                    subSpecializations.add(sub)
                }
                nationalityId = it.Data.NationalityId
//                subSpecializations = it.Data.DoctorSubSpecialist as ArrayList<String>
                statusname = it.Data.StatusName.toString()
                if (it.Data.StatusId == 1) {
                    binding.nationalIdEt.isFocusable = true
                    binding.syndicateIdEt.isFocusable = true
                    binding.emailEt.isFocusable = true
                    binding.biographyArEt.isFocusable = true
                    binding.biographyEnEt.isFocusable = true
                }

                it.Data.Image?.let { img -> UserData.setImage(img) }
                ImageUtil.loadPicture(binding.uploadPhotoBg, it.Data.Image)

                binding.enNameEt.setText("${it.Data.FirstName} ${it.Data.MiddelName} ${it.Data.LastName}")

                binding.nameEtArabic.setText("${it.Data.FirstNameAr} ${it.Data.MiddelNameAr} ${it.Data.LastNameAr}")

                binding.specializationSelect.setText(it.Data.SpecialistName)

                binding.subSpecializationSelect.setText(
                    it.Data.SubSpecialistName.toString().removePrefix("[").removeSuffix("]")
                )

                //should work
                var subSpecilization = ""
                if (it.Data.SubSpecialistName.size > 0) {

                    for (index in it.Data.SubSpecialistName.indices) {
                        if (index > 1) {
                            subSpecilization =
                                subSpecilization + "," + it.Data.SubSpecialistName.get(index)
                        } else {
                            subSpecilization = it.Data.SubSpecialistName.get(index)
                        }
                    }
                }
                // binding.subSpecializationSelect.setText(subSpecilization)
                binding.specializationSelect.setText(it.Data.SpecialistName)
                binding.senioritySelect.setText(it.Data.SeniorityLevelName)
                binding.nationalitySelect.setText(it.Data.NationalityName)
                binding.licenseIdEt.setText(it.Data.LicenseNumber)
                binding.biographyArEt.setText(it.Data.DoctorInfoAr)
                binding.biographyEnEt.setText(it.Data.DoctorInfo)
                binding.phoneEt.setText(it.Data.Phone)
                it.Data.NickName?.let {
                    binding.NicknameEtEnglish.setText(it)
                }
                it.Data.NickNameAr?.let {
                    binding.NicknameEtArabic.setText(it)
                }
                gender = it.Data.GenderId.toString()
                specializationId = it.Data.SpecialistId
                if (it.Data.GenderId == 1) {
                    binding.genderEt.text = getString(R.string.male)
                } else {
                    binding.genderEt.text = getString(R.string.female)
                }
                // new update
                binding.nationalIdEt.setText(it.Data.NationalId)
                binding.syndicateIdEt.setText(it.Data.SyndicateId)
                //standard
                binding.profileToolbar.root.visibility = VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    birthEt = it.Data.Birthday
                    birthDateTxt = ConvertDate(it.Data.Birthday)
                    binding.birthEt.setText(ConvertDate(it.Data.Birthday))
//                    binding.birthEt.setText(it.Data.Birthday)
                    binding.birthEt.setText(ConvertDate(it.Data.Birthday))
                }
                if (!it.Data.Email.isNullOrEmpty()) {
                    binding.emailEt.setText(it.Data.Email)
                }
            }
        })

        binding.certificatesBtn.setOnClickListener {
            binding.yourStateProgressBarId.visibility = View.INVISIBLE
            binding.horizontalScrollView.visibility = VISIBLE
            setButtonColor(binding.certificatesBtn)
//            val bundle = Bundle()
//            bundle.putString("edit", "edit")
            certificatesEdit = "edit"
            CommonFunctions.replaceFragment(
                requireActivity(),
                certificateFragment, R.id.fg_certificate
            )
            binding.profileBottomToolbar.root.visibility = GONE
            binding.fgCertificate.visibility = VISIBLE
        }
        binding.legalBtn.setOnClickListener {
            binding.yourStateProgressBarId.visibility = View.INVISIBLE
            binding.horizontalScrollView.visibility = VISIBLE
            setButtonColor(binding.legalBtn)
            CommonFunctions.replaceFragment(
                requireActivity(),
                documentFragment, R.id.fg_certificate
            )
            binding.fgCertificate.visibility = VISIBLE
            binding.profileBottomToolbar.root.visibility = GONE
            documentEdit = "edit"
        }
        viewModel.showLoader.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })
        viewModel.deleteProfileModel.observe(viewLifecycleOwner, Observer {
            sessionManagement.clearUser()
            UserData.logout()
            navController.navigate(R.id.action_logout_3)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun ConvertDate(startTime: String): String {
        val inputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.getDefault())
        val date: LocalDate = LocalDate.parse(startTime, inputFormatter)
        val formattedDate: String = outputFormatter.format(date)

        return formattedDate
    }

    @SuppressLint("InvalidAnalyticsName")
    private fun setUi() {
        if (getInfo.equals("edit")) {
            binding.profileToolbar.tvSaveedit.visibility = GONE
            binding.profileBottomToolbar.submitBtn.visibility = View.VISIBLE
            binding.profileBottomToolbar.submitBtn.text = getString(R.string.update)
            binding.profileToolbar.tvSaveedit.visibility = GONE
            binding.profileBottomToolbar.submitBtn.visibility = View.VISIBLE
            binding.profileBottomToolbar.submitBtn.text = getString(R.string.update)
        }
        binding.profileBottomToolbar.submitBtn.setOnClickListener {
            checkUpdateValidity()
        }
        binding.profileBottomToolbar.submitBtn.setOnClickListener {

            if (getInfo.equals("edit")) {

                if (editSpeciality == 1 && specialityClicked == true) {
                    checkValidity2()
                } else
                    checkUpdateValidity()
                // firebase event
                firebaseAnalytics = Firebase.analytics
                val bundle = Bundle()
                firebaseAnalytics.logEvent("Profile updates", bundle)
            } else {
                if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                    if (profileStatus == 0) {
                        checkValidity1()
                    } else if (profileStatus == 1) {
                        checkValidity2()
                    }
                } else {
                    if (polyClinicType == 0) {
                        checkValidity1()
                    } else if (polyClinicType == 1) {
                        checkValidity2()
                    }
                }

            }
        }

        binding.birthEt.setOnClickListener {
            val d = CalendarDialog(binding.birthEt, binding.birthErrorTxt, this)
            d.show(childFragmentManager, "")
            Log.e("", d.toString())
        }
        binding.genderEt.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(R.layout.gender_layout)
            val done = bottomSheetDialog.findViewById<MaterialButton>(R.id.Done_bottom)
            val male = bottomSheetDialog.findViewById<RadioButton>(R.id.male_btn)
            val female = bottomSheetDialog.findViewById<RadioButton>(R.id.female_btn)
            bottomSheetDialog.show()
            male?.setOnClickListener {
                gender = "1"
                binding.genderEt.text = getString(R.string.male)
            }
            female?.setOnClickListener {
                gender = "2"
                binding.genderEt.text = getString(R.string.female)
            }
            done?.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

        binding.uploadPhotoIv.setOnClickListener {
//            checkMediaPermissions()
            selectPic()
            // showDialog(cameraRequestCode, galleryRequestCode)
        }

        binding.uploadPhotoBg.setOnClickListener {
//            checkMediaPermissions()
            selectPic()
            //  showDialog(cameraRequestCode, galleryRequestCode)
        }
        // todo 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getSpecialities()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        screenViewModel.specialties.observe(viewLifecycleOwner) {

            specialityList.clear()
            it.Data.forEach { item ->
                specialityList.add(item)
            }
            specialitiesAdapter.submitData(specialityList)

        }
        //TODO 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getSeniorityLevels()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }
        screenViewModel.seniorityLevels.observe(viewLifecycleOwner) {
            seniorityList.clear()
            it.Data.forEach { item ->
                seniorityList.add(item)
            }
            seniorityLevelsAdapter.submitData(seniorityList)
        }
        //TODO 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getCountries()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        screenViewModel.countriesResponse.observe(viewLifecycleOwner) {
            countriesList.clear()
            it.Data.forEach { item ->
                countriesList.add(item)
            }
            countriesAdapter.submitData(countriesList)
        }
        binding.nationalitySelect.setOnClickListener {
            val d = NationalitiesBottomSheetDialog(countriesAdapter, countriesList)
            d.show(childFragmentManager, "")
        }
        binding.specializationSelect.setOnClickListener {
            subSpecialityList.clear()
            val d = SpecialitiesBottomSheetDialog(specialitiesAdapter, specialityList)
            d.show(childFragmentManager, "")
        }
        binding.senioritySelect.setOnClickListener {
            val d = SeniorityLevelsBottomSheetDialog(seniorityLevelsAdapter, seniorityList)
            d.show(childFragmentManager, "")
        }
    }

    override fun onPickDate(birthDate: String) {
        Log.e(TAG, "onPickDate: $birthDate")
        birthDateTxt = birthDate
        val date = getDate(birthDate)
        birthdateVaild = date
        yearOfBirth = date.substring(0, 4)
        DateUtils.setYear(yearOfBirth)
        Log.e("date", date)
//        if (birthDateTxt.isEmpty())
        binding.birthErrorTxt.visibility = GONE
    }

    // screenViewModel.createExtraInfo   (Speciality)
    private fun checkValidity2() {

        if (
            seniorityLevelId != 0 && specializationId != 0
            && subSpecializations.isNotEmpty()
        ) {
            Log.e(TAG, "checkValidityRequest: $gender ")

            loadingDialog.show()
            val createExtraInfoModel = CreateExtraInfoModel(
                "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)!!}",
                ExtraInfoData(
                    seniorityLevelId,
                    specializationId,
                    subSpecializations,
                    binding.biographyEnEt.text.toString(),
                    binding.biographyArEt.text.toString(), doctorId
                )
            )

            if (isOnline(mContext)) {
                loadingDialog.show()
                screenViewModel.createExtraInfo(createExtraInfoModel)
                val gson = Gson()
                val Json: String = gson.toJson(createExtraInfoModel)
                Log.d("getJson", "checkValidity2: $Json")
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
            }

            screenViewModel.createExtraInfoResponse.observe(viewLifecycleOwner) {
                Log.e(TAG, "setUi: $it")
                if (it != null) {
                    loading.dismiss()
                    loadingDialog.dismiss()
                    if (it.Success) {
                        if (it.MessageCode == 200) {

                            sessionManagement.setIsProfileFinished(it.Data?.ProfileStatus.toString())
                            sessionManagement.setDoctorId(it.Data?.Id.toString())
                            Log.d(TAG, "checkValidity2: " + it.Data?.Id.toString())
                        } else if (it.MessageCode == 201) {
                            val sucess = UpdateDialog(findNavController(), "")
                            sucess.show(childFragmentManager, "0")
                        }

                        if (editSpeciality == 1) {

                        } else {
                            binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)

                            CommonFunctions.replaceFragment(
                                requireActivity(),
                                certificateFragment,
                                R.id.fg_certificate
                            )
                            binding.fgCertificate.visibility = VISIBLE
                            binding.profileBottomToolbar.root.visibility = GONE
                        }
                        toast(mContext, it.Message)
                    } else {
                        loading.dismiss()
                        loadingDialog.dismiss()
                        Log.d(TAG, "checkValidity2: ${it.Message}")
                    }

                }
            }
        } else {
            showError(getString(R.string.please_fill_correct_data))
        }

    }

    // screenViewModel.createDoctorProfile
    private fun checkValidity1() {

        if (isOnline(mContext)) {
            if (isvalidateFeilds())
                if (
                    binding.enNameEt.text.toString().isNotEmpty() &&
                    binding.nameEtArabic.text.toString().isNotEmpty() &&
                    binding.nationalIdEt.text.toString().isNotEmpty() &&
                    binding.nationalIdEt.text.toString().length == 14 &&
                    binding.syndicateIdEt.text.toString().isNotEmpty() &&
                    (binding.birthEt.text.toString().isNotEmpty() && birthDateTxt.isNotEmpty())
                    && (gender == "1" || gender == "2")
                    && nationalityId != 0 && imageFile != null
                ) {
                    var str = binding.enNameEt.text.toString()
                    val name = str.split(Regex("\\s+")).filter { it.isNotEmpty() }

                    var strArabic = binding.nameEtArabic.text.toString()
                    val nameAr = strArabic.split(Regex("\\s+")).filter { it.isNotEmpty() }

                    Log.d("getJson", "checkValidity1: $nameAr")
                    if (nameAr.size >= 3 && binding.enNameEt.text.toString().length >= 3) {
                        val setupDoctorInfoModel = CreateDoctorProfileModel(
                            "",
                            DoctorProfileModel(
                                sessionManagement.getValue(USER_ID)!!,
                                name[0],
                                nameAr[0],
                                name[1],
                                nameAr[1],
                                name[2],
                                nameAr[2],
                                gender,
                                birthDateTxt,
                                "",
//                        binding.linkEt.text.toString(),
                                binding.biographyEnEt.text.toString(),
                                binding.biographyArEt.text.toString(),
                                nationalityId.toString(),
                                subSpecializations,
                                imageFile,
                                binding.nationalIdEt.text.toString(),
                                binding.syndicateIdEt.text.toString(),
                                binding.licenseIdEt.text.toString(),
                                binding.NicknameEtEnglish.text.toString(),
                                binding.NicknameEtArabic.text.toString(),
                            )
                        )
                        nationalityId = nationalityId.toString().toInt()
                        if (isOnline(mContext)) {
                            loadingDialog.show()
                            if (args.clinicid > 0) {
                                certificatesEdit = ""
                                documentEdit = ""
                                sendData(
                                    name[0],
                                    nameAr[0],
                                    name[1],
                                    nameAr[1],
                                    name[2],
                                    nameAr[2], 0
                                )
                            } else {
                                certificatesEdit = ""
                                documentEdit = ""
                                //   screenViewModel.createDoctorProfile(setupDoctorInfoModel)
                                sendData(
                                    name[0],
                                    nameAr[0],
                                    name[1],
                                    nameAr[1],
                                    name[2],
                                    nameAr[2], 2
                                )
                            }
                            val gson = Gson()
                            val Json: String = gson.toJson(setupDoctorInfoModel)
                            Log.d("getJson", "checkValidity1: $Json")
                            sessionManagement.setString(binding.enNameEt.text.toString())
                        } else {
                            noConnectivityDialog.noInternetDialogFragment(requireContext())
                        }
                    } else {
                        if (binding.enNameEt.text.toString().length < 3) {
                            // binding.enNameEt.error = getString(R.string.please_enter_valid_name)
                        }
                        if (nameAr.size < 3) {
                            //  binding.nameEtArabic.error = getString(R.string.name_size)
                            binding.tvFullNameARError.visibility = View.VISIBLE
                            binding.tvFullNameARError.text =
                                getString(R.string.Enter_fullName_Arabic)
                        }


                    }
                } else {


                    binding.llPersonalInfo.visibility = VISIBLE
                    binding.llSpecialty.visibility = GONE

                    toast(mContext, getString(R.string.fields_required))
                    Log.e(TAG, "checkValidityRequest: $gender ")


                    // new National Id
                    if (binding.nationalIdEt.text.toString().isEmpty())
                        binding.nationalIdEt.error = getString(R.string.nationalid_required)

                    if (binding.birthEt.text.toString().isEmpty() || birthDateTxt.isEmpty())
//                    binding.birthEt.error = getString(R.string.birth_required)

//                if (binding.birthEt.text.toString().isEmpty() || birthDateTxt.isEmpty()){
                        binding.birthErrorTxt.visibility = VISIBLE
                    binding.birthErrorTxt.text = getString(R.string.birth_required)
//                }

                    if (!(gender == "1" || gender == "2"))
//                    binding.genderErrorTxt.visibility = VISIBLE
//                if ((gender == "1" || gender == "2"))
//                    binding.genderErrorTxt.visibility = GONE

                        if (nationalityId == 0)
                            binding.nationalityErrorTxt.visibility = VISIBLE
//                if (nationalityId != 0 || binding.nationalitySelect.text.toString().isNotEmpty())
//                    binding.nationalityErrorTxt.visibility = GONE

// Todo error handle
                    if (seniorityLevelId == 0)
//                    binding.seniorityErrorTxt.visibility = VISIBLE
//                if (seniorityLevelId != 0)
//                    binding.seniorityErrorTxt.visibility = GONE

                        if (specializationId == 0)
//                    binding.specialityErrorTxt.visibility = VISIBLE
//                if (specializationId != 0)
//                    binding.specialityErrorTxt.visibility = GONE

                            if (subSpecializations.isEmpty())
//                    binding.subSpecialityErrorTxt.visibility = VISIBLE
//                if (subSpecializations.isNotEmpty())
//                    binding.subSpecialityErrorTxt.visibility = GONE

                                if (!isFilled) {
                                    binding.photoErrorTxt.visibility = VISIBLE
                                }

                    if (binding.nationalIdEt.text.toString().length < 14)
                        binding.nationalIdEt.error = getString(R.string.nationalid_lenght)

                    if (binding.syndicateIdEt.text.toString().isEmpty())
                    // binding.syndicateIdEt.error = getString(R.string.syndicate_required)

//                    if (binding.syndicateIdEt.text.toString().length < 2)
//                     //   binding.syndicateIdEt.error = getString(R.string.syndicate_lenght)


//                    if (!isEnglishValid(binding.biographyEnEt.text.toString()) && binding.biographyEnEt.text.toString() != "") {
//                        binding.biographyEnEt.error = getString(R.string.english_name)
//                    }
//
//                    if (!isArabicValid(binding.biographyArEt.text.toString()) && binding.biographyArEt.text.toString() != "") {
//                        binding.biographyArEt.error = getString(R.string.arabic_name)
//                    }
                        if (imageFile == null) {
                            toast(requireContext(), getString(R.string.please_upload_profile_pic))
                            binding.photoErrorTxt.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.cancel_appointment
                                )
                            )
                            binding.uploadPhotoTxt.visibility = View.VISIBLE
                        }
                    binding.photoErrorTxt.setTextColor(
                        ContextCompat.getColor(
                            binding.photoErrorTxt.context,
                            R.color.green
                        )
                    )
                }
        } else {
            noInternetDialog.show(
                childFragmentManager, ""
            )
        }


    }

    // screenViewModel.updateDoctorProfile
    private fun checkUpdateValidity() {
        if (!certificatesFragmentVisible && !legalDocumentsFragmentVisible && !specialityClicked) {
            if (isOnline(mContext)) {
                if (
                    binding.enNameEt.text.toString().isNotEmpty() &&
                    binding.nameEtArabic.text.toString().isNotEmpty() &&
                    binding.nationalIdEt.text.toString().isNotEmpty() &&
                    binding.nationalIdEt.text.toString().length == 14 &&
                    binding.syndicateIdEt.text.toString().isNotEmpty()
                    && isEnglishValid(binding.enNameEt.text.toString())
                    && isArabicValid(binding.nameEtArabic.text.toString())
                ) {
                    var firstnameEn = ""
                    var middleNameEt = ""
                    var lastNameEt = ""
                    var firstnameAr = ""
                    var middleNameArEt = ""
                    var lastNameArEt = ""
                    var str = binding.enNameEt.text.toString()
                    val parts = str.split(Regex("\\s+")).filter { it.isNotEmpty() }
                    var strArabic = binding.nameEtArabic.text.toString()
                    val partsAR = strArabic.split(Regex("\\s+")).filter { it.isNotEmpty() }
                    if (partsAR.size >= 3 && parts.size >= 3) {
                        firstnameEn = parts[0]
                        middleNameEt = parts[1]
                        lastNameEt = parts[2]

                        firstnameAr = partsAR[0]
                        middleNameArEt = partsAR[1]
                        lastNameArEt = partsAR[2]
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Complete your name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    if (imageFile != null) {
                        binding.uploadPhotoTxt.visibility = View.GONE
                        //TODO
                        if (isOnline(mContext)) {
                            //     screenViewModel.updateDoctorProfile(setupDoctorInfoModel)
                            sendUpdateData(
                                firstnameEn,
                                firstnameAr,
                                middleNameEt,
                                middleNameArEt,
                                lastNameEt,
                                lastNameArEt, 1
                            )
                        } else {
                            noConnectivityDialog.noInternetDialogFragment(requireContext())
                        }


                    } else {
                        if (isOnline(mContext)) {
                            // screenViewModel.updateDoctorProfile(setupDoctorInfoModel)
                            sendUpdateData(
                                firstnameEn,
                                firstnameAr,
                                middleNameEt,
                                middleNameArEt,
                                lastNameEt,
                                lastNameArEt, 1
                            )
                        } else {
                            noConnectivityDialog.noInternetDialogFragment(requireContext())
                        }
                    }

                    if (getInfo.equals("edit")) {
                        if (statusname.contains("Approved")) {

                        }
                    } else {

                        binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
                        CommonFunctions.replaceFragment(
                            requireActivity(),
                            certificateFragment,
                            R.id.fg_certificate
                        )
//                    navController.navigate(R.id.certificatesFragment)
                    }
//                screenViewModel.createDoctorProfileResponse.observe(viewLifecycleOwner) {
//                    Log.e(TAG, "setUi: $it")
//                    when (it) {
//
//                        is Resource.Success -> {
//                            loadingDialog.dismiss()
////                            navController.navigate(R.id.certificatesFragment)
//                            val sucess = UpdateDialog(findNavController(), "")
//                            sucess.show(childFragmentManager, "0")
//                            toast(mContext, it.data.Message)
//                        }
//                        is Resource.Error -> {
//                            loadingDialog.dismiss()
//                            Log.e(TAG, "checkValidityError: ${it.message} ")
//                        }
//
//                    }
//                }

//                binding.genderErrorTxt.visibility = GONE
//                binding.seniorityErrorTxt.visibility = GONE
//                binding.specialityErrorTxt.visibility = GONE
//                binding.specialityErrorTxt.visibility = GONE
                    binding.photoErrorTxt.visibility = GONE
                    binding.birthErrorTxt.visibility = GONE

//                loadingDialog.show()
                } else {

                    Log.e(TAG, "checkValidityRequest: $gender ")

//                if (binding.uploadPhotoIv.visibility== VISIBLE){
//                    toast(mContext, getString(R.string.upload_your_image))
//                }

                    if (binding.nationalIdEt.text.toString().isEmpty())
                        binding.nationalIdEt.error = getString(R.string.nationalid_required)

//                    if (binding.syndicateIdEt.text.toString().isEmpty())
//                        binding.syndicateIdEt.error = getString(R.string.syndicate_required)

//                    if (!isEmailValid(binding.emailEt.text.toString())) {
//                        binding.emailEt.error = getString(R.string.email_valid)
//                    }
//
//                    if (binding.emailEt.text.toString().isEmpty()) {
//                        binding.emailEt.error = getString(R.string.email_required)
//                    }

                    if (binding.nationalIdEt.text.toString().length < 14)
                        binding.nationalIdEt.error = getString(R.string.nationalid_lenght)


                    if (!isEnglishValid(binding.biographyEnEt.text.toString()) && binding.biographyEnEt.text.toString() != "") {
                        binding.biographyEnEt.error = getString(R.string.english_name)
                    }

                    if (!isArabicValid(binding.biographyArEt.text.toString()) && binding.biographyArEt.text.toString() != "") {
                        binding.biographyArEt.error = getString(R.string.arabic_name)
                    }
                    toast(mContext, getString(R.string.fields_required))
                }
            } else {

                noInternetDialog.show(
                    childFragmentManager, ""
                )
            }
        } else {
            if (certificatesFragmentVisible) {
                val intent = Intent("CertificateFragment")
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(intent)
            } else if (legalDocumentsFragmentVisible) {
                val intent = Intent("legalDocumentFragment")
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(intent)
            } else if (specialityClicked) {

            }
        }
    }

    override fun onCountrySelect(countryId: Int, countryTxt: String, position: Int) {
        nationalityId = countryId
        binding.nationalitySelect.text = countryTxt
        //remove error message
        if (nationalityId != 0) {
            binding.nationalityErrorTxt.visibility = GONE
        }
    }

    override fun onSenioritySelect(seniorityId: Int, seniorityTxt: String, position: Int) {
        seniorityLevelId = seniorityId
        binding.senioritySelect.text = seniorityTxt

        //remove error message
        if (seniorityLevelId != 0) {
//            binding.seniorityErrorTxt.visibility = GONE
        }
    }

    override fun onSpecialitySelect(specialityId: Int, specialityTxt: String, position: Int) {
        specializationId = specialityId
        binding.specializationSelect.text = specialityTxt
        binding.subSpecializationSelect.text = ""
        //TODO
        if (isOnline(mContext)) {
            screenViewModel.getSubSpecialities(specialityId)
        } else {
            noInternetDialog.show(childFragmentManager, "")
        }
        //remove error message
        if (specializationId != 0) {
//            binding.specialityErrorTxt.visibility = GONE
        }
    }

    //TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    override fun onSubSpecialitySelect(
        subSpecialityId: Int,
        subSpecialityTxt: String,
        position: Int
    ) {
        subSpecializations = subSpecialitiesAdapter.getAdditionIds()
        Log.e(TAG, "onSubSpecialitySelect: ${subSpecializations.size}")
        binding.subSpecializationSelect.text = subSpecialitiesAdapter.getAdditionTexts()
        //remove error message
        if (subSpecialitiesAdapter.getAdditionIds().isNotEmpty()) {
//            binding.subSpecialityErrorTxt.visibility = GONE
        }
    }

    private fun bindSubSpecializations() {
        screenViewModel.subSpecialties.observe(viewLifecycleOwner) {
            Log.e(TAG, "bindSubSpecializations: $it")
            subSpecialityList.clear()
            if (!it.Data.isNullOrEmpty()) {
                it.Data.forEach { item ->
                    subSpecialityList.add(item)
                }
            }
            subSpecialitiesAdapter.submitData(subSpecialityList)
            subSpecialitiesAdapter.notifyDataSetChanged()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.e("broadcastreciever", "onReceive")

                if (intent.hasExtra("stepStatus")) {
                    val step = intent.getStringExtra("stepStatus")
                    if (step == "3") {
                        binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
                        CommonFunctions.replaceFragment(
                            requireActivity(),
                            documentFragment,
                            R.id.fg_certificate
                        )
                        binding.fgCertificate.visibility = VISIBLE
                        binding.profileBottomToolbar.root.visibility = GONE
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        //requireContext().unregisterReceiver(broadcastReceiver)
        broadcastReceiver = null
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context?.registerReceiver(
                broadcastReceiver,
                IntentFilter(".Fragment.SetupProfile"),
                RECEIVER_EXPORTED
            )
        } else {
            context?.registerReceiver(broadcastReceiver, IntentFilter(".Fragment.SetupProfile"))
        }

    }

    override fun onStop() {
        super.onStop()
        broadcastReceiver?.let { receiver ->
            requireContext().unregisterReceiver(receiver)
        }
    }


    override fun onPause() {
        super.onPause()
        val name = "${binding.nameEtArabic.text.toString()}"
        UserData.setUserBirthDate(birthEt)
        if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
            UserData.setUserNameAR(name)
            UserData.setUserName(binding.enNameEt.text.toString())
        }

    }

    private fun isvalidateFeilds(): Boolean {

        var value = false

        if (imageFile == null) {
            binding.uploadPhotoTxt.visibility = View.VISIBLE
        } else {
            binding.uploadPhotoTxt.visibility = View.GONE
        }
        if (binding.enNameEt.text?.trim().isNullOrEmpty()) {
            binding.tvFullNameError.text = getString(R.string.requiredEnglish)
            binding.tvFullNameError.visibility = View.VISIBLE
            binding.mCardName.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.enNameEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )
        } else if (binding.enNameEt.text?.trim()?.split(" ")!!.size < 3) {
            binding.tvFullNameError.visibility = View.VISIBLE
            binding.tvFullNameError.text = getString(R.string.Enter_fullName_english)
            binding.mCardName.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.enNameEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )
        } else {
            binding.tvFullNameError.visibility = View.GONE
            binding.mCardName.setBackgroundResource(R.drawable.bg_edittext)
            binding.enNameEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        if (binding.nameEtArabic.text?.trim().isNullOrEmpty()) {
            binding.tvFullNameARError.text = getString(R.string.requiredarabic)
            binding.tvFullNameARError.visibility = View.VISIBLE
            binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )


        } else if (binding.nameEtArabic.text?.trim()!!.split(" ").size < 3) {
            binding.tvFullNameARError.text = getString(R.string.Enter_fullName_Arabic)
            binding.tvFullNameARError.visibility = View.VISIBLE
            binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )
        } else {
            binding.tvFullNameARError.visibility = View.GONE
            binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        if (binding.phoneEt.text?.trim()!!.isEmpty()) {
            binding.tvPohneError.text = getString(R.string.required)
            // binding.tvPohneError.visibility = View.VISIBLE
            binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )

        } else if (binding.phoneEt.text?.length!! < 11) {
            binding.tvPohneError.text = getString(R.string.phonedigits)
            binding.tvPohneError.visibility = View.GONE
            binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )

        } else if (!isPhoneValid(binding.phoneEt.text.toString())) {
            binding.tvPohneError.text = getString(R.string.phone_valid)
            // binding.tvPohneError.visibility = View.VISIBLE
            binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )
        } else {
            binding.tvPohneError.visibility = View.GONE
            binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

        }
//        if (binding.emailEt.text?.trim()!!.isEmpty()) {
//            binding.tvEmailError.text = getString(R.string.required)
//            binding.tvEmailError.visibility = View.VISIBLE
//            binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//            binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(
//                0,
//                0,
//                R.drawable.ic_error,
//                0
//            )
//
//        } else if (!(binding.emailEt.text?.toString()?.isValidEmail() == true)) {
//            binding.tvEmailError.text = getString(R.string.match_email)
//            binding.tvEmailError.visibility = View.VISIBLE
//            binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//            binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(
//                0,
//                0,
//                R.drawable.ic_error,
//                0
//            )
//
//        } else {
//            binding.tvEmailError.visibility = View.GONE
//            binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext)
//            binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
//
//        }

        if (binding.syndicateIdEt.text.toString().isEmpty()) {
            binding.tvSyndicateError.text = getString(R.string.syndicate_required)
            binding.tvSyndicateError.visibility = View.VISIBLE
            binding.mCardSyndicateIdname.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.syndicateIdEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )
        } else if (binding.syndicateIdEt.text?.length!! in 2..8) {
            binding.tvSyndicateError.visibility = View.GONE
            binding.mCardSyndicateIdname.setBackgroundResource(R.drawable.bg_edittext)
            binding.syndicateIdEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            binding.tvSyndicateError.text = getString(R.string.syndicatefromto)
            binding.tvSyndicateError.visibility = View.VISIBLE
            binding.mCardSyndicateIdname.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.syndicateIdEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )
        }

        if (binding.licenseIdEt.text.toString().isEmpty()) {
            binding.tvLicenseError.text = getString(R.string.licences_required)
            binding.tvLicenseError.visibility = View.VISIBLE
            binding.mCardLicence.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.licenseIdEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )

        } else {
            binding.tvLicenseError.visibility = View.GONE
            binding.mCardLicence.setBackgroundResource(R.drawable.bg_edittext)
            binding.licenseIdEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        if (!birthdateVaild.isNullOrEmpty()) {
            if (binding.nationalIdEt.text.isNullOrEmpty() || binding.nationalIdEt.text?.length!! < 14) {
                binding.tvNatoinalidError.text = getString(R.string.enter14number)
                binding.tvNatoinalidError.visibility = View.VISIBLE
                binding.nationalIdEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            } else if (birthdateVaild.substring(2, 8) != binding.nationalIdEt.text?.substring(
                    1,
                    7
                )
            ) {
                binding.tvNatoinalidError.text =
                    getString(R.string.birthdateandnationaldoesnotmatch)
                binding.tvNatoinalidError.visibility = View.VISIBLE
                binding.nationalIdEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            } else {
                binding.tvNatoinalidError.visibility = View.GONE
                binding.nationalIdEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                value = true
            }
        } else {
            binding.birthErrorTxt.visibility = View.VISIBLE
            binding.birthErrorTxt.text = getString(R.string.required)
        }


        return value

    }

    fun String.isValidEmail() =
        !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun getDate(item: String): String {
        return item.replace("-", "")
    }

    private fun validity() {
        binding.enNameEt.doOnTextChanged { text, start, before, count ->
            if (binding.enNameEt.text?.trim().isNullOrEmpty()) {
                binding.tvFullNameError.text = getString(R.string.requiredEnglish)
                binding.tvFullNameError.visibility = View.VISIBLE
                binding.mCardName.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.enNameEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            } else if (binding.enNameEt.text?.trim()?.split(" ")!!.size < 3) {
                binding.tvFullNameError.visibility = View.VISIBLE
                binding.tvFullNameError.text = getString(R.string.Enter_fullName_english)
                binding.mCardName.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.enNameEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            } else {
                binding.tvFullNameError.visibility = View.GONE
                binding.mCardName.setBackgroundResource(R.drawable.bg_edittext)
                binding.enNameEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }


        }

        binding.syndicateIdEt.doOnTextChanged { text, start, before, count ->
            if (binding.syndicateIdEt.text.toString().isEmpty()) {
                binding.tvSyndicateError.text = getString(R.string.syndicate_required)
                binding.tvSyndicateError.visibility = View.VISIBLE
                binding.mCardSyndicateIdname.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.syndicateIdEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )

            } else if (binding.syndicateIdEt.text?.length!! in 2..8) {
                binding.tvSyndicateError.visibility = View.GONE
                binding.mCardSyndicateIdname.setBackgroundResource(R.drawable.bg_edittext)
                binding.syndicateIdEt.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            } else {
                binding.tvSyndicateError.text = getString(R.string.syndicatefromto)
                binding.tvSyndicateError.visibility = View.VISIBLE
                binding.mCardSyndicateIdname.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.syndicateIdEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            }

        }
        binding.licenseIdEt.doOnTextChanged { text, start, before, count ->
            if (binding.licenseIdEt.text.toString().isEmpty()) {
                binding.tvLicenseError.text = getString(R.string.licences_required)
                binding.tvLicenseError.visibility = View.VISIBLE
                binding.mCardLicence.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.licenseIdEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )

            } else {
                binding.tvLicenseError.visibility = View.GONE
                binding.mCardLicence.setBackgroundResource(R.drawable.bg_edittext)
                binding.licenseIdEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

        }


        binding.nameEtArabic.doOnTextChanged { text, start, before, count ->
            if (binding.nameEtArabic.text?.trim().isNullOrEmpty()) {
                binding.tvFullNameARError.text = getString(R.string.requiredarabic)
                binding.tvFullNameARError.visibility = View.VISIBLE
                binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            } else if (binding.nameEtArabic.text?.trim()!!.split(" ").size < 3) {
                binding.tvFullNameARError.text = getString(R.string.Enter_fullName_Arabic)
                binding.tvFullNameARError.visibility = View.VISIBLE
                binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            } else {
                binding.tvFullNameARError.visibility = View.GONE
                binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }

        binding.phoneEt.doOnTextChanged { text, start, before, count ->
            if (binding.phoneEt.text?.trim()!!.isEmpty()) {
                binding.tvPohneError.text = getString(R.string.required)
                // binding.tvPohneError.visibility = View.VISIBLE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )

            } else if (binding.phoneEt.text?.length!! < 11) {
                binding.tvPohneError.text = getString(R.string.phonedigits)
                // binding.tvPohneError.visibility = View.VISIBLE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )

            } else if (!isPhoneValid(binding.phoneEt.text.toString())) {
                binding.tvPohneError.text = getString(R.string.phone_valid)
                //   binding.tvPohneError.visibility = View.VISIBLE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            } else {
                binding.tvPohneError.visibility = View.GONE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }
        }
//        binding.emailEt.doOnTextChanged { text, start, before, count ->
//            if (binding.emailEt.text?.trim()!!.isEmpty()) {
//                binding.tvEmailError.text = getString(R.string.required)
//                binding.tvEmailError.visibility = View.VISIBLE
//                binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//                binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
//
//            } else if (!(binding.emailEt.text?.toString()?.isValidEmail() == true)) {
//                binding.tvEmailError.text = getString(R.string.match_email)
//                binding.tvEmailError.visibility = View.VISIBLE
//                binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//                binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
//
//            } else {
//                binding.tvEmailError.visibility = View.GONE
//                binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext)
//                binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
//
//            }
//        }
        binding.nationalIdEt.doOnTextChanged { text, start, before, count ->
            if (!birthDateTxt.isNullOrEmpty() && binding.nationalIdEt.text?.length!! > 9) {
                if (birthdateVaild.substring(2, 8) != binding.nationalIdEt.text?.substring(1, 7)) {
                    binding.tvNatoinalidError.text =
                        getString(R.string.birthdateandnationaldoesnotmatch)
                    binding.tvNatoinalidError.visibility = View.VISIBLE
                    binding.nationalIdEt.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_error,
                        0
                    )
                    binding.mCardNationalIdname.setBackgroundResource(R.drawable.bg_edittext_error)
                } else {
                    binding.mCardNationalIdname.setBackgroundResource(R.drawable.bg_edittext)
                    binding.tvNatoinalidError.visibility = View.GONE
                    binding.nationalIdEt.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                }
            }
        }

    }

    private fun bindViews() {
        descriptionData = arrayOf(
            getString(R.string.personal_info),
            getString(R.string.speciality),
            getString(R.string.certificates),
            getString(R.string.legal_doc)
        )
        if (!DateUtils.getAdminOwnerId().isNullOrEmpty()) {
            binding.mcardphoneLayout.visibility = View.GONE
            binding.mcardEmailLayout.visibility = View.GONE
        }
    }

    private fun sendData(
        firstnameEn: String,
        firstnameAr: String,
        middleNameEt: String,
        middleNameArEt: String,
        lastNameEt: String,
        lastNameArEt: String, type: Int
    ) {
        println("type: ${type}")
        checkDataForDoctorProfile(
            firstnameEn, firstnameAr, middleNameEt, middleNameArEt, lastNameEt, lastNameArEt, type
        )
        doctorProfileViewModel.doctorUpdateProfileResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
//                    val logger = AppEventsLogger.newLogger(requireContext())
//                    logger.logEvent("Profile updates")
                    if (it.data?.messageCode == 200) {
                        profileStatus = it.data.data?.profileStatus!!
                        loadingDialog.dismiss()
                        binding.llSpecialty.visibility = VISIBLE
                        binding.llPersonalInfo.visibility = GONE
                        DateUtils.setDoctorId(it.data.data!!.id.toString())
                        doctorId = it.data.data!!.id
                        polyClinicType = 1
                        binding.yourStateProgressBarId.setCurrentStateNumber(
                            StateProgressBar.StateNumber.TWO
                        )

                    } else if (it.data?.messageCode == 201) {
                        val sucess = UpdateDialog(findNavController(), "")
                        sucess.show(childFragmentManager, "0")
                    }
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
            }
        })
    }

    private fun sendUpdateData(
        firstnameEn: String,
        firstnameAr: String,
        middleNameEt: String,
        middleNameArEt: String,
        lastNameEt: String,
        lastNameArEt: String, type: Int
    ) {
        checkDataForDoctorProfile(
            firstnameEn, firstnameAr, middleNameEt, middleNameArEt, lastNameEt, lastNameArEt, type
        )
        doctorProfileViewModel.doctorUpdateProfileResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
//                    val logger = AppEventsLogger.newLogger(requireContext())
//                    logger.logEvent("Profile updates")
                    loadingDialog.dismiss()
                    if (it.data?.messageCode == 200) {
                        DateUtils.setDoctorId(it.data.data!!.id.toString())
                        doctorId = it.data.data!!.id
                    } else if (it.data?.messageCode == 201) {
                        val sucess = UpdateDialog(findNavController(), "")
                        sucess.show(childFragmentManager, "0")
                    }
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
            }
        })
    }

    private fun checkDataForDoctorProfile(
        firstnameEn: String,
        firstnameAr: String,
        middleNameEt: String,
        middleNameArEt: String,
        lastNameEt: String,
        lastNameArEt: String, type: Int
    ) {
        if (args.clinicid != 0) {
            partMapImage = partMapImage + mapOf("clinicId" to args.clinicid)
        }

        if (firstnameEn != null) {
            partMapImage = partMapImage + mapOf("FirstName" to firstnameEn)
        }
        if (firstnameAr != null) {
            partMapImage = partMapImage + mapOf("FirstNameAr" to firstnameAr)
        }
        if (middleNameEt != null) {
            partMapImage = partMapImage + mapOf("MiddelName" to middleNameEt)
        }
        if (middleNameArEt != null) {
            partMapImage = partMapImage + mapOf("MiddelNameAr" to middleNameArEt)
        }
        if (lastNameEt != null) {
            partMapImage = partMapImage + mapOf("LastName" to lastNameEt)
        }
        if (lastNameArEt != null) {
            partMapImage = partMapImage + mapOf("LastNameAr" to lastNameArEt)
        }
        if (gender != null) {
            partMapImage = partMapImage + mapOf("GenderId" to gender)
        }
        if (birthDateTxt != null) {
            partMapImage = partMapImage + mapOf("Birthday" to birthDateTxt)
        }
        if (nationalityId.toString() != null) {
            partMapImage = partMapImage + mapOf("NationalityId" to nationalityId)
        }
        if (!binding.NicknameEtEnglish.text.toString().isNullOrEmpty()) {
            partMapImage =
                partMapImage + mapOf("NickName" to binding.NicknameEtEnglish.text.toString())
        }
        if (!binding.nationalIdEt.text.toString().isNullOrEmpty()) {
            partMapImage =
                partMapImage + mapOf("NationalId" to binding.nationalIdEt.text.toString())
        }
        if (!binding.NicknameEtArabic.text.toString().isNullOrEmpty()) {
            partMapImage =
                partMapImage + mapOf("NickNameAr" to binding.NicknameEtArabic.text.toString())
        }
        if (!binding.emailEt.text.toString().isNullOrEmpty()) {
            partMapImage = partMapImage + mapOf("Email" to binding.emailEt.text.toString())
        }
        if (!binding.syndicateIdEt.text.toString().isNullOrEmpty()) {
            partMapImage =
                partMapImage + mapOf("SyndicateId" to binding.syndicateIdEt.text.toString())
        }
        if (!binding.licenseIdEt.text.toString().isNullOrEmpty()) {
            partMapImage =
                partMapImage + mapOf("LicenseNumber" to binding.licenseIdEt.text.toString())
        }
        imageFile?.let {
            partMapImage = partMapImage + mapOf("profileImage" to it)
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMapImage) {
            val key = item.key
            if (key.contains("profileImage")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }
        val userId = DateUtils.getUserId()?.toIntOrNull()?:0
        doctorProfileViewModel.updateDoctorProfileFirstStep(
            builder.build(), type, args.clinicid,
            userId
        )
    }


    override fun onImageSelected(file: File) {
        Glide.with(requireContext())
            .load(file)
            .into(binding.uploadPhotoBg)
    }

}