package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salamtak.databinding.ItemLayoutGatesBinding
import com.salamtak.domain.entities.response.Gates


class GatesAdapter(val action: Action ):ListAdapter<Gates,GatesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutGatesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
        Glide.with(holder.binding.ivServices.context)
            .load(services.imageURL)
            .into(holder.binding.ivServices)


        holder.binding.cardview.setOnClickListener {
           action.onItemGatesClick(services.id)
        }

    }


    class ViewHolder(itemBinding: ItemLayoutGatesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutGatesBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<Gates>() {
        override fun areItemsTheSame(oldItem: Gates, newItem: Gates): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Gates, newItem: Gates): Boolean {
            return true
        }
    }

    interface Action{
        fun onItemGatesClick(gateId: Int)
    }



}