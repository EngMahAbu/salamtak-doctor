package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.FragmentHospitalDoctorClinicsBinding
import com.salamtak.databinding.ItemHospitalDoctorClinicBinding
import com.salamtak.domain.entities.response.hospital.HospitalClinic
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors.HospitalDoctorsFragment.Companion.ARG_CLINIC_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors.HospitalDoctorsFragment.Companion.ARG_DOCTOR_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource

/** "Doctor Clinics": the doctor's clinic(s) with edit and remove. */
class HospitalDoctorClinicsFragment : Fragment() {

    private lateinit var binding: FragmentHospitalDoctorClinicsBinding
    private lateinit var viewModel: HospitalDoctorsViewModel
    private val adapter = ClinicsAdapter()
    private val doctorId by lazy { requireArguments().getInt(ARG_DOCTOR_ID) }
    private var handledAction: Resource<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalDoctorClinicsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalDoctorsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.rvClinics.adapter = adapter

        viewModel.clinics.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
            if (it is Resource.sucess) {
                val list = it.data.orEmpty()
                adapter.submit(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        viewModel.action.observe(viewLifecycleOwner) {
            if (it === handledAction) return@observe
            handledAction = it
            when (it) {
                is Resource.Loading -> binding.progress.visibility = View.VISIBLE
                is Resource.sucess -> viewModel.loadClinics(doctorId)
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    Snackbar.make(requireView(), it.message ?: getString(R.string.hospital_info_error), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadClinics(doctorId)
    }

    private fun confirmRemove(clinic: HospitalClinic) {
        val confirm = BottomSheetDialog(requireContext())
        confirm.setContentView(R.layout.cancelappointmentbottomsheet)
        confirm.findViewById<TextView>(R.id.tv_display)?.setText(R.string.hospital_remove_clinic_confirm)
        confirm.findViewById<MaterialButton>(R.id.yes_btn)?.setOnClickListener {
            confirm.dismiss()
            viewModel.removeDoctorFromClinic(clinic.ClinicId, doctorId)
        }
        confirm.findViewById<MaterialButton>(R.id.No_button)?.setOnClickListener { confirm.dismiss() }
        confirm.show()
    }

    private inner class ClinicsAdapter : RecyclerView.Adapter<ClinicsAdapter.ViewHolder>() {
        private var clinics: List<HospitalClinic> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun submit(list: List<HospitalClinic>) {
            clinics = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            ItemHospitalDoctorClinicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun getItemCount() = clinics.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val clinic = clinics[position]
            val arabic = Localization.getLanguageName() == "ar"
            with(holder.binding) {
                tvClinicName.text = (if (arabic) clinic.NameAr else clinic.Name)
                    ?.takeIf { it.isNotBlank() } ?: clinic.Name.orEmpty()
                tvClinicPhone.text = clinic.Phones?.firstOrNull().orEmpty()
                ImageUtil.loadPicture(ivLogo, clinic.Logo)
                ivEdit.setOnClickListener {
                    findNavController().navigate(
                        R.id.hospitalClinicFormFragment,
                        bundleOf(ARG_DOCTOR_ID to doctorId, ARG_CLINIC_ID to clinic.ClinicId)
                    )
                }
                ivDelete.setOnClickListener { confirmRemove(clinic) }
            }
        }

        inner class ViewHolder(val binding: ItemHospitalDoctorClinicBinding) : RecyclerView.ViewHolder(binding.root)
    }
}
