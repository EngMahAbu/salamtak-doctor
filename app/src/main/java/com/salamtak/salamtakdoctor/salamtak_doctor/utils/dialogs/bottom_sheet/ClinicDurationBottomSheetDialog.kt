package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.R
import com.salamtak.databinding.BottomSheetSpinnerBinding
import com.salamtak.domain.entities.response.MedicalExaminationDurationData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters.ClinicDurationsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class ClinicDurationBottomSheetDialog
    (val list: ArrayList<MedicalExaminationDurationData>
    ,val listener: DialogsListener,val durationSelect: TextView):
    BottomSheetDialogFragment(), RecyclerViewOnClickListener
{
//    val clinicDurationsAdapter: ClinicDurationsAdapter
    private lateinit var binding: BottomSheetSpinnerBinding
    private lateinit var mContext: Context
    private val TAG = ClinicDurationBottomSheetDialog::class.java.simpleName
    private lateinit var clinicDurationsAdapter: ClinicDurationsAdapter

    var durationTxt = ""
    var durationId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_spinner,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        clinicDurationsAdapter = ClinicDurationsAdapter(
            this
        )
        clinicDurationsAdapter.submitData(list)
        setUp()

    }


    private fun setUp() {
        binding.choicesRv.layoutManager = LinearLayoutManager(mContext)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog!!.window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog!!.window!!.attributes = wlp

        val bottomSheet = binding.bottomSheetSpinner
        val behavior = BottomSheetBehavior.from (bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isHideable = false

// new
        behavior.isDraggable = false

        binding.submitBtn.setOnClickListener {
            Log.e(TAG, "setUp: $durationTxt $durationId" )
            listener.onDurationConfirm(durationId,durationTxt)
            durationSelect.text = durationTxt

            dialog!!.dismiss()
        }

        binding.headerTitleTxt.text = getString(R.string.time_patient_slot_time)
        binding.choicesRv.adapter = clinicDurationsAdapter
    }

    override fun onDurationSelect(durationId: Int, durationTxt: String, position: Int) {
        this.durationTxt = durationTxt

        Log.e(TAG, "onDurationSelect: $durationTxt == ${this.durationTxt}" )

        durationSelect.text = durationTxt
        this.durationId = durationId

    }

}