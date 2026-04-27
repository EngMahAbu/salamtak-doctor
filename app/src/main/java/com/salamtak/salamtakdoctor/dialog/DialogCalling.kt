package com.salamtak.salamtakdoctor.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.R
import com.salamtak.databinding.DialogCallingBinding


class DialogCalling : BottomSheetDialogFragment() {

   private lateinit var binding:DialogCallingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DialogCallingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      binding.btnCalling.setOnClickListener {
          call()
      }
    }
    private fun call() {

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "17143", null))
        this.startActivity(intent)
    }

}