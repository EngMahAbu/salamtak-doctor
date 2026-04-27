package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.salamtak.R
import com.salamtak.databinding.DialogTimePickerBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.checkTiming
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class TimePickerDialog(val listener: DialogsListener, val timeEt: TextView) :
    BottomSheetDialogFragment() {

    private lateinit var binding: DialogTimePickerBinding
    private val TAG = TimePickerDialog::class.java.simpleName
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_time_picker, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = binding.root.context
        setUi()

    }

    private fun setUi() {

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
        binding.fromTimePicker.setIs24HourView(true)
        binding.toTimePicker.setIs24HourView(true)

        var workingFrom = ""
        var workingTo = ""
        binding.submitBtn.setOnClickListener {

            binding.fromTimePicker.setOnTimeChangedListener { _, selectedHour, selectedMinute ->

                workingFrom = "${String.format("%02d", selectedHour)}:${
                    String.format(
                        "%02d",
                        selectedMinute
                    )
                }"
                val sdf = SimpleDateFormat("HH:mm", Locale.US)
                val date = sdf.parse(workingFrom)

                workingFrom = sdf.format(date)


                Log.e(TAG, "setUi:From $workingFrom")


            }



            binding.toTimePicker.setOnTimeChangedListener { _, selectedHour, selectedMinute ->

                workingTo = "${String.format("%02d", selectedHour)}:${
                    String.format(
                        "%02d",
                        selectedMinute
                    )
                }"
                val sdf = SimpleDateFormat("HH:mm",Locale.US)
                val date = sdf.parse(workingTo)

                workingTo = sdf.format(date)


                Log.e(TAG, "setUi:To $workingTo")


            }

            if (workingFrom.isNotEmpty() && workingTo.isNotEmpty()) {

                Log.e(TAG, "setUi:Set  ")

                if (checkTiming(workingFrom, workingTo)) {
                    val from = mContext.getString(R.string.From)
                    val to = mContext.getString(R.string.to)
                    timeEt.setText("$from $workingFrom $to $workingTo")
                    listener.onTimePick(workingFrom, workingTo)
                    dismiss()

                } else {
                    val helperDialog = HelperDialog(getString(R.string.time_error_txt))
                    helperDialog.show(childFragmentManager, "0")
                }

            } else {

                Log.e(TAG, "setUi: Else ")

                val timeFrom = "${String.format("%02d", binding.fromTimePicker.currentHour)}:${
                    String.format(
                        "%02d",
                        binding.fromTimePicker.currentMinute
                    )
                }"

                val timeTo = "${String.format("%02d", binding.toTimePicker.currentHour)}:${
                    String.format(
                        "%02d",
                        binding.toTimePicker.currentMinute
                    )
                }"

                if (checkTiming(timeFrom, timeTo)) {
                    workingFrom = timeFrom

                    workingTo = timeTo

                    val from = mContext.getString(R.string.From)
                    val to = mContext.getString(R.string.to)
                    timeEt.setText("$from $workingFrom $to $workingTo")
                    listener.onTimePick(workingFrom, workingTo)
                    dismiss()

                } else {
                    val helperDialog = HelperDialog(getString(R.string.time_error_txt))
                    helperDialog.show(childFragmentManager, "0")
                }

            }


        }

    }

}