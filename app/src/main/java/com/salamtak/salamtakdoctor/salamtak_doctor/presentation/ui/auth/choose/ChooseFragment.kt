package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.choose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.salamtak.R
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.core.utils.SessionManagement.Companion.KEY_LANGUAGE_AR
import com.salamtak.core.utils.SessionManagement.Companion.KEY_LANGUAGE_EN
import com.salamtak.databinding.FragmentChooseBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.setLocale
import org.koin.android.ext.android.inject


class ChooseFragment : Fragment() {

    lateinit var binding: FragmentChooseBinding
    lateinit var navController: NavController
    lateinit var mContext: Context
    private val sessionManagement: SessionManagement by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context

        setUi()
    }

    fun setUi(){

        binding.tvLang.setOnClickListener {

            val lang = if (sessionManagement.getLang() == KEY_LANGUAGE_AR) KEY_LANGUAGE_EN
            else KEY_LANGUAGE_AR

            setLocale(requireActivity(), lang)

            sessionManagement.setLang(lang)

        }

        binding.toLoginBtn.setOnClickListener {
            Log.d(TAG, "setUi: " + UserData.getToken() )
            Log.d(TAG, "setUi: " + sessionManagement.getSignIn())

            navController.navigate(R.id.loginFragment)
        }

        binding.toNewAccountBtn.setOnClickListener {
            navController.navigate(R.id.signUpFragment)
        }

    }


}