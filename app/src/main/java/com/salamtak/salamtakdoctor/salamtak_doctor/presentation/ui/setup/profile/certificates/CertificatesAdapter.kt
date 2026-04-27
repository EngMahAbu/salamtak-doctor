package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.certificates

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.ItemCertificateBinding
import com.salamtak.domain.entities.response.CertificatesData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.profile.SetupProfileFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.DeleteCertificateDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.DeleteGallaryDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.squareup.picasso.Picasso

import java.io.File
import java.lang.Exception

class CertificatesAdapter(
    val mContext: Context, val loadingDialog: LoadingDialog,
    val listener: RecyclerViewOnClickListener, val childFragmentManager: FragmentManager,
    val onImageClickListener: OnImageClickListener
) : RecyclerView.Adapter<CertificatesAdapter.ViewHolder>() {

    var isCollapsed = true
    private lateinit var imageFile: File

    var list = mutableListOf<CertificatesData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val TAG = CertificatesAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_certificate, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)

        // when click to eye to show image of certificates list
//        holder.binding.root.upload_layout.iv_view.setOnClickListener {
//            onImageClickListener.onImageClicked(list[position])
//        }

    }

    fun deleteAsset(position: Int) {
        Log.e(TAG, "deleteAsset: $position")
        try {
            list.removeAt(position)
            notifyDataSetChanged()
            notifyItemRemoved(position)
        } catch (e: Exception) {
            Log.e(TAG, "deleteAsset: ${e.message}")
        }
    }


    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemCertificateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CertificatesData, position: Int) {

            binding.submitBtn.text = mContext.getString(R.string.edit)
            if (item.CertificateUrl != null && item.CertificateUrl.toString().isNotEmpty())
                Picasso.get().load("${item.CertificateUrl.toString()}")
                    .into(binding.uploadLayout.uploadPhotoIv)



            binding.uploadLayout.uploadPhotoTxt.text = item.Title
            binding.certificateTitleEt.setText(item.Title)
            binding.certificateTitleArEt.setText(item.TitleAr)
            binding.certificateBodyEt.setText(item.Description)
            binding.certificateBodyArEt.setText(item.DescriptionAr)
            binding.certificateLabel.text = item.Title
            binding.birthEt.setText(item.Year.toString())
            binding.uploadLayout.ivView.visibility = VISIBLE

            binding.ivDelete.setOnClickListener {
//                loadingDialog.show()
//                listener.onAssetDelete(item.Id,position)
                val sucess = DeleteCertificateDialog(loadingDialog, listener, item, position)
                sucess.show(childFragmentManager, "0")
            }

            binding.ivCollapse.setOnClickListener {

                if (isCollapsed) {
                    animateExpand(binding.ivCollapse, mContext)
                    binding.birthEt.visibility = VISIBLE
                    binding.certificateBodyEt.visibility = VISIBLE
                    binding.certificateTitleEt.visibility = VISIBLE
                    binding.uploadLayout.root.visibility = VISIBLE
                    binding.certificateBodyArEt.visibility = VISIBLE
                    binding.certificateTitleArEt.visibility = VISIBLE
                    binding.dateLabel.visibility = VISIBLE
                    binding.submitBtn.visibility = VISIBLE

                    binding.titleEnTv.visibility = VISIBLE
                    binding.titleArTv.visibility = VISIBLE
                    binding.descEnTv.visibility = VISIBLE
                    binding.descArTv.visibility = VISIBLE

                } else {
                    animateCollapse(binding.ivCollapse, mContext)
                    binding.birthEt.visibility = GONE
                    binding.certificateBodyEt.visibility = GONE
                    binding.certificateTitleEt.visibility = GONE
                    binding.uploadLayout.root.visibility = GONE
                    binding.certificateBodyArEt.visibility = GONE
                    binding.certificateTitleArEt.visibility = GONE
                    binding.dateLabel.visibility = GONE
                    binding.submitBtn.visibility = GONE

                    binding.titleEnTv.visibility = GONE
                    binding.titleArTv.visibility = GONE
                    binding.descEnTv.visibility = GONE
                    binding.descArTv.visibility = GONE
                }

            }

            binding.submitBtn.setOnClickListener {
                listener.onCertificateEdit(binding, position, item.Id)
            }

            binding.uploadLayout.uploadPhotoCon.setOnClickListener {
                listener.onImagePickAction(binding, position)
            }

            if (SetupProfileFragment.certificatesEdit != "edit"){
                binding.ivDelete.visibility = GONE
            }else{
                binding.ivDelete.visibility = VISIBLE
            }

        }

    }

    private fun animateExpand(binding: ImageView, mContext: Context) {
        isCollapsed = false
        val rotate = RotateAnimation(
            360F,
            180F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        binding.setImageDrawable(mContext.getDrawable(R.drawable.ic_up))
    }

    private fun animateCollapse(binding: ImageView, mContext: Context) {
        isCollapsed = true
        val rotate = RotateAnimation(
            180F,
            360F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        binding.setImageDrawable(mContext.getDrawable(R.drawable.ic_down))
    }

    interface OnImageClickListener{
        fun onImageClicked(certificatesData: CertificatesData)
    }

}