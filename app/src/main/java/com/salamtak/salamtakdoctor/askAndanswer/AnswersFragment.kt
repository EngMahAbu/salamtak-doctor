package com.salamtak.salamtakdoctor.askAndanswer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salamtak.databinding.AnswersfragmentBinding
import com.salamtak.salamtakdoctor.adapter.AnswersAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel


class AnswersFragment : Fragment(), AnswersAdapter.IBook{

    private lateinit var binding: AnswersfragmentBinding
    private lateinit var answersAdapter: AnswersAdapter
    private lateinit var polyViewModel: PolyClinicsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= AnswersfragmentBinding.inflate(layoutInflater,container,false)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
        questionscRecylerview()
        callGetQuestions()
    }

    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun questionscRecylerview() {
        answersAdapter = AnswersAdapter(this)
        binding.rvAnswers.apply {
            adapter = answersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onItemClick(doctorId: Int,clinicId: Int) {

    }

    private fun callGetQuestions() {

        polyViewModel.answersDoctorResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    binding.progressBar.visibility=View.GONE
                    binding.questions.text=""
                    answersAdapter.submitList(it.data?.data)
                    answersAdapter.notifyDataSetChanged()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                }

            }
        })

        polyViewModel.getAnswersByDoctorId()

    }
}