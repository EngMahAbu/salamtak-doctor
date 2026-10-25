package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.signup

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.salamtak.R
import com.salamtak.databinding.DocUploadingImageLayoutBinding
import com.salamtak.databinding.FragmentHospitalSignupAdminBinding
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.EMAIL
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.PHONE
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/** Hospital sign-up step 1: the hospital admin (owner). Next stays grey until every required field is set. */
class HospitalSignUpAdminFragment : NewBaseFragment(R.layout.fragment_hospital_signup_admin) {

    private lateinit var binding: FragmentHospitalSignupAdminBinding
    private lateinit var viewModel: HospitalSignUpViewModel
    private var frontFile: File? = null
    private var backFile: File? = null
    private var pickingFront = true
    private var handled: Resource<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalSignupAdminBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalSignUpViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // The account already exists: don't go back to sign-up / OTP
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { }
        binding.stepper.setStateDescriptionData(
            arrayOf(getString(R.string.hospital_admin_tab), getString(R.string.hospital_info_tab))
        )
        binding.tvHelp.setOnClickListener { findNavController().navigate(R.id.dialogCalling) }
        binding.etPhone.setText(arguments?.getString(PHONE).orEmpty())

        setUpImage(binding.idFront, front = true)
        setUpImage(binding.idBack, front = false)

        binding.etNameEn.validateOnLeave(binding.tilNameEn, getString(R.string.hospital_full_name_en_required)) { updateNextState() }
        binding.etNameAr.validateOnLeave(binding.tilNameAr, getString(R.string.hospital_full_name_ar_required)) { updateNextState() }
        binding.etNationalId.validateOnLeave(
            binding.tilNationalId, getString(R.string.hospital_national_id_digits), { it.length == NATIONAL_ID_LENGTH }
        ) { updateNextState() }
        binding.etPhone.validateOnLeave(
            binding.tilPhone, getString(R.string.hospital_phone_required), { it.length == PHONE_LENGTH }
        ) { updateNextState() }
        binding.btnNext.setOnClickListener { submit() }

        viewModel.ownerResult.observe(viewLifecycleOwner) {
            if (it === handled) return@observe
            handled = it
            when (it) {
                is Resource.Loading -> showProgress(true)
                is Resource.sucess -> {
                    showProgress(false)
                    DateUtils.setHospitalOwnerId(it.data.toString())
                    findNavController().navigate(
                        R.id.hospitalSignUpThanksFragment,
                        bundleOf(
                            HospitalSignUpThanksFragment.ARG_STEP to 1,
                            HospitalSignUpThanksFragment.ARG_NAME to text(binding.etNameEn),
                            PHONE to text(binding.etPhone),
                            EMAIL to arguments?.getString(EMAIL)
                        )
                    )
                }
                is Resource.Error -> {
                    showProgress(false)
                    Toast.makeText(requireContext(), it.message ?: getString(R.string.hospital_info_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
        updateNextState()
    }

    private fun text(edit: TextInputEditText) = edit.text?.toString()?.trim().orEmpty()

    private fun isValid() = text(binding.etNameEn).isNotEmpty() && text(binding.etNameAr).isNotEmpty() &&
            text(binding.etNationalId).length == NATIONAL_ID_LENGTH && text(binding.etPhone).length == PHONE_LENGTH &&
            frontFile != null && backFile != null

    private fun updateNextState() {
        val valid = isValid()
        binding.btnNext.isEnabled = valid
        binding.btnNext.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), if (valid) R.color.new_themeBlue_color else R.color.hint_color)
        )
    }

    private fun submit() {
        if (!isValid()) return
        val userId = DateUtils.getHospitalUserId() ?: DateUtils.getUserId()?.toIntOrNull()
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("Name", text(binding.etNameEn))
            .addFormDataPart("NameAr", text(binding.etNameAr))
            .addFormDataPart("IdNumber", text(binding.etNationalId))
            .addFormDataPart("Phone", text(binding.etPhone))
            .addFormDataPart("UserType", DateUtils.HOSPITAL_STATUS)
        userId?.let { builder.addFormDataPart("UserID", it.toString()) }
        arguments?.getString(EMAIL)?.takeIf { it.isNotBlank() }?.let { builder.addFormDataPart("Email", it) }
        text(binding.etMobile1).takeIf { it.isNotEmpty() }?.let { builder.addFormDataPart("Mobile1", it) }
        text(binding.etMobile2).takeIf { it.isNotEmpty() }?.let { builder.addFormDataPart("Mobile2", it) }
        frontFile?.let { builder.addFormDataPart("ID_Front", "${it.name}.jpeg", it.asRequestBody("image/*".toMediaTypeOrNull())) }
        backFile?.let { builder.addFormDataPart("ID_Back", "${it.name}.jpeg", it.asRequestBody("image/*".toMediaTypeOrNull())) }
        viewModel.createOwner(builder.build())
    }

    private fun setUpImage(view: DocUploadingImageLayoutBinding, front: Boolean) {
        view.tvPhoto.text = getString(R.string.hospital_national_id_photo)
        val pick = View.OnClickListener {
            pickingFront = front
            selectPic()
        }
        view.ivAddDoc.setOnClickListener(pick)
        view.ivAddPhoto.setOnClickListener(pick)
    }

    override fun onImageSelected(file: File) {
        val view = if (pickingFront) binding.idFront else binding.idBack
        if (pickingFront) frontFile = file else backFile = file
        view.ivAddPhoto.visibility = View.GONE
        view.tvPhoto.visibility = View.GONE
        view.editFrontIv.visibility = View.VISIBLE
        view.editFrontIv.setOnClickListener {
            pickingFront = view === binding.idFront
            selectPic()
        }
        Glide.with(this).load(file).into(view.ivAddDoc)
        updateNextState()
    }

    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnNext.isEnabled = !show && isValid()
    }

    companion object {
        private const val NATIONAL_ID_LENGTH = 14
        private const val PHONE_LENGTH = 11
    }
}
