package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.language

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentLanguageBinding
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.setLocale
import org.koin.android.ext.android.inject

class LanguageFragment : Fragment() {

    lateinit var binding: FragmentLanguageBinding
    lateinit var navController: NavController
    lateinit var mContext: Context
    private val sessionManagement: SessionManagement by inject()
    var lang:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_language, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        Handler().postDelayed({
            binding.layoutAll.visibility=View.VISIBLE
            binding.ivCamp.visibility=View.GONE
        }, 2000)
        setUi()
        chooseLanuage()

//        binding.chooseLangBtn.setOnClickListener {
//
//            if (lang==null) {
//                Toast.makeText(requireContext(),getString(R.string.choose_language), Toast.LENGTH_SHORT).show()
//            } else {
//                setLocale(requireActivity(), lang!!)
//                UserData.setLanguageID(Localization.ARABIC_VALUE)
//                Localization.setApplicationLanguage(mContext)
//                sessionManagement.setLang(lang!!)
//                navController.navigate(R.id.action_languageFragment_to_splash2Fragment)
//            }
//        }

    }

    fun setUi() {

        binding.langEn.setOnClickListener {
            binding.radioEn.isChecked = true
            binding.radioAr.isChecked = false
            binding.tvChoos.text="To get started, select your language"
            binding.chooseLangBtn.text="Confirm"
            binding.tvRadioAr.text="Arabic"
            binding.tvRadioEn.text="English"

        }

        binding.langAr.setOnClickListener {
            binding.radioAr.isChecked = true
            binding.radioEn.isChecked = false
            binding.tvChoos.text="للبدء, اختار اللغة"
            binding.chooseLangBtn.text="تأكيد"
            binding.tvRadioAr.text="اللغة العربية"
            binding.tvRadioEn.text="اللغة الانجليزية"
        }

        binding.chooseLangBtn.setOnClickListener {
            if (binding.radioEnglish.isChecked){
                val lang = SessionManagement.KEY_LANGUAGE_EN
               // setLocale(requireActivity(), lang!!)
                // Mansour Comment New
                UserData.setLanguageID(Localization.ENGLISH_VALUE)
                Localization.setApplicationLanguage(mContext)
                DefaultLocaleHelper.getInstance(requireContext()).setCurrentLocale(lang!!)
                requireActivity().finish()
                startActivity(requireActivity().intent)
                sessionManagement.setLang(lang)
                DateUtils.setLang(lang)
                binding.tvChoos.text="To get started, select your language"
                binding.chooseLangBtn.text="Confirm"
                binding.radioAr.text="Arabic"
                binding.radioEn.text="English"

           //     sessionManagement.setLang(lang)
//            navController.navigate(R.id.splash2Fragment)
          //      navController.navigate(R.id.action_languageFragment_to_splash2Fragment)
            }

            if (binding.radioArabic.isChecked){
                val lang = SessionManagement.KEY_LANGUAGE_AR
              //  setLocale(requireActivity(), lang)

                // Mansour Comment New
                DefaultLocaleHelper.getInstance(requireContext()).setCurrentLocale(lang!!)
                requireActivity().finish()
                startActivity(requireActivity().intent)
                sessionManagement.setLang(lang)
                DateUtils.setLang(lang)
                UserData.setLanguageID(Localization.ARABIC_VALUE)
                Localization.setApplicationLanguage(mContext)
                binding.tvChoos.text="للبدء, اختار اللغة"
                binding.chooseLangBtn.text="تأكيد"
                binding.radioAr.text="اللغة العربية"
                binding.radioEn.text="اللغة الانجليزية"
                sessionManagement.setLang(lang)
//            navController.navigate(R.id.splash2Fragment)
       //         navController.navigate(R.id.action_languageFragment_to_splash2Fragment)
            }
        }

    }

    private fun chooseLanuage() {

        binding.radio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_english -> {
                    // Pirates are the best
                    lang = SessionManagement.KEY_LANGUAGE_EN
                    binding.tvChoos.text="To Get Started, Select Your Language"
                    binding.chooseLangBtn.text="Confirm"
                    binding.radioArabic.text="Arabic"
                    binding.radioEnglish.text="English"


                }
                R.id.radio_arabic -> {
                    lang = SessionManagement.KEY_LANGUAGE_AR
                    binding.tvChoos.text="للبدء, اختار اللغة"
                    binding.chooseLangBtn.text="تأكيد"
                    binding.radioArabic.text="اللغة العربية"
                    binding.radioEnglish.text="اللغة الانجليزية"
                }
            }


        }


    }


}