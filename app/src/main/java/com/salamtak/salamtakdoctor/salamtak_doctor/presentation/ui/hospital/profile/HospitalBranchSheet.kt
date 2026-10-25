package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.profile

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.databinding.BottomSheetHospitalBranchBinding
import com.salamtak.domain.entities.response.hospital.HospitalBranch
import com.salamtak.domain.entities.response.hospital.HospitalBranchRequest
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SearchablePickerBottomSheet
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource

/**
 * "Add Branch" (when [branch] is null) or "Branch Details" sheet.
 * Details opens read-only; the pencil turns into "Update" and enables the fields; the trash asks
 * before deleting. The host fragment observes the result and closes the sheet on success.
 */
class HospitalBranchSheet(
    private val fragment: Fragment,
    private val viewModel: HospitalProfileViewModel,
    private val hospitalId: Int,
    private val cities: List<PickerItem>,
    private val branch: HospitalBranch?
) {

    private val context = fragment.requireContext()
    private val dialog = BottomSheetDialog(context)
    private val binding = BottomSheetHospitalBranchBinding.inflate(fragment.layoutInflater)
    private val isNew = branch == null

    private var cityId: Int? = branch?.CityId
    private var areaId: Int? = branch?.AreaId
    private var areaItems: List<PickerItem> = emptyList()
    private var openAreaPickerOnLoad = false

    private val areasObserver = Observer<Resource<List<PickerItem>>?> {
        if (it is Resource.sucess) {
            areaItems = it.data.orEmpty()
            if (openAreaPickerOnLoad) {
                openAreaPickerOnLoad = false
                pickArea()
            }
        }
    }

    fun show() {
        dialog.setContentView(binding.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.skipCollapsed = true
        dialog.setOnDismissListener {
            viewModel.branchAreas.removeObserver(areasObserver)
        }
        // Drop areas left from a previous sheet, then load this branch's city
        viewModel.branchAreas.value = null
        viewModel.branchAreas.observe(fragment.viewLifecycleOwner, areasObserver)
        cityId?.let { viewModel.loadAreas(it, forBranch = true) }

        branch?.let { b ->
            binding.etBranchNameEn.setText(b.Name)
            binding.etBranchNameAr.setText(b.NameAr)
            binding.etBranchPhone.setText(b.Phone)
            binding.etBranchStreet.setText(b.Address)
            binding.tvBranchCity.text = b.CityName.orEmpty()
            binding.tvBranchArea.text = b.AreaName.orEmpty()
        }

        if (isNew) {
            binding.tvSheetTitle.setText(R.string.hospital_add_branch)
            setEditable(true)
        } else {
            binding.tvSheetTitle.setText(R.string.hospital_branch_details)
            binding.ivEdit.visibility = View.VISIBLE
            binding.ivDelete.visibility = View.VISIBLE
            binding.btnSave.visibility = View.GONE
            setEditable(false)
        }

        binding.tvBranchCity.setOnClickListener { pickCity() }
        binding.tvBranchArea.setOnClickListener {
            if (cityId == null) return@setOnClickListener
            if (areaItems.isEmpty()) {
                openAreaPickerOnLoad = true
                cityId?.let { id -> viewModel.loadAreas(id, forBranch = true) }
            } else {
                pickArea()
            }
        }
        listOf(binding.etBranchNameEn, binding.etBranchNameAr, binding.etBranchPhone, binding.etBranchStreet)
            .forEach { it.doAfterTextChanged { updateSaveState() } }

        binding.ivEdit.setOnClickListener {
            binding.ivEdit.visibility = View.GONE
            binding.tvUpdate.visibility = View.VISIBLE
            setEditable(true)
        }
        binding.tvUpdate.setOnClickListener { if (isValid()) viewModel.updateBranch(request()) }
        binding.btnSave.setOnClickListener { if (isValid()) viewModel.createBranch(request()) }
        binding.ivDelete.setOnClickListener { confirmDelete() }

        updateSaveState()
        dialog.show()
    }

    fun dismiss() = dialog.dismiss()

    fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !show && isValid()
        binding.tvUpdate.isEnabled = !show
    }

    private fun setEditable(enabled: Boolean) {
        listOf(binding.tilBranchNameEn, binding.tilBranchNameAr, binding.tilBranchPhone, binding.tilBranchStreet)
            .forEach { it.isEnabled = enabled }
        listOf(binding.tvBranchCity, binding.tvBranchArea).forEach { picker ->
            picker.isEnabled = enabled
            // Greyed out while read-only, as in the design
            picker.alpha = if (enabled) 1f else 0.5f
        }
    }

    private fun pickCity() {
        SearchablePickerBottomSheet.show(
            fragment.childFragmentManager, context.getString(R.string.choose_city), null, cities, cityId
        ) { picked ->
            if (picked.id == cityId) return@show
            cityId = picked.id
            binding.tvBranchCity.text = picked.name
            areaId = null
            areaItems = emptyList()
            binding.tvBranchArea.text = ""
            picked.id?.let { viewModel.loadAreas(it, forBranch = true) }
            updateSaveState()
        }
    }

    private fun pickArea() {
        SearchablePickerBottomSheet.show(
            fragment.childFragmentManager, context.getString(R.string.choose_area), null, areaItems, areaId
        ) { picked ->
            areaId = picked.id
            binding.tvBranchArea.text = picked.name
            updateSaveState()
        }
    }

    private fun text(view: TextView) = view.text?.toString()?.trim().orEmpty()

    private fun isValid() =
        text(binding.etBranchNameEn).isNotEmpty() && text(binding.etBranchNameAr).isNotEmpty() &&
                text(binding.etBranchPhone).isNotEmpty() && text(binding.etBranchStreet).isNotEmpty() &&
                cityId != null && areaId != null

    // Save stays grey until every field is filled
    private fun updateSaveState() {
        val valid = isValid()
        binding.btnSave.isEnabled = valid
        binding.btnSave.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, if (valid) R.color.new_themeBlue_color else R.color.hint_color)
        )
    }

    private fun request() = HospitalBranchRequest(
        HospitalId = hospitalId,
        Name = text(binding.etBranchNameEn),
        NameAr = text(binding.etBranchNameAr),
        CityId = cityId,
        AreaId = areaId,
        Address = text(binding.etBranchStreet),
        Latitude = branch?.Latitude,
        Longitude = branch?.Longitude,
        Phone = text(binding.etBranchPhone),
        IsMain = branch?.IsMain ?: false,
        BranchId = branch?.BranchId,
        IsActive = branch?.let { it.IsActive ?: true }
    )

    private fun confirmDelete() {
        val branchId = branch?.BranchId ?: return
        val confirm = BottomSheetDialog(context)
        confirm.setContentView(R.layout.cancelappointmentbottomsheet)
        confirm.findViewById<TextView>(R.id.tv_display)?.setText(R.string.hospital_delete_branch_confirm)
        confirm.findViewById<MaterialButton>(R.id.yes_btn)?.setOnClickListener {
            confirm.dismiss()
            viewModel.deleteBranch(branchId)
        }
        confirm.findViewById<MaterialButton>(R.id.No_button)?.setOnClickListener { confirm.dismiss() }
        confirm.show()
    }
}
