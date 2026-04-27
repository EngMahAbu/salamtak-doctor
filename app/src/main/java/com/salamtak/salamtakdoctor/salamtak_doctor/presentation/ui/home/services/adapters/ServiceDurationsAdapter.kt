package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.MedicalExaminationDurationData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener


class ServiceDurationsAdapter(val listener: RecyclerViewOnClickListener)
    : RecyclerView.Adapter<ServiceDurationsAdapter.ViewHolder>()
{

    var scheduleList = ArrayList<MedicalExaminationDurationData>()
    private var lastCheckedRB: RadioButton? = null
    private val TAG = ServiceDurationsAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_spinner,parent,false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scheduleList[position],position)
    }

    override fun getItemCount(): Int = scheduleList.size

    inner class ViewHolder(val binding: ItemSpinnerBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: MedicalExaminationDurationData, position: Int){

            Log.e(TAG, "bind: $item  ==> ${item.Duration}")
            binding.radioButton.text = item.Duration.toString()


            binding.radioButton.setOnClickListener {
                listener.onDurationSelect(item.Id,item.Duration.toString(),position)

                Log.e(TAG, "bind: ${item.Duration}" )
                if (lastCheckedRB != null) {
                    lastCheckedRB!!.isChecked = false
                }
                //store the clicked radiobutton
                lastCheckedRB = binding.radioButton

//                binding.radioButton.setTextColor(mContext.getColor(R.color.head_color))
            }


        }

    }

    fun submitData(list: ArrayList<MedicalExaminationDurationData>){
        Log.e(TAG, "submitData: ${list.size}" )
        this.scheduleList = list
        notifyDataSetChanged()
    }


}