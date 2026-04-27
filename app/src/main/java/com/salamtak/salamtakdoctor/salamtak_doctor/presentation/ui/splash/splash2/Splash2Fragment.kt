package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash2

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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.salamtak.R
import com.salamtak.databinding.FragmentSplash2Binding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash2.screen.Screen1Fragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash2.screen.Screen2Fragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash2.screen.Screen3Fragment



class Splash2Fragment : Fragment() {

    lateinit var binding: FragmentSplash2Binding
    lateinit var sliderAdapter: SliderAdapter
    lateinit var navController: NavController
    private lateinit var mContext: Context
    private var fragmentList = ArrayList<Fragment>()
    var pagePosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash2, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentList.clear()
        fragmentList.add(Screen1Fragment())
        fragmentList.add(Screen2Fragment())
        fragmentList.add(Screen3Fragment())

        sliderAdapter = SliderAdapter(fragmentList, requireActivity())
        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context

        setUi()

        binding.skipOrStartBtn.setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.splashFragment) }
    }

    private fun setUi(){

        binding.splashSlider.adapter = sliderAdapter

//        binding.tabLayout.setupWithViewPager(binding.splashSlider)


        binding.splashSlider.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        binding.ButtonNext.setOnClickListener {
            if (binding.splashSlider.currentItem != 2)
                binding.splashSlider.currentItem = binding.splashSlider.currentItem + 1
            else
                navController.navigate(R.id.loginFragment)
        }


        val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                pagePosition = position
                if (position == 2){
                    binding.ButtonNext.text = getString(R.string.lets_start)
                }else{
                    binding.ButtonNext.text =getString(R.string.next)
                }
            }
        }

        binding.splashSlider.registerOnPageChangeCallback(myPageChangeCallback)

    }



}