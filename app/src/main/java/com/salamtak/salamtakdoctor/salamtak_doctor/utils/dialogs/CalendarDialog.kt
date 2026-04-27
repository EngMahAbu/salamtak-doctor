package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.R
import com.salamtak.databinding.DialogChooseDateBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.doBirthDateValidate
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.getDateFromDatePicker
import java.text.SimpleDateFormat
import java.util.*

class CalendarDialog(private val dateBinding: TextView,
                     private val errorText: TextView,val listener: DialogsListener):BottomSheetDialogFragment()
{

    private lateinit var binding: DialogChooseDateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater, R.layout.dialog_choose_date,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setUi()


    }

    private fun setUi(){

        binding.datePicker.maxDate = System.currentTimeMillis() - 1000;



        binding.dialogOk.setOnClickListener {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val sdf2 = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

            if (doBirthDateValidate(sdf.format(getDateFromDatePicker(binding.datePicker)))){

                dateBinding.setText(sdf2.format(getDateFromDatePicker(binding.datePicker)))
                errorText.visibility = GONE
                listener.onPickDate(sdf.format(getDateFromDatePicker(binding.datePicker)))
            }
            else{
                errorText.visibility = VISIBLE
                errorText.text = getString(R.string.birth_required2)
            }
            dismiss()

        }

//        binding.closeBtn.setOnClickListener {
//            dismiss()
//        }

    }

}