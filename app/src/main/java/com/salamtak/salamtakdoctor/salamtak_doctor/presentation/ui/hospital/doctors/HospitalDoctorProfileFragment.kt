package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.databinding.FragmentHospitalDoctorProfileBinding
import com.salamtak.domain.entities.response.hospital.HospitalDoctorProfile
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors.HospitalDoctorsFragment.Companion.ARG_DOCTOR_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SearchablePickerBottomSheet
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Calendar
import java.util.Locale

/** "Doctor Profile" (pencil on a doctor card): edit the doctor and save with Hospital/UpdateDoctor. */
class HospitalDoctorProfileFragment : NewBaseFragment(R.layout.fragment_hospital_doctor_profile) {

    private lateinit var binding: FragmentHospitalDoctorProfileBinding
    private lateinit var viewModel: HospitalDoctorsViewModel
    private val doctorId by lazy { requireArguments().getInt(ARG_DOCTOR_ID) }
    private var handledAction: Resource<*>? = null

    private var profile: HospitalDoctorProfile? = null
    private var countryId: Int? = null
    private var specialistId: Int? = null
    private var seniorityId: Int? = null
    private var subSpecialistIds: Set<Int> = emptySet()
    private var subSpecialistItems: List<PickerItem> = emptyList()
    /** "yyyy-MM-dd" */
    private var birthday: String? = null
    private var photoFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalDoctorProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalDoctorsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnPhoto.setOnClickListener { selectPic() }
        binding.tvBirthDate.setOnClickListener { pickBirthday() }
        binding.tvCountry.setOnClickListener {
            pick(R.string.hospital_country, HospitalLookupKind.COUNTRIES, countryId) { item ->
                countryId = item.id
                binding.tvCountry.setText(item.name)
            }
        }
        binding.tvSpecialty.setOnClickListener {
            pick(R.string.hospital_specialty, HospitalLookupKind.SPECIALISTS, specialistId) { item ->
                if (item.id == specialistId) return@pick
                specialistId = item.id
                binding.tvSpecialty.setText(item.name)
                // Sub-specialities belong to the specialty
                subSpecialistIds = emptySet()
                subSpecialistItems = emptyList()
                binding.tvSubSpecialty.setText("")
                item.id?.let { id -> viewModel.loadSubSpecialists(id) }
            }
        }
        binding.tvSubSpecialty.setOnClickListener {
            if (specialistId == null) return@setOnClickListener
            SearchablePickerBottomSheet.showMulti(
                childFragmentManager, getString(R.string.hospital_sub_specialty), subSpecialistItems, subSpecialistIds
            ) { ids ->
                subSpecialistIds = ids
                showSubSpecialists()
            }
        }
        binding.tvSeniority.setOnClickListener {
            pick(R.string.hospital_seniority_level, HospitalLookupKind.SENIORITY, seniorityId) { item ->
                seniorityId = item.id
                binding.tvSeniority.setText(item.name)
            }
        }
        binding.btnUpdate.setOnClickListener { save() }

        viewModel.profile.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
            when (it) {
                is Resource.sucess -> it.data?.let { data -> bind(data) }
                is Resource.Error -> showMessage(it.message ?: getString(R.string.hospital_info_error))
                else -> Unit
            }
        }
        viewModel.lookups.observe(viewLifecycleOwner) { showLookupNames() }
        viewModel.subSpecialists.observe(viewLifecycleOwner) {
            subSpecialistItems = it
            showSubSpecialists()
        }
        viewModel.action.observe(viewLifecycleOwner) {
            if (it === handledAction) return@observe
            handledAction = it
            when (it) {
                is Resource.Loading -> showProgress(true)
                is Resource.sucess -> {
                    showProgress(false)
                    showMessage(getString(R.string.hospital_saved))
                }
                is Resource.Error -> {
                    showProgress(false)
                    showMessage(it.message ?: getString(R.string.hospital_info_error))
                }
            }
        }

        viewModel.loadLookups(HospitalLookupKind.COUNTRIES, HospitalLookupKind.SPECIALISTS, HospitalLookupKind.SENIORITY)
        viewModel.loadProfile(doctorId)
    }

    private fun bind(data: HospitalDoctorProfile) {
        profile = data
        // FirstName holds the full name; join the parts in case middle/last are filled
        binding.etNameEn.setText(joinName(data.FirstName, data.MiddelName, data.LastName))
        binding.etNameAr.setText(joinName(data.FirstNameAr, data.MiddelNameAr, data.LastNameAr))
        binding.etNickEn.setText(data.NickName)
        binding.etNickAr.setText(data.NickNameAr)
        binding.etPhone.setText(data.Phone)
        binding.etEmail.setText(data.Email)
        binding.etNationalId.setText(data.NationalId)
        binding.etLicense.setText(data.LicenseNumber)
        binding.etSyndicate.setText(data.SyndicateId)
        ImageUtil.loadPicture(binding.ivPhoto, data.Image)

        countryId = data.NationalityId
        specialistId = data.SpecialistId
        seniorityId = data.SeniorityLevelId
        subSpecialistIds = data.DoctorSubSpecialist.orEmpty().toSet()
        specialistId?.let { viewModel.loadSubSpecialists(it) }

        // "0001-01-01" means not set
        birthday = data.Birthday?.take(10)?.takeIf { !it.startsWith("0001") && it.isNotBlank() }
        binding.tvBirthDate.setText(birthday.orEmpty())

        when (data.GenderId) {
            1 -> binding.toggleGender.check(R.id.btn_male)
            2 -> binding.toggleGender.check(R.id.btn_female)
        }
        showLookupNames()
    }

    private fun joinName(vararg parts: String?) =
        parts.mapNotNull { it?.trim()?.takeIf { p -> p.isNotEmpty() } }.joinToString(" ")

    private fun showLookupNames() {
        val lookups = viewModel.lookups.value.orEmpty()
        fun nameOf(kind: HospitalLookupKind, id: Int?) = lookups[kind]?.firstOrNull { it.id == id }?.name
        nameOf(HospitalLookupKind.COUNTRIES, countryId)?.let { binding.tvCountry.setText(it) }
        nameOf(HospitalLookupKind.SPECIALISTS, specialistId)?.let { binding.tvSpecialty.setText(it) }
        nameOf(HospitalLookupKind.SENIORITY, seniorityId)?.let { binding.tvSeniority.setText(it) }
    }

    private fun showSubSpecialists() {
        binding.tvSubSpecialty.setText(
            subSpecialistItems.filter { it.id in subSpecialistIds }.joinToString(", ") { it.name }
        )
    }

    private fun pick(title: Int, kind: HospitalLookupKind, selected: Int?, onPicked: (PickerItem) -> Unit) {
        val items = viewModel.lookups.value?.get(kind).orEmpty()
        SearchablePickerBottomSheet.show(childFragmentManager, getString(title), null, items, selected, onPicked)
    }

    private fun pickBirthday() {
        val calendar = Calendar.getInstance()
        birthday?.split("-")?.mapNotNull { it.toIntOrNull() }?.takeIf { it.size == 3 }?.let { (y, m, d) ->
            calendar.set(y, m - 1, d)
        }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                birthday = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month + 1, day)
                binding.tvBirthDate.setText(birthday)
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
    }

    private fun text(value: CharSequence?) = value?.toString()?.trim().orEmpty()

    private fun save() {
        val nameEn = text(binding.etNameEn.text)
        val nameAr = text(binding.etNameAr.text)
        val phone = text(binding.etPhone.text)
        val required = getString(R.string.hospital_field_required)
        binding.tilNameEn.error = if (nameEn.isEmpty()) required else null
        binding.tilNameAr.error = if (nameAr.isEmpty()) required else null
        binding.tilPhone.error = if (phone.isEmpty()) required else null
        if (nameEn.isEmpty() || nameAr.isEmpty() || phone.isEmpty()) {
            binding.scroll.scrollTo(0, 0)
            return
        }

        val gender = when (binding.toggleGender.checkedButtonId) {
            R.id.btn_male -> 1
            R.id.btn_female -> 2
            else -> profile?.GenderId
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("DoctorId", doctorId.toString())
            .addFormDataPart("FullName", nameEn)
            .addFormDataPart("FullNameAr", nameAr)
            .addFormDataPart("Phone", phone)
        builder.addIfPresent("Email", text(binding.etEmail.text))
        builder.addIfPresent("NickName", text(binding.etNickEn.text))
        builder.addIfPresent("NickNameAr", text(binding.etNickAr.text))
        builder.addIfPresent("NationalId", text(binding.etNationalId.text))
        builder.addIfPresent("LicenseNumber", text(binding.etLicense.text))
        builder.addIfPresent("SyndicateId", text(binding.etSyndicate.text))
        builder.addIfPresent("Birthday", birthday)
        // Not on this screen: send the stored values back
        builder.addIfPresent("DoctorInfo", profile?.DoctorInfo)
        builder.addIfPresent("DoctorInfoAr", profile?.DoctorInfoAr)
        gender?.let { builder.addFormDataPart("GenderId", it.toString()) }
        countryId?.let { builder.addFormDataPart("NationalityId", it.toString()) }
        specialistId?.let { builder.addFormDataPart("SpecialistId", it.toString()) }
        seniorityId?.let { builder.addFormDataPart("SeniorityLevelId", it.toString()) }
        profile?.StatusId?.let { builder.addFormDataPart("StatusId", it.toString()) }
        subSpecialistIds.forEach { builder.addFormDataPart("DoctorSubSpecialist", it.toString()) }
        photoFile?.let {
            builder.addFormDataPart("profileImage", "${it.name}.jpeg", it.asRequestBody("image/*".toMediaTypeOrNull()))
        }
        viewModel.updateDoctor(builder.build())
    }

    private fun MultipartBody.Builder.addIfPresent(key: String, value: String?) {
        value?.trim()?.takeIf { it.isNotEmpty() }?.let { addFormDataPart(key, it) }
    }

    override fun onImageSelected(file: File) {
        photoFile = file
        Glide.with(this).load(file).into(binding.ivPhoto)
    }

    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnUpdate.isEnabled = !show
    }

    private fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}
