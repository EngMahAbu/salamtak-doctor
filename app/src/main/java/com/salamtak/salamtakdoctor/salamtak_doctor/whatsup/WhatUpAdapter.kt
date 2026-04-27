package com.salamtak.salamtakdoctor.salamtak_doctor.whatsup

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemWhatUpBinding
import com.salamtak.domain.entities.response.AdsData

class WhatUpAdapter(
    val adsList: ArrayList<AdsData>,
    var context: Context
) : RecyclerView.Adapter<WhatUpAdapter.ViewHolder>() {

    val list = ArrayList<String>()
    private lateinit var imageList: ArrayList<Int>

    class ViewHolder(val binding: ItemWhatUpBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhatUpAdapter.ViewHolder {

        var binding =
            ItemWhatUpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WhatUpAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WhatUpAdapter.ViewHolder, position: Int) {

//        holder.binding.whatsUp = adsList[position]

        holder.binding.titleTv.text = adsList[position].CompName
        holder.binding.descWhatsUpTv.text = adsList[position].Discription
        holder.binding.viewPager.adapter = WhatsUpSliderAdapter(context, adsList)
        holder.binding.tabLayout.setupWithViewPager(holder.binding.viewPager)
//        if (adsList[position].IsVideo){
//            holder.binding.viewPager.adapter = SliderAdapter(context,  homeAdsData)
//        }

//        holder.binding.descWhatsUpTv.setText(adsList[position].)

//        holder.binding.viewPager.visibility =
//        adsList.forEach { ddModel ->
//            list.add("https://salamtechapi.azurewebsites.net"+ddModel.ImageURL)
//        }
        list.clear()
        list.add("https://salamtechapi.azurewebsites.net/Images/Doctor/Profile/c252cdbe-b094-44f2-9b15-0a5296a07e34.jpg")
        list.add("https://salamtechapi.azurewebsites.net/Images/MedicalType/Home.png")
        Log.d(ContentValues.TAG, "AdsImagesA: " + list)

//        holder.binding.viewPager.adapter =
//            list?.let { SliderAdapter((context as FragmentActivity), it as ArrayList<HomeAdsData>) }

        imageList = ArrayList(2)
        imageList.add(R.drawable.app_logo)
        imageList.add(R.drawable.ic_new_logo)

    }

    override fun getItemCount(): Int {
        return adsList.size
    }

}