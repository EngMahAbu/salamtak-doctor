package com.salamtak.salamtakdoctor.statistics

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.salamtak.R
import com.salamtak.databinding.OtherpackagesfragmentBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.hideLoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.showLoadingDialog


class OtherPackagesFragment : BottomSheetDialogFragment() {

    private lateinit var binding:OtherpackagesfragmentBinding
    private lateinit var statisticsPackgeAdapter: StatisticsAllPackgesAdapter
    private lateinit var viewModel: InsuranceViewModel
    private lateinit var loading: Dialog
    private val args:OtherPackagesFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loading = Dialog(requireContext())
        binding= OtherpackagesfragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(InsuranceViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatisticsMonthRecylerview()
        binding.tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.zm_green))
        getPackagesCallback()
    }


    private fun StatisticsMonthRecylerview() {
        statisticsPackgeAdapter = StatisticsAllPackgesAdapter()
        binding.rvPackages.apply {
            adapter = statisticsPackgeAdapter
            setHasFixedSize(true)
        }
    }


    private fun getPackagesCallback(){

       viewModel.packages.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
           if (!it.data.isNullOrEmpty()){
               statisticsPackgeAdapter.submitList(it.data)
               binding.vpContainer.adapter=statisticsPackgeAdapter
               TabLayoutMediator(binding.tabLayout, binding.vpContainer) { tab, position ->
//            tab.text = list[position].toString()
               }.attach()
           }else{
               dismiss()
               binding.rvPackages.visibility=View.GONE
               Toast.makeText(requireContext(),"there is no packages to show ",Toast.LENGTH_SHORT).show()
           }
       })


        viewModel.errorOtherPackages.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            errorMessage(it.Message.toString())
            dismiss()
        })

        viewModel.showLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        viewModel.getOtherPackagesByClinicID(args.clincId)
    }


}