package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

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
import com.salamtak.databinding.DialogUpdateBinding

class EditDialog (): DialogFragment() {

    private lateinit var binding: DialogUpdateBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate(inflater, R.layout.dialog_update,container,false)

        binding.dialogHeader.setText(getString(R.string.done))
        binding.dialogTxt.setText(getString(R.string.you_just_edit_your_times))
        binding.dialogOk.setText(getString(R.string.ok))

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dialogOk.setOnClickListener {
        dialog!!.dismiss()
        }

    }

}