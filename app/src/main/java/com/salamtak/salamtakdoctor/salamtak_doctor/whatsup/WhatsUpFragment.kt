package com.salamtak.salamtakdoctor.salamtak_doctor.whatsup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salamtak.R
import com.salamtak.databinding.FragmentWhatsUpBinding
import com.salamtak.salamtakdoctor.adapter.GatesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage

class WhatsUpFragment : Fragment(),GatesAdapter.Action{

    private lateinit var binding: FragmentWhatsUpBinding
    private lateinit var mContext: Context
    private lateinit var viewModelinsurance: InsuranceViewModel
    private lateinit var gatesAdapter: GatesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_whats_up, container, false)
        viewModelinsurance = ViewModelProvider(this).get(InsuranceViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        healthTopicsRecylerview()
        getSalamtakGatesCompanyCallback()
    }

    private fun healthTopicsRecylerview() {
        gatesAdapter = GatesAdapter(this)
        binding.whatsUpRv.apply {
            adapter = gatesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
    private fun getSalamtakGatesCompanyCallback() {
        viewModelinsurance.gateResponse.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                gatesAdapter.submitList(it.data)
                gatesAdapter.notifyDataSetChanged()

            }
        })

        viewModelinsurance.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        viewModelinsurance.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
            //loading.showLoadingDialog()
                showprogtessbar()
            else
            // loading.hideLoadingDialog()
                hideprogressbar()
        })
        viewModelinsurance.getSalamtakGate()
    }

    private fun showprogtessbar() {
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideprogressbar() {
        binding.progressBar.visibility = View.GONE

    }
    override fun onItemGatesClick(gateId: Int) {
        val action = WhatsUpFragmentDirections.actionWhatsUpFragmentToSalamtakgatesFragment(gateId)
        findNavController().navigate(action)
    }



}