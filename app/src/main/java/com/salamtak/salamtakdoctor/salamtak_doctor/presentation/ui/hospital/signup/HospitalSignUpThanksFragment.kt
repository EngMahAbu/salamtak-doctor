package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentHospitalSignupThanksBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.EMAIL
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.PHONE
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import org.koin.android.ext.android.inject

/**
 * Step 1 done: admin name, "Your Profile Is Completed Successfully", "Hospital Profile" → Hospital Info.
 * Step 2 done: hospital name, "For Completing Hospital Profile", "Let's Start" → opens the session → Home.
 */
class HospitalSignUpThanksFragment : Fragment() {

    private lateinit var binding: FragmentHospitalSignupThanksBinding
    private lateinit var viewModel: HospitalSignUpViewModel
    private val sessionManagement: SessionManagement by inject()
    private val step by lazy { requireArguments().getInt(ARG_STEP, 1) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalSignupThanksBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalSignUpViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { }
        binding.tvName.text = arguments?.getString(ARG_NAME).orEmpty()

        if (step == 1) {
            binding.tvMessage.setText(R.string.yourprofilecompleted)
            binding.tvSubMessage.setText(R.string.hospital_next_step_profile)
            binding.btnContinue.setText(R.string.hospital_profile_button)
            binding.btnContinue.setOnClickListener {
                findNavController().navigate(
                    R.id.hospitalSignUpInfoFragment,
                    bundleOf(PHONE to arguments?.getString(PHONE), EMAIL to arguments?.getString(EMAIL))
                )
            }
        } else {
            binding.tvMessage.setText(R.string.hospital_for_completing_profile)
            binding.tvSubMessage.visibility = View.GONE
            binding.btnContinue.setText(R.string.lets_start)
            binding.btnContinue.setOnClickListener { DateUtils.getHospitalInfoId()?.let { viewModel.loadSession(it) } }
        }

        viewModel.sessionInfo.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
            binding.btnContinue.isEnabled = it !is Resource.Loading
            when (it) {
                is Resource.sucess -> it.data?.let { info -> openSession(info.HospitalId, info.Name, info.NameAr) }
                is Resource.Error -> Toast.makeText(
                    requireContext(), it.message ?: getString(R.string.hospital_info_error), Toast.LENGTH_SHORT
                ).show()
                else -> Unit
            }
        }
    }

    /** Same session the hospital login opens: hospital keys, hospital name, status "7". */
    private fun openSession(hospitalId: Int?, name: String?, nameAr: String?) {
        val hospitalId = hospitalId ?: return
        DateUtils.setHospitalId(hospitalId.toString())
        DateUtils.setAdminOwnerId("")
        name?.takeIf { it.isNotBlank() }?.let { UserData.setUserName(it) }
        nameAr?.takeIf { it.isNotBlank() }?.let { UserData.setUserNameAR(it) }
        DateUtils.setStatus(DateUtils.HOSPITAL_STATUS)
        sessionManagement.setLoggedIn()
        findNavController().navigate(
            R.id.homeFragment, null,
            NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
        )
    }

    companion object {
        const val ARG_STEP = "step"
        const val ARG_NAME = "name"
    }
}
