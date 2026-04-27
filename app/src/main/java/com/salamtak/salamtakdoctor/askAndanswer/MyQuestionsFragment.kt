package com.salamtak.salamtakdoctor.askAndanswer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.databinding.MyQuestionsfragmentBinding
import com.salamtak.domain.entities.Questions
import com.salamtak.salamtakdoctor.adapter.QuestionsAdapter
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource

class MyQuestionsFragment : Fragment(),QuestionsAdapter.IAddAnswer{

 private lateinit var binding: MyQuestionsfragmentBinding

    private lateinit var questionsAdapter: QuestionsAdapter
    private lateinit var polyViewModel: PolyClinicsViewModel
    private var partMap: Map<String, Any> = mutableMapOf()
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private var id:Int?=null
    private var doctorId:Int?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= MyQuestionsfragmentBinding.inflate(layoutInflater,container,false)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        initButton()
        questionsRecylerview()
        callGetQuestions()
        callCancelQuestions()
    }
    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.layoutBottomsheetpersistant.btnConfirm.setOnClickListener {
           if (binding.layoutBottomsheetpersistant.etAnswer.text.toString().isNullOrEmpty())
           {
               Toast.makeText(requireContext(),
                   getString(R.string.please_enter_your_answer),Toast.LENGTH_SHORT).show()
           }else {
               sendingAnswer()
           }
        }

    }
    private fun questionsRecylerview() {
        questionsAdapter = QuestionsAdapter(this,false)
        binding.rvQuestions.apply {
            adapter = questionsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    private fun callGetQuestions() {

        polyViewModel.questionsUserResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                  binding.progressBar.visibility=View.VISIBLE
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    binding.progressBar.visibility=View.GONE
                    questionsAdapter.submitList(it.data?.data)
                    updateUi(it.data?.data as List<Questions>?)
                    questionsAdapter.notifyDataSetChanged()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                }

            }
        })

        polyViewModel.getAllQandAByDoctorId(1)

    }
    private fun callCancelQuestions() {

        polyViewModel.cancelQuestionsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {

                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    callGetQuestions()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {

                }

            }
        })

    }
    private fun updateUi(it: List<Questions>?) {
        if (it.isNullOrEmpty()) {
            binding.layoutCardview.visibility = View.VISIBLE
        } else {
            binding.layoutCardview.visibility = View.GONE
        }
    }
    private fun bindViews() {
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.addAnswerBottomsheet)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun addAnswer(questions: Questions) {
        id=questions.id
        doctorId=questions.doctorID
        if (DefaultLocaleHelper.getInstance(requireContext()).getCurrentLocale()=="en"){
            binding.layoutBottomsheetpersistant.tvQuestions.text="${getString(R.string.your_question)} : ${questions.question} ?"
        }else{
            binding.layoutBottomsheetpersistant.tvQuestions.text="${getString(R.string.your_question)} : ${questions.question}؟ "
        }
        bottomsheetbeahavoir.state =
            if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
    }

    override fun cancelAnswer(id: Int?) {
        if (id != null) {
            polyViewModel.cancelQuestion(id)
        }
    }

    private fun sendingAnswer(){

        if (id != null && id != 0) {
            partMap = (partMap + mapOf("id" to id!!))
        }
        if (doctorId != null && doctorId != 0) {
            partMap = (partMap + mapOf("DoctorID" to doctorId!!))
        }
        if (binding.layoutBottomsheetpersistant.etAnswer.text.toString() != null) {
            partMap = partMap + mapOf("Answer" to binding.layoutBottomsheetpersistant.etAnswer.text.toString())
        }

        polyViewModel.createAnswerPatientQ(partMap as HashMap<String, String>)
        polyViewModel.answerUserResponse.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.sucess -> {
                        binding.layoutBottomsheetpersistant.progressBar.visibility = View.GONE
                        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
                        callGetQuestions()
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
}