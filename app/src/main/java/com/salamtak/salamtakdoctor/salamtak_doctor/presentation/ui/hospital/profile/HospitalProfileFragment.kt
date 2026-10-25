package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.core.localDataBase.UserData
import com.salamtak.databinding.DocUploadingImageLayoutBinding
import com.salamtak.databinding.FragmentHospitalProfileBinding
import com.salamtak.domain.entities.response.hospital.HospitalBranch
import com.salamtak.domain.entities.response.hospital.HospitalInfoData
import com.salamtak.domain.entities.response.hospital.HospitalOwner
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SearchablePickerBottomSheet
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Hospital profile (More → header card): "Hospital Admin" and "Hospital Info" tabs.
 * Fields are editable in place; the bottom button saves the tab that is shown.
 */
class HospitalProfileFragment : NewBaseFragment(R.layout.fragment_hospital_profile) {

    private lateinit var binding: FragmentHospitalProfileBinding
    private lateinit var viewModel: HospitalProfileViewModel
    private lateinit var branchesAdapter: HospitalBranchesAdapter
    private var branchSheet: HospitalBranchSheet? = null

    private val hospitalId = DateUtils.getHospitalId()
    private val infoId = DateUtils.getHospitalInfoId()

    private var showingInfo = false
    private var owner: HospitalOwner? = null
    private var info: HospitalInfoData? = null
    private var cities: List<PickerItem> = emptyList()
    private var serviceItems: List<PickerItem> = emptyList()
    private var featureItems: List<PickerItem> = emptyList()
    private var areaItems: List<PickerItem> = emptyList()
    private var cityId: Int? = null
    private var areaId: Int? = null
    // Area name from the API, matched once that city's areas arrive
    private var pendingAreaName: String? = null
    private var serviceIds: Set<Int> = emptySet()
    private var featureIds: Set<Int> = emptySet()

    // Which image the picker result belongs to
    private var pickingImage: ImageTarget? = null
    private var idFrontFile: File? = null
    private var idBackFile: File? = null
    private var taxCardFile: File? = null

    private enum class ImageTarget { ID_FRONT, ID_BACK, TAX_CARD }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.tabAdmin.setOnClickListener { selectTab(info = false) }
        binding.tabInfo.setOnClickListener { selectTab(info = true) }
        binding.btnEdit.setOnClickListener { if (showingInfo) saveInfo() else saveOwner() }

        setUpImage(binding.idFront, getString(R.string.hospital_national_id_photo), ImageTarget.ID_FRONT)
        setUpImage(binding.idBack, getString(R.string.hospital_national_id_photo), ImageTarget.ID_BACK)
        setUpImage(binding.taxCard, getString(R.string.hospital_front_tax_card_image), ImageTarget.TAX_CARD)

        binding.tvInfoCity.setOnClickListener { pickCity() }
        binding.tvInfoArea.setOnClickListener { pickArea() }
        binding.tvInfoServices.setOnClickListener {
            SearchablePickerBottomSheet.showMulti(
                childFragmentManager, getString(R.string.hospital_services), serviceItems, serviceIds
            ) { ids ->
                serviceIds = ids
                binding.tvInfoServices.text = namesOf(serviceItems, ids)
            }
        }
        binding.tvInfoFeatures.setOnClickListener {
            SearchablePickerBottomSheet.showMulti(
                childFragmentManager, getString(R.string.hospital_features), featureItems, featureIds
            ) { ids ->
                featureIds = ids
                binding.tvInfoFeatures.text = namesOf(featureItems, ids)
            }
        }

        branchesAdapter = HospitalBranchesAdapter { openBranchSheet(it) }
        binding.rvBranches.adapter = branchesAdapter
        binding.btnAddBranch.setOnClickListener { openBranchSheet(null) }

        observe()
        if (hospitalId != null && infoId != null) {
            viewModel.loadProfile(hospitalId, infoId)
        }
    }

    override fun onDestroyView() {
        branchSheet?.dismiss()
        branchSheet = null
        super.onDestroyView()
    }

    private fun selectTab(info: Boolean) {
        showingInfo = info
        binding.containerAdmin.visibility = if (info) View.GONE else View.VISIBLE
        binding.containerInfo.visibility = if (info) View.VISIBLE else View.GONE
        binding.tabAdmin.setBackgroundResource(if (info) R.drawable.bg_help else R.drawable.bg_button_sellector)
        binding.tabInfo.setBackgroundResource(if (info) R.drawable.bg_button_sellector else R.drawable.bg_help)
        val selected = ContextCompat.getColor(requireContext(), R.color.white)
        val normal = ContextCompat.getColor(requireContext(), R.color.color_primary)
        binding.tabAdmin.setTextColor(if (info) normal else selected)
        binding.tabInfo.setTextColor(if (info) selected else normal)
        binding.scroll.scrollTo(0, 0)
    }

    private fun observe() {
        viewModel.profile.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> showProgress(true)
                is Resource.sucess -> {
                    showProgress(false)
                    it.data?.let { data -> bind(data) }
                }
                is Resource.Error -> {
                    showProgress(false)
                    showMessage(getString(R.string.hospital_info_error))
                }
            }
        }

        viewModel.areas.observe(viewLifecycleOwner) {
            if (it is Resource.sucess) {
                areaItems = it.data.orEmpty()
                pendingAreaName?.let { name ->
                    areaItems.firstOrNull { area -> area.name.equals(name.trim(), true) }?.let { area ->
                        areaId = area.id
                        binding.tvInfoArea.text = area.name
                    }
                    pendingAreaName = null
                }
            }
        }

        viewModel.branches.observe(viewLifecycleOwner) {
            if (it is Resource.sucess) branchesAdapter.submit(it.data.orEmpty())
        }

        viewModel.saveOwnerResponse.observe(viewLifecycleOwner) { handleSave(it) {
            val nameEn = binding.etAdminNameEn.text?.toString()?.trim().orEmpty()
            owner = owner?.copy(Name = nameEn)
        } }

        viewModel.saveInfoResponse.observe(viewLifecycleOwner) { handleSave(it) {
            // The greeting and More header show the hospital name
            binding.etInfoNameEn.text?.toString()?.trim()?.takeIf { name -> name.isNotEmpty() }
                ?.let { name -> UserData.setUserName(name) }
            binding.etInfoNameAr.text?.toString()?.trim()?.takeIf { name -> name.isNotEmpty() }
                ?.let { name -> UserData.setUserNameAR(name) }
        } }

        viewModel.branchActionResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> branchSheet?.showProgress(true)
                is Resource.sucess -> {
                    branchSheet?.dismiss()
                    branchSheet = null
                    showMessage(getString(R.string.hospital_saved))
                    hospitalId?.let { id -> viewModel.loadBranches(id) }
                }
                is Resource.Error -> {
                    branchSheet?.showProgress(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleSave(result: Resource<*>, onSuccess: () -> Unit) {
        when (result) {
            is Resource.Loading -> showProgress(true)
            is Resource.sucess -> {
                showProgress(false)
                onSuccess()
                showMessage(getString(R.string.hospital_saved))
            }
            is Resource.Error -> {
                showProgress(false)
                showMessage(result.message ?: getString(R.string.hospital_info_error))
            }
        }
    }

    private fun bind(data: HospitalProfileData) {
        owner = data.owner
        info = data.info
        cities = data.cities
        serviceItems = data.services.map { PickerItem(it.Id, it.Name.orEmpty()) }
        featureItems = data.features.map { PickerItem(it.Id, it.Name.orEmpty()) }

        data.owner?.let { o ->
            binding.etAdminNameEn.setText(o.Name)
            binding.etAdminNameAr.setText(o.NameAr)
            binding.etAdminNationalId.setText(o.IdNumber)
            binding.etAdminPhone.setText(o.Phone)
            binding.etAdminMobile1.setText(o.Mobile1)
            binding.etAdminMobile2.setText(o.Mobile2)
            showRemoteImage(binding.idFront, o.IdImageFront)
            showRemoteImage(binding.idBack, o.IdImageBack)
        }

        data.info?.let { i ->
            binding.etInfoNameEn.setText(i.Name)
            binding.etInfoNameAr.setText(i.NameAr)
            binding.etInfoPhone.setText(i.Phones?.firstOrNull())
            binding.etInfoStreet.setText(i.Address)
            binding.etInfoBioEn.setText(data.bioEn)
            binding.etInfoBioAr.setText(data.bioAr)

            // The API returns names; map them to the lookup ids
            cities.firstOrNull { c -> c.name.equals(i.CityName?.trim(), true) }?.let { city ->
                cityId = city.id
                binding.tvInfoCity.text = city.name
                pendingAreaName = i.AreaName
                city.id?.let { id -> viewModel.loadAreas(id) }
            }
            val names = i.Services.orEmpty().map { s -> s.trim().lowercase() }.toSet()
            serviceIds = serviceItems.filter { s -> s.name.trim().lowercase() in names }
                .mapNotNull { s -> s.id }.toSet()
            binding.tvInfoServices.text = namesOf(serviceItems, serviceIds)
        }

        branchesAdapter.submit(data.branches)
    }

    // region Pickers

    private fun pickCity() {
        SearchablePickerBottomSheet.show(
            childFragmentManager, getString(R.string.choose_city), null, cities, cityId
        ) { picked ->
            if (picked.id == cityId) return@show
            cityId = picked.id
            binding.tvInfoCity.text = picked.name
            areaId = null
            areaItems = emptyList()
            binding.tvInfoArea.text = ""
            picked.id?.let { viewModel.loadAreas(it) }
        }
    }

    private fun pickArea() {
        if (cityId == null) {
            showMessage(getString(R.string.choose_city))
            return
        }
        SearchablePickerBottomSheet.show(
            childFragmentManager, getString(R.string.choose_area), null, areaItems, areaId
        ) { picked ->
            areaId = picked.id
            binding.tvInfoArea.text = picked.name
        }
    }

    private fun namesOf(items: List<PickerItem>, ids: Set<Int>) =
        items.filter { it.id in ids }.joinToString(", ") { it.name }

    // endregion

    // region Images

    private fun setUpImage(view: DocUploadingImageLayoutBinding, label: String, target: ImageTarget) {
        view.tvPhoto.text = label
        view.editFrontIv.visibility = View.VISIBLE
        val pick = View.OnClickListener {
            pickingImage = target
            selectPic()
        }
        view.ivAddDoc.setOnClickListener(pick)
        view.ivAddPhoto.setOnClickListener(pick)
        view.editFrontIv.setOnClickListener(pick)
    }

    private fun showRemoteImage(view: DocUploadingImageLayoutBinding, url: String?) {
        if (url.isNullOrBlank()) return
        view.ivAddPhoto.visibility = View.GONE
        view.tvPhoto.visibility = View.GONE
        Glide.with(this).load(url).into(view.ivAddDoc)
    }

    override fun onImageSelected(file: File) {
        val view = when (pickingImage) {
            ImageTarget.ID_FRONT -> { idFrontFile = file; binding.idFront }
            ImageTarget.ID_BACK -> { idBackFile = file; binding.idBack }
            ImageTarget.TAX_CARD -> { taxCardFile = file; binding.taxCard }
            null -> return
        }
        view.ivAddPhoto.visibility = View.GONE
        view.tvPhoto.visibility = View.GONE
        Glide.with(this).load(file).into(view.ivAddDoc)
    }

    // endregion

    // region Save

    private fun saveOwner() {
        val nameEn = binding.etAdminNameEn.text?.toString()?.trim().orEmpty()
        val nameAr = binding.etAdminNameAr.text?.toString()?.trim().orEmpty()
        val nationalId = binding.etAdminNationalId.text?.toString()?.trim().orEmpty()
        val phone = binding.etAdminPhone.text?.toString()?.trim().orEmpty()
        var valid = true
        binding.tilAdminNameEn.error = requiredError(nameEn).also { if (it != null) valid = false }
        binding.tilAdminNameAr.error = requiredError(nameAr).also { if (it != null) valid = false }
        binding.tilAdminPhone.error = requiredError(phone).also { if (it != null) valid = false }
        binding.tilAdminNationalId.error = when {
            nationalId.isEmpty() -> getString(R.string.hospital_field_required)
            nationalId.length != 14 -> getString(R.string.hospital_national_id_digits)
            else -> null
        }.also { if (it != null) valid = false }
        if (!valid) return

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("Name", nameEn)
            .addFormDataPart("NameAr", nameAr)
            .addFormDataPart("IdNumber", nationalId)
            .addFormDataPart("Phone", phone)
            .addFormDataPart("UserType", DateUtils.HOSPITAL_STATUS)
        // The screen has no email field: send the stored one back unchanged
        owner?.Email?.takeIf { it.isNotBlank() }?.let { builder.addFormDataPart("Email", it) }
        (owner?.UserID ?: DateUtils.getHospitalUserId())?.let { builder.addFormDataPart("UserID", it.toString()) }
        binding.etAdminMobile1.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
            ?.let { builder.addFormDataPart("Mobile1", it) }
        binding.etAdminMobile2.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
            ?.let { builder.addFormDataPart("Mobile2", it) }
        idFrontFile?.let { builder.addImage("ID_Front", it) }
        idBackFile?.let { builder.addImage("ID_Back", it) }
        viewModel.saveOwner(builder.build())
    }

    private fun saveInfo() {
        val hospitalId = hospitalId ?: return
        val nameEn = binding.etInfoNameEn.text?.toString()?.trim().orEmpty()
        val nameAr = binding.etInfoNameAr.text?.toString()?.trim().orEmpty()
        var valid = true
        binding.tilInfoNameEn.error = requiredError(nameEn).also { if (it != null) valid = false }
        binding.tilInfoNameAr.error = requiredError(nameAr).also { if (it != null) valid = false }
        if (!valid) {
            binding.scroll.scrollTo(0, 0)
            return
        }

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("HealthEntityId", hospitalId.toString())
            .addFormDataPart("Name", nameEn)
            .addFormDataPart("NameAr", nameAr)
            .addFormDataPart("Status", (info?.StatusId ?: 1).toString())
        builder.addIfPresent("CommercialRecNo", binding.etInfoCommercialNo.text?.toString())
        builder.addIfPresent("TaxCertificateNo", binding.etInfoTaxNo.text?.toString())
        builder.addIfPresent("BioEn", binding.etInfoBioEn.text?.toString())
        builder.addIfPresent("BioAr", binding.etInfoBioAr.text?.toString())
        builder.addIfPresent("Email", info?.Email)
        builder.addIfPresent("Address", binding.etInfoStreet.text?.toString())
        info?.Latitude?.let { builder.addFormDataPart("Latitude", it.toString()) }
        info?.Longitude?.let { builder.addFormDataPart("Longitude", it.toString()) }
        cityId?.let { builder.addFormDataPart("CityId", it.toString()) }
        areaId?.let { builder.addFormDataPart("AreaId", it.toString()) }
        // Arrays are sent as repeated form fields
        val phone = binding.etInfoPhone.text?.toString()?.trim().orEmpty()
        val otherPhones = info?.Phones.orEmpty().drop(1)
        (listOf(phone) + otherPhones).filter { it.isNotBlank() }
            .forEach { builder.addFormDataPart("Phones", it) }
        serviceIds.forEach { builder.addFormDataPart("ServiceIds", it.toString()) }
        featureIds.forEach { builder.addFormDataPart("Features", it.toString()) }
        // Insurance is not on this screen: keep what the hospital already has
        info?.Insurance.orEmpty().forEach { builder.addFormDataPart("InsuranceIds", it) }
        taxCardFile?.let { builder.addImage("TaxCertificateImage", it) }
        viewModel.saveInfo(builder.build())
    }

    private fun requiredError(value: String) =
        if (value.isEmpty()) getString(R.string.hospital_field_required) else null

    private fun MultipartBody.Builder.addIfPresent(key: String, value: String?) {
        value?.trim()?.takeIf { it.isNotEmpty() }?.let { addFormDataPart(key, it) }
    }

    private fun MultipartBody.Builder.addImage(key: String, file: File) {
        addFormDataPart(key, "${file.name}.jpeg", file.asRequestBody("image/*".toMediaTypeOrNull()))
    }

    // endregion

    // region Branches

    private fun openBranchSheet(branch: HospitalBranch?) {
        val hospitalId = hospitalId ?: return
        branchSheet?.dismiss()
        branchSheet = HospitalBranchSheet(
            fragment = this,
            viewModel = viewModel,
            hospitalId = hospitalId,
            cities = cities,
            branch = branch
        ).also { it.show() }
    }

    // endregion

    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnEdit.isEnabled = !show
    }

    private fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

}
