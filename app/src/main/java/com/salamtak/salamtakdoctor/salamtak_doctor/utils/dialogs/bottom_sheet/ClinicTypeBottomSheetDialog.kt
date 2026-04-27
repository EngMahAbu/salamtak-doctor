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
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters.ClinicDurationsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters.NewServicesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG

class ClinicTypeBottomSheetDialog(
    val list: ArrayList<ServicesTypesData>,
    val listener: DialogsListener,
    val serviceSelect: TextView
) :
    BottomSheetDialogFragment(), RecyclerViewOnClickListener {

    private lateinit var binding: BottomSheetSpinnerBinding
    private lateinit var mContext: Context
    var clinicTypelId = 0
    private lateinit var newServicesAdapter: NewServicesAdapter
// new
    var servTxt = ""
    var servId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_spinner, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context

        newServicesAdapter = NewServicesAdapter(
            this
        )
        newServicesAdapter.submitData(list)

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
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isHideable = false

        // new not drag dialog
        behavior.isDraggable = false

        binding.headerTitleTxt.text = getString(R.string.consultation_type)
        binding.submitBtn.text = getString(R.string.done)

        binding.choicesRv.adapter = newServicesAdapter

        binding.submitBtn.setOnClickListener {
            listener.onServiceConfirm(clinicTypelId,servTxt)
            serviceSelect.text = servTxt
            dialog!!.dismiss()
        }

    }

    override fun onClinicTypeSelect(clinicId: Int, clinicNameTxt: String, position: Int) {
        this.servTxt = clinicNameTxt

        Log.d(TAG, "mansourclick: " + clinicTypelId)
        clinicTypelId = clinicId
        serviceSelect.text = clinicNameTxt

    }


}