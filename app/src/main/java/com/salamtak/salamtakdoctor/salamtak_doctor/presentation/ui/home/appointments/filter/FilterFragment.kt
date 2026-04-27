package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.filter

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salamtak.R
import com.salamtak.databinding.FragmentAppointmentsBinding
import com.salamtak.databinding.FragmentFilterBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.CLINIC_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    lateinit var loading: Dialog
    var filterResult: String = ""
    private lateinit var viewModel: FilterViewModel
    var x = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        arguments?.let {
//            filterResult = it.getString("filterResult", "")
//            Log.d(TAG, "mans: " + filterResult)
//        }

        loading = Dialog(requireContext())
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)

        viewModel = ViewModelProvider(this).get(FilterViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListner()
        initObserve()

    }

    private fun initObserve() {

        viewModel.filterResponseModel.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "mans: " + it.Data)
        })

//        binding.appointmentRv.adapter = AppointmentAdapter(filterResult, this, this)


    }


    private fun initListner() {

        binding.toolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.toolbarHeader.text = getString(R.string.filter_result)
        binding.toolbar.ivBackBg.visibility = View.GONE
        binding.toolbar.ivBack.visibility = View.GONE

        binding.appointmentRv.layoutManager = LinearLayoutManager(requireContext())
    }

    fun stringToWords(s : String) = s.trim().splitToSequence(' ')
        .filter { it.isNotEmpty() } // or: .filter { it.isNotBlank() }
        .toList()



}