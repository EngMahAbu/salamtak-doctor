package com.salamtak.salamtakdoctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemImagesBinding
import java.io.File

class ImagesAdapter(private val imageOperation: ImagesOperation):
    ListAdapter<File, ImagesAdapter.ViewHolder>(
        DiffCallback()
    ) {

    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: ItemImagesBinding =
            DataBindingUtil.inflate(
                this.layoutInflater!!,
                R.layout.item_images, parent, false
            )
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.model = getItem(position)

        holder.binding.ivDeleteImage.setOnClickListener {
            imageOperation.deleteImage(position)
        }

    }

    class ViewHolder(itemBinding: ItemImagesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemImagesBinding = itemBinding
    }

    private class DiffCallback : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return true
        }
    }

    interface ImagesOperation{
        fun deleteImage(pos: Int)
    }
}