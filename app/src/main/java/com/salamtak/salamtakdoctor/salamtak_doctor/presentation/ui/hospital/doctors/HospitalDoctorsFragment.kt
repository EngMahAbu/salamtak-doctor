package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.databinding.FragmentHospitalDoctorsBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource

/** Hospital "Doctors" tab: the hospital's doctors with clinic / schedule / profile / remove actions. */
class HospitalDoctorsFragment : Fragment(), HospitalDoctorsAdapter.Listener {

    private lateinit var binding: FragmentHospitalDoctorsBinding
    private lateinit var viewModel: HospitalDoctorsViewModel
    private val adapter = HospitalDoctorsAdapter(this)
    private val hospitalId = DateUtils.getHospitalId()
    private var handledAction: Resource<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalDoctorsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HospitalDoctorsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDoctors.adapter = adapter
        binding.btnAddDoctor.setOnClickListener {
            // Existing 4-step add-doctor flow; SetupProfileFragment runs it in hospital mode
            findNavController().navigate(
                R.id.setupProfileFragment, bundleOf("clinicid" to 0, "editProfile" to "")
            )
        }

        viewModel.doctors.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
            when (it) {
                is Resource.sucess -> {
                    val rows = it.data.orEmpty()
                    adapter.submit(rows)
                    binding.tvEmpty.visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> showMessage(it.message ?: getString(R.string.hospital_info_error))
                else -> Unit
            }
        }

        viewModel.action.observe(viewLifecycleOwner) {
            // The last result is re-delivered when this view is recreated: handle each once
            if (it === handledAction) return@observe
            handledAction = it
            when (it) {
                is Resource.Loading -> binding.progress.visibility = View.VISIBLE
                is Resource.sucess -> {
                    showMessage(getString(R.string.hospital_doctor_removed))
                    load()
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    showMessage(it.message ?: getString(R.string.hospital_info_error))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload on return: a clinic may have been added or the profile changed
        load()
    }

    private fun load() {
        hospitalId?.let { viewModel.loadDoctors(it) }
    }

    override fun onClinics(row: HospitalDoctorRow) {
        findNavController().navigate(
            R.id.hospitalDoctorClinicsFragment, bundleOf(ARG_DOCTOR_ID to row.doctor.DoctorId)
        )
    }

    override fun onAddClinic(row: HospitalDoctorRow) {
        findNavController().navigate(
            R.id.hospitalClinicFormFragment, bundleOf(ARG_DOCTOR_ID to row.doctor.DoctorId)
        )
    }

    override fun onSchedule(row: HospitalDoctorRow) {
        val clinicId = row.clinic?.ClinicId
        if (clinicId == null) {
            // The schedule belongs to the doctor's clinic
            showMessage(getString(R.string.hospital_add_clinic_first))
            return
        }
        findNavController().navigate(
            R.id.hospitalDoctorScheduleFragment,
            bundleOf(ARG_DOCTOR_ID to row.doctor.DoctorId, ARG_CLINIC_ID to clinicId)
        )
    }

    override fun onEdit(row: HospitalDoctorRow) {
        findNavController().navigate(
            R.id.hospitalDoctorProfileFragment, bundleOf(ARG_DOCTOR_ID to row.doctor.DoctorId)
        )
    }

    override fun onDelete(row: HospitalDoctorRow) {
        val hospitalId = hospitalId ?: return
        val confirm = BottomSheetDialog(requireContext())
        confirm.setContentView(R.layout.cancelappointmentbottomsheet)
        confirm.findViewById<TextView>(R.id.tv_display)?.setText(R.string.hospital_remove_doctor_confirm)
        confirm.findViewById<MaterialButton>(R.id.yes_btn)?.setOnClickListener {
            confirm.dismiss()
            viewModel.deleteDoctor(hospitalId, row.doctor.DoctorId)
        }
        confirm.findViewById<MaterialButton>(R.id.No_button)?.setOnClickListener { confirm.dismiss() }
        confirm.show()
    }

    private fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val ARG_DOCTOR_ID = "doctorId"
        const val ARG_CLINIC_ID = "clinicId"
    }
}
