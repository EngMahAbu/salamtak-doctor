package com.salamtak.salamtakdoctor.salamtak_doctor.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.FragmentChatBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.chat.adapters.ChatAdapter

class ChatFragment : Fragment() {

    private lateinit var binding : FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chatRecyclerView.adapter = ChatAdapter(requireContext())
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }

    }

}