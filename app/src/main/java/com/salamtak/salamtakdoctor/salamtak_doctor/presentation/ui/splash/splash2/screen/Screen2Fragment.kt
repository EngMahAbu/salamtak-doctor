package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash2.screen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentScreen2Binding


class Screen2Fragment() : Fragment() {

    lateinit var binding: FragmentScreen2Binding
    lateinit var navController: NavController



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_screen2, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        navController = Navigation.findNavController(binding.root)
//        mContext = binding.root.context
        setUi()

    }

    private fun setUi(){

//        binding.skipOrStartBtn.text = mContext.resources.getString(R.string.skip)
//        Log.e("TAGScreen1", "newInstance: ${binding.skipOrStartBtn.text}" )
//        binding.skipOrStartBtn.setOnClickListener {
//            navController.navigate(R.id.chooseFragment)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController().navigate(R.id.splashFragment)
    }


}