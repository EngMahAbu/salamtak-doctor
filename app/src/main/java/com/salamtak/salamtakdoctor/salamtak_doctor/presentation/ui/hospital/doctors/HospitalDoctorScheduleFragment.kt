package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.databinding.BottomSheetHospitalAddSlotBinding
import com.salamtak.databinding.BottomSheetHospitalTimePickerBinding
import com.salamtak.databinding.FragmentHospitalDoctorScheduleBinding
import com.salamtak.databinding.ItemHospitalScheduleDayBinding
import com.salamtak.databinding.ItemHospitalScheduleSlotBinding
import com.salamtak.domain.entities.response.hospital.HospitalScheduleRequest
import com.salamtak.domain.entities.response.hospital.HospitalScheduleSlot
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors.HospitalDoctorsFragment.Companion.ARG_CLINIC_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors.HospitalDoctorsFragment.Companion.ARG_DOCTOR_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SearchablePickerBottomSheet
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** "Doctor Schedule": the doctor's slots in their clinic, grouped by day, with Add Slot and delete. */
class HospitalDoctorScheduleFragment : Fragment() {

    private lateinit var binding: FragmentHospitalDoctorScheduleBinding
    private lateinit var viewModel: HospitalDoctorsViewModel
    private val adapter = SlotsAdapter()
    private val doctorId by lazy { requireArguments().getInt(ARG_DOCTOR_ID) }
    private val clinicId by lazy { requireArguments().getInt(ARG_CLINIC_ID) }
    private var handledAction: Resource<*>? = null
    private var addSheet: AddSlotSheet? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalDoctorScheduleBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalDoctorsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.rvSlots.adapter = adapter
        binding.btnAddSlot.setOnClickListener {
            addSheet = AddSlotSheet().also { it.show() }
        }

        viewModel.schedule.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
            if (it is Resource.sucess) {
                val slots = it.data.orEmpty()
                adapter.submit(slots)
                binding.tvEmpty.visibility = if (slots.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        viewModel.action.observe(viewLifecycleOwner) {
            if (it === handledAction) return@observe
            handledAction = it
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    addSheet?.showProgress(true)
                }
                is Resource.sucess -> {
                    addSheet?.dismiss()
                    addSheet = null
                    viewModel.loadSchedule(doctorId, clinicId)
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    addSheet?.showProgress(false)
                    Snackbar.make(requireView(), it.message ?: getString(R.string.hospital_info_error), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.loadLookups(HospitalLookupKind.DAYS, HospitalLookupKind.DURATIONS, HospitalLookupKind.BOOKING_TYPES)
        viewModel.loadSchedule(doctorId, clinicId)
    }

    override fun onDestroyView() {
        addSheet?.dismiss()
        addSheet = null
        super.onDestroyView()
    }

    private fun confirmDelete(slot: HospitalScheduleSlot) {
        val confirm = BottomSheetDialog(requireContext())
        confirm.setContentView(R.layout.cancelappointmentbottomsheet)
        confirm.findViewById<TextView>(R.id.tv_display)?.setText(R.string.hospital_delete_slot_confirm)
        confirm.findViewById<MaterialButton>(R.id.yes_btn)?.setOnClickListener {
            confirm.dismiss()
            viewModel.deleteSlot(slot.WorkingDayTimeId)
        }
        confirm.findViewById<MaterialButton>(R.id.No_button)?.setOnClickListener { confirm.dismiss() }
        confirm.show()
    }

    // region Slot list (day headers + slot cards)

    private sealed class Row {
        data class Day(val name: String) : Row()
        data class Slot(val slot: HospitalScheduleSlot) : Row()
    }

    private inner class SlotsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var rows: List<Row> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun submit(slots: List<HospitalScheduleSlot>) {
            rows = slots.sortedWith(compareBy({ it.DayId ?: 0 }, { it.TimeFrom.orEmpty() }))
                .groupBy { it.DayName.orEmpty() }
                .flatMap { (day, daySlots) -> listOf(Row.Day(day)) + daySlots.map { Row.Slot(it) } }
            notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int) = if (rows[position] is Row.Day) 0 else 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == 0) DayHolder(ItemHospitalScheduleDayBinding.inflate(inflater, parent, false))
            else SlotHolder(ItemHospitalScheduleSlotBinding.inflate(inflater, parent, false))
        }

        override fun getItemCount() = rows.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (val row = rows[position]) {
                is Row.Day -> (holder as DayHolder).binding.tvDay.text = row.name
                is Row.Slot -> with((holder as SlotHolder).binding) {
                    tvTime.text = "${displayTime(row.slot.TimeFrom)} - ${displayTime(row.slot.TimeTo)}"
                    tvFees.text = getString(R.string.hospital_fees_egp, formatFees(row.slot.Fees))
                    ivDelete.setOnClickListener { confirmDelete(row.slot) }
                }
            }
        }

        inner class DayHolder(val binding: ItemHospitalScheduleDayBinding) : RecyclerView.ViewHolder(binding.root)
        inner class SlotHolder(val binding: ItemHospitalScheduleSlotBinding) : RecyclerView.ViewHolder(binding.root)
    }

    // endregion

    // region Add Slot sheet

    private inner class AddSlotSheet {
        private val dialog = BottomSheetDialog(requireContext())
        private val sheet = BottomSheetHospitalAddSlotBinding.inflate(layoutInflater)
        private var dayId: Int? = null
        private var durationId: Int? = null
        private var bookingTypeId: Int? = null
        // 24h minutes of the day; both default to now, as in the design
        private var fromMinutes = nowMinutes()
        private var toMinutes = nowMinutes()

        fun show() {
            dialog.setContentView(sheet.root)
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.behavior.skipCollapsed = true
            dialog.setOnDismissListener { if (addSheet === this) addSheet = null }

            sheet.tvTimeFrom.text = displayMinutes(fromMinutes)
            sheet.tvTimeTo.text = displayMinutes(toMinutes)
            sheet.tvDay.setOnClickListener {
                pick(R.string.hospital_choose_day, HospitalLookupKind.DAYS, dayId) { item ->
                    dayId = item.id
                    sheet.tvDay.text = item.name
                }
            }
            sheet.tvDuration.setOnClickListener {
                pick(R.string.hospital_detection_duration, HospitalLookupKind.DURATIONS, durationId) { item ->
                    durationId = item.id
                    sheet.tvDuration.text = item.name
                }
            }
            sheet.tvBookingType.setOnClickListener {
                pick(R.string.hospital_choose_booking_type, HospitalLookupKind.BOOKING_TYPES, bookingTypeId) { item ->
                    bookingTypeId = item.id
                    sheet.tvBookingType.text = item.name
                }
            }
            sheet.tvTimeFrom.setOnClickListener {
                pickTime(R.string.hospital_operation_hours_from, fromMinutes) { minutes ->
                    fromMinutes = minutes
                    sheet.tvTimeFrom.text = displayMinutes(minutes)
                    updateSaveState()
                }
            }
            sheet.tvTimeTo.setOnClickListener {
                pickTime(R.string.hospital_operation_hours_to, toMinutes) { minutes ->
                    toMinutes = minutes
                    sheet.tvTimeTo.text = displayMinutes(minutes)
                    updateSaveState()
                }
            }
            sheet.etFees.doAfterTextChanged { updateSaveState() }
            sheet.etFollowUpFees.doAfterTextChanged { updateSaveState() }
            sheet.btnSave.setOnClickListener { save() }
            updateSaveState()
            dialog.show()
        }

        fun dismiss() = dialog.dismiss()

        fun showProgress(show: Boolean) {
            sheet.progress.visibility = if (show) View.VISIBLE else View.GONE
            sheet.btnSave.isEnabled = !show && isValid()
        }

        private fun pick(title: Int, kind: HospitalLookupKind, selected: Int?, onPicked: (PickerItem) -> Unit) {
            val items = viewModel.lookups.value?.get(kind).orEmpty()
            SearchablePickerBottomSheet.show(childFragmentManager, getString(title), null, items, selected) {
                onPicked(it)
                updateSaveState()
            }
        }

        private fun fees(view: TextView) = view.text?.toString()?.trim()?.toDoubleOrNull()

        private fun isValid() = dayId != null && durationId != null && bookingTypeId != null &&
                fees(sheet.etFees) != null && fees(sheet.etFollowUpFees) != null && toMinutes > fromMinutes

        private fun updateSaveState() {
            val valid = isValid()
            sheet.btnSave.isEnabled = valid
            sheet.btnSave.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), if (valid) R.color.new_themeBlue_color else R.color.hint_color)
            )
        }

        private fun save() {
            if (!isValid()) return
            viewModel.addSlot(
                doctorId,
                HospitalScheduleRequest(
                    DayId = dayId!!,
                    TimeFrom = apiMinutes(fromMinutes),
                    TimeTo = apiMinutes(toMinutes),
                    Fees = fees(sheet.etFees)!!,
                    FollowUp_Fees = fees(sheet.etFollowUpFees)!!,
                    DurationMedicalExaminationId = durationId!!,
                    MedicalExaminationTypeId = bookingTypeId!!,
                    ClinicId = clinicId
                )
            )
        }
    }

    private fun pickTime(title: Int, minutes: Int, onDone: (Int) -> Unit) {
        val dialog = BottomSheetDialog(requireContext())
        val sheet = BottomSheetHospitalTimePickerBinding.inflate(layoutInflater)
        sheet.tvTitle.setText(title)
        sheet.npHour.minValue = 0
        sheet.npHour.maxValue = 23
        sheet.npHour.value = minutes / 60
        sheet.npHour.setFormatter { String.format(Locale.ENGLISH, "%02d", it) }
        sheet.npMinute.minValue = 0
        sheet.npMinute.maxValue = 59
        sheet.npMinute.value = minutes % 60
        sheet.npMinute.setFormatter { String.format(Locale.ENGLISH, "%02d", it) }
        sheet.btnDone.setOnClickListener {
            onDone(sheet.npHour.value * 60 + sheet.npMinute.value)
            dialog.dismiss()
        }
        dialog.setContentView(sheet.root)
        dialog.show()
    }

    // endregion

    // region Time / fee formatting

    private fun nowMinutes() = Calendar.getInstance().let { it.get(Calendar.HOUR_OF_DAY) * 60 + it.get(Calendar.MINUTE) }

    /** API format "HH:mm" (24h). */
    private fun apiMinutes(minutes: Int) = String.format(Locale.ENGLISH, "%02d:%02d", minutes / 60, minutes % 60)

    private fun displayMinutes(minutes: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, minutes / 60)
            set(Calendar.MINUTE, minutes % 60)
        }
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
    }

    /** "17:44" → "5:44 PM" */
    private fun displayTime(apiTime: String?): String {
        val parts = apiTime?.split(":") ?: return ""
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return apiTime
        val minute = parts.getOrNull(1)?.take(2)?.toIntOrNull() ?: 0
        return displayMinutes(hour * 60 + minute)
    }

    private fun formatFees(fees: Double?): String {
        val value = fees ?: 0.0
        return if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
    }

    // endregion
}
