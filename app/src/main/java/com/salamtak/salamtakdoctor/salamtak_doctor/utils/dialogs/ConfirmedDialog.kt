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
import com.salamtak.databinding.DialogConfirmedBinding

class ConfirmedDialog(private val navController: NavController, var loged: String) :
    DialogFragment() {

    private lateinit var binding: DialogConfirmedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirmed, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (loged.equals("toCompleteProfile")) {
            binding.dialogTxt.setText(getString(R.string.now_you_can_create_your_salamtak_profile))
            binding.dialogOk.setText(getString(R.string.create_profile))
        }

        binding.dialogOk.setOnClickListener {

            if (loged.equals("loged")) {
                navController.navigate(R.id.moreFragment)
            } else if (loged.equals("toCompleteProfile")) {
                navController.navigate(R.id.setupProfileFragment)
            } else {
                navController.navigate(R.id.loginFragment)
            }

        }

    }

}