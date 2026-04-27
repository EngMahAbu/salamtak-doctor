package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.salamtak.R
import com.salamtak.databinding.DialogPickBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener

class PickerDialog(val listener: DialogsListener): DialogFragment()
{


    private lateinit var binding: DialogPickBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater, R.layout.dialog_pick,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvFile.setOnClickListener {

//            listener.onFilesClick(PDF_REQ_CODE,this)

        }

        binding.tvImage.setOnClickListener {

//            listener.onImagesClick(MEDIA_REQUEST_CODE,this)

        }

    }


}