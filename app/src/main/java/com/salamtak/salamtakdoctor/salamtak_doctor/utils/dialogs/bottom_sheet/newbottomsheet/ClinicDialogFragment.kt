package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.newbottomsheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.salamtak.R
import com.salamtak.databinding.ClinicDialogfragmentBinding


class ClinicDialogFragment : Fragment() {

   private lateinit var binding:ClinicDialogfragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= ClinicDialogfragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}