package com.salamtak.salamtakdoctor.salamtak_doctor.doctorProfile

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.salamtak.R
import com.salamtak.databinding.FragmentEditDoctorProfileBinding

class EditDoctorProfileFragment : Fragment() {

    private lateinit var binding : FragmentEditDoctorProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditDoctorProfileBinding.inflate(inflater,container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListnner()
        setButtonColor(binding.personalInfoBtn)
        showLayout(binding.profileInfo)
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }

    }

    private fun initListnner() {
        binding.personalInfoBtn.setOnClickListener {
            setButtonColor(binding.personalInfoBtn)
            showLayout(binding.profileInfo)
        }
        binding.specialtyBtn.setOnClickListener {
            setButtonColor(binding.specialtyBtn)
            showLayout(binding.specialty)
        }
        binding.certificatesBtn.setOnClickListener {
            setButtonColor(binding.certificatesBtn)
            showLayout(binding.certificatesLayout)
        }
        binding.legalBtn.setOnClickListener {
            setButtonColor(binding.legalBtn)
            showLayout(binding.legalLayout)
        }

//        todo pending my site
//        binding.nationalityTv.setOnTouchListener { view, motionEvent ->
//            if(motionEvent.isButtonPressed(true)) {
//                val bottomSheetDialog = BottomSheetDialog(requireContext())
//                bottomSheetDialog.setContentView(R.layout.country_bottom_sheet)
//
////            clinicRV = bottomSheetDialog.findViewById<RecyclerView>(R.id.clinics_home_rv)!!
////            val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.done_btn)
//                bottomSheetDialog.show()
//            }
//        }
//

    }

    @SuppressLint("ResourceAsColor")
    private fun setButtonColor(textView: TextView){
        binding.personalInfoBtn.setTextColor(Color.parseColor("#2B2979"))
        binding.specialtyBtn.setTextColor(Color.parseColor("#2B2979"))
        binding.certificatesBtn.setTextColor(Color.parseColor("#2B2979"))
        binding.legalBtn.setTextColor(Color.parseColor("#2B2979"))

        binding.personalInfoBtn.setBackgroundColor(Color.parseColor("#E8E8FD"))
        binding.specialtyBtn.setBackgroundColor(Color.parseColor("#E8E8FD"))
        binding.certificatesBtn.setBackgroundColor(Color.parseColor("#E8E8FD"))
        binding.legalBtn.setBackgroundColor(Color.parseColor("#E8E8FD"))

        textView.setTextColor(Color.parseColor("#FFFFFF"))
        textView.setBackgroundColor(Color.parseColor("#2B2979"))
    }

    fun showLayout(view:View){
        binding.profileInfo.visibility = View.GONE
        binding.specialty.visibility = View.GONE
        binding.certificatesLayout.visibility = View.GONE
        binding.legalLayout.visibility = View.GONE

        view.visibility = View.VISIBLE
    }
}