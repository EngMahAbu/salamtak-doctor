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
import com.salamtak.databinding.AboutAppfragmentBinding


class AboutAppFragment : Fragment() {

private lateinit var binding: AboutAppfragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= AboutAppfragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.moreFragment) }
        initview()
    }

    private fun initview() {

        binding.webview.apply {

            webViewClient = WebViewClient()
            loadUrl("https://korashigroup.com/")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

        }
    }
}