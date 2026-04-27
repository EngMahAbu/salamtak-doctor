package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salamtak.R
import com.salamtak.databinding.DocumentsLayoutBinding
import com.salamtak.domain.entities.response.LegalDocumentsData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class LegalDocumentAdapterNew(
    val listener: RecyclerViewOnClickListener,
    val childFragmentManager: FragmentManager,
    var editLabel: String = ""
) : RecyclerView.Adapter<LegalDocumentAdapterNew.ViewHolder>() {


    //    var legalDocumentItemsList = mutableListOf<ModifiyDocumentModel>()
    var legalDocumentItemsList = mutableListOf<LegalDocumentsData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.documents_layout, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (legalDocumentItemsList[position] != null) {
            holder.bind(legalDocumentItemsList[position], position)
            holder.binding.showDocument = legalDocumentItemsList[position]
        }

    }


    override fun getItemCount(): Int = legalDocumentItemsList.size


    inner class ViewHolder(val binding: DocumentsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation")
        fun bind(item: LegalDocumentsData, position: Int) {
            binding.itemLabel.text = "${item.LegalDocumentTypeName}"
            binding.descLabel.text = item.Doc_Desc
            if (!item.Doc_Desc.isNullOrEmpty()) {

            }


//            if (!item.DocumentUrl.isNullOrEmpty()){
//                ImageUtil.loadPicture(binding.ivAddDoc, item.DocumentUrl)
//            }


            // to hideen plus if image found
//            if(item.items.size > 0){
//                if (!item.items[position].DocumentUrlFront.isNullOrEmpty()) {
//                    ImageUtil.loadPicture(binding.frontView.ivAddDoc, item.items[position].DocumentUrlFront)
//                    binding.frontView.ivAddPhoto.visibility = View.GONE
//                }
//
//                if (!item.items[position].DocumentUrlBack.isNullOrEmpty()) {
//                    ImageUtil.loadPicture(binding.backView.ivAddDoc, item.items[position].DocumentUrlBack)
//                    binding.backView.ivAddPhoto.visibility = View.GONE
//                }
//
//            }
//            if (editLabel.equals("edit") && !item.DocumentUrl.isNullOrEmpty()) {


            if (item.URLs.isNotEmpty()) {
                if (item.RequiredImages == 1 && item.ImageCount==1) {
                    if (item.URLs is List<*>) {
                        val data = item.URLs
                        ImageUtil.loadPicture(
                            binding.frontView.ivAddDoc,
                            data[item.URLs.size - 1].URL
                        )
                    } else {
                        val data = item.URLs
                        ImageUtil.loadPicture(binding.frontView.ivAddDoc, data[0].URL)
                    }

                    if (item.URLs.size == 1) {
                        binding.frontView.editFrontIv.visibility = View.VISIBLE
                        binding.frontView.ivAddPhoto.visibility = View.GONE
                    }

                } else if (item.RequiredImages == 2 || item.ImageCount == 2) {
                    binding.frontView.tvPhoto.visibility=View.GONE
                    binding.backView.tvPhoto.visibility=View.GONE
                    if (!item.URLs.isNullOrEmpty()) {
                        var data = item.URLs
                        if (data.size > 1) {
                            if (item.LegalDocumentTypeId == 1 || data[0].DocumentOrder==1 ) {
                                ImageUtil.loadPicture(binding.frontView.ivAddDoc, data[0].URL)
                                binding.frontView.ivAddPhoto.visibility=View.GONE
                                binding.frontView.tvPhoto.visibility=View.GONE
                                binding.backView.tvPhoto.visibility=View.GONE
                            }
                            if (item.LegalDocumentTypeId == 2 || data[0].DocumentOrder==2) {
                                ImageUtil.loadPicture(binding.backView.ivAddDoc, data[1].URL)
                                binding.backView.ivAddPhoto.visibility=View.GONE
                                binding.frontView.tvPhoto.visibility=View.GONE
                                binding.backView.tvPhoto.visibility=View.GONE
                            }
                            if (item.LegalDocumentTypeId == 1 || data[0].DocumentOrder==1) {
                                ImageUtil.loadPicture(binding.backView.ivAddDoc, data[1].URL)
                                binding.frontView.ivAddPhoto.visibility=View.GONE
                                binding.frontView.tvPhoto.visibility=View.GONE
                                binding.backView.tvPhoto.visibility=View.GONE
                            }
                            if (item.LegalDocumentTypeId == 3 || data[0].DocumentOrder==3) {
                                ImageUtil.loadPicture(binding.frontView.ivAddDoc, data[0].URL)
                                binding.frontView.tvPhoto.visibility=View.GONE
                                binding.backView.tvPhoto.visibility=View.GONE
                            }
                            if (item.LegalDocumentTypeId == 3 || data[0].DocumentOrder==3) {
                                ImageUtil.loadPicture(binding.backView.ivAddDoc, data[1].URL)
                                binding.frontView.tvPhoto.visibility=View.GONE
                                binding.backView.tvPhoto.visibility=View.GONE
                            }
                        } else {
                            if (item.LegalDocumentTypeId == 1 || data[0].DocumentOrder==1) {
                                ImageUtil.loadPicture(binding.frontView.ivAddDoc, data[0].URL)
                                binding.frontView.ivAddPhoto.visibility = View.GONE
                                binding.frontView.tvPhoto.visibility=View.GONE
                                //binding.backView.tvPhoto.visibility=View.GONE
                            }
                            if (item.LegalDocumentTypeId == 2 || data[0].DocumentOrder==2) {
                                ImageUtil.loadPicture(binding.frontView.ivAddDoc, data[0].URL)
                                binding.frontView.ivAddPhoto.visibility=View.GONE
                                binding.frontView.editFrontIv.visibility=View.VISIBLE
                                binding.frontView.tvPhoto.visibility=View.GONE
                                binding.backView.tvPhoto.visibility=View.GONE
                            }
                            if (item.LegalDocumentTypeId == 3 || data[0].DocumentOrder==3) {
                                ImageUtil.loadPicture(binding.frontView.ivAddDoc, data[0].URL)
                                binding.frontView.ivAddPhoto.visibility=View.GONE
                                binding.frontView.editFrontIv.visibility=View.VISIBLE
                                binding.frontView.tvPhoto.visibility=View.GONE
                                binding.backView.tvPhoto.visibility=View.GONE
                            }

//                            if (data.size == 1)
//                                ImageUtil.loadPicture(binding.frontView.ivAddDoc, data[item.URLs.size - 1].URL)
                        }

                    } else {
                        val data = item.URLs
                        ImageUtil.loadPicture(binding.frontView.ivAddDoc, data[0].URL)
                    }

                    if (item.URLs.size > 1) {
                        binding.backView.editFrontIv.visibility = View.VISIBLE
                        binding.frontView.editFrontIv.visibility = View.VISIBLE
                        binding.frontView.ivAddPhoto.visibility = View.GONE
                        binding.backView.ivAddPhoto.visibility = View.GONE
                    }


                }
            }


            binding.frontView.editFrontIv.setOnClickListener {
                listener.onImagePickAction(binding, position, item, ImageTypeEnum.FRONT, false)
            }

            binding.backView.editFrontIv.setOnClickListener {
                listener.onImagePickAction(binding, position, item, ImageTypeEnum.BACk, false)
            }

            binding.frontView.ivAddDoc.setOnClickListener {
                listener.onImagePickAction(binding, position, item, ImageTypeEnum.FRONT, true)
            }

            binding.backView.ivAddDoc.setOnClickListener {
                listener.onImagePickAction(binding, position, item, ImageTypeEnum.BACk, true)
            }


//

            // TODO 10/01

            if (item.RequiredImages == 2 || item.ImageCount == 2) {
//            if(item.items.size >  1){
                //It means both images are required
                binding.backView.root.visibility = View.VISIBLE
                binding.frontView.root.visibility = View.VISIBLE


                if (position == 0) {
                    if (!item.URLs.isNullOrEmpty() ) {
                        if (item.LegalDocumentTypeId==2 ) {
                            Glide.with(binding.backView.ivAddPhoto.context).load("")
                                .placeholder(R.drawable.iv_addimage)
                                .into(binding.frontView.ivAddPhoto)
                            binding.backView.tvPhoto.text=binding.backView.tvPhoto.context.getString(R.string.upload_naitona_id_photo)
                            binding.frontView.tvPhoto.text=binding.frontView.tvPhoto.context.getString(R.string.upload_naitona_id_photo)
                        }else{
                            //binding.backView.ivAddPhoto.setImageDrawable(null)
                            binding.backView.tvPhoto.text=""
                            binding.frontView.tvPhoto.text=""
                        }
                    }else{
                        Glide.with(binding.backView.ivAddPhoto.context).load("")
                            .placeholder(R.drawable.iv_addimage)
                            .into(binding.frontView.ivAddPhoto)
                        Glide.with(binding.backView.ivAddPhoto.context).load("")
                            .placeholder(R.drawable.iv_addimage)
                            .into(binding.backView.ivAddPhoto)
                        binding.backView.tvPhoto.text=binding.backView.tvPhoto.context.getString(R.string.upload_naitona_id_photo)
                        binding.frontView.tvPhoto.text=binding.frontView.tvPhoto.context.getString(R.string.upload_naitona_id_photo)
                    }

                }
                if (position == 1) {
                    Glide.with(binding.backView.ivAddPhoto.context).load("")
                        .placeholder(R.drawable.iv_addimage)
                        .into(binding.backView.ivAddPhoto)
                    binding.backView.tvPhoto.text=binding.backView.tvPhoto.context.getString(R.string.upload_syndicateid_photo)
                    binding.frontView.tvPhoto.text=binding.frontView.tvPhoto.context.getString(R.string.upload_syndicateid_photo)
                }
                if (position == 2) {
                    Glide.with(binding.backView.ivAddPhoto.context).load("")
                        .placeholder(R.drawable.iv_addimage)
                        .into(binding.backView.ivAddPhoto)
                    binding.backView.tvPhoto.text=binding.backView.tvPhoto.context.getString(R.string.upload_clinic_linces_photo)
                    binding.frontView.tvPhoto.text=binding.frontView.tvPhoto.context.getString(R.string.upload_clinic_linces_photo)
                }else{
                   // binding.backView.ivAddPhoto.setImageDrawable(null)
                  //  binding.frontView.ivAddPhoto.setImageDrawable(null)
                }
            }

            if (item.RequiredImages == 1 ) {

                if(item.ImageCount == 2){
                    binding.backView.root.visibility = View.VISIBLE
                    binding.frontView.root.visibility = View.VISIBLE
                }else{
                    binding.backView.root.visibility = View.GONE
                    binding.frontView.root.visibility = View.VISIBLE
                }

                if (position == 1) {
                    Glide.with(binding.frontView.ivAddPhoto.context).load("")
                        .placeholder(R.drawable.iv_addimage)
                        .into(binding.frontView.ivAddPhoto)
                }
                if (position == 2) {
                    Glide.with(binding.frontView.ivAddPhoto.context).load("")
                        .placeholder(R.drawable.iv_addimage)
                        .into(binding.frontView.ivAddPhoto)
                }

            }


        }

    }

    fun submitData(d: MutableList<LegalDocumentsData>) {
        this.legalDocumentItemsList = d
        notifyDataSetChanged()

    }


}