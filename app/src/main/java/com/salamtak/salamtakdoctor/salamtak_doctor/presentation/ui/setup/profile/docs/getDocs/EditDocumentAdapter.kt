package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs.getDocs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemShowDocumentBinding
import com.salamtak.domain.entities.response.EditDocumentData

class EditDocumentAdapter (val list: List<EditDocumentData>) : RecyclerView.Adapter<EditDocumentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemShowDocumentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditDocumentAdapter.ViewHolder {

        var binding =
            ItemShowDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditDocumentAdapter.ViewHolder(
            binding
        )

    }

    override fun onBindViewHolder(holder: EditDocumentAdapter.ViewHolder, position: Int) {

        holder.binding.showDocument = list[position]

    }

    override fun getItemCount(): Int {
        return list.size
    }


}