package com.salamtak.salamtakdoctor.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.salamtak.R
import com.salamtak.databinding.ItemLayoutChooseclinksBinding
import com.salamtak.databinding.ItemLayoutPolyclinicDoctorsBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.domain.entities.response.data.DataInfo
import com.salamtak.domain.entities.response.offers.Offers
import com.salamtak.domain.entities.response.poly.ListOfDoctors
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper


class AppointemtsPollyClinicsAdapter(val iSelectDoctor:ISelectClinics):ListAdapter<ClinicData,AppointemtsPollyClinicsAdapter.ViewHolder>(DiffCallback()) {

    private var selectedItemPosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=
            ItemLayoutChooseclinksBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data=getItem(position)

        holder.binding.apply {
            if (DefaultLocaleHelper.getInstance(this.tvDoctorName.context).getCurrentLocale()=="en"){
                tvDoctorName.text = "${data.Name}"
                tvAddress.text = "${data.Address}"

            }else{
                tvDoctorName.text = "${data.NameAr}"
                tvAddress.text = "${data.Address}"
            }

        }

        holder.binding.ivDoctorProfile.load(data.Logo) {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }


        holder.itemView.setOnClickListener {
            selectedItemPosition = holder.bindingAdapterPosition
            iSelectDoctor.onClinicsSelected(data)
            notifyDataSetChanged()
        }
        if (selectedItemPosition == position) {
            holder.binding.cardview.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_chooselang_button)
            holder.binding.tvDoctorName.setTextColor(Color.WHITE)
            holder.binding.tvAddress.setTextColor(Color.WHITE)
            holder.binding.radio.isChecked=true
        } else {
            holder.binding.cardview.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_helpgray)
            holder.binding.tvDoctorName.setTextColor(Color.BLACK)
            holder.binding.tvAddress.setTextColor(Color.BLACK)
            holder.binding.radio.isChecked=false
        }


    }

    class ViewHolder(itemBinding: ItemLayoutChooseclinksBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutChooseclinksBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<ClinicData>() {
        override fun areItemsTheSame(oldItem: ClinicData, newItem: ClinicData): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: ClinicData, newItem: ClinicData): Boolean {
            return true
        }
    }


    interface ISelectClinics{
        fun onClinicsSelected(listOfDoctors:ClinicData)

    }


}