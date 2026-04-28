package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.otp

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentOtpBinding
import com.salamtak.domain.entities.resquest.auth.ForgetPasswordModel
import com.salamtak.domain.entities.resquest.auth.RegisterModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SignUpViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class OtpFragment : BaseFragment<SignUpViewModel>(0), DialogsListener {

    lateinit var binding: FragmentOtpBinding
    lateinit var navController: NavController
    private lateinit var polyclinicsviewModel: PolyClinicsViewModel
    lateinit var mContext: Context
    var code: String = ""
    var textOtp: String = ""

    var phone: String = ""
    var email: String = ""
    var password: String = ""
    var name: String = ""
    var typeId: Int = 2
    var newCode: String = ""

    override val screenViewModel: SignUpViewModel by sharedViewModel(
        StringQualifier(
            ViewModelsModule.AUTH_VIEW_MODEL
        )
    )

    private val sessionManagement: SessionManagement by inject()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var noInternetDialog: NoInternetDialog

    private val TAG = OtpFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(layoutInflater,container,false)
        polyclinicsviewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        code = sessionManagement.getCode().toString()
//        val mArguments = requireArguments()
//        code = mArguments.getString(NEW_OTP_CODE, "")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = binding.root.context
        navController = Navigation.findNavController(binding.root)
        noInternetDialog = NoInternetDialog(mContext, this)
        loadingDialog = LoadingDialog(mContext)
        code = sessionManagement.getCode().toString()
        Log.d(TAG, "onViewCreateddddddd: " + code)
        setUi()
        zx()
        sentotp()
        binding.arrow.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    fun setUi() {

        binding.forgetPasswordTv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvHelp.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }

        binding.toolbar.toolbarHeader.text = getString(R.string.verify_mobile)

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.otpTimer.visibility = VISIBLE
//                binding.otpTimer.text = "seconds remaining: " + millisUntilFinished / 1000
                binding.otpTimer.text = "" + millisUntilFinished / 1000
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                // to block code after limit time
                sessionManagement.setCode("0", "0")
             //   binding.otpSendAgain.isEnabled = true
                binding.otpSendAgain.visibility = VISIBLE
              //  binding.otpSendAgain.setTextColor( ContextCompat.getColor(binding.otpSendAgain.context, R.color.green))

            }
        }.start()

        val mArguments = requireArguments()
        val phone = mArguments.getString(PHONE, "")
        binding.otpSent.text = "$phone"
        binding.toolbar.ivBack.setOnClickListener {
            navController.popBackStack()
        }


        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {

//                val code = sessionManagement.getCode()

                if (Localization.getLanguageName()=="ar"){
                    code.reversed()
                }
                val code = code
//                val againCode = newCode
                textOtp = otp

                Log.d(TAG, "onOTPComplete: $otp")
//                        || otp.toString() != againCode
                if (otp.toString() != code) {
                    Log.d(TAG, "new onOTPComplete: $code")
                    binding.otpError.visibility = VISIBLE
                } else {
                    Log.d(TAG, "subscribeToObserver: $code")
                    loadingDialog.show()
                    subscribeToObserver()
                }

            }
        }

        binding.sendBtn.setOnClickListener {

//            val code = sessionManagement.getCode()

//            binding.otpView.otp = code

            if (binding.otpView.otp.isNullOrEmpty()) {

                binding.otpError.visibility = VISIBLE
                binding.otpError.text = getString(R.string.otp_error)

            } else {

                if (binding.otpView.otp.toString() != code) {
                    binding.otpError.visibility = VISIBLE
                } else {
                    loadingDialog.show()
                    subscribeToObserver()
                }

            }

        }


        binding.otpSendAgain.setOnClickListener {

            sendOtpAgain()
           // setUi()
        }


    }

    //TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    private fun subscribeToObserver() {

        if (isOnline(mContext)) {
            loadingDialog.show()
            try {
                val mArguments = requireArguments()
                phone = mArguments.getString(PHONE, "")
                email = mArguments.getString(EMAIL, "")
                password = mArguments.getString(PASSWORD, "")
                name = mArguments.getString(DOCTOR_NAME, "")
                typeId = mArguments.getInt(TypeId, 2)
                val token = sessionManagement.getFireBaseToken()

                val registerModel = RegisterModel(
                    phone, email,
                    password, name,
                    typeId
                )
                //TODO 29/11/22
                if (isOnline(mContext)) {
                    screenViewModel.createUser(registerModel)
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }

                screenViewModel.createUserResponse.observe(viewLifecycleOwner) {

                    if (it != null) {

                        if (it.Success) {
                            loadingDialog.dismiss()
                            Log.e(TAG, "subscribeToObserver: ${it.Data}")
                            sessionManagement.createSession(
                                it.Data.Id.toString(),
                                it.Data.Token,
                                it.Data.Phone
                            )
                            DateUtils.setUserId(it.Data.Id.toString())
                            // new comment Mansour
                            UserData.setToken(it.Data.Token)
                            UserData.setImage("")
                            UserData.setUserName(it.Data.Name)
                            // UserData.setUserNameAR(it.Data.NameAR)
                            // new
                            sessionManagement.setDoctorId("0")
//                            val sucess = ConfirmedDialog(findNavController(), "toCompleteProfile")
//                            sucess.show(childFragmentManager, "0")
                            if (typeId==2){
                                val action=OtpFragmentDirections.actionOtpFragmentToSetupProfileFragment(0,"")
                                navController.navigate(action)
                            }else{
                                navController.navigate(R.id.enterpriseInfoFragment)
                            }

                        }

                    }

                }

            } catch (e: HttpException) {
                Log.e(TAG, "subscribeToObserver: ${e.message}")
                loadingDialog.dismiss()
            }

        } else {
            noInternetDialog.show(
                childFragmentManager, ""
            )
        }

    }


    private fun sendOtpAgain() {

        if (isOnline(mContext)) {
            loadingDialog.show()

            try {
                val mArguments = requireArguments()
                val phone = mArguments.getString(PHONE, "")
                val email = mArguments.getString(EMAIL, "")
                val password = mArguments.getString(PASSWORD, "")
                val name = mArguments.getString(DOCTOR_NAME, "")
                typeId = mArguments.getInt(TypeId, 2)
                val token = sessionManagement.getFireBaseToken()

                val registerModel = RegisterModel(
                    "+2${phone}", email,
                    password, name,
                    typeId
                )
                //TODO 29/11/22
                if (isOnline(mContext)) {
                 //   screenViewModel.signUp(registerModel)
                    polyclinicsviewModel.sendOTP(registerModel)
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }

//                screenViewModel.createUser(registerModel)
//                screenViewModel.signUpResponse.observe(viewLifecycleOwner, Observer {
//                    if (it != null) {
//                        if (it.Success) {
//                    loadingDialog.dismiss()
//                            sessionManagement.setCode(it.Data.Code.toString(), it.Data.UserId.toString())

//                    val bundle = Bundle()
//                    bundle.putString(EMAIL, email)
//                    bundle.putString(PHONE, phone)
//                    bundle.putString(PASSWORD, password)
//                    bundle.putString(DOCTOR_NAME, name)
//                    bundle.putString(NEW_OTP_CODE, it.Data.Code.toString())
//                    Log.d(TAG, "onViewCreatessss: " + it.Data.Code)
//                    navController.navigate(R.id.otpFragment, bundle)
//                        }
//                    }
//                })

                screenViewModel.failure.removeObservers(viewLifecycleOwner)


                toast(mContext, getString(R.string.otp_sent_again))

            } catch (e: HttpException) {
                Log.e(TAG, "SendAgain: ${e.message}")
            }
        } else {
            noInternetDialog.show(
                childFragmentManager, ""
            )
        }

    }

    private fun sentotp() {

        polyclinicsviewModel.otpResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    loadingDialog.dismiss()
                    code = it.data?.Data?.Code.toString()
                    setUi()
                    toast(requireContext(), getString(R.string.otp_sent_again))
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    loadingDialog.dismiss()
                }

            }
        })


    }

    private fun zx() {
        screenViewModel.signUpResponse.observe(viewLifecycleOwner, Observer {
            loadingDialog.dismiss()
            Log.d(TAG, "onViewCreatessssxxx: " + it.Data.Code)
            sessionManagement.setCode(it.Data.Code.toString(), it.Data.UserId.toString())
//            newCode = it.Data.Code.toString()
        })
    }



}