package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonElement
import com.salamtak.R
import com.salamtak.databinding.FragmentHospitalClinicFormBinding
import com.salamtak.domain.entities.response.hospital.HospitalClinic
import com.salamtak.domain.entities.response.hospital.HospitalCreateClinicRequest
import com.salamtak.domain.entities.response.hospital.HospitalUpdateClinicRequest
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors.HospitalDoctorsFragment.Companion.ARG_CLINIC_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors.HospitalDoctorsFragment.Companion.ARG_DOCTOR_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SearchablePickerBottomSheet
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * "Add Clinic" (no [ARG_CLINIC_ID]): logo + names + services + features, Finish creates the clinic
 * linked to the doctor. "Edit Clinic": names + services + features, Update keeps the other fields.
 */
class HospitalClinicFormFragment : NewBaseFragment(R.layout.fragment_hospital_clinic_form) {

    private lateinit var binding: FragmentHospitalClinicFormBinding
    private lateinit var viewModel: HospitalDoctorsViewModel
    private val doctorId by lazy { requireArguments().getInt(ARG_DOCTOR_ID) }
    private val clinicId by lazy { requireArguments().getInt(ARG_CLINIC_ID, 0).takeIf { it > 0 } }
    private val isEdit get() = clinicId != null

    private var clinic: HospitalClinic? = null
    private var serviceItems: List<PickerItem> = emptyList()
    private var featureItems: List<PickerItem> = emptyList()
    private var serviceIds: Set<Int> = emptySet()
    private var featureIds: Set<Int> = emptySet()
    private var logoFile: File? = null
    private var handledAction: Resource<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalClinicFormBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalDoctorsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        if (isEdit) {
            binding.tvTitle.setText(R.string.hospital_edit_clinic)
            binding.btnSubmit.setText(R.string.update)
            binding.layoutLogo.visibility = View.GONE
        }
        binding.btnLogo.setOnClickListener { selectPic() }
        binding.etNameEn.doAfterTextChanged { updateSubmitState() }
        binding.etNameAr.doAfterTextChanged { updateSubmitState() }
        binding.tvServices.setOnClickListener {
            SearchablePickerBottomSheet.showMulti(
                childFragmentManager, getString(R.string.hospital_services), serviceItems, serviceIds
            ) { ids ->
                serviceIds = ids
                showSelection()
            }
        }
        binding.tvFeatures.setOnClickListener {
            SearchablePickerBottomSheet.showMulti(
                childFragmentManager, getString(R.string.hospital_clinic_features), featureItems, featureIds
            ) { ids ->
                featureIds = ids
                showSelection()
            }
        }
        binding.btnSubmit.setOnClickListener { submit() }

        viewModel.lookups.observe(viewLifecycleOwner) { lookups ->
            serviceItems = lookups[HospitalLookupKind.SERVICES].orEmpty()
            featureItems = lookups[HospitalLookupKind.FEATURES].orEmpty()
            clinic?.let { preselect(it) }
            showSelection()
        }
        viewModel.clinics.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
            if (it is Resource.sucess) {
                clinic = it.data.orEmpty().firstOrNull { c -> c.ClinicId == clinicId }
                clinic?.let { c -> bind(c) }
            }
        }
        viewModel.action.observe(viewLifecycleOwner) {
            if (it === handledAction) return@observe
            handledAction = it
            when (it) {
                is Resource.Loading -> showProgress(true)
                is Resource.sucess -> {
                    showProgress(false)
                    Snackbar.make(requireView(), getString(R.string.hospital_saved), Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    showProgress(false)
                    Snackbar.make(requireView(), it.message ?: getString(R.string.hospital_info_error), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.loadLookups(HospitalLookupKind.SERVICES, HospitalLookupKind.FEATURES)
        if (isEdit) viewModel.loadClinics(doctorId)
        updateSubmitState()
    }

    private fun bind(clinic: HospitalClinic) {
        binding.etNameEn.setText(clinic.Name)
        binding.etNameAr.setText(clinic.NameAr)
        preselect(clinic)
        showSelection()
    }

    // The clinic's services/features may come as ids, names or objects
    private fun preselect(clinic: HospitalClinic) {
        if (serviceIds.isEmpty()) serviceIds = idsIn(clinic.Services, serviceItems)
        if (featureIds.isEmpty()) featureIds = idsIn(clinic.Features, featureItems)
    }

    private fun idsIn(json: JsonElement?, items: List<PickerItem>): Set<Int> {
        if (json == null || !json.isJsonArray) return emptySet()
        return json.asJsonArray.mapNotNull { e ->
            runCatching {
                when {
                    e.isJsonPrimitive && e.asJsonPrimitive.isNumber -> e.asInt
                    e.isJsonPrimitive -> items.firstOrNull { it.name.equals(e.asString.trim(), true) }?.id
                    e.isJsonObject -> e.asJsonObject.let { o ->
                        (o.get("Id") ?: o.get("ServiceId") ?: o.get("FeatureId"))?.asInt
                            ?: o.get("Name")?.asString?.let { n -> items.firstOrNull { it.name.equals(n.trim(), true) }?.id }
                    }
                    else -> null
                }
            }.getOrNull()
        }.toSet()
    }

    private fun showSelection() {
        binding.tvServices.text = serviceItems.filter { it.id in serviceIds }.joinToString(", ") { it.name }
        binding.tvFeatures.text = featureItems.filter { it.id in featureIds }.joinToString(", ") { it.name }
    }

    private fun text(value: CharSequence?) = value?.toString()?.trim().orEmpty()

    // Add Clinic also needs the logo; the button stays grey until the form is complete
    private fun isValid() = text(binding.etNameEn.text).isNotEmpty() && text(binding.etNameAr.text).isNotEmpty() &&
            (isEdit || logoFile != null)

    private fun updateSubmitState() {
        val valid = isValid()
        binding.btnSubmit.isEnabled = valid
        binding.btnSubmit.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), if (valid) R.color.new_themeBlue_color else R.color.hint_color)
        )
        if (!isEdit) {
            binding.tvLogoLabel.setTextColor(
                ContextCompat.getColor(requireContext(), if (logoFile == null) R.color.red else R.color.black)
            )
        }
    }

    private fun submit() {
        if (!isValid()) return
        val nameEn = text(binding.etNameEn.text)
        val nameAr = text(binding.etNameAr.text)
        val current = clinic
        val clinicId = clinicId
        if (clinicId != null) {
            viewModel.updateClinic(
                HospitalUpdateClinicRequest(
                    ClinicId = clinicId,
                    Name = nameEn,
                    NameAr = nameAr,
                    Email = current?.Email,
                    FixedFee = current?.FixedFee,
                    FollowUp_Period = current?.FollowUp_Period,
                    Phones = current?.Phones.orEmpty(),
                    InfoEn = current?.InfoEn,
                    InfoAr = current?.InfoAr,
                    FeatureIds = featureIds.toList(),
                    ServiceIds = serviceIds.toList()
                )
            )
        } else {
            val hospitalId = DateUtils.getHospitalId() ?: return
            val logo = logoFile?.let { file ->
                MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("clinicLogo", "${file.name}.jpeg", file.asRequestBody("image/*".toMediaTypeOrNull()))
                    .build()
            }
            viewModel.createClinic(
                HospitalCreateClinicRequest(
                    HospitalId = hospitalId,
                    DoctorId = doctorId,
                    Name = nameEn,
                    NameAr = nameAr,
                    Email = null,
                    FixedFee = null,
                    Phones = emptyList(),
                    InfoEn = null,
                    InfoAr = null,
                    FeatureIds = featureIds.toList(),
                    ServiceIds = serviceIds.toList()
                ),
                logo
            )
        }
    }

    override fun onImageSelected(file: File) {
        logoFile = file
        Glide.with(this).load(file).into(binding.ivLogo)
        updateSubmitState()
    }

    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !show && isValid()
    }

}
