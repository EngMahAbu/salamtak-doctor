package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemHospitalBranchBinding
import com.salamtak.domain.entities.response.hospital.HospitalBranch

/** Branch cards on the Hospital Info tab: name, address line and a "Main" chip. */
class HospitalBranchesAdapter(
    private val onClick: (HospitalBranch) -> Unit
) : RecyclerView.Adapter<HospitalBranchesAdapter.ViewHolder>() {

    private var branches: List<HospitalBranch> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submit(list: List<HospitalBranch>) {
        branches = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHospitalBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = branches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val branch = branches[position]
        val arabic = Localization.getLanguageName() == "ar"
        holder.binding.tvBranchName.text =
            (if (arabic) branch.NameAr else branch.Name)?.takeIf { it.isNotBlank() } ?: branch.Name.orEmpty()
        holder.binding.tvBranchAddress.text = branch.Address.orEmpty()
        holder.binding.tvBranchMain.visibility = if (branch.IsMain == true) View.VISIBLE else View.GONE
        holder.binding.root.setOnClickListener { onClick(branch) }
    }

    class ViewHolder(val binding: ItemHospitalBranchBinding) : RecyclerView.ViewHolder(binding.root)
}
