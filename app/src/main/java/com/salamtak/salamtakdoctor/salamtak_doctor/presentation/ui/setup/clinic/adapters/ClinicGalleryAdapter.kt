package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.GalleryLayoutBinding
import com.salamtak.domain.entities.response.ClinicGalleryData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.create_gallery.FullscreenImageDialogFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.showImage.ShowImageActivity
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.DeleteGallaryDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.squareup.picasso.Picasso
import java.lang.Exception

class ClinicGalleryAdapter(
    val mContext: Context, val loadingDialog: LoadingDialog,
    val listener: RecyclerViewOnClickListener
    , val childFragmentManager: FragmentManager
) : RecyclerView.Adapter<ClinicGalleryAdapter.ViewHolder>() {

    var clinicGalleryList = ArrayList<ClinicGalleryData>()
    private val TAG = ClinicGalleryAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.gallery_layout, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clinicGalleryList[position], position)
    }

    override fun getItemCount(): Int = clinicGalleryList.size

    fun deleteAsset(position: Int) {
        Log.e(TAG, "deleteAsset: $position")
        try {
            clinicGalleryList.removeAt(position)
            notifyDataSetChanged()
            notifyItemRemoved(position)
        } catch (e: Exception) {
            Log.e(TAG, "deleteAsset: ${e.message}")
        }
    }

    inner class ViewHolder(val binding: GalleryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClinicGalleryData, position: Int) {

            binding.ivAddPhoto.visibility = GONE
//            binding.ivView.visibility = VISIBLE
            binding.ivDelete.visibility = VISIBLE

            if (item.ImageUrl!!.isNotEmpty()) {
                Picasso.get().load("${item.ImageUrl}")
                    .into(binding.ivAddDoc)
            }

            binding.ivAddDoc.setOnClickListener {
                val dialogFragment = FullscreenImageDialogFragment("${item.ImageUrl}")
                dialogFragment.show(childFragmentManager, "FullscreenImageDialog")
            }

            binding.ivDelete.setOnClickListener {
//                loadingDialog.show()
//                listener.onClinicGallerySelect(item.Id,position)
                val sucess = DeleteGallaryDialog(loadingDialog, listener, item, position)
                sucess.show(childFragmentManager, "0")
            }

//            binding.ivView.setOnClickListener {
//                listener.onClinicGalleryURlSelect(item.ImageUrl,position)
//            }

        }
    }

    fun submitData(d: ArrayList<ClinicGalleryData>) {

        this.clinicGalleryList = d
        notifyDataSetChanged()

    }

}