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
import com.salamtak.domain.entities.response.ServiceData
import com.salamtak.domain.entities.response.ServicesBySpecialistResponse
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.domain.entities.response.SubSpecialitiesData
import com.salamtak.salamtakdoctor.ServiceTypeAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters.ServicesTypesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.ServicesAdapter

class ServicesBottomSheetDialog(val servicesAdapter: ServicesAdapter?, val serviceTypeAdapter: ServiceTypeAdapter?,
                                val servicesList: java.util.ArrayList<ServiceData>
): BottomSheetDialogFragment()
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

        // new not drag dialog
        behavior.isDraggable = false

        binding.headerTitleTxt.text = getString(R.string.chooseservices)
        if(servicesAdapter!=null)
        binding.choicesRv.adapter = servicesAdapter
        else
            binding.choicesRv.adapter = serviceTypeAdapter

        binding.submitBtn.setOnClickListener {
            dialog!!.dismiss()
        }
        binding.search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText?.length != 0){
                    var filterList =  serviceTypeAdapter?.filter(newText!!)
                    if(filterList!!.size > 0){
                        binding.noCountryFound.visibility = View.GONE
                        binding.choicesRv.visibility = View.VISIBLE

                    }else{
                        binding.noCountryFound.visibility = View.VISIBLE
                        binding.choicesRv.visibility = View.GONE
                    }
                }else{
                    serviceTypeAdapter!!.submitData(servicesList)
                    binding.noCountryFound.visibility = View.GONE
                    binding.choicesRv.visibility = View.VISIBLE
                }

                return false
            }
        })

    }


}