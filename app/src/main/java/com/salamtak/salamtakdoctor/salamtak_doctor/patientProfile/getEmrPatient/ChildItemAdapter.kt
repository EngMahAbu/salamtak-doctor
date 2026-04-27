package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.getEmrPatient

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.domain.entities.response.NewPatientEmrdetail

class ChildItemAdapter (
    private var childItemList: List<NewPatientEmrdetail>,
    private val parentPosition: Int
//    ,private val listener: OnChildItemClickListener
) :
    RecyclerView.Adapter<ChildItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildItemHolder {
        return ChildItemHolder.from(parent, parentPosition)
    }

    override fun onBindViewHolder(holder: ChildItemHolder, position: Int) {
        holder.bind(child = childItemList[position], position = position)
    }

    override fun getItemCount(): Int {
        return childItemList.size
    }

    fun setChildren(children: List<NewPatientEmrdetail>) {
        childItemList = children
        notifyDataSetChanged()
    }

}
