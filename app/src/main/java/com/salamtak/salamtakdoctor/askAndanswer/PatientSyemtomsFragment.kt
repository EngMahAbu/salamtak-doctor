package com.salamtak.salamtakdoctor.askAndanswer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salamtak.R
import com.salamtak.databinding.PatientSyemtomsfragmentBinding
import com.salamtak.domain.entities.response.symtemos.Data
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.DoctorAccountViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.APPOINTMENT_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource

import androidx.activity.addCallback

class PatientSyemtomsFragment : Fragment() {

 private lateinit var binding:PatientSyemtomsfragmentBinding
    private lateinit var doctorAccountviewModel: DoctorAccountViewModel
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private var partMap: Map<String, Any> = mutableMapOf()
    var appointmentId: Long = -1
    var patientSymptomId=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= PatientSyemtomsfragmentBinding.inflate(layoutInflater,container,false)
        doctorAccountviewModel = ViewModelProvider(this)[DoctorAccountViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            appointmentId = it.getLong(APPOINTMENT_ID, 0)
        }
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.addAnswerBottomsheet)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        callBackGetAllPatientInvoice()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.patientProfileFragment)
            }
        }
        initButton()
    }

    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.patientProfileFragment)
            }
        }
        binding.btnAddAnswer.setOnClickListener {
            bottomsheetbeahavoir.state =
                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
        }

        binding.layoutBottomsheetpersistant.btnConfirm.setOnClickListener {
            sendingFeedBack()
        }
    }

    private fun callBackGetAllPatientInvoice() {

        doctorAccountviewModel.patientsyemtomsResponse.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.sucess -> {
                        binding.progressBar.visibility=View.GONE
                        response.data?.data?.let { bindViews(it) }
                        patientSymptomId= response.data?.data?.patientSymptomId!!
                        if (response.data?.data?.doctorResponded==true){
                            binding.btnAddAnswer.visibility=View.GONE
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility=View.GONE
                    }

                    is Resource.Loading -> {
                     binding.progressBar.visibility=View.VISIBLE
                    }
                }
            })
        doctorAccountviewModel.getSymptomsByAppointmentId(appointmentId.toInt())

    }

    private fun sendingFeedBack(){


        if (patientSymptomId != null && patientSymptomId != 0) {
            partMap = (partMap + mapOf("PatientSymptomId" to patientSymptomId!!))
        }
        if (binding.layoutBottomsheetpersistant.etAnswer.text.toString() != null) {
            partMap = partMap + mapOf("ResponseText" to binding.layoutBottomsheetpersistant.etAnswer.text.toString())
        }

        doctorAccountviewModel.createDoctorResponse(partMap as HashMap<String, String>)
        doctorAccountviewModel.doctorFeedbackResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.sucess -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility = View.GONE
                    bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
                    callBackGetAllPatientInvoice()
                }

                is Resource.Error -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility = View.GONE

                }

                is Resource.Loading -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun bindViews(data:Data){
        val list= mutableListOf<String>()
        data.symptoms?.forEach {
            if (it != null) {
                it.name?.let { it1 -> list.add(it1) }
            }
        }
        binding.patientNameEt.setText(data.patientName)
        data.age?.let { binding.patientNumberEt.setText(it.toString()) }
        binding.patientAgeEt.setText(list.toString().removePrefix("[")?.removeSuffix("]"))
        binding.patientCommentEt.setText(data.comment)
        binding.doctorReplayEt.setText(data.doctorResponseText)
        binding.layoutBottomsheetpersistant.btnConfirm.text=getString(R.string.sendfeedback)
        binding.layoutBottomsheetpersistant.tvQuestions.text=getString(R.string.doctorreplay)
    }
}