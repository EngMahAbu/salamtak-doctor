package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.ItemClinicBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.squareup.picasso.Picasso

class ClinicsAdapter(
    val onClinicClickListener: ClinicsAdapter.OnClinicClickListener,
    val onCallClickListener: ClinicsAdapter.OnCallClickListener,
    val addDoctor:AddDoctors
) : RecyclerView.Adapter<ClinicsAdapter.ViewHolder>() {
    var clinicsList = ArrayList<ClinicData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_clinic, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clinicsList[position], position)



//        if (clinicsList[position].Area_PackageID.toString()=="null"){
//            holder.binding.ivPackge.visibility= View.GONE
//            holder.binding.tvP.visibility= View.GONE
//        }
//        else{
//            holder.binding.ivPackge.visibility= View.VISIBLE
//            holder.binding.tvP.visibility= View.VISIBLE
//        }

        if (DateUtils.getAdminOwnerId().isNullOrEmpty()){
            holder.binding.tvListofDoctors.visibility=View.GONE
        }else{
            holder.binding.tvListofDoctors.visibility=View.VISIBLE
        }
        holder.binding.tvAdddoctors.setOnClickListener {

            clinicsList[position].ClinicId?.let { it1 -> addDoctor.adddoctors(it1) }
        }

        holder.binding.tvListofDoctors.setOnClickListener {

            clinicsList[position].ClinicId?.let { it1 -> addDoctor.viewDoctors(it1) }
        }
        holder.binding.ivPackge.setOnClickListener {

             clinicsList[position].ClinicId?.let { it1 -> onCallClickListener.getClincId(it1) }
//            if (clinicsList[position].Area_PackageID.toString()=="null"){
//                Toast.makeText(holder.itemView.context,"There no packages to show", Toast.LENGTH_SHORT).show()
//            }else{
//                it.findNavController().navigate(R.id.otherPackagesFragment)
//            }

          //  clinicsList[position].ClinicId?.let { it1 -> onCallClickListener.getClincId(it1) }

        }

    }

    override fun getItemCount(): Int = clinicsList.size

    inner class ViewHolder(val binding: ItemClinicBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClinicData, position: Int) {


            if (Localization.getLanguageName()=="ar"){
                binding.clinicName.text = item.NameAr
            }else{
                binding.clinicName.text = item.Name
            }
            binding.clinicAppointments.text = item.Phone

            if(item.AreaName != null ){
                binding.clinicArea.text = "${item.Address}"
            }
            binding.btnEdit.setOnClickListener {
                onClinicClickListener.onClinicClicked(clinicsList[position])
            }

            binding.ivCall.setOnClickListener {
                onCallClickListener.onCallClicked(clinicsList[position])
            }

            binding.btnDecline.setOnClickListener{
                it.findNavController().navigate(R.id.dialogCalling)
            }

            if (item.Logo!!.isNotEmpty())
                Picasso.get().load("${item.Logo}")
                    .into(binding.clinicPhoto)


        }


    }

    fun submitData(list: ArrayList<ClinicData>) {
        this.clinicsList = list
        notifyDataSetChanged()
    }

    interface OnClinicClickListener {
        fun onClinicClicked(clinicData: ClinicData)
    }

    interface OnCallClickListener {
        fun onCallClicked(clinicData: ClinicData)
        fun getClincId(clincId:Int)
    }

    interface AddDoctors{
        fun adddoctors(clinicId:Int)
        fun viewDoctors(clinicId:Int)
    }



}