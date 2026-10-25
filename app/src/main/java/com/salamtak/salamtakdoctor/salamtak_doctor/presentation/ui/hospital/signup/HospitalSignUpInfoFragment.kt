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
import com.salamtak.databinding.FragmentHospitalSignupInfoBinding
import com.salamtak.domain.entities.response.hospital.HospitalCreateInfoRequest
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.EMAIL
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.PHONE
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SearchablePickerBottomSheet
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Hospital sign-up step 2: hospital info. Every field is required (user rule), so Next stays
 * grey until all are filled, the tax card image included.
 */
class HospitalSignUpInfoFragment : NewBaseFragment(R.layout.fragment_hospital_signup_info) {

    private lateinit var binding: FragmentHospitalSignupInfoBinding
    private lateinit var viewModel: HospitalSignUpViewModel
    private var handled: Resource<*>? = null

    private var cities: List<PickerItem> = emptyList()
    private var areas: List<PickerItem> = emptyList()
    private var services: List<PickerItem> = emptyList()
    private var insuranceNames: List<PickerItem> = emptyList()
    private var insuranceIds: List<String> = emptyList()
    private var cityId: Int? = null
    private var areaId: Int? = null
    private var serviceIds: Set<Int> = emptySet()
    /** Positions in [insuranceIds]. */
    private var insurancePositions: Set<Int> = emptySet()
    private var taxCardFile: File? = null
    private var openAreasOnLoad = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalSignupInfoBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalSignUpViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // The owner already exists: stay on this step
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { }
        binding.stepper.setStateDescriptionData(
            arrayOf(getString(R.string.hospital_admin_tab), getString(R.string.hospital_info_tab))
        )
        binding.tvHelp.setOnClickListener { findNavController().navigate(R.id.dialogCalling) }
        binding.etPhone.setText(arguments?.getString(PHONE).orEmpty())

        val required = getString(R.string.hospital_field_required)
        binding.etNameEn.validateOnLeave(binding.tilNameEn, getString(R.string.hospital_name_en_required)) { updateNextState() }
        binding.etNameAr.validateOnLeave(binding.tilNameAr, getString(R.string.hospital_name_ar_required)) { updateNextState() }
        binding.etCommercialNo.validateOnLeave(binding.tilCommercialNo, required) { updateNextState() }
        binding.etTaxNo.validateOnLeave(binding.tilTaxNo, required) { updateNextState() }
        binding.etPhone.validateOnLeave(
            binding.tilPhone, getString(R.string.hospital_phone_required), { it.length == PHONE_LENGTH }
        ) { updateNextState() }
        binding.etStreet.validateOnLeave(binding.tilStreet, required) { updateNextState() }
        binding.etBioEn.validateOnLeave(binding.tilBioEn, required) { updateNextState() }
        binding.etBioAr.validateOnLeave(binding.tilBioAr, required) { updateNextState() }

        binding.tvCity.setOnClickListener { pickCity() }
        binding.tvArea.setOnClickListener {
            val cityId = cityId ?: return@setOnClickListener
            if (areas.isEmpty()) {
                openAreasOnLoad = true
                viewModel.loadAreas(cityId)
            } else {
                pickArea()
            }
        }
        binding.tvServices.setOnClickListener {
            SearchablePickerBottomSheet.showMulti(
                childFragmentManager, getString(R.string.hospital_services), services, serviceIds
            ) { ids ->
                serviceIds = ids
                binding.tvServices.text = services.filter { it.id in ids }.joinToString(", ") { it.name }
                updateNextState()
            }
        }
        binding.tvInsurance.setOnClickListener {
            SearchablePickerBottomSheet.showMulti(
                childFragmentManager, getString(R.string.hospital_insurance), insuranceNames, insurancePositions
            ) { positions ->
                insurancePositions = positions
                binding.tvInsurance.text = insuranceNames.filter { it.id in positions }.joinToString(", ") { it.name }
                updateNextState()
            }
        }

        binding.taxCard.tvPhoto.text = getString(R.string.hospital_front_tax_card_image)
        binding.taxCard.ivAddDoc.setOnClickListener { selectPic() }
        binding.taxCard.ivAddPhoto.setOnClickListener { selectPic() }
        binding.taxCard.editFrontIv.setOnClickListener { selectPic() }
        binding.btnNext.setOnClickListener { submit() }

        viewModel.lookups.observe(viewLifecycleOwner) {
            cities = it.cities
            services = it.services
            insuranceNames = it.insuranceNames
            insuranceIds = it.insuranceIds
        }
        viewModel.areas.observe(viewLifecycleOwner) {
            areas = it
            if (openAreasOnLoad) {
                openAreasOnLoad = false
                pickArea()
            }
        }
        viewModel.infoResult.observe(viewLifecycleOwner) {
            if (it === handled) return@observe
            handled = it
            when (it) {
                is Resource.Loading -> showProgress(true)
                is Resource.sucess -> {
                    showProgress(false)
                    DateUtils.setHospitalInfoId(it.data.toString())
                    findNavController().navigate(
                        R.id.hospitalSignUpThanksFragment,
                        bundleOf(
                            HospitalSignUpThanksFragment.ARG_STEP to 2,
                            HospitalSignUpThanksFragment.ARG_NAME to text(binding.etNameEn)
                        )
                    )
                }
                is Resource.Error -> {
                    showProgress(false)
                    Toast.makeText(requireContext(), it.message ?: getString(R.string.hospital_info_error), Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.loadLookups()
        updateNextState()
    }

    private fun pickCity() {
        SearchablePickerBottomSheet.show(
            childFragmentManager, getString(R.string.choose_city), null, cities, cityId
        ) { picked ->
            if (picked.id == cityId) return@show
            cityId = picked.id
            binding.tvCity.text = picked.name
            areaId = null
            areas = emptyList()
            binding.tvArea.text = ""
            picked.id?.let { viewModel.loadAreas(it) }
            updateNextState()
        }
    }

    private fun pickArea() {
        SearchablePickerBottomSheet.show(
            childFragmentManager, getString(R.string.choose_area), null, areas, areaId
        ) { picked ->
            areaId = picked.id
            binding.tvArea.text = picked.name
            updateNextState()
        }
    }

    private fun text(edit: TextInputEditText) = edit.text?.toString()?.trim().orEmpty()

    private fun isValid(): Boolean {
        val texts = listOf(
            binding.etNameEn, binding.etNameAr, binding.etCommercialNo, binding.etTaxNo,
            binding.etStreet, binding.etBioEn, binding.etBioAr
        )
        return texts.all { text(it).isNotEmpty() } && text(binding.etPhone).length == PHONE_LENGTH &&
                cityId != null && areaId != null && serviceIds.isNotEmpty() && insurancePositions.isNotEmpty() &&
                taxCardFile != null
    }

    private fun updateNextState() {
        val valid = isValid()
        binding.btnNext.isEnabled = valid
        binding.btnNext.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), if (valid) R.color.new_themeBlue_color else R.color.hint_color)
        )
    }

    private fun submit() {
        if (!isValid()) return
        val ownerId = DateUtils.getHospitalOwnerId()
        val userId = DateUtils.getHospitalUserId() ?: DateUtils.getUserId()?.toIntOrNull()
        if (ownerId == null || userId == null) {
            Toast.makeText(requireContext(), getString(R.string.hospital_info_error), Toast.LENGTH_SHORT).show()
            return
        }
        val image = taxCardFile?.let { file ->
            MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("TaxCertificateImage", "${file.name}.jpeg", file.asRequestBody("image/*".toMediaTypeOrNull()))
                .build()
        }
        viewModel.createInfo(
            HospitalCreateInfoRequest(
                Name = text(binding.etNameEn),
                NameAr = text(binding.etNameAr),
                CommercialRecNo = text(binding.etCommercialNo),
                TaxCertificateNo = text(binding.etTaxNo),
                BioAr = text(binding.etBioAr),
                BioEn = text(binding.etBioEn),
                OwnerID = ownerId,
                UserID = userId,
                Status = 1,
                Email = arguments?.getString(EMAIL)?.takeIf { it.isNotBlank() },
                CityId = cityId!!,
                AreaId = areaId!!,
                Address = text(binding.etStreet),
                Latitude = null,
                Longitude = null,
                Phones = listOf(text(binding.etPhone)),
                ServiceIds = serviceIds.toList(),
                InsuranceIds = insurancePositions.sorted().mapNotNull { insuranceIds.getOrNull(it) },
                Features = emptyList()
            ),
            image
        )
    }

    override fun onImageSelected(file: File) {
        taxCardFile = file
        binding.taxCard.ivAddPhoto.visibility = View.GONE
        binding.taxCard.tvPhoto.visibility = View.GONE
        binding.taxCard.editFrontIv.visibility = View.VISIBLE
        Glide.with(this).load(file).into(binding.taxCard.ivAddDoc)
        updateNextState()
    }

    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnNext.isEnabled = !show && isValid()
    }

    companion object {
        private const val PHONE_LENGTH = 11
    }
}
