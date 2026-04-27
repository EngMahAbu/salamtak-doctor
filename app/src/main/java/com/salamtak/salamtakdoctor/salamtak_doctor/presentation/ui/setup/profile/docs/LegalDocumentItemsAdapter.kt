//package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.FragmentManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.salamtak.R
//import com.salamtak.databinding.DocumentsLayoutBinding
//import com.salamtak.domain.entities.response.EmrTitleData
//import com.salamtak.domain.entities.response.LegalDocumentsData
//import com.salamtak.domain.entities.response.NewPatientEmrdocument
//import com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.getEmrPatient.AttachChildItemAdapter
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.DeleteDocumentDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.DeleteGetDcumentDialog
//
//class LegalDocumentItemsAdapter(
//    val listener: RecyclerViewOnClickListener,
//    val childFragmentManager: FragmentManager,
//    var editLabel: String
//) : RecyclerView.Adapter<LegalDocumentItemsAdapter.ViewHolder>() {
//
//
////    var legalDocumentItemsList = mutableListOf<ModifiyDocumentModel>()
//    var legalDocumentItemsList = mutableListOf<ModifiyDocumentModel>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
//        ViewHolder(
//            DataBindingUtil.inflate(
//                LayoutInflater.from(parent.context),
//                R.layout.documents_layout, parent, false
//            )
//        )
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (legalDocumentItemsList[position] != null) {
//            holder.bind(legalDocumentItemsList[position], position)
//            holder.binding.showDocument = legalDocumentItemsList[position]
//        }
//
//    }
//
//    override fun getItemCount(): Int = legalDocumentItemsList.size
//
//
//    inner class ViewHolder(val binding: DocumentsLayoutBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: ModifiyDocumentModel, position: Int) {
//       //     binding.itemLabel.text = item.LegalDocumentTypeName
//
////            if (!item.DocumentUrl.isNullOrEmpty()){
////                ImageUtil.loadPicture(binding.ivAddDoc, item.DocumentUrl)
////            }
//
//            // to hideen plus if image found
////            if (editLabel.equals("edit") && !item.DocumentUrl.isNullOrEmpty()) {
//            if (!item.DocumentUrlFront.isNullOrEmpty()) {
//                ImageUtil.loadPicture(binding.frontView.ivAddDoc, item.DocumentUrlFront)
//                binding.frontView.ivAddPhoto.visibility = View.GONE
//            }
//
//            if (!item.DocumentUrlBack.isNullOrEmpty()) {
//                ImageUtil.loadPicture(binding.backView.ivAddDoc, item.DocumentUrlBack)
//                binding.backView.ivAddPhoto.visibility = View.GONE
//            }
//
//            binding.frontView.ivAddPhoto.setOnClickListener {
//                listener.onImagePickAction(binding, position, item, ImageTypeEnum.FRONT)
//            }
//
//            binding.backView.ivAddPhoto.setOnClickListener {
//                listener.onImagePickAction(binding, position, item, ImageTypeEnum.BACk)
//            }
//
//
//            if (item.LegalDocumentTypeId == 2){
//                binding.backView.root.visibility = View.GONE
//                binding.descLabel.setText(R.string.front_image_required)
//            }
//
//            if (item.LegalDocumentTypeId == 3){
//                binding.backView.root.visibility = View.GONE
//                binding.descLabel.setText(R.string.front_image_required)
//            }
//
//
//            binding.frontView.ivDelete.setOnClickListener {
//                listener.onImageDelete(position,binding)
//                val sucess = DeleteDocumentDialog(listener, binding, position)
//                sucess.show(childFragmentManager, "0")
//
//            }
//
//            binding.frontView.ivNewDelete.setOnClickListener {
//                val sucess = DeleteGetDcumentDialog(
//                    listener,
//                    binding,
//                    position,
//                    item.IdFront,
//                    item.LegalDocumentTypeId,
//                    ImageTypeEnum.FRONT
//                )
//                sucess.show(childFragmentManager, "0")
//            }
//
//            binding.backView.ivNewDelete.setOnClickListener {
//                val sucess = DeleteGetDcumentDialog(
//                    listener,
//                    binding,
//                    position,
//                    item.IdBack,
//                    item.LegalDocumentTypeId,
//                    ImageTypeEnum.BACk
//                )
//                sucess.show(childFragmentManager, "0")
//            }
//
//
//        }
//
//    }
//
//    fun submitData(d: ArrayList<ModifiyDocumentModel>) {
//
//        this.legalDocumentItemsList = d
//        notifyDataSetChanged()
//
//    }
//
//}