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
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemLayoutChooseclinksBinding
import com.salamtak.domain.entities.response.offers.ClinicData


class ChooseClinksDoctorsAdapter(private val book: Booking) :
    ListAdapter<ClinicData, ChooseClinksDoctorsAdapter.ViewHolder>(DiffCallback()) {
    private var selectedItemPosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            ItemLayoutChooseclinksBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services = getItem(position)
        var clinicName=""
        if (Localization.getLanguageName()=="ar"){
            holder.binding.tvDoctorName.text = services.nameAr
            clinicName=services.nameAr
        }else{
            holder.binding.tvDoctorName.text = services.name
            clinicName=services.name
        }

        holder.binding.tvAddress.text = " ${services.address}"

        holder.binding.ivDoctorProfile.load(services.logo) {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }

        holder.itemView.setOnClickListener {
            selectedItemPosition = holder.bindingAdapterPosition
            book.onItemClick(services.clinicId,clinicName,services.healthEntityPhoneDtos)
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
            return oldItem.clinicId == newItem.clinicId
        }

        override fun areContentsTheSame(oldItem: ClinicData, newItem: ClinicData): Boolean {
            return true
        }
    }

    interface Booking {
        fun onItemClick(clinkid: Int,clinkname:String,list: List<String>)
    }
}