package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.addEmr

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemAttachmentEmrBinding
import com.salamtak.domain.entities.response.AttachmentEmrData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import java.lang.Exception

class AttachmentEmrAdapter (
    val mContext: Context, val loadingDialog: LoadingDialog,
    val listener: RecyclerViewOnClickListener
    , val childFragmentManager: FragmentManager
) : RecyclerView.Adapter<AttachmentEmrAdapter.ViewHolder>() {

    var attachmentEmrList = ArrayList<AttachmentEmrData>()
    private val TAG = AttachmentEmrAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_attachment_emr, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(attachmentEmrList[position], position)
    }

    override fun getItemCount(): Int = attachmentEmrList.size

    fun deleteAsset(position: Int) {
        Log.e(TAG, "deleteAsset: $position")
        try {
            attachmentEmrList.removeAt(position)
            notifyDataSetChanged()
            notifyItemRemoved(position)
        } catch (e: Exception) {
            Log.e(TAG, "deleteAsset: ${e.message}")
        }
    }

    inner class ViewHolder(val binding: ItemAttachmentEmrBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AttachmentEmrData, position: Int) {

//            binding.ivAddPhoto.visibility = View.GONE
            binding.ivView.visibility = View.VISIBLE
            binding.ivDelete.visibility = View.VISIBLE

          /*  if (item.DocumentUrl.isNotEmpty()) {
                Picasso.get().load("${UseCaseModule..}${item.DocumentUrl}")
                    .into(binding.attachmentIv)
            }*/

            binding.ivDelete.setOnClickListener {
                loadingDialog.show()
                listener.onClinicGallerySelect(item.Id,position)
//                val sucess = DeleteGallaryDialog(loadingDialog, listener, item, position)
//                sucess.show(childFragmentManager, "0")
            }

        }
    }

    fun submitData(d: ArrayList<AttachmentEmrData>) {
        this.attachmentEmrList = d
        notifyDataSetChanged()

    }

}