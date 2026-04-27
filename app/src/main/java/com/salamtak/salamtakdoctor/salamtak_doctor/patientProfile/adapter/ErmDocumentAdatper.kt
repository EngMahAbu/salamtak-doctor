package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemAttachmentEmrBinding
import com.salamtak.domain.entities.response.NewPatientEmrdocument

class ErmDocumentAdatper(
    private var childItemList: List<NewPatientEmrdocument>,
//    private var childItemList: ArrayList<String>,
) :
    RecyclerView.Adapter<DocumentChildHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentChildHolder {
        return DocumentChildHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DocumentChildHolder, position: Int) {
        holder.bind(child = childItemList[position], position = position)
    }

    override fun getItemCount(): Int {
        return childItemList.size
    }

    fun setNewChildren(children: List<NewPatientEmrdocument>) {
//    fun setNewChildren(children: ArrayList<String>) {
        childItemList = children
        notifyDataSetChanged()
    }
}

class DocumentChildHolder (private val binding: ItemAttachmentEmrBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(child: NewPatientEmrdocument, position: Int) {
//    fun bind(child: String, position: Int) {
        binding.emrDetails = child
    }



    companion object {
        fun from(
            parent: ViewGroup,
        ): DocumentChildHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemAttachmentEmrBinding.inflate(layoutInflater, parent, false)
            return DocumentChildHolder(binding)
        }
    }
}