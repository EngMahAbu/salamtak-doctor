package com.salamtak.salamtakdoctor.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.NotificationListfragmentBinding
import com.salamtak.salamtakdoctor.adapter.NotificationsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource

class NotificationListFragment : Fragment() {

   private lateinit var binding: NotificationListfragmentBinding
    private lateinit var viewModel: InsuranceViewModel
    private lateinit var polyClinicsViewModel: PolyClinicsViewModel
    private lateinit var notificationsAdapter: NotificationsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= NotificationListfragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(InsuranceViewModel::class.java)
        polyClinicsViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationsRecylerview()
        callBack()
        callBackUpdateNotification()
        initButton()
    }

    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun notificationsRecylerview() {
        notificationsAdapter = NotificationsAdapter()
        binding.rvNotifications.apply {
            adapter = notificationsAdapter
            setHasFixedSize(true)
        }
    }
    private fun callBack() {

            viewModel.notificationsResponse.observe(viewLifecycleOwner, Observer {

                if (it.data != null) {
                    notificationsAdapter.submitList(it.data)
                }
                polyClinicsViewModel.updateNotification()

            })



            viewModel.error.observe(viewLifecycleOwner, Observer {
                errorMessage(it.Message.toString())
            })

            viewModel.showLoading.observe(viewLifecycleOwner, Observer {
                if (it)
                binding.healthProgressBar.visibility=View.VISIBLE
                else
                    binding.healthProgressBar.visibility=View.GONE
            })

            viewModel.getUserNotifications()

    }
    private fun callBackUpdateNotification(){
        polyClinicsViewModel.updatenotificationResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.sucess -> {

                }

                is Resource.Error -> {

                }

                is Resource.Loading -> {
                }
            }
        })
    }



}