package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash2.screen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentScreen3Binding


class Screen3Fragment() : Fragment() {

    lateinit var binding: FragmentScreen3Binding
    lateinit var navController: NavController




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_screen3, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mContext = binding.root.context
//        navController = Navigation.findNavController(binding.root)
        Log.e("Welcome", "Welcome Opened")
        setUi()
    }

    private fun setUi(){

//        binding.skipOrStartBtn.text = mContext.resources.getString(R.string.start)
//        Log.e("TAGScreen1", "newInstance: ${binding.skipOrStartBtn.text}" )
//        binding.skipOrStartBtn.setOnClickListener {
//            navController.navigate(R.id.chooseFragment)
//        }
        binding.skipOrStartBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_screen3Fragment_to_welcomeFragment
            )
        }
    }



}