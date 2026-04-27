package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash2.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.salamtak.R
import com.salamtak.databinding.FragmentScreen1Binding


class Screen1Fragment(

) : Fragment() {

    private lateinit var binding: FragmentScreen1Binding
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_screen1, container, false)
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
//        Log.e("TAGScreen1", "newInstance: ${binding.skipOrStartBtn.text}")
//        binding.skipOrStartBtn.setOnClickListener {
//            navController.navigate(R.id.chooseFragment)
//        }
    }



}