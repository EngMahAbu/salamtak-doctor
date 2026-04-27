package com.salamtak.salamtakdoctor.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salamtak.databinding.DocumentsLayoutBinding
import com.salamtak.domain.entities.response.newdocuents.LegalDataDocuments


class LegalDocumentsAdapter(val action: Action ):ListAdapter<LegalDataDocuments,LegalDocumentsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=DocumentsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
        holder.binding.descLabel.text=services.Doc_Desc
        holder.binding.itemLabel.text=services.LegalDocumentTypeName
        holder. binding.backView.tvPhoto.text=services.LegalDocumentTypeName
        holder. binding.frontView.tvPhoto.text=services.LegalDocumentTypeName


       if (services.LegalDocumentTypeId==1){
           if (services.URLs.isNotEmpty()) {
               if (services.URLs.size>=2) {
                   if (services.URLs[0].DocumentOrder == 1) {
                       Glide.with(holder.binding.frontView.ivAddDoc.context)
                           .load(services.URLs[0].URL)
                           .into(holder.binding.frontView.ivAddPhoto)
                       holder. binding.frontView.editFrontIv.setOnClickListener {
                           action.onItemClickUpdateDocuments(services.URLs[0].id)
                       }
                   }
                   if (services.URLs[1].DocumentOrder == 2) {
                       Glide.with(holder.binding.frontView.ivAddDoc.context)
                           .load(services.URLs[1].URL)
                           .into(holder.binding.backView.ivAddPhoto)

                       holder. binding.backView.editFrontIv.setOnClickListener {
                           action.onItemClickUpdateDocuments(services.URLs[1].id)
                       }
                   }
                   if (services.URLs[0].DocumentOrder == 2) {
                       Glide.with(holder.binding.frontView.ivAddDoc.context)
                           .load(services.URLs[0].URL)
                           .into(holder.binding.backView.ivAddPhoto)

                       holder. binding.backView.editFrontIv.setOnClickListener {
                           action.onItemClickUpdateDocuments(services.URLs[0].id)
                       }
                   }
                   if (services.URLs[1].DocumentOrder == 1) {
                       Glide.with(holder.binding.frontView.ivAddDoc.context)
                           .load(services.URLs[1].URL)
                           .into(holder.binding.frontView.ivAddPhoto)
                       holder. binding.frontView.editFrontIv.setOnClickListener {
                           action.onItemClickUpdateDocuments(services.URLs[1].id)
                       }
                   }
                   holder.binding.frontView.editFrontIv.visibility=View.VISIBLE
                   holder.binding.backView.editFrontIv.visibility=View.VISIBLE
                   holder.binding.frontView.ivAddDoc.visibility=View.GONE
                   holder.binding.backView.ivAddDoc.visibility=View.GONE
               }else{
                   if (services.URLs[0].DocumentOrder == 1) {
                       Glide.with(holder.binding.frontView.ivAddDoc.context)
                           .load(services.URLs[0].URL)
                           .into(holder.binding.frontView.ivAddPhoto)
                       holder. binding.frontView.editFrontIv.setOnClickListener {
                           action.onItemClickUpdateDocuments(services.URLs[0].id)
                       }
                       holder.binding.frontView.editFrontIv.visibility=View.VISIBLE
                       holder.binding.frontView.ivAddDoc.visibility=View.GONE
                   }else{
                       Glide.with(holder.binding.frontView.ivAddDoc.context)
                           .load(services.URLs[0].URL)
                           .into(holder.binding.backView.ivAddPhoto)
                       holder. binding.backView.editFrontIv.setOnClickListener {
                           action.onItemClickUpdateDocuments(services.URLs[0].id)
                       }
                       holder.binding.backView.editFrontIv.visibility=View.VISIBLE
                       holder.binding.backView.ivAddDoc.visibility=View.GONE
                   }
               }
           }
       }

        when(position){
            1->{
                if (services.LegalDocumentTypeId==2){
                    if (services.URLs.isNotEmpty()) {
                        if (services.URLs[0].DocumentOrder==1) {
                            Glide.with(holder.binding.frontView.ivAddDoc.context)
                                .load(services.URLs[0].URL)
                                .into(holder.binding.frontView.ivAddPhoto)
                            holder. binding.frontView.editFrontIv.setOnClickListener {
                                action.onItemClickUpdateDocuments(services.URLs[0].id)
                            }
                            holder.binding.frontView.editFrontIv.visibility=View.VISIBLE
                            holder.binding.backView.editFrontIv.visibility=View.GONE
                            holder.binding.frontView.ivAddDoc.visibility=View.GONE
                            holder.binding.backView.ivAddDoc.visibility=View.GONE
                         //  holder.binding.backView.ivAddPhoto.visibility= View.VISIBLE
                        }
                    }
                }
            }
            2->{
                if (services.LegalDocumentTypeId==3){
                    holder.binding.frontView.ivAddPhoto.visibility= View.VISIBLE
                    holder.binding.backView.ivAddPhoto.visibility= View.VISIBLE
                    if (services.URLs.isNotEmpty()) {
                        if (services.URLs.size>=2) {
                            if (services.URLs[0].DocumentOrder == 1) {
                                Glide.with(holder.binding.frontView.ivAddDoc.context)
                                    .load(services.URLs[0].URL)
                                    .into(holder.binding.frontView.ivAddPhoto)
                                holder. binding.frontView.editFrontIv.setOnClickListener {
                                    action.onItemClickUpdateDocuments(services.URLs[0].id)
                                }
                            }
                            if (services.URLs[1].DocumentOrder == 2) {
                                Glide.with(holder.binding.frontView.ivAddDoc.context)
                                    .load(services.URLs[1].URL)
                                    .into(holder.binding.backView.ivAddPhoto)
                                holder. binding.backView.editFrontIv.setOnClickListener {
                                    action.onItemClickUpdateDocuments(services.URLs[1].id)
                                }
                            }
                            if (services.URLs[0].DocumentOrder == 2) {
                                Glide.with(holder.binding.frontView.ivAddDoc.context)
                                    .load(services.URLs[0].URL)
                                    .into(holder.binding.backView.ivAddPhoto)
                                holder. binding.backView.editFrontIv.setOnClickListener {
                                    action.onItemClickUpdateDocuments(services.URLs[0].id)
                                }
                            }
                            if (services.URLs[1].DocumentOrder == 1) {
                                Glide.with(holder.binding.frontView.ivAddDoc.context)
                                    .load(services.URLs[1].URL)
                                    .into(holder.binding.frontView.ivAddPhoto)
                                holder. binding.frontView.editFrontIv.setOnClickListener {
                                    action.onItemClickUpdateDocuments(services.URLs[0].id)
                                }
                            }
                            holder.binding.frontView.editFrontIv.visibility=View.VISIBLE
                            holder.binding.backView.editFrontIv.visibility=View.VISIBLE
                            holder.binding.frontView.ivAddDoc.visibility=View.GONE
                            holder.binding.backView.ivAddDoc.visibility=View.GONE
                        }else{
                            if (services.URLs[0].DocumentOrder == 1) {
                                Glide.with(holder.binding.frontView.ivAddDoc.context)
                                    .load(services.URLs[0].URL)
                                    .into(holder.binding.frontView.ivAddPhoto)
                                holder. binding.frontView.editFrontIv.setOnClickListener {
                                    action.onItemClickUpdateDocuments(services.URLs[0].id)
                                }
                                holder.binding.frontView.editFrontIv.visibility=View.VISIBLE
                                holder.binding.frontView.ivAddDoc.visibility=View.GONE
                            }else{
                                Glide.with(holder.binding.frontView.ivAddDoc.context)
                                    .load(services.URLs[0].URL)
                                    .into(holder.binding.backView.ivAddPhoto)
                                holder.binding.backView.editFrontIv.setOnClickListener {
                                    action.onItemClickUpdateDocuments(services.URLs[0].id)
                                }
                                holder.binding.backView.editFrontIv.visibility=View.VISIBLE
                                holder.binding.backView.ivAddDoc.visibility=View.GONE
                            }
                        }
                    }
                }
            }
        }

        holder.binding.frontView.ivAddDoc.setOnClickListener {
           action.onItemClick(1,services.LegalDocumentTypeId)
        }
        holder.binding.backView.ivAddDoc.setOnClickListener {
            action.onItemClick(2,services.LegalDocumentTypeId)
        }

        if (services.LegalDocumentTypeId==2){

         val  layoutParams=  holder.binding.linearLayout4.layoutParams
            layoutParams.height = 200.toPx(holder.binding.backView.editFrontIv.context)
//            holder.binding.backView.rootView.visibility=View.GONE
//            holder.binding.backView.editFrontIv.visibility=View.GONE
        }
    }


    class ViewHolder(itemBinding: DocumentsLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: DocumentsLayoutBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<LegalDataDocuments>() {
        override fun areItemsTheSame(oldItem: LegalDataDocuments, newItem: LegalDataDocuments): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: LegalDataDocuments, newItem: LegalDataDocuments): Boolean {
            return true
        }
    }



    interface Action{
        fun onItemClick(documentOrder: Int,legalDocumentTypeId:Int)
        fun onItemClickUpdateDocuments(id: Int)
    }

    fun Int.toPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

}