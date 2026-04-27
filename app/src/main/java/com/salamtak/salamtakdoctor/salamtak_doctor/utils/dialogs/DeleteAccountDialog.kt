package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.DialogDeleteBinding
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.doctorProfile.DoctorProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import org.koin.android.ext.android.inject

class DeleteAccountDialog(
    private val navController: NavController,
    var viewModel: DoctorProfileViewModel
) : DialogFragment(),DialogsListener{

    private lateinit var binding: DialogDeleteBinding
    lateinit var mContext: Context
    lateinit var noInternetDialog: NoInternetDialog
    private val sessionManagement: SessionManagement by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_delete, container, false)

        binding.dialogTxt.setText(getString(R.string.are_you_sure_you_want_to_delete_your_account))

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext,this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.cancelBtn.setOnClickListener {
            dialog!!.dismiss()
        }

        binding.deleteBtn.setOnClickListener {

            // TODO 28/11/22
            if(isOnline(mContext)){
              viewModel.deleteUser()
//            viewModel.deleteProfileModel.observe(viewLifecycleOwner, Observer {
//
//                sessionManagement.clearUser()
//                UserData.logout()
//                navController.navigate(R.id.action_logout_3)
//            })
            }
            else{
                           noConnectivityDialog.noInternetDialogFragment(requireContext())

          }
            dialog!!.dismiss()


        }

    }
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }


}