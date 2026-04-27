package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.ItemClinicBinding
import com.salamtak.databinding.ItemHomeClinicBinding
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.SubSpecialitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.squareup.picasso.Picasso

class ClinicFilterAdapter(
    val listener: RecyclerViewOnClickListener
) : RecyclerView.Adapter<ClinicFilterAdapter.ViewHolder>() {

    var clinicsList = ArrayList<ClinicData>()
    private val TAG = SubSpecialitiesAdapter::class.java.simpleName
//    private val texts = HashMap<String, Boolean>()
//    private val idsList = HashMap<String, Boolean>()
    private var lastCheckedRB: RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_home_clinic, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clinicsList[position], position)
    }

    override fun getItemCount(): Int = clinicsList.size

    inner class ViewHolder(val binding: ItemHomeClinicBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClinicData, position: Int) {

            binding.tvDoctorName.text = item.Name
//            binding.clinicNameTv.text = item.Address
//            binding.checkbox.visibility = View.VISIBLE
//            binding.radioButton.visibility = View.GONE
//
//            binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
//
//                texts[item.Name] = isChecked
//                idsList[item.ClinicId.toString()] = isChecked
//                listener.onSubSpecialitySelect(item.ClinicId,item.Name,position)
//            }

            binding.radio.setOnClickListener {
                item.ClinicId?.let { it1 -> item.Name?.let { it2 ->
                    listener.onClinicSelect(it1,
                        it2,position)
                } }
                if (lastCheckedRB != null) {
                    lastCheckedRB!!.isChecked = false
                }
                //store the clicked radiobutton
                lastCheckedRB = binding.radio
            }

            binding.executePendingBindings()

        }
    }

    fun submitData(list: ArrayList<ClinicData>) {
        this.clinicsList = list
        notifyDataSetChanged()
    }

//    fun getAdditionIds(): ArrayList<String>{
//
//        val addedTexts = ArrayList<String>()
//
//        this.idsList.forEach{ i ->
//            if (i.value && i.key.isNotEmpty()) addedTexts.add(i.key)
//        }
//
//        return addedTexts
//
//    }
//
//    fun getAdditionTexts(): String{
//
//        var addedTexts = ""
//
//        this.texts.forEach{ i ->
//            if (i.value && i.key.isNotEmpty()) addedTexts = i.key + "," + addedTexts
//        }
//
//        return addedTexts
//
//    }


}