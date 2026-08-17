package com.salamtak.salamtakdoctor.webview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment : Fragment() {

    private lateinit var binding:FragmentPrivacyPolicyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyPolicyBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.moreFragment) }

        initview()
    }

    private fun initview() {
        if (Localization.getLanguageName() == "ar") {
            createLink("https://salamtakgroup.com/ar/سياسة-الخصوصية")
        } else {
            createLink("https://salamtakgroup.com/en/privacyPolicy")
        }
    }

    private fun createLink(link: String) {
        binding.webview.apply {
            webViewClient = WebViewClient()
            loadUrl(link)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }

    }

}