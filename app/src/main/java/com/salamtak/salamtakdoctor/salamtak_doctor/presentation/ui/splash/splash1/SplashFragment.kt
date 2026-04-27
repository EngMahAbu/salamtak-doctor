package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.splash1

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentSplashBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import org.koin.android.ext.android.inject

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var navController: NavController
    lateinit var mContext: Context
    private val sessionManagement: SessionManagement by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
            //doSomethingHere()
        mContext = binding.root.context
        initView(mContext)

    }

    private fun initView(mContext: Context) {

        // Mansour Comment New
//        UserData.setLanguageID(Localization.ENGLISH_VALUE)
  //     Localization.setApplicationLanguage(mContext)

        val animationimage = AnimationUtils.loadAnimation(
            context,
            R.anim.fade_in
        )
        binding.imageView2.startAnimation(animationimage)
        binding.powerTv.startAnimation(animationimage)
        binding.logotext.startAnimation(animationimage)


        animationimage.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {

                if (DateUtils.getLang().isNullOrEmpty()) {
                    navController.navigate(R.id.languageFragment)
                } else {

                    if (!DateUtils.getLanguageFromMore().isNullOrEmpty()) {
                        findNavController().navigate(R.id.moreFragment)
                        DateUtils.setLanguageFromMore("")
                    } else {
                        if (DateUtils.getStatus() != "4") {
                            sessionManagement.clearUser()
                            UserData.logout()
//                    navController.navigate(R.id.splash2Fragment)
                            navController.navigate(R.id.splash2Fragment)
                        } else {
                            if (sessionManagement.isLoggedIn()) {
                                navController.navigate(R.id.homeFragment)
//                    navController.navigate(R.id.action_homeFragment_to_signInFragment)
                            } else {
                                navController.navigate(R.id.loginFragment)
                            }
                        }

                        //  navController.navigate(R.id.languageFragment)
                    }
                  // navController.navigate(R.id.splash2Fragment)
//                navController.navigate(R.id.addNewClinicFragment)
                }
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })

    }

}