package com.salamtak.salamtakdoctor.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemLayoutSubcategoryBinding
import com.salamtak.domain.entities.response.offers.SubcategoryData


class SubCategoryAdapter(private val onServicesClick:Action ):ListAdapter<SubcategoryData,SubCategoryAdapter.ViewHolder>(DiffCallback()) {
    private var selectedItemPosition: Int = -1
    private var name=""
    constructor(name:String,onServicesClick:Action):this(onServicesClick){
        this.name=name
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutSubcategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)

        holder.binding.tvServiceName.text=services.name
        holder.binding.root.setOnClickListener {
            name=""
            selectedItemPosition = holder.bindingAdapterPosition
            onServicesClick.onItemClickSubCategoryId(services.id)
            notifyDataSetChanged()
        }
        if (selectedItemPosition == position) {
            holder.binding.layputServices.background =
                ContextCompat.getDrawable(holder.itemView.context,R.drawable.bg_help)
            holder.binding.tvServiceName.setTextColor(Color.BLACK)
        } else {
            holder.binding.layputServices.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_category_white)
            holder.binding.tvServiceName.setTextColor(Color.BLACK)
        }

        if (name==services.name){
            holder.binding.layputServices.background =
                ContextCompat.getDrawable(holder.itemView.context,R.drawable.bg_help)
            holder.binding.tvServiceName.setTextColor(Color.BLACK)
        }

    }


    class ViewHolder(itemBinding: ItemLayoutSubcategoryBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutSubcategoryBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<SubcategoryData>() {
        override fun areItemsTheSame(oldItem: SubcategoryData, newItem: SubcategoryData): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: SubcategoryData, newItem: SubcategoryData): Boolean {
            return true
        }
    }

    interface Action{
        fun onItemClickSubCategoryId(subCategoryId: Int)
    }



}