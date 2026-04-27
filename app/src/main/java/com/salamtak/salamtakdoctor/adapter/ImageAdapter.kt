package com.salamtak.salamtakdoctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salamtak.R
import com.salamtak.databinding.ItemImageBinding
import com.salamtak.domain.entities.response.offers.AllGAllary

class ImageAdapter(private val imageOperation: ImagesOperation):
    ListAdapter<AllGAllary, ImageAdapter.ViewHolder>(
        DiffCallback()
    ) {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: ItemImageBinding =
            DataBindingUtil.inflate(
                this.layoutInflater!!,
                R.layout.item_image, parent, false
            )
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        Glide.with(holder.binding.ivPhoto).load(data.imageUrl)
            .into(holder.binding.ivPhoto)
        holder.binding.ivDeleteImage.setOnClickListener {
            imageOperation.deleteImageFromGallery(position,data.id)
        }

    }

    class ViewHolder(itemBinding: ItemImageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemImageBinding = itemBinding
    }

    private class DiffCallback : DiffUtil.ItemCallback<AllGAllary>() {
        override fun areItemsTheSame(oldItem: AllGAllary, newItem: AllGAllary): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AllGAllary, newItem: AllGAllary): Boolean {
            return true
        }
    }

    interface ImagesOperation{
        fun deleteImageFromGallery(pos: Int,imageId:Int)
    }
}