package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.salamtak.R

object CommonFunctions {

    fun replaceFragment(activity : FragmentActivity, fragment: Fragment, fragmentId : Int) {
        try {
            val transition: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            transition.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transition.replace(fragmentId, fragment)
            transition.commitAllowingStateLoss()
            transition.commit()
            activity.supportFragmentManager.executePendingTransactions()
        } catch (e: Exception) {
            Log.e("TAG", "changeFragment: ${e.message}")
        }
    }
}