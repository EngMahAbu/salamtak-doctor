package com.salamtak.salamtakdoctor.salamtakgates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.salamtak.databinding.ReferencefragmenBinding

class ReferenceFragment : Fragment() {

    private lateinit var binding: ReferencefragmenBinding
    private val args:ReferenceFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= ReferencefragmenBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
    }
    private fun initview() {

        binding.webview.apply {

            webViewClient = WebViewClient()
            args.link?.let { loadUrl(it) }
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

        }
    }

}