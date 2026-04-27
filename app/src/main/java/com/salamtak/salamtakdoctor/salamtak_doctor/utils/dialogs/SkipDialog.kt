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
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.DialogDeleteBinding
import com.salamtak.databinding.DialogSkipBinding
import com.salamtak.databinding.DocumentsLayoutBinding
import com.salamtak.domain.entities.response.ClinicGalleryData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.ClinicGalleryAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import org.koin.android.ext.android.inject

class SkipDialog(private val navController: NavController) : DialogFragment() {

    private lateinit var binding: DialogSkipBinding
    private val sessionManagement: SessionManagement by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_skip, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.skipBtn.setOnClickListener {
//            navController.navigate(R.id.homeFragment)
            navController.navigate(R.id.action_documentsFragment_to_homeFragment)
            dialog!!.dismiss()
        }

        binding.letsGoBtn.setOnClickListener {
            sessionManagement.setClinicId("0")
            navController.navigate(R.id.addNewClinicFragment)
            dialog!!.dismiss()
        }


    }

}