package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemHomeClinicBinding
import com.salamtak.domain.entities.response.ClinicData

class ClinicHomeAdapter(val clinicsList : List<ClinicData>,
    val onClinicClickListener: ClinicHomeAdapter.OnClinicClickListener
    ) : RecyclerView.Adapter<ClinicHomeAdapter.ViewHolder>() {

    private var lastCheckedRB: RadioButton? = null
    var selectedPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_home_clinic, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clinicsList[position], position)
       holder.binding.ivDoctorProfile.load("https://salamtechapi.azurewebsites.net/${clinicsList[position].Logo}") {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }
        with(holder){
            if(position == selectedPosition){
                binding.tvDoctorName.setTextColor(Color.parseColor("#FFFFFF"))
               // binding.layoutDoctorsDetail.background.setColorFilter(Color.parseColor("#2B2979"), PorterDuff.Mode.SRC_ATOP)
                binding.radio.isSelected = !binding.radio.isSelected
                holder.binding.cardview.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_chooselang_button)
                holder.binding.radio.isChecked=true
                holder.binding.tvDoctorName.setTextColor(Color.WHITE)
                holder.binding.tvAddress.setTextColor(Color.WHITE)

            }else{
                binding.tvDoctorName.setTextColor(Color.parseColor("#000000"))
            //    binding.layoutDoctorsDetail.background.setColorFilter(Color.parseColor("#BFBABA"), PorterDuff.Mode.SRC_ATOP)
                holder.binding.cardview.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_helpgray)
                holder.binding.radio.isChecked=false
                holder.binding.tvDoctorName.setTextColor(Color.BLACK)
                holder.binding.tvAddress.setTextColor(Color.BLACK)

            }
        }

    }

    override fun getItemCount(): Int = clinicsList.size

    inner class ViewHolder(val binding: ItemHomeClinicBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClinicData, position: Int) {

            if (Localization.getLanguageName()=="ar"){
                binding.tvDoctorName.text = item.NameAr
                binding.tvAddress.text = item.Address
            }else{
                binding.tvDoctorName.text = item.Name
                binding.tvAddress.text = item.Address

            }



            binding.cardview.setOnClickListener {
                selectedPosition = position
               onClinicClickListener.onClinicClicked(clinicsList[position])
//                binding.clinicNameTv.setTextColor(Color.parseColor("#FFFFFF"))
//                binding.llClinics.background.setColorFilter(Color.parseColor("#2B2979"), PorterDuff.Mode.SRC_ATOP)
//                binding.clinicNameTv.isSelected = !binding.clinicNameTv.isSelected
//                binding.clinicNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.click_background, 0)

                notifyDataSetChanged()


                if (lastCheckedRB != null) {
                    lastCheckedRB!!.isChecked = false


                }

//                else{
//                    binding.clinicNameTv.setTextColor(Color.parseColor("#2B2979"))
//                    binding.llClinics.background.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP)
//                    binding.clinicNameTv.isSelected = !binding.clinicNameTv.isSelected
//                    binding.clinicNameTv.isChecked = false
//                    binding.clinicNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
//
//                }
                //store the clicked radiobutton
                lastCheckedRB = binding.radio

//                if (!binding.clinicNameTv.isSelected){
//                    binding.clinicNameTv.setTextColor(Color.parseColor("#000000"))
//                    binding.llClinics.background.setColorFilter(Color.parseColor("#E9EAEF"), PorterDuff.Mode.SRC_ATOP)
//                    binding.clinicNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
//                }

            }

            binding.position = position

//            binding.clinicNameTv.setOnClickListener {
//
////                listener.onTimeItemClicked(position, binding.position!!)
//
//                if (lastCheckedRB != null) {
//                    lastCheckedRB!!.isChecked = false
//                }
//                //store the clicked radiobutton
//                lastCheckedRB = binding.clinicNameTv
//
//            }

            binding.executePendingBindings()
        }

    }

    interface OnClinicClickListener {
        fun onClinicClicked(clinicData: ClinicData )
    }

}