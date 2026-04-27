package com.salamtak.salamtakdoctor.statistics

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemLayoutAllpackagesBinding
import com.salamtak.domain.entities.response.OtherPackagesData


class StatisticsAllPackgesAdapter() :
    ListAdapter<OtherPackagesData, StatisticsAllPackgesAdapter.ViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = ItemLayoutAllpackagesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packagesData = getItem(position)

        holder.binding.apply {
            if (packagesData.packageName == "Platinum" || packagesData.packageName=="Diamond") {
                PackageName.text=packagesData.packageName
                OneMonthFreeSponsoredAds.text = packagesData.oneMonthFreeSponsoredAds.toString()
                accessToSponsoredAds.text = packagesData.accessToSponsoredAds.toString()
                CashBack.text = packagesData.cashbackOnRepeatPatient.toString()

                if (packagesData.packagePrice == 0) {
                    Total.text = "unlimited"
                }else if (packagesData.packagePrice == -1){
                    Total.text = "no extra fess"
                }
                else {
                    Total.text = packagesData.packagePrice.toString()
                }

                if (packagesData.inclinicPatients == 0) {
                    InclinicPatients.text = "unlimited"
                }else if (packagesData.inclinicPatients == -1) {
                    InclinicPatients.text = "no extra fess"
                }
                else {
                    InclinicPatients.text = packagesData.inclinicPatients.toString()
                }
                if (packagesData.teleConsultations == 0) {
                    teleConsultations.text = "unlimited"
                }else if (packagesData.teleConsultations == -1){
                    teleConsultations.text = "no extra fess"
                }
                else {
                    teleConsultations.text = packagesData.teleConsultations.toString()
                }
                if (packagesData.extraTeleconsultationPrice == 0) {
                    ExtraTeleconsultationPrice.text = "unlimited"
                }else if (packagesData.extraTeleconsultationPrice == -1){
                    ExtraTeleconsultationPrice.text = "no extra fess"
                }
                else {
                    ExtraTeleconsultationPrice.text = packagesData.extraTeleconsultationPrice.toString()
                }
                if (packagesData.inclinicInsurancePetients == 0) {
                    insurance.text = "unlimited"
                }else if (packagesData.inclinicInsurancePetients == -1) {
                    insurance.text = "no extra fess"
                }
                else {
                    insurance.text = packagesData.inclinicInsurancePetients.toString()
                }
                if (packagesData.extraInclinicInsurancePrice == 0) {
                    ExtraInclinicInsurancePrice.text = "unlimited"
                }else if (packagesData.extraInclinicInsurancePrice == -1){
                    ExtraInclinicInsurancePrice.text = "no extra fess"
                }
                else {
                    ExtraInclinicInsurancePrice.text = packagesData.extraInclinicInsurancePrice.toString()
                }

            } else {
                PackageName.text = packagesData.packageName
                Total.text = packagesData.packagePrice.toString()
                InclinicPatients.text = packagesData.inclinicPatients.toString()
                ExtraInclinicPatientPrice.text = packagesData.extraInclinicPatientPrice.toString()
                teleConsultations.text = packagesData.teleConsultations.toString()
                ExtraTeleconsultationPrice.text = packagesData.extraTeleconsultationPrice.toString()
                insurance.text = packagesData.inclinicInsurancePetients.toString()
                ExtraInclinicInsurancePrice.text = packagesData.extraInclinicInsurancePrice.toString()
                OneMonthFreeSponsoredAds.text = packagesData.oneMonthFreeSponsoredAds.toString()
                accessToSponsoredAds.text = packagesData.accessToSponsoredAds.toString()
                CashBack.text = packagesData.cashbackOnRepeatPatient.toString()
            }
        }
    }


    class ViewHolder(itemBinding: ItemLayoutAllpackagesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutAllpackagesBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<OtherPackagesData>() {
        override fun areItemsTheSame(
            oldItem: OtherPackagesData,
            newItem: OtherPackagesData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: OtherPackagesData,
            newItem: OtherPackagesData
        ): Boolean {
            return true
        }
    }

    private fun color(): Int {
        val random = java.util.Random()
        val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
        return color
    }


    interface PackageAction {
        fun onItemClickPackage()
    }


}