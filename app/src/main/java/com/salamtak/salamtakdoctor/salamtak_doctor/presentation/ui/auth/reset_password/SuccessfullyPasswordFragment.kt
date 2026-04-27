package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.reset_password

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentSuccessfullyPasswordBinding


class SuccessfullyPasswordFragment : Fragment() {

    private lateinit var binding: FragmentSuccessfullyPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuccessfullyPasswordBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

}