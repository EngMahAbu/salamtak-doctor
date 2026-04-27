package com.salamtak.salamtakdoctor.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.salamtak.R
import com.salamtak.databinding.ItemLayoutMedicalrvservicesBinding
import com.salamtak.domain.entities.response.offers.Category


class CategoryAdapter(private val onServicesClick:Action,var name:String):ListAdapter<Category,CategoryAdapter.ViewHolder>(DiffCallback()) {
    private var selectedItemPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutMedicalrvservicesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
        Glide.with(holder.binding.ivServices)
            .load(services.image)
            .transform( GranularRoundedCorners(25F, 25F, 0F, 0F))
            .into(holder.binding.ivServices)


        holder.binding.tvServiceName.text=services.name
        holder.binding.cardview.setOnClickListener {
            name=""
            selectedItemPosition = holder.bindingAdapterPosition
            onServicesClick.onItemClick(services.id)
            notifyDataSetChanged()
        }
        if (selectedItemPosition == position) {
            holder.binding.cardview.background =
                ContextCompat.getDrawable(holder.itemView.context,R.drawable.bg_help)
            holder.binding.tvServiceName.setTextColor(Color.BLACK)
        } else {
            holder.binding.cardview.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_category_white)
            holder.binding.tvServiceName.setTextColor(Color.BLACK)
        }

        if (name == services.name){
            holder.binding.cardview.background =
                ContextCompat.getDrawable(holder.itemView.context,R.drawable.bg_help)
            holder.binding.tvServiceName.setTextColor(Color.BLACK)
        }

    }


    class ViewHolder(itemBinding: ItemLayoutMedicalrvservicesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutMedicalrvservicesBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return true
        }
    }

    interface Action{
        fun onItemClick(servicesId: Int)
    }



}