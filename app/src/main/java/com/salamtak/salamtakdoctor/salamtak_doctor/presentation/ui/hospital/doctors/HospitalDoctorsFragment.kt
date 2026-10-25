package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.salamtak.databinding.FragmentHospitalDoctorsBinding

/** Hospital "Doctors" tab. Placeholder for now; the doctors list is built in Milestone 3. */
class HospitalDoctorsFragment : Fragment() {

    private lateinit var binding: FragmentHospitalDoctorsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalDoctorsBinding.inflate(inflater, container, false)
        return binding.root
    }
}
