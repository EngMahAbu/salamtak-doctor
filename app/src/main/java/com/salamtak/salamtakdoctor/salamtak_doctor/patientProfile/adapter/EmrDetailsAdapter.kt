package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemAppointmentBinding
import com.salamtak.databinding.ItemEmrDetailsBinding
import com.salamtak.databinding.ItemEmrPatientBinding
import com.salamtak.domain.entities.response.*

class EmrDetailsAdapter(
    val list: List<NewPatientEmrdetail>
//    , val onEmrClickListener: EmrDetailsAdapter.OnEmrClickListener
) : RecyclerView.Adapter<EmrDetailsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEmrDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmrDetailsAdapter.ViewHolder {

        var binding =
            ItemEmrDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmrDetailsAdapter.ViewHolder(
            binding
        )

    }

    override fun onBindViewHolder(holder: EmrDetailsAdapter.ViewHolder, position: Int) {

        holder.binding.emrDetails = list[position]

//        holder.binding.ivCollapse.setOnClickListener {
//            onEmrClickListener.onEmrClicked(list[position])
////            holder.binding.emrDetailLayout.root.visibility = View.VISIBLE
//        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

//    interface OnEmrClickListener {
//        fun onEmrClicked(emrTitleData:EmrTitleData)
//    }


}