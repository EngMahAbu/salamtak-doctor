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
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.DialogDeleteBinding
import com.salamtak.domain.entities.response.Item
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline

class CancelAppointmentDialog(
    var viewModel: AppointmentViewModel, var newsData: Item, val navController: NavController,val messagecode:Int
) : DialogFragment(),DialogsListener {

    private lateinit var binding: DialogDeleteBinding
    private lateinit var mContext: Context
    private lateinit var noInternetDialog: NoInternetDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_delete, container, false)
        binding.dialogHeader.setText(getString(R.string.cancel_appointment))
        binding.dialogTxt.setText(getString(R.string.are_you_sure_you_want_to_cancel_this_appointment))
        binding.deleteBtn.setText(getString(R.string.confirm))
        binding.cancelBtn.setText(getString(R.string.cancelappointements))

        binding.cancelReasonEt.visibility = View.VISIBLE

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
           // TODO
           if(isOnline(mContext))
           {
                   viewModel.cancelAppointment(
                       newsData.AppointmentId.toInt(),
                       binding.cancelReasonEt.text.toString()
                   )
                   dialog!!.dismiss()


           }
            else{
                           noConnectivityDialog.noInternetDialogFragment(requireContext())

           }
        }


    }

}