package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.getEmrPatient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemEmrPatientBinding
import com.salamtak.domain.entities.response.EmrTitleData
import com.salamtak.domain.entities.response.NewPatientEmrdetail
import com.salamtak.domain.entities.response.NewPatientEmrdocument
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class EmrDetailsHolder(
     val binding: ItemEmrPatientBinding
    ,val listener: RecyclerViewOnClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(result: EmrTitleData, position: Int) {
        binding.emrTitle = result
        binding.positions = position
        initChildrenRV(result.PatientEmrdetails, position)
        initChildrenAttRV(result.PatientEmrdocuments, position)

    }

    private fun initChildrenRV(children: List<NewPatientEmrdetail>, position: Int) {
        val adapter = ChildItemAdapter(listOf(), position)
//        binding.emrRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
//        binding.emrRecyclerView.adapter = adapter

        adapter.setChildren(children)
    }

    private fun initChildrenAttRV(children: List<NewPatientEmrdocument>, position: Int) {
        val adapter = AttachChildItemAdapter(listOf(), position,listener)
//        binding.attatchmentRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
//        binding.attatchmentRecyclerView.adapter = adapter

        adapter.setNewChildren(children)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: RecyclerViewOnClickListener
        ): EmrDetailsHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemEmrPatientBinding.inflate(layoutInflater, parent, false)
            return EmrDetailsHolder(binding,listener)
        }
    }

}