package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.dialog

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.salamtak.R
import com.salamtak.databinding.DialogWrongAppBinding

class WrongAppDialogFragment : DialogFragment() {

    private var _binding: DialogWrongAppBinding? = null
    private val binding get() = _binding!!

    private val isDoctorApp: Boolean
        get() = arguments?.getBoolean(ARG_IS_DOCTOR_APP, true) ?: true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWrongAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUiByRole()
        initClicks()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.88f).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun bindUiByRole() {
        if (isDoctorApp) {

            binding.tvDialogIcon.text = getString(R.string.wrong_app_icon_patient)
            binding.tvDialogTag.text = getString(R.string.wrong_app_tag_patient)
            binding.tvDialogTag.setBackgroundResource(R.drawable.bg_tag_blue)
            binding.tvDialogTag.setTextColor(requireContext().getColor(R.color.tag_blue_text))
            binding.tvDialogTitle.text = getString(R.string.wrong_app_title_patient)
            binding.tvDialogDesc.text = getString(R.string.wrong_app_desc_patient)
            binding.btnDownloadCorrectApp.text = getString(R.string.wrong_app_btn_download_patient)
            binding.btnDownloadCorrectApp.backgroundTintList =
                ColorStateList.valueOf(requireContext().getColor(R.color.doctor_primary))
        }
//        else {
//            binding.tvDialogIcon.text = getString(R.string.wrong_app_icon_doctor)
//            binding.tvDialogTag.text = getString(R.string.wrong_app_tag_doctor)
//            binding.tvDialogTag.setBackgroundResource(R.drawable.bg_tag_blue)
//            binding.tvDialogTag.setTextColor(requireContext().getColor(R.color.tag_blue_text))
//            binding.tvDialogTitle.text = getString(R.string.wrong_app_title_doctor)
//            binding.tvDialogDesc.text = getString(R.string.wrong_app_desc_doctor)
//            binding.btnDownloadCorrectApp.text = getString(R.string.wrong_app_btn_download_patient)
//            binding.btnDownloadCorrectApp.backgroundTintList =
//                android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.doctor_primary))
//        }
    }

    private fun initClicks() {
        binding.btnDownloadCorrectApp.setOnClickListener {

            val url = if (isDoctorApp) {
                "https://play.google.com/store/apps/details?id=com.korashiGroup.salamtakPatient"
            } else {
                "https://play.google.com/store/apps/details?id=com.korashiGroup.salamtakPatient"
            }
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        binding.btnCloseApp.setOnClickListener {
            requireActivity().finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IS_DOCTOR_APP = "isDoctorApp"

        fun newInstance(isPatientApp: Boolean): WrongAppDialogFragment {
            return WrongAppDialogFragment().apply {
                arguments = bundleOf(ARG_IS_DOCTOR_APP to isPatientApp)
            }
        }
    }
}
