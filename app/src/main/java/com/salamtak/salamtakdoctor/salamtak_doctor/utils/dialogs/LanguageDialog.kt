package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.DialogLanguageBinding
import com.salamtak.databinding.DialogUpdateBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.setLocale
import org.koin.android.ext.android.inject

class LanguageDialog(private val navController: NavController) : DialogFragment() {

    private lateinit var binding: DialogLanguageBinding
    private val sessionManagement: SessionManagement by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_language, container, false)

//        binding.dialogHeader.setText("Done")

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        binding.dialogOk.setOnClickListener {
//            dialog!!.dismiss()
//        }

        binding.langAr.setOnClickListener {
            toggleArabicLanguage()
//            dialog!!.dismiss()
//            navController.navigate(R.id.splashFragment)
        }

        binding.langEn.setOnClickListener {
            toggleLanguage()
//            dialog!!.dismiss()
//            navController.navigate(R.id.splashFragment)
        }

    }

    private fun toggleLanguage() {
        val lang =
            if (sessionManagement.getLang() == SessionManagement.KEY_LANGUAGE_AR) SessionManagement.KEY_LANGUAGE_EN
            else SessionManagement.KEY_LANGUAGE_AR
        setLocale(requireActivity(), lang)
        sessionManagement.setLang(lang)
    }

    private fun toggleArabicLanguage() {
        val lang =
            if (sessionManagement.getLang() == SessionManagement.KEY_LANGUAGE_EN) SessionManagement.KEY_LANGUAGE_AR
            else SessionManagement.KEY_LANGUAGE_EN
        setLocale(requireActivity(), lang)
        sessionManagement.setLang(lang)
    }

}