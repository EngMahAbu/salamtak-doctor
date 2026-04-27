package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.reset_password

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentNewPasswordBinding
import com.salamtak.domain.entities.resquest.auth.NewPasswordModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.help.HelpBottomSheetFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.more.MoreFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.ConfirmedDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog

class NewPasswordFragment : Fragment(), DialogsListener {

    lateinit var binding: FragmentNewPasswordBinding
    private lateinit var viewModel: ForgetPasswordViewModel
    var userId: String? = null
    var loged: String = ""
    var REQUEST_PHONE_CALL = 1
    lateinit var loading: Dialog
    private lateinit var noInternetDialog: NoInternetDialog
    lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            userId = it.getString(USER_ID, null)
            loged = it.getString("loged", "")
        }



        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_password, container, false)
        viewModel = ViewModelProvider(this).get(ForgetPasswordViewModel::class.java)
        loading = Dialog(requireContext())


//        if (loged == "loged") {
//            binding.oldPasswordTv.visibility = View.VISIBLE
//            binding.oldPasswordLayout.visibility = View.VISIBLE
//
////            binding.footerBg.visibility = View.GONE
//        }

        if (MoreFragment.changePassword == "edit") {
            binding.oldPasswordLayout.visibility = View.VISIBLE
        }else {
            binding.oldPasswordLayout.visibility = View.GONE
            binding.errorOldPassword.visibility = View.GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)

        setUi()
        initObserve()

    }

    private fun initObserve() {

        viewModel.forgetPasswordModel.observe(viewLifecycleOwner, Observer {
//            val sucess = ConfirmedDialog(findNavController(), loged)
//            sucess.show(childFragmentManager, "0")
            findNavController().navigate(R.id.passwordResetSuccess)
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            it.Message?.let { it1 -> errorMessage(it1) }
        })

    }

    fun setUi() {

//        binding.toolbar.toolbarHeader.text = getString(R.string.new_password)

        binding.ivBackBg.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvHelp.setOnClickListener {
            val bottomSheet = HelpBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, "ModalBottomSheet")
        }

//        binding.numTv.setOnClickListener {
//            checkPermisiion("17143")
//        }

        binding.submit.setOnClickListener {
            if (isOnline(mContext)) {

                if ((binding.passwordEt.text.toString().isNotEmpty() &&
                            binding.confirmPasswordEt.text.toString().isNotEmpty())
                ) {
                    binding.tvError.visibility = View.GONE
                    binding.errorOldPassword.visibility = View.GONE
                    binding.errorNewPassword.visibility = View.GONE
                    binding.ivError.visibility = View.GONE
                    binding.ivPasserror.visibility = View.GONE
                    binding.ivOldPassError.visibility = View.GONE
                    if ((binding.passwordEt.text.toString() == binding.confirmPasswordEt.text.toString())) {

                        if (loged == "loged") {
                            //TODO
                            if (isOnline(mContext)) {
                                viewModel.newPassword(fillNewPasswordRequest(NewPasswordModel()))
                            } else {
                                noConnectivityDialog.noInternetDialogFragment(requireContext())
                            }

                        } else {
                            //TODO
                            if (isOnline(mContext)) {
                                viewModel.firstNewPassword(fillNewPasswordRequest(NewPasswordModel()))
                            } else {
                                noConnectivityDialog.noInternetDialogFragment(requireContext())

                            }

                        }
//                    val sucess = ConfirmedDialog(findNavController(), loged)
//                    sucess.show(childFragmentManager, "0")

                    } else {
//                    binding.passwordEt.error = getString(R.string.password_identical)
//                    binding.confirmPasswordEt.error = getString(R.string.password_identical)
                        binding.tvError.visibility = View.VISIBLE
                        binding.ivError.visibility = View.VISIBLE
//                        binding.confirmPasswordEt.hint = getString(R.string.password_identical)
//                        binding.passwordEt.hint = getString(R.string.password_identical)
                        binding.tvError.text = getString(R.string.new_confirm_password)
//                        binding.tvError.setTextColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                `in`.aabhasjindal.otptextview.R.color.red
//                            )
//                        )
//                        binding.passwordEt.setHintTextColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                `in`.aabhasjindal.otptextview.R.color.red
//                            )
//                        )
//                        binding.confirmPasswordEt.setHintTextColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                `in`.aabhasjindal.otptextview.R.color.red
//                            )
//                        )


                    }

                } else {

                    if (binding.passwordEt.text.toString().isEmpty()) {
                        //   binding.passwordEt.error = getString(R.string.password_required)
                        binding.ivPasserror.visibility = View.VISIBLE
                        binding.errorNewPassword.visibility = View.VISIBLE
//                        binding.passwordEt.hint = getString(R.string.password_required)
//                        binding.passwordEt.setHintTextColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                `in`.aabhasjindal.otptextview.R.color.red
//                            )
//                        )

                    } else {
                        binding.ivPasserror.visibility = View.GONE
                    }
                    if (binding.confirmPasswordEt.text.toString().isEmpty()) {
                        //   binding.confirmPasswordEt.error = getString(R.string.password_required)
                        binding.ivError.visibility = View.VISIBLE
                        binding.tvError.visibility = View.VISIBLE
//                        binding.confirmPasswordEt.hint = getString(R.string.password_required)
//                        binding.confirmPasswordEt.setHintTextColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                `in`.aabhasjindal.otptextview.R.color.red
//                            )
//                        )

                    } else {
                        binding.ivError.visibility = View.GONE
                    }
                    if (binding.oldPasswordEt.text.toString().isEmpty()) {
                        binding.ivOldPassError.visibility = View.VISIBLE
                        binding.errorOldPassword.visibility = View.VISIBLE

                    } else {
                        binding.ivOldPassError.visibility = View.GONE
                    }

                }
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())


            }
        }
        binding.imageView11.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }


    private fun fillNewPasswordRequest(newPasswordModel: NewPasswordModel): NewPasswordModel {

        newPasswordModel.Password = binding.passwordEt.text.toString()
        newPasswordModel.UserId = userId?.toInt()
        newPasswordModel.OldPassword = binding.oldPasswordEt.text.toString()

        return newPasswordModel
    }

    fun checkPermisiion(phone: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL
        )
        else {
            makeCall(phone)
        }

    }

    private fun makeCall(phone: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:$phone"))
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show()
            return
        }
        startActivity(intent)
    }


}