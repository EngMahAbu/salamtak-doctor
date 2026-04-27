package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.more

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentMoreSettingsBinding
import org.koin.android.ext.android.inject

class MoreSettingsFragment : Fragment() {

    private lateinit var binding: FragmentMoreSettingsBinding
    private lateinit var navController: NavController
    private lateinit var mContext: Context
    private val sessionManagement: SessionManagement by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_more_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context

        setUi()


            requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }


    }

    private fun setUi(){

        binding.toolbar.ivBack.setOnClickListener {
            navController.popBackStack()
        }
        binding.toolbar.toolbarHeader.text = getString(R.string.more)

        binding.tvLogout.setOnClickListener {

            sessionManagement.clearUser()
            navController.navigate(R.id.action_logout_settings)

        }

    }

}