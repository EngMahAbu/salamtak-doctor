package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.getEmrPatient

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemEmrPatientBinding
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.EmrDescriptionModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.PatientProfileFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.PatientProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.adapter.ErmDocumentAdatper
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener


class EmrPatientAdapter(
    private var list: List<EmrTitleData>, val listener: OnEmrClickListener, val mContext: Context,val listener2: RecyclerViewOnClickListener,
    val patientProfileFragment: PatientProfileFragment, var onEMRPatientDocuments: EMRPatientDocuments
) : RecyclerView.Adapter<EmrDetailsHolder>() {

    var documentAdapter : ErmDocumentAdatper? = null

    interface EMRPatientDocuments{
        fun onEmrPatientDocuments(position: Int )
    }

    var isCollapsed = false
    private lateinit var viewModel: PatientProfileViewModel

//    class ViewHolder(val binding: ItemEmrPatientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmrDetailsHolder {
        viewModel = ViewModelProvider(patientProfileFragment)[PatientProfileViewModel::class.java]

        return EmrDetailsHolder.from(parent,listener2)

    }

//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): EmrPatientAd`apter.ViewHolder =
//        ViewHolder(
//            DataBindingUtil.inflate(
//                LayoutInflater.from(parent.context),
//                R.layout.item_emr_patient, parent, false
//            )
//        )

    override fun onBindViewHolder(holder: EmrDetailsHolder, position: Int) {

        holder.bind(list[position], position)

        //set the adapter for the documents
        documentAdapter = ErmDocumentAdatper(list[position].PatientEmrdocuments)
//        var documentAdapter = ErmDocumentAdatper(abc)
//        holder.binding.attachmentRv.adapter = documentAdapter

//        holder.binding.emrTitle = list[position]
//        holder.bind(list , position)

//        holder.binding.ivCollapse.setOnClickListener {
//            listener.onEmrClicked(list[position])
////            holder.binding.emrDetailLayout.root.visibility = View.VISIBLE
////            holder.binding.emrDetailLayout.instructionsEmrEt.setText("hello")
//        }

        holder.binding.ivCollapse.setOnClickListener {
            listener.onEmrClicked(list[position])
            if (isCollapsed) {
                animateExpand(holder.binding.ivCollapse,mContext, this)
//                holder.binding.emrRecyclerView.visibility = View.VISIBLE
//                holder.binding.instructionsEmrEt.visibility = View.VISIBLE
////                holder.binding.separatedLineLeft.visibility = View.VISIBLE
////                holder.binding.separatedLineRight.visibility = View.VISIBLE
//                holder.binding.instructionsTv.visibility = View.VISIBLE
//                holder.binding.linearAddBtn.visibility = View.VISIBLE
//                holder.binding.rxDescriptionEt.visibility = View.VISIBLE
//                holder.binding.rxTitleEt.visibility = View.VISIBLE
//                holder.binding.attatchmentRecyclerView.visibility = View.VISIBLE
//                holder.binding.attachmentsTv.visibility = View.VISIBLE
//                holder.binding.separatedLineeLeft.visibility = View.VISIBLE
//                holder.binding.separatedLineeRight.visibility = View.VISIBLE

                // new line
//                holder.binding.specialities.visibility = View.VISIBLE
//                holder.binding.separateddLineRight.visibility = View.VISIBLE
//                holder.binding.separateddLineLeft.visibility = View.VISIBLE


            } else {
                animateCollapse(holder.binding.ivCollapse, mContext, this)
//                holder.binding.emrRecyclerView.visibility = View.GONE
//                holder.binding.instructionsEmrEt.visibility = View.GONE
////                holder.binding.separatedLineLeft.visibility = View.GONE
////                holder.binding.separatedLineRight.visibility = View.GONE
//                holder.binding.instructionsTv.visibility = View.GONE
//                holder.binding.instructionsTv.visibility = View.GONE
//                holder.binding.linearAddBtn.visibility = View.GONE
//                holder.binding.rxDescriptionEt.visibility = View.GONE
//                holder.binding.rxTitleEt.visibility = View.GONE
//                holder.binding.attatchmentRecyclerView.visibility = View.GONE
//                holder.binding.attachmentsTv.visibility = View.GONE
//                holder.binding.separatedLineeLeft.visibility = View.GONE
//                holder.binding.separatedLineeRight.visibility = View.GONE

                // new line
//                holder.binding.specialities.visibility = View.GONE
//                holder.binding.separateddLineRight.visibility = View.GONE
//                holder.binding.separateddLineLeft.visibility = View.GONE
            }




//            listener.onEmrClicked(list[position])
//            holder.binding.emrRecyclerView.visibility = View.VISIBLE
//            holder.binding.instructionsEmrTxt.visibility = View.VISIBLE
        }


//        holder.binding.addDocumentIv.setOnClickListener {
//            onEMRPatientDocuments.onEmrPatientDocuments(position)
//        }
//        holder.binding.linearAddBtn.setOnClickListener {
//            if (holder.binding.rxTitleEt.text.toString().isNotEmpty()
//            ) {
//                if (isOnline(mContext)) {
//                    viewModel.setEmrDescription(fillEmrDescriptionRequest(
//                        EmrDescriptionModel(), holder.binding.rxTitleEt.text.toString()
//                    ))
//                } else {
//                    noConnectivityDialog.noInternetDialogFragment(mContext)
//
//                }
//
//
//                viewModel.descriptionModel.observe(patientProfileFragment.viewLifecycleOwner, Observer {
////                    binding.emrSendLayout.showRxTitleEt.setText(it.Data.Title)
//                    holder.binding.instructionsEmrEt.setText(it.Data.Description)
//                })
//            }
//        }
        holder.binding.topIv.setOnClickListener {
            listener.onEmrClicked(list[position])
        }

    }

    fun fillEmrDescriptionRequest(emrDescriptionModel: EmrDescriptionModel, title: String): EmrDescriptionModel {

        emrDescriptionModel.Title = title
        emrDescriptionModel.AppointmentId = patientProfileFragment.appointmentId.toInt()

        return emrDescriptionModel
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setResults(results: List<EmrTitleData>) {
        list = results
        notifyDataSetChanged()
    }

    fun setDocumentData(position: Int, documentUrl : String, documentId : Int){
        if(position != -1 ){
            var url = documentUrl.split("/")
            var fileName = url[url.size-1]
            list[position].PatientEmrdocuments.add(NewPatientEmrdocument(fileName, documentId))
            documentAdapter?.notifyDataSetChanged()

        }
    }

    fun getResult(position: Int) = list[position]

    interface OnEmrClickListener {
        fun onEmrClicked(emrTitleData: EmrTitleData)
    }

    inner class ViewHolder(val binding: ItemEmrPatientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EmrDetailsData, position: Int) {

//            binding.ivCollapse.setOnClickListener {
//            onEmrClickListener.onEmrClicked(list[position])
//            binding.emrDetailLayout.root.visibility = View.VISIBLE
//            binding.emrDetailLayout.instructionsEmrEt.setText("hello")
        }


            }
    }


    private fun animateExpand(binding: ConstraintLayout, mContext: Context, emrPatientAdapter: EmrPatientAdapter ) {
        emrPatientAdapter.isCollapsed = false
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
//        binding.setImageDrawable(mContext.getDrawable(R.drawable.ic_up))
    }

    private fun animateCollapse(binding: ConstraintLayout, mContext: Context, emrPatientAdapter: EmrPatientAdapter ) {
        emrPatientAdapter.isCollapsed = true
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
//        binding.setImageDrawable(mContext.getDrawable(R.drawable.ic_down))
    }

