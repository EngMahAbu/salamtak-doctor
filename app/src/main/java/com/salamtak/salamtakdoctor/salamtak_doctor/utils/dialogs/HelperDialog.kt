package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.salamtak.R
import com.salamtak.databinding.DialogHelperBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener

class HelperDialog(private var msg: String?,private var dialogsListener: DialogsListener? = null) : DialogFragment() {

    private lateinit var binding: DialogHelperBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_helper, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()

        if (msg != null) binding.dialogMsgTv.text = msg

        binding.tvYes.setOnClickListener {
            if (dialogsListener != null)
                dialogsListener!!.onDismiss()

            dismiss()
        }

    }

    private fun setUp() {

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        val wlp = dialog!!.window!!.attributes
//        wlp.gravity = Gravity.BOTTOM
//        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
//        dialog!!.window!!.attributes = wlp

    }

}