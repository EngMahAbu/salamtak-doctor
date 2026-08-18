package com.salamtak.salamtakdoctor.salamtakgates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.salamtak.databinding.GatesDetailsfragmentBinding
import java.text.SimpleDateFormat
import java.util.*


class GatesDetailsFragment : Fragment() {

    private lateinit var binding: GatesDetailsfragmentBinding
    private val args:GatesDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= GatesDetailsfragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        Glide.with(binding.ivDoctorProfile.context)
            .load(args.gatesdetails?.image)
            .into(binding.ivDoctorProfile)

        val dateText = try {
            if (!args.gatesdetails?.date.isNullOrEmpty()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val parsedDate = sdf.parse(args.gatesdetails?.date!!)
                parsedDate?.let { SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(it) } ?: ""
            } else ""
        } catch (e: Exception) {
            args.gatesdetails?.date ?: ""
        }

        binding.apply {
            tvHealth.text = args.gatesdetails?.title ?: ""
            tvDescription.text = args.gatesdetails?.description ?: ""
            tvDate.text = dateText
        }
        initButton()
    }

    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            findNavController().popBackStack()
        }

//        binding.tvRefrence.setOnClickListener {
//            if (!args.gatesdetails?.links.isNullOrEmpty()){
//                val action=GatesDetailsFragmentDirections.actionGatesDetailsFragmentToReferenceFragment( args.gatesdetails?.links?.first())
//                findNavController().navigate(action)
//            }
//        }
    }

}