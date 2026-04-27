package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.salamtak.databinding.ItemLayoutPolyclinicDoctorsBinding
import com.salamtak.domain.entities.response.data.DataInfo
import com.salamtak.domain.entities.response.offers.Offers
import com.salamtak.domain.entities.response.poly.ListOfDoctors
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper


class PollyClinicsAdapter(val iCreate:CreateSchedule):ListAdapter<ListOfDoctors,PollyClinicsAdapter.ViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutPolyclinicDoctorsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data=getItem(position)

        holder.binding.apply {
            if (DefaultLocaleHelper.getInstance(this.tvDoctorName.context).getCurrentLocale()=="en"){
                tvDoctorName.text = "${data.fullName}"
                tvSeniorityLevel.text = "${data.seniorityLevelName}"
                tvSpecialist.text = "${data.specialistName}"
            }else{
                tvDoctorName.text = "${data.fullNameAr}"
                tvSeniorityLevel.text = "${data.seniorityLevelName}"
                tvSpecialist.text = "${data.specialistName}"
            }
        }

        holder.binding.ivDoctorProfile.load(data.image) {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }

        holder.itemView.setOnClickListener {
            iCreate.createSchedule(data.id)
        }
        holder.binding.ivEdit.setOnClickListener {
            iCreate.editDoctorProfile(data.id)
        }



    }

    class ViewHolder(itemBinding: ItemLayoutPolyclinicDoctorsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutPolyclinicDoctorsBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<ListOfDoctors>() {
        override fun areItemsTheSame(oldItem: ListOfDoctors, newItem: ListOfDoctors): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: ListOfDoctors, newItem: ListOfDoctors): Boolean {
            return true
        }
    }


    interface CreateSchedule{
        fun createSchedule(doctorId:Int)
        fun editDoctorProfile(doctorId:Int)

    }


}