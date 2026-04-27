package com.salamtak.salamtakdoctor.aboutUs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentAboutUsBinding
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutUsBinding.inflate(inflater, container, false)

        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button press
      requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }


    }





    private fun initListener() {
        binding.toolbar.toolbarHeader.text = getString(R.string.about_us)
        binding.toolbar.ivBackBg.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back_bg -> {
                findNavController().popBackStack()
            }
        }
    }


}

