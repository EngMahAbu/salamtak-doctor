package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.R
import com.salamtak.databinding.BottomSheetSpinnerBinding
import com.salamtak.databinding.ItemSpinnerBinding

/** One row of [SearchablePickerBottomSheet]; a null [id] is the "All …" option. */
data class PickerItem(val id: Int?, val name: String)

/**
 * Single-choice bottom sheet: title, search field, radio list whose first row is "All …", Done.
 * [onDone] receives the chosen row (the "All …" row when nothing else is picked).
 */
class SearchablePickerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSpinnerBinding
    private var title: String = ""
    private var items: List<PickerItem> = emptyList()
    private var selectedId: Int? = null
    private var onDone: ((PickerItem) -> Unit)? = null
    private val adapter = PickerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Items and callback are not restorable; close a sheet recreated by the system.
        if (onDone == null) dismissAllowingStateLoss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_spinner, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val wlp = window.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
        }
        BottomSheetBehavior.from(binding.bottomSheetSpinner).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            isHideable = false
            isDraggable = false
        }

        binding.headerTitleTxt.text = title
        binding.choicesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.choicesRv.adapter = adapter
        adapter.filter("")

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter(newText)
                return false
            }
        })

        binding.submitBtn.setOnClickListener {
            val chosen = items.firstOrNull { it.id == selectedId } ?: items.firstOrNull()
            chosen?.let { onDone?.invoke(it) }
            dismiss()
        }
    }

    private inner class PickerAdapter : RecyclerView.Adapter<PickerAdapter.ViewHolder>() {

        private var filtered: List<PickerItem> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun filter(query: String) {
            filtered = if (query.isBlank()) items
            else items.filter { it.id == null || it.name.contains(query, ignoreCase = true) }
            binding.noCountryFound.visibility =
                if (filtered.none { it.id != null } && items.size > 1) View.VISIBLE else View.GONE
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_spinner, parent, false
            )
        )

        override fun getItemCount(): Int = filtered.size

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = filtered[position]
            holder.binding.radioButton.visibility = View.VISIBLE
            holder.binding.radioButton.text = item.name
            holder.binding.radioButton.isChecked = item.id == selectedId
            holder.binding.radioButton.setOnClickListener {
                selectedId = item.id
                notifyDataSetChanged()
            }
        }

        inner class ViewHolder(val binding: ItemSpinnerBinding) : RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        private const val TAG = "SearchablePickerBottomSheet"

        /**
         * @param allLabel text of the first row ("All Clinics", "All Doctors"…), whose id is null.
         */
        fun show(
            fragmentManager: FragmentManager,
            title: String,
            allLabel: String,
            items: List<PickerItem>,
            selectedId: Int?,
            onDone: (PickerItem) -> Unit
        ) {
            SearchablePickerBottomSheet().apply {
                this.title = title
                this.items = listOf(PickerItem(null, allLabel)) + items
                this.selectedId = selectedId
                this.onDone = onDone
            }.show(fragmentManager, TAG)
        }
    }
}
