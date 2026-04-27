package com.salamtak.salamtakdoctor.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.salamtak.databinding.DialogfragmentBinding
import com.salamtak.salamtakdoctor.adapter.PackagesDetailsAdapter


class StatisticsDialogFragment : DialogFragment() {

    private lateinit var binding:DialogfragmentBinding
    private val args:StatisticsDialogFragmentArgs by navArgs()
    private lateinit var packagesDetailsAdapter: PackagesDetailsAdapter
      override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
          binding=DialogfragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        bindViews()
        StatisticsMonthRecylerview()
    }

    private fun setupViews(){
     binding.title.text=args.type
        if (args.status){
            binding.layoutSample.visibility=View.VISIBLE
            binding.cardviewFooter.visibility=View.VISIBLE
        }else{
            binding.layoutInclinkPatient.visibility=View.VISIBLE
            binding.cardviewInclink.visibility=View.VISIBLE
        }

    }

    private fun StatisticsMonthRecylerview() {
        packagesDetailsAdapter = PackagesDetailsAdapter()
        binding.rvDetails.apply {
            adapter = packagesDetailsAdapter
            setHasFixedSize(true)
        }
        packagesDetailsAdapter.submitList(args.clinksAppointement?.inclinicAppointments )
        packagesDetailsAdapter.notifyDataSetChanged()
    }


    private fun bindViews(){
        binding.tvNumberofPatient1.text= args.clinksAppointement?.currentPackageInclinicPatient ?: ""
        binding.tvNumberofPatient.text= args.clinksAppointement?.lastPackageInclinicPatient ?: ""
        binding.tvNumberofPatientInpackage1.text= args.clinksAppointement?.currentCompleteInclinicPatientCount ?: ""
        binding.tvNumberofPatientInpackage.text= args.clinksAppointement?.lastCompleteInclinicPatientCount ?: ""
        binding.TotalcardviewInclink.text=args.clinksAppointement?.lastCompleteInclinicPatientCount ?: ""
        binding.PaidcardviewInclink.text=args.clinksAppointement?.lastPackageInclinicPatient ?: ""
        binding.currentcardviewInclink.text=args.clinksAppointement?.currentPackageInclinicPatient ?: ""
        binding.packagecardviewInclink.text=args.clinksAppointement?.currentCompleteInclinicPatientCount ?: ""
        binding.Total.text=args.clinksAppointement?.currentCompleteInclinicPatientCount ?: ""
        binding.Paid.text=args.clinksAppointement?.currentPackageInclinicPatient ?: ""
        binding.tvThirdnumberofPatient.text=args.clinksAppointement?.currentCompleteInclinicPatientCount ?: ""
        binding.tvThirdnumberofPatientInpackage.text=args.clinksAppointement?.currentPackageInclinicPatient ?: ""

    }

}