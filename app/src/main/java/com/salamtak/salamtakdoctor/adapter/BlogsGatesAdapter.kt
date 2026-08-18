package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salamtak.databinding.ItemLayoutBlogofGatesBinding
import com.salamtak.domain.entities.response.Blogs
import com.salamtak.salamtakdoctor.salamtakgates.GatesDetails


class BlogsGatesAdapter(val details:Action ):ListAdapter<Blogs,BlogsGatesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view= ItemLayoutBlogofGatesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
        Glide.with(holder.binding.ivDoctorProfile.context)
            .load(services.image)
            .into(holder.binding.ivDoctorProfile)

        holder.binding.root.setOnClickListener {
            val gatesDetails= GatesDetails(services.typeID,services.title,services.description,services.image,services.createdDate,services.links)
            details.onItemGatesClick(gatesDetails)
        }
    }
    class ViewHolder(itemBinding: ItemLayoutBlogofGatesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutBlogofGatesBinding = itemBinding
    }

    private class DiffCallback : DiffUtil.ItemCallback<Blogs>() {
        override fun areItemsTheSame(oldItem: Blogs, newItem: Blogs): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Blogs, newItem: Blogs): Boolean {
            return oldItem == newItem
        }
    }
    interface Action{
        fun onItemGatesClick(gatesDetails: GatesDetails)
    }



}