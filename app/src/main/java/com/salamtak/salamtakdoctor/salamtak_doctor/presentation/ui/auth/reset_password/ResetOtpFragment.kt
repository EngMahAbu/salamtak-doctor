package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.reset_password

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentResetPasswordOtpBinding
import com.salamtak.domain.entities.resquest.auth.ForgetPasswordModel
import com.salamtak.domain.entities.resquest.auth.RegisterModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.OTP_CODE
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.USER_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isEmailValid
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isPhoneValid
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toast
import `in`.aabhasjindal.otptextview.OTPListener
import retrofit2.HttpException
import java.util.concurrent.TimeUnit


class ResetOtpFragment : Fragment(), DialogsListener, View.OnClickListener {

    lateinit var binding: FragmentResetPasswordOtpBinding
    private lateinit var loadingDialog: LoadingDialog
    lateinit var viewModel: ForgetPasswordViewModel
    private lateinit var polyclinicsviewModel: PolyClinicsViewModel
    var scode: String = ""
    var email: String = ""
    var userId: String = ""
    var textOtp: String = ""
    var phoneEmail: String = ""
    private val FORMAT = "%02d:%02d"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reset_password_otp,
            container,
            false
        )

        arguments?.let {
            scode = it.getString(OTP_CODE, "")
            userId = it.getString(USER_ID, "")
            phoneEmail = it.getString("phone_email", "")
        }
        viewModel = ViewModelProvider(this).get(ForgetPasswordViewModel::class.java)
        polyclinicsviewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())
        binding.otpSendAgain.isEnabled=true
        initListeners()
        initialObserver()
        setUi()
        binding.arrow.setOnClickListener {
            findNavController().navigate(R.id.forgetPasswordFragment)
        }

    }

    fun setUi() {
        binding.phoneNumber.text = phoneEmail
        binding.tvHelp.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }

        binding.toolbar.toolbarHeader.text = getString(R.string.reset_password)
//        binding.otpSent.visibility = GONE

        binding.toolbar.toolbarHeader.text = getString(R.string.verify_mobile)

        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {

                val code = scode
                textOtp = otp
                if (otp.toString() != code) {
                    binding.otpError.visibility = View.VISIBLE
                }
            }
        }

        binding.cardResendCode.setOnClickListener {
            binding.cardResendCode.visibility = View.GONE
           sendOtpAgain()
          //  sentotp()
        }

        binding.forgetPasswordTv.setOnClickListener {
            findNavController().popBackStack()
//            findNavController().navigate(R.id.action_resetOtpFragment_to_forgetPasswordFragment)
        }

    }
    val  timer= object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.otpTimer.visibility = VISIBLE
            binding.otpSendAgain.isEnabled = false
            // binding.otpTimer.text = "seconds remaining: " + millisUntilFinished / 1000
            binding.otpTimer.text = ""+String.format(FORMAT,
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))

            // for show time in ( hours:minutes:seconds)
//                binding.otpTimer.text = ""+String.format(FORMAT,
//                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
//                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
//                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
//                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
//                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
        }

        override fun onFinish() {
            // to block code after limit time
            scode = "0"
            binding.otpSendAgain.isEnabled = true
            binding.cardResendCode.visibility = VISIBLE
            binding.otpSendAgain.setTextColor( ContextCompat.getColor(binding.otpSendAgain.context, R.color.green))

        }
    }

    private fun initListeners() {
        binding.sendBtn.setOnClickListener(this)
    }

    private fun initialObserver() {

        viewModel.forgetPasswordModel.observe(viewLifecycleOwner, Observer {
            loadingDialog.dismiss()
            scode = it.Data.Code.toString()
            Log.d("scode",scode)
            timer.start()
            setUi()
            toast(requireContext(), getString(R.string.otp_sent_again))
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.send_btn -> {
                Log.d(TAG, "onClick1: " + textOtp)
                Log.d(TAG, "onClick2: " + scode)
                if (textOtp == scode) {
                    val bundle = Bundle()
                    bundle.putString(USER_ID, userId)
                    findNavController().navigate(R.id.newPasswordFragment, bundle)
                } else {
                    binding.otpError.visibility = VISIBLE
                }

            }
        }
    }


    private fun sendOtpAgain() {

        if (isOnline(requireContext())) {
            loadingDialog.show()

            try {
                if (isOnline(requireContext())) {
                    checkEmailOrPhone()
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }
            } catch (e: HttpException) {
                Log.e(TAG, "SendAgain: ${e.message}")
            }
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

    }

    private fun checkEmailOrPhone() {
        if (!phoneEmail.isNullOrEmpty()) {

            if (isEmailValid(phoneEmail)) {
                viewModel.forgetPassword(fillForgetPasswordRequest(ForgetPasswordModel()))
            } else {
                if (isPhoneValid(phoneEmail)) {
                //    viewModel.forgetPassword(fillPhoneForgetRequest(ForgetPasswordModel()))
                   sentotp()
                } else {
                    errorMessage("Invalid email or phone number")
//                    toast(requireContext(),"Check your phone number.")
                }
            }
        }
    }

    private fun fillForgetPasswordRequest(forgetPasswordModel: ForgetPasswordModel): ForgetPasswordModel {

        forgetPasswordModel.Email = phoneEmail
        forgetPasswordModel.UserTypeId = 2
        forgetPasswordModel.Phone = ""
        forgetPasswordModel.ResetMethod = 1

        return forgetPasswordModel
    }

    private fun fillPhoneForgetRequest(forgetPasswordModel: ForgetPasswordModel): ForgetPasswordModel {

        forgetPasswordModel.Email = ""
        forgetPasswordModel.UserTypeId = 2
        forgetPasswordModel.Phone = phoneEmail
        forgetPasswordModel.ResetMethod = 2
        return forgetPasswordModel
    }
    private fun sentotp() {

        polyclinicsviewModel.otpResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    loadingDialog.dismiss()
                    scode = it.data?.Data?.Code.toString()
                    Log.d("scode",scode)
                    timer.start()
                    setUi()
                    toast(requireContext(), getString(R.string.otp_sent_again))
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    loadingDialog.dismiss()
                }

            }
        })

        polyclinicsviewModel.sendOTP(RegisterModel(phone = "+2${fillPhoneForgetRequest(ForgetPasswordModel()).Phone}"))

    }
    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}