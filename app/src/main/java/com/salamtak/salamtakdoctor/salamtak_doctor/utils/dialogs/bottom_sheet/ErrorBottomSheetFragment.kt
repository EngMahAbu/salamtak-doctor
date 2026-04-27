package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.databinding.FragmentErrorBottomSheetBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.MESSAGE

class ErrorBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(message: String): ErrorBottomSheetFragment {
            val args = Bundle()
            args.putString(MESSAGE, message)
            val fragment = ErrorBottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding: FragmentErrorBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErrorBottomSheetBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        if (arguments != null) {
            arguments?.getString(MESSAGE)?.let {
                binding.errorMessageTextView.text = it
            }
        }
        binding.closeImageView.setOnClickListener { dismiss() }
        binding.backButton.setOnClickListener { dismiss() }
    }
}