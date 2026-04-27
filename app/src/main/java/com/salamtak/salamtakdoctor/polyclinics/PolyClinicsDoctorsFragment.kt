package com.salamtak.salamtakdoctor.polyclinics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.salamtak.R
import com.salamtak.databinding.PolyClinicsDoctorsfragmentBinding
import com.salamtak.domain.entities.response.data.DataInfo
import com.salamtak.salamtakdoctor.adapter.PollyClinicsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource

class PolyClinicsDoctorsFragment : Fragment(),PollyClinicsAdapter.CreateSchedule{

    private lateinit var binding: PolyClinicsDoctorsfragmentBinding
    private lateinit var viewModel: PolyClinicsViewModel
    private lateinit var polyClinicsAdapter:PollyClinicsAdapter
    private val args:PolyClinicsDoctorsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PolyClinicsDoctorsfragmentBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doctorsRecylerview()
        callGetDoctorsClinicDayId()
        initButton()
    }

    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            findNavController().navigate(R.id.clinicsFragment)
        }
        binding.btnNext.setOnClickListener {
            val action=PolyClinicsDoctorsFragmentDirections.actionPolyClinicsDoctorsFragmentToSetupProfileFragment(args.clinicId,"")
            findNavController().navigate(action)
        }
    }

    private fun doctorsRecylerview() {
        polyClinicsAdapter = PollyClinicsAdapter(this)
        binding.rvDoctors.apply {
            adapter = polyClinicsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun callGetDoctorsClinicDayId() {
        viewModel.polyClinicDoctorsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                   binding.progressBar.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.progressBar.visibility=View.GONE
                    polyClinicsAdapter.submitList(it.data?.data)
                    polyClinicsAdapter.notifyDataSetChanged()

                }

                is Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                }

            }
        })
        viewModel.getDoctorByClinicId(args.clinicId)
    }

    override fun createSchedule(doctorId: Int) {
        val action=PolyClinicsDoctorsFragmentDirections.actionPolyClinicsDoctorsFragmentToPolyClinicsScheduleFragment(args.clinicId,doctorId)
        findNavController().navigate(action)
    }

    override fun editDoctorProfile(doctorId: Int) {
        DateUtils.setDoctorId(doctorId.toString())
        val action=PolyClinicsDoctorsFragmentDirections.actionPolyClinicsDoctorsFragmentToSetupProfileFragment(0,"edit")
        findNavController().navigate(action)
    }


}