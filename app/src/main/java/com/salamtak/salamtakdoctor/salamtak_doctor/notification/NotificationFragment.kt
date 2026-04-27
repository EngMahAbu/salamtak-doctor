package com.salamtak.salamtakdoctor.salamtak_doctor.notification

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salamtak.R
import com.salamtak.databinding.FragmentNotificationBinding
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog

class NotificationFragment : Fragment(), DialogsListener {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var viewModel: NotificationViewModel
    private lateinit var mContext: Context
    private lateinit var noInternetDialog: NoInternetDialog
    lateinit var loading: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loading = Dialog(requireContext())
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)

        initListener()
        initObservers()


            requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }



    }

    private fun initListener() {

        binding.notificationRv.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun initObservers() {
        if (isOnline(mContext)) {
            viewModel.getNotification()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        viewModel.notificationModel.observe(viewLifecycleOwner) {
            binding.notificationRv.adapter = NotificationAdapter(it.Data)
        }

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()

        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }
}