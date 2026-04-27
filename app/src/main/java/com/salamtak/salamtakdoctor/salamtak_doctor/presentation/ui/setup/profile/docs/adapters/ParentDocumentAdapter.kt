package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.DocumentsLayoutBinding
import com.salamtak.databinding.ItemShowDocumentBinding
import com.salamtak.domain.entities.response.EditDocumentData
import com.salamtak.domain.entities.response.LegalDocumentsData
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs.LegalDocumentItemsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class ParentDocumentAdapter (val listener: RecyclerViewOnClickListener): RecyclerView.Adapter<ParentDocumentAdapter.ViewHolder>()
{

    var legalDocumentItemsList = ArrayList<LegalDocumentsData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.documents_layout,parent,false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(legalDocumentItemsList[position],position)
    }

    override fun getItemCount(): Int = legalDocumentItemsList.size


    inner class ViewHolder(val binding: DocumentsLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item : LegalDocumentsData, position: Int)
        {
//            binding.itemLabel.text = item.Name

//            binding.ivAddDoc.setOnClickListener {
//                listener.onImagePickAction(binding,position,item)
//            }
//
//            binding.ivDelete.setOnClickListener {
////                listener.onImageDelete(position,binding)
//            }

        }
    }

    fun submitData(d: ArrayList<LegalDocumentsData>){

        this.legalDocumentItemsList = d
        notifyDataSetChanged()

    }

}