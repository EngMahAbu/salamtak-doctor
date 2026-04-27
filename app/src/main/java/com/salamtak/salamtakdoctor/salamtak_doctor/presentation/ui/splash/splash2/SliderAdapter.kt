package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.salamtak.databinding.FragmentSplash2Binding

class SliderAdapter(
    private val fragmentList: MutableList<Fragment>,
    fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)
{

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }


}