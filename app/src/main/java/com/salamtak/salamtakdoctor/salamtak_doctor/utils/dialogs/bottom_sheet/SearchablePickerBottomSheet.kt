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
 * Bottom sheet with a title, search field, list and Done.
 * - [show]: single choice (radio), optionally with an "All …" first row whose id is null.
 * - [showMulti]: several choices (checkboxes).
 */
class SearchablePickerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSpinnerBinding
    private var title: String = ""
    private var items: List<PickerItem> = emptyList()
    private var multiChoice = false
    private var selectedId: Int? = null
    private val selectedIds = LinkedHashSet<Int>()
    private var onDone: ((PickerItem) -> Unit)? = null
    private var onDoneMulti: ((Set<Int>) -> Unit)? = null
    private val adapter = PickerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Items and callback are not restorable; close a sheet recreated by the system.
        if (onDone == null && onDoneMulti == null) dismissAllowingStateLoss()
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
            if (multiChoice) {
                onDoneMulti?.invoke(selectedIds.toSet())
            } else {
                val chosen = items.firstOrNull { it.id == selectedId }
                    ?: items.firstOrNull { it.id == null }
                chosen?.let { onDone?.invoke(it) }
            }
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
                if (filtered.none { it.id != null } && items.any { it.id != null }) View.VISIBLE else View.GONE
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
            val row = holder.binding
            if (multiChoice) {
                row.radioButton.visibility = View.GONE
                row.checkbox.visibility = View.VISIBLE
                row.minTxt.visibility = View.VISIBLE
                row.minTxt.text = item.name
                row.checkbox.setOnCheckedChangeListener(null)
                row.checkbox.isChecked = item.id in selectedIds
                row.checkbox.setOnCheckedChangeListener { _, checked ->
                    val id = item.id ?: return@setOnCheckedChangeListener
                    if (checked) selectedIds.add(id) else selectedIds.remove(id)
                }
                row.minTxt.setOnClickListener { row.checkbox.toggle() }
            } else {
                row.checkbox.visibility = View.GONE
                row.minTxt.visibility = View.GONE
                row.radioButton.visibility = View.VISIBLE
                row.radioButton.text = item.name
                row.radioButton.isChecked = item.id == selectedId
                row.radioButton.setOnClickListener {
                    selectedId = item.id
                    notifyDataSetChanged()
                }
            }
        }

        inner class ViewHolder(val binding: ItemSpinnerBinding) : RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        private const val TAG = "SearchablePickerBottomSheet"

        /**
         * @param allLabel text of the first row ("All Clinics", "All Doctors"…), whose id is null;
         * pass null for a list without that row (e.g. City). Done without a choice then does nothing.
         */
        fun show(
            fragmentManager: FragmentManager,
            title: String,
            allLabel: String?,
            items: List<PickerItem>,
            selectedId: Int?,
            onDone: (PickerItem) -> Unit
        ) {
            SearchablePickerBottomSheet().apply {
                this.title = title
                this.items = if (allLabel != null) listOf(PickerItem(null, allLabel)) + items else items
                this.selectedId = selectedId
                this.onDone = onDone
            }.show(fragmentManager, TAG)
        }

        fun showMulti(
            fragmentManager: FragmentManager,
            title: String,
            items: List<PickerItem>,
            selectedIds: Set<Int>,
            onDone: (Set<Int>) -> Unit
        ) {
            SearchablePickerBottomSheet().apply {
                this.title = title
                this.items = items
                this.multiChoice = true
                this.selectedIds.addAll(selectedIds)
                this.onDoneMulti = onDone
            }.show(fragmentManager, TAG)
        }
    }
}
