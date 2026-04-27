package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.R
import com.salamtak.databinding.BottomSheetDatesFilterBinding
import com.salamtak.databinding.BottomSheetFilterBinding
import com.salamtak.domain.entities.resquest.auth.FilterModel
import com.salamtak.domain.entities.resquest.auth.UpdateDoctorProfileModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.filter.FilterViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicFilterAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicTypesAdapter
import java.util.*

class FilterDateBottomSheetDialog() : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetDatesFilterBinding
    private lateinit var mContext: Context
    private val birthDateCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dates_filter, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        setUp()
    }

    private fun setUp() {

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog!!.window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog!!.window!!.attributes = wlp

        val bottomSheet = binding.bottomSheetSpinner
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isHideable = false

        binding.fromDateTxt.setOnClickListener {
            openDatePicker()
        }

        binding.toDateTxt.setOnClickListener {
            openToDatePicker()
        }

        binding.doneBtn.setOnClickListener {
//            dialog!!.dismiss()
        }

    }

    private fun openDatePicker() {

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                birthDateCalendar[Calendar.YEAR] = year
                birthDateCalendar[Calendar.MONTH] = monthOfYear
                birthDateCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateBirthDate()
            }

        val datePicker = DatePickerDialog(
            requireContext(),
            date,
            birthDateCalendar[Calendar.YEAR],
            birthDateCalendar[Calendar.MONTH],
            birthDateCalendar[Calendar.DAY_OF_MONTH]
        )

        val maxDateCalender = Calendar.getInstance()
        maxDateCalender.set(currentYear , 11, 31)

        val minDateCalender = Calendar.getInstance()
        minDateCalender.set(currentYear - 100, 0, 1)

        datePicker.datePicker.maxDate = maxDateCalender.timeInMillis
        datePicker.datePicker.minDate = minDateCalender.timeInMillis

        datePicker.show()

    }


    private fun updateBirthDate() {
        binding.fromDateTxt.setText("${birthDateCalendar[Calendar.YEAR]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.DAY_OF_MONTH]}")
    }

    private fun openToDatePicker() {

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                birthDateCalendar[Calendar.YEAR] = year
                birthDateCalendar[Calendar.MONTH] = monthOfYear
                birthDateCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateToBirthDate()
            }

        val datePicker = DatePickerDialog(
            requireContext(),
            date,
            birthDateCalendar[Calendar.YEAR],
            birthDateCalendar[Calendar.MONTH],
            birthDateCalendar[Calendar.DAY_OF_MONTH]
        )

        val maxDateCalender = Calendar.getInstance()
        maxDateCalender.set(currentYear , 11, 31)

        val minDateCalender = Calendar.getInstance()
        minDateCalender.set(currentYear - 100, 0, 1)

        datePicker.datePicker.maxDate = maxDateCalender.timeInMillis
        datePicker.datePicker.minDate = minDateCalender.timeInMillis

        datePicker.show()

    }


    private fun updateToBirthDate() {
        binding.toDateTxt.setText("${birthDateCalendar[Calendar.YEAR]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.DAY_OF_MONTH]}")
    }



}