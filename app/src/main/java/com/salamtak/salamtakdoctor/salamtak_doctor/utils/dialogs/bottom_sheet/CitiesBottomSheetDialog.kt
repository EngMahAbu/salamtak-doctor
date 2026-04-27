package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.R
import com.salamtak.databinding.BottomSheetSpinnerBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.CitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.ServicesAdapter

class CitiesBottomSheetDialog(val citiesAdapter: CitiesAdapter): BottomSheetDialogFragment()
{

    private lateinit var binding: BottomSheetSpinnerBinding
    private lateinit var mContext: Context

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

        binding.headerTitleTxt.text = getString(R.string.city)
        binding.choicesRv.adapter = citiesAdapter

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                citiesAdapter.filterData(newText)
                return false
            }
        })

        binding.submitBtn.setOnClickListener {
            dialog!!.dismiss()
        }

    }


}