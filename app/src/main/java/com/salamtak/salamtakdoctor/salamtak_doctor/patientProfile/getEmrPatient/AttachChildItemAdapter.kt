package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.getEmrPatient

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.domain.entities.response.NewPatientEmrdocument
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class AttachChildItemAdapter (
    private var childItemList: List<NewPatientEmrdocument>,
    private val parentPosition: Int,
    private val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<AttachChildItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachChildItemHolder {
        return AttachChildItemHolder.from(parent,listener, parentPosition)
    }

    override fun onBindViewHolder(holder: AttachChildItemHolder, position: Int) {
        holder.bind(child = childItemList[position], position = position)
    }

    override fun getItemCount(): Int {
        return childItemList.size
    }

    fun setNewChildren(children: List<NewPatientEmrdocument>) {
        childItemList = children
        notifyDataSetChanged()
    }

}