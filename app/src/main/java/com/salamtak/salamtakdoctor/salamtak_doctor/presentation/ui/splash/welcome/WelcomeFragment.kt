package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.splash.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentWelcomeBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.dialog.WrongAppDialogFragment

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
    }

    private fun initClicks() {
//        binding.btnPatient.setOnClickListener {
//            findNavController().navigate(R.id.action_welcome_to_register)
//        }

        binding.btnPatient.setOnClickListener {
            WrongAppDialogFragment.newInstance(isPatientApp = true)
                .show(parentFragmentManager, "WrongAppDialog")
        }
        binding.btnDoctor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("typId", 0)
            }

            findNavController().navigate(
                R.id.action_welcome_to_register,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
