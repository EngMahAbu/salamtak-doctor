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

class UpdateDialog(private val navController: NavController, var goToMainList: String) :
    DialogFragment() {

    private lateinit var binding: DialogUpdateBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_update, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (goToMainList.equals("vacationDone")) {
            binding.dialogTxt.setText(getString(R.string.you_add_vew_vacation))
        }

        binding.dialogOk.setOnClickListener {

            if (goToMainList.equals("clinic")) {
                navController.navigate(R.id.clinicsFragment)
            }

            if (goToMainList.equals("more")) {
                navController.navigate(R.id.homeFragment)
            }

            if (goToMainList.equals("vacationDone")) {
//                val bundle = Bundle()
//                bundle.putString("edit", "edit")
//                navController.navigate(R.id.vacationClinicFragment,bundle)
//                navController.navigate(R.id.vacationClinicFragment)
                dialog!!.dismiss()
            }

            dialog!!.dismiss()

        }

    }

}