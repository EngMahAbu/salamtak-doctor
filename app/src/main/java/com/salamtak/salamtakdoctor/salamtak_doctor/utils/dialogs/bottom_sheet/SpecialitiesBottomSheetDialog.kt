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
import com.salamtak.domain.entities.response.SubSpecialitiesData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.SpecialitiesAdapter
import java.util.ArrayList

class SpecialitiesBottomSheetDialog(
    val specialitiesAdapter: SpecialitiesAdapter,
    val specialityList: ArrayList<SubSpecialitiesData>
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
        search()

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

        binding.submitBtn.setOnClickListener {
            dialog!!.dismiss()
        }

        binding.headerTitleTxt.text = getString(R.string.specialization)
        binding.choicesRv.adapter = specialitiesAdapter
    }

    fun search(){
        binding.search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {


                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText?.length != 0){
                    var filterList =  specialitiesAdapter.filter(newText!!)
                    if(filterList.size > 0){
                        binding.noCountryFound.visibility = View.GONE
                        binding.choicesRv.visibility = View.VISIBLE

                    }else{
                        binding.noCountryFound.visibility = View.VISIBLE
                        binding.choicesRv.visibility = View.GONE
                    }
                }else{
                    specialitiesAdapter.submitData(specialityList)
                    binding.noCountryFound.visibility = View.GONE
                    binding.choicesRv.visibility = View.VISIBLE
                }

                return false
            }
        })

        binding.search.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                specialitiesAdapter.submitData(specialityList)
                return true
            }

        })
    }



}