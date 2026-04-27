package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.newbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.databinding.DailogDatePickerfragmentBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel


class DailogDatePickerFragment() : BottomSheetDialogFragment() {

    private lateinit var binding: DailogDatePickerfragmentBinding
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    private val args:DailogDatePickerFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DailogDatePickerfragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
    }

    private fun initButton() {
        binding.btnGetDate.setOnClickListener {
            args.status?.let { it1 -> getDate(it1) }
            dismiss()
        }
    }
    private fun getDate(date:String) {
        val dateResult =
            "" + binding.datePicker1.getYear() + "-" + (binding.datePicker1.getMonth() + 1) + "-" + binding.datePicker1.getDayOfMonth()
        if (date=="date"){
            sharedDataViewmodel.getBirthDate(dateResult)
        }else if (date=="endDate"){
            sharedDataViewmodel.getEdndDate(dateResult)
        }


    }
}