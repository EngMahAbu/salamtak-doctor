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
import com.salamtak.R
import com.salamtak.databinding.DialogNoInternetBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.reset_password.ForgetPasswordFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener

class NoInternetDialog(private val mContext: Context,private val listener: DialogsListener): DialogFragment()
{

    lateinit var binding: DialogNoInternetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.dialog_no_internet,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvRefresh.setOnClickListener {
            listener.onInternetOKClick(this)
        }

    }

}