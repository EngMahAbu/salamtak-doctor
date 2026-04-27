package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.reset_password

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentForgetPasswordBinding
import com.salamtak.domain.entities.resquest.auth.ForgetPasswordModel
import com.salamtak.domain.entities.resquest.auth.RegisterModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.help.HelpBottomSheetFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import kotlin.concurrent.timer

class ForgetPasswordFragment : Fragment(), DialogsListener, View.OnClickListener {

    lateinit var binding: FragmentForgetPasswordBinding
    lateinit var loading: Dialog
    lateinit var viewModel: ForgetPasswordViewModel
    var REQUEST_PHONE_CALL = 1
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var polyclinicsviewModel: PolyClinicsViewModel
    lateinit var mContext: Context
    private var typeid:Int?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forget_password, container, false
        )

        viewModel = ViewModelProvider(this).get(ForgetPasswordViewModel::class.java)
        polyclinicsviewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        loading = Dialog(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext,this)

        initialListener()
        initialObserver()

        binding.tvHelp.setOnClickListener {
           findNavController().navigate(R.id.dialogCalling)
        }

        binding.forgetPasswordTv.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun initialListener() {
        binding.toolbar.toolbarHeader.text = getString(R.string.forget_password)
        binding.sendBtn.setOnClickListener(this)

        binding.toolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

//        binding.numTv.setOnClickListener {
//            checkPermisiion("17143")
//        }

    }


//    fun checkPermisiion(phone: String) {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(), Manifest.permission.CALL_PHONE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL
//        )
//        else {
//            makeCall(phone)
//        }
//
//    }


//    private fun makeCall(phone: String) {
//        val intent = Intent(Intent.ACTION_CALL)
//        intent.setData(Uri.parse("tel:$phone"))
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.CALL_PHONE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show()
//            return
//        }
//        startActivity(intent)
//    }

    private fun initialObserver() {

        viewModel.forgetPasswordModel.observe(viewLifecycleOwner, Observer {
            val bundle = Bundle()
            bundle.putString(OTP_CODE, it.Data.Code.toString())
            Log.d("otp", "OTP_CODE:${it.Data.Code}")
            bundle.putString(USER_ID, it.Data.UserId.toString())
            bundle.putString("phone_email",binding.emailEt.text.toString())
            findNavController().navigate(R.id.resetOtpFragment , bundle)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            it.Message?.let { it1 -> errorMessage(it1) }
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
            R.id.send_btn -> {
                if (isOnline(mContext)) {
//                    if (typeid!=null){
                        checkEmailOrPhone()
//                    }else{
//                        Toast.makeText(requireContext(),
//                            getString(R.string.choose_account_first), Toast.LENGTH_SHORT).show()
//                    }
                }
                else{
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }

            }

//            R.id.email_rb -> {
//                binding.emailLayout.visibility = View.VISIBLE
//                binding.phoneLayout.visibility = View.GONE
//                binding.phoneLabel.visibility = View.GONE
//            }
//            R.id.phone_rb -> {
//                binding.emailLayout.visibility = View.GONE
//                binding.phoneLayout.visibility = View.VISIBLE
//                binding.phoneLabel.visibility = View.VISIBLE
//            }

        }
    }


    private fun checkEmailOrPhone(){
        if (binding.emailEt.text.toString().isEmpty() ){
            binding.emailEt.error = getString(R.string.email_required)
        }else{
            if (isEmailValid(binding.emailEt.text.toString())){
                viewModel.forgetPassword(fillForgetPasswordRequest(ForgetPasswordModel()))
            }else{
                if (isPhoneValid(binding.emailEt.text.toString())){
                    viewModel.forgetPassword(fillPhoneForgetRequest(ForgetPasswordModel()))
                }
                else{
                    errorMessage("Invalid email or phone number")
//                    toast(requireContext(),"Check your phone number.")
                }
            }
        }
    }

    private fun payTheReservation() {


        if(isOnline(mContext)){
            viewModel.forgetPassword(fillForgetPasswordRequest(ForgetPasswordModel()))
        }
        else{
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

//        when {
//            binding.emailRb.isChecked ->
//                if(isOnline(mContext)){
//                    viewModel.forgetPassword(fillForgetPasswordRequest(ForgetPasswordModel()))
//                }
//                else{
//                    noConnectivityDialog.noInternetDialogFragment(requireContext())
//                }
//
//            binding.phoneRb.isChecked ->
//                if(isOnline(mContext)){
//                    viewModel.forgetPassword(fillPhoneForgetRequest(ForgetPasswordModel()))
//                }
//            else{
//                           noConnectivityDialog.noInternetDialogFragment(requireContext())
//
//                }
//
//        }

    }

//    fun isValid(): Boolean {
//        if (binding.emailRb.isChecked || binding.phoneRb.isChecked) {
//            return true
//        } else {
//            return false
//        }
//    }

    private fun fillForgetPasswordRequest(forgetPasswordModel: ForgetPasswordModel): ForgetPasswordModel {

        forgetPasswordModel.Email = binding.emailEt.text.toString()
        forgetPasswordModel.UserTypeId = 2
        forgetPasswordModel.Phone = ""
        forgetPasswordModel.ResetMethod = 1

        return forgetPasswordModel
    }


    private fun fillPhoneForgetRequest(forgetPasswordModel: ForgetPasswordModel): ForgetPasswordModel {

        forgetPasswordModel.Email = ""
        forgetPasswordModel.UserTypeId = 2
        forgetPasswordModel.Phone = binding.emailEt.text.toString()
        forgetPasswordModel.ResetMethod = 2

        return forgetPasswordModel
    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }
    private fun chooseLanuage() {

        binding.radio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_english -> {
                    // Pirates are the best
                    typeid = 2
                }
                R.id.radio_arabic -> {

                    typeid = 4
                }
            }
        }
    }

    private fun sentotp() {

        polyclinicsviewModel.otpResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {


                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {

                }

            }
        })

        polyclinicsviewModel.sendOTP(RegisterModel(ForgetPasswordModel().Phone))

    }

}