package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.salamtak.R
import com.salamtak.core.localDataBase.SharedPreferencesKeys
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentLoginBinding
import com.salamtak.domain.entities.response.UserDataModel
import com.salamtak.domain.entities.resquest.auth.LoginModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.HospitalViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import org.koin.android.ext.android.inject


class NewLogInFragment : Fragment(), DialogsListener, View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: NewLoginViewModel
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    lateinit var loading: Dialog
    lateinit var mContext:Context
    private lateinit var noInternetDialog: NoInternetDialog
    private val sessionManagement: SessionManagement by inject()
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private var typeid:Int?=2
    private lateinit var hospitalViewModel: HospitalViewModel
    private var awaitingHospitalInfo = false
//    private lateinit var viewModele: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loading = Dialog(requireContext())
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(NewLoginViewModel::class.java)
        hospitalViewModel = ViewModelProvider(this).get(HospitalViewModel::class.java)
//        viewModele = ViewModelProvider(this).get(HomeViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {findNavController().navigate(R.id.loginFragment) }
        initBottomSheetServices()
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
       if (!DateUtils.getPhone().isNullOrEmpty()){
           binding.phoneEt.setText(DateUtils.getPhone())
       }
        initListeners()
        initObservers()
//        viewModele.getHome()
        binding.tvHelp.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }
        initButton()
        chooseLanuage()
    }



    private fun initButton(){
        binding.createNew.setOnClickListener {
            DateUtils.setAdminOwnerId("")
            bottomsheetbeahavoir.state =
                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
        }

        binding.layoutBottomsheetpersistant.cardview.setOnClickListener {
            val action=NewLogInFragmentDirections.actionLoginFragmentToSignUpFragment(2)
            findNavController().navigate(action)
            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.layoutBottomsheetpersistant.cardview2.setOnClickListener {
            val action=NewLogInFragmentDirections.actionLoginFragmentToSignUpFragment(4)
            findNavController().navigate(action)
            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN

        }
    }

    fun initListeners() {
        binding.toLoginBtn.setOnClickListener(this)
     //   binding.createNew.setOnClickListener(this)
        binding.forgetPassword.setOnClickListener(this)
        binding.toolbar.ivBack.visibility = View.GONE
        binding.toolbar.ivBackBg.visibility = View.GONE
    }

    fun initObservers() {

        viewModel.signInModel.observe(viewLifecycleOwner, Observer {

            sessionManagement.createSession(
                it.Data.Id.toString(),
                it.Data.Token,
                it.Data.Phone)

            UserData.setToken(it.Data.Token)
            if (it.Data.Image != null) {
                UserData.setImage(it.Data.Image!!)
            }

            UserData.setProfileStatus(SharedPreferencesKeys.PREF_PROFILE_STATUS.name, it.Data.ProfileStatus)
            sessionManagement.setStatus(it.Data.ProfileStatus.toString())
            DateUtils.setPhone(it.Data.Phone)
            sessionManagement.setLoggedIn()

            UserData.setUserId(it.Data.Id.toLong())

            //new
            sessionManagement.setDoctorId("0")
            var email=it.Data.Email
            if (email==null){
                email="notfound"
                val userData = UserDataModel(
                   email,
                    it.Data.Name,
                    "",
                    it.Data.Phone
                )
                val gson = Gson()
                val jsonData = gson.toJson(userData)
                if (!sessionManagement.hasUserData()) {
                    sessionManagement.saveUserData(jsonData)
                    sessionManagement.setString(it.Data.Name)
                }
            }
            val userData = UserDataModel(
                email,
                it.Data.Name,
                "",
                it.Data.Phone
            )
            val gson = Gson()
            val jsonData = gson.toJson(userData)
            if (!sessionManagement.hasUserData()) {
                sessionManagement.saveUserData(jsonData)
                sessionManagement.setString(it.Data.Name)
            }

//            if (it.Data.ProfileStatus == 0) {
//                    findNavController().navigate(R.id.setupProfileFragment)
//            }
//            if (it.Data.ProfileStatus == 1) {
//                findNavController().navigate(R.id.certificatesFragment)
//            }
//            if (it.Data.ProfileStatus == 2) {
//                findNavController().navigate(R.id.documentsFragment)
//            }
//            if (it.Data.ProfileStatus == 3) {
//                findNavController().navigate(R.id.homeFragment)3
//            }
            // test use
//            if (it.Data.ProfileStatus == 3) {
//                findNavController().navigate(R.id.homeFragment)
//            }else
            if (typeid==HOSPITAL_TYPE_ID){
                // Hospital must not set AdminOwnerId: a non-empty one means polyclinic everywhere.
                DateUtils.setUserId("")
                DateUtils.setAdminOwnerId("")
                DateUtils.setHospitalUserId(it.Data.Id.toString())
                DateUtils.setHospitalOwnerId(it.Data.OwnerID.toString())
                DateUtils.setHospitalInfoId(it.Data.InfoID.toString())
                UserData.setUserName(it.Data.Name)
                it.Data.NameAR?.let { nameAr -> UserData.setUserNameAR(nameAr) }
                awaitingHospitalInfo = true
                hospitalViewModel.getHospitalInfoById(it.Data.InfoID)
            }else if (typeid==4){
                DateUtils.setUserId(it.Data.Id.toString())
                DateUtils.setAdminOwnerId(it.Data.OwnerID.toString())
                DateUtils.setAdminInfoId(it.Data.InfoID.toString())
                sharedDataViewmodel.setHealthEntityId(it.Data.InfoID)
                if (it.Data.ProfileStatus==0){
                    findNavController().navigate(R.id.enterpriseInfoFragment)
                } else if (it.Data.ProfileStatus==1){
                    val action=NewLogInFragmentDirections.actionLoginFragmentToPolyClinicAdminFragment(it.Data.OwnerID)
                    findNavController().navigate(action)
                }else{
                    DateUtils.setStatus("4")
                    findNavController().navigate(R.id.homeFragment)
                }
                UserData.setUserName(it.Data.Name)
                it?.Data?.NameAR?.let {
                    UserData.setUserNameAR(it)
                }

            }else {
                UserData.setUserName(it.Data.Name)
                it?.Data?.NameAR?.let {
                    UserData.setUserNameAR(it)
                }
                if (it.Data.ProfileStatus == 4) {
                    DateUtils.setStatus(it.Data.ProfileStatus.toString())
                    findNavController().navigate(R.id.homeFragment)
                } else {
                    val action =NewLogInFragmentDirections.actionLoginFragmentToSetupProfileFragment(0,"")
                    findNavController().navigate(action)
                }
                DateUtils.setUserId("")
                DateUtils.setAdminOwnerId("")
            }

        })

        hospitalViewModel.hospitalInfoResponse.observe(viewLifecycleOwner, Observer {
            if (!awaitingHospitalInfo) return@Observer
            when (it) {
                is Resource.Loading -> loading.showLoadingDialog()
                is Resource.sucess -> {
                    awaitingHospitalInfo = false
                    loading.hideLoadingDialog()
                    val info = it.data?.Data
                    val hospitalId = info?.HospitalId
                    if (hospitalId == null) {
                        Snackbar.make(requireView(), getString(R.string.hospital_info_error), Snackbar.LENGTH_SHORT).show()
                        return@Observer
                    }
                    DateUtils.setHospitalId(hospitalId.toString())
                    info.Name?.takeIf { name -> name.isNotBlank() }?.let { name -> UserData.setUserName(name) }
                    info.NameAr?.takeIf { name -> name.isNotBlank() }?.let { name -> UserData.setUserNameAR(name) }
                    DateUtils.setStatus(DateUtils.HOSPITAL_STATUS)
                    findNavController().navigate(R.id.homeFragment)
                }
                is Resource.Error -> {
                    awaitingHospitalInfo = false
                    loading.hideLoadingDialog()
                    Snackbar.make(requireView(), it.message ?: getString(R.string.hospital_info_error), Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
           // errorMessage(it.Message.toString())
            Snackbar.make(requireView(),it.Message.toString(),Snackbar.LENGTH_SHORT).show()
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.to_login_btn -> {
                //TODO
                if(isOnline(mContext)){

                    if (isValid()) {

//                    if (DoesNetworkHaveInternet.execute())
                       if (typeid!=null){
                           viewModel.newSignIn(fillSignInRequest())
                       }else{
                           Toast.makeText(requireContext(),
                               getString(R.string.chooseaccountfirst),Toast.LENGTH_SHORT).show()
                       }
                    }
                }
                else{
                    noInternetDialog.show(childFragmentManager, "")
                }

            }

            R.id.create_new -> {
             //   findNavController().navigate(R.id.signUpFragment)
            }

            R.id.forget_password -> {
                findNavController().navigate(R.id.forgetPasswordFragment)
            }

        }

    }

    fun isValid(): Boolean {
        var isValid = true

        if (!binding.phoneEt.isPhoneValid()) {
            isValid = false
//            binding.phoneEt.error = resources.getString(R.string.phone_required)

            binding.ivPhoneError.visibility = View.VISIBLE
            binding.errorPhone.visibility = View.VISIBLE

        }
        if (!binding.passwordEt.isPasswordValid()) {
            isValid = false
          //  binding.passwordEt.error = resources.getString(R.string.password_required)
            binding.ivPasserror.visibility=View.VISIBLE
            binding.errorPassword.visibility=View.VISIBLE
//            binding.passwordEt.hint=getString(R.string.password_required)
//            binding.passwordEt.setHintTextColor(ContextCompat.getColor(requireContext(), `in`.aabhasjindal.otptextview.R.color.red))

        }else{
            binding.ivPasserror.visibility = View.GONE
        }
        return isValid
    }


    fun fillSignInRequest(): LoginModel {
               return LoginModel(
                Phone = binding.phoneEt.text.toString(),
                Password = binding.passwordEt.text.toString(), typeid!!, DateUtils.getFcmToken().let {
                    it
            }
            )
    }
    private fun initBottomSheetServices() {
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.registerBottomsheet)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN

    }
    private fun chooseLanuage() {

        binding.radio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_english -> typeid = 2
                R.id.radio_arabic -> typeid = 4
                R.id.radio_hospital -> typeid = HOSPITAL_TYPE_ID
            }
            listOf(binding.radioEnglish, binding.radioArabic, binding.radioHospital).forEach { button ->
                val selected = button.id == checkedId
                button.background = ContextCompat.getDrawable(
                    requireContext(),
                    if (selected) R.drawable.bg_choose_account_radiobtn else R.drawable.bg_choose_account_unselected
                )
                button.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), if (selected) R.color.green else R.color.new_gray)
                )
            }
        }
    }

    companion object {
        private const val HOSPITAL_TYPE_ID = 7
    }


    //TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

}