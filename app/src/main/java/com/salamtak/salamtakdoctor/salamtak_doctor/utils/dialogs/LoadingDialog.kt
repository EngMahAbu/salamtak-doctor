package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.salamtak.R
import com.salamtak.databinding.DialogLoadingBinding

class LoadingDialog(private val mContext: Context) : Dialog(mContext) {

    lateinit var binding: DialogLoadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_loading, null, false)

        setContentView(binding.root)


        setUp()

    }


    private fun setUp() {

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setCancelable(false)

        //        val wlp = dialog!!.window!!.attributes
        //        wlp.gravity = Gravity.BOTTOM
        //        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        //        dialog!!.window!!.attributes = wlp

    }

}