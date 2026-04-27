package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemLayoutQandaBinding
import com.salamtak.domain.entities.Questions
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding


class QuestionsAdapter(val addAnswer:IAddAnswer,val isDoctor:Boolean ):ListAdapter<Questions,QuestionsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view= ItemLayoutQandaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
         holder.binding.apply {
             tvSpecialistName.text="${tvSpecialistName.context.getString(R.string.specialization)} : ${services.specialityName}"
             tvAge.text="${tvSpecialistName.context.getString(R.string.patientage)} : ${services.age}"
             if (DefaultLocaleHelper.getInstance(tvQuestions.context).getCurrentLocale()=="en"){
                 tvQuestions.text="${tvSpecialistName.context.getString(R.string.your_question)} : ${services.question} ?"
             }else{
                 tvQuestions.text="${tvSpecialistName.context.getString(R.string.your_question)} : ${services.question}؟ "
             }
             if (!services.chronic_disease.isNullOrEmpty()){
                 holder.binding.tvChronicDisease.visibility= View.VISIBLE
                 tvChronicDisease.text="${tvSpecialistName.context.getString(R.string.chronicdisease)} : ${services.chronic_disease}"
             }else{
                 holder.binding.tvChronicDisease.visibility= View.GONE
             }
             if (!services.prevSurgery.isNullOrEmpty()){
                 holder.binding.tvPrevSurgery.visibility= View.VISIBLE
                 tvPrevSurgery.text="${tvSpecialistName.context.getString(R.string.prevSurgery)} : ${services.prevSurgery}"
             }else{
                 holder.binding.tvPrevSurgery.visibility= View.GONE
             }
             if (!services.qDate.isNullOrEmpty() || services.qDate!=null){
                 holder.binding.tvQuestionDate.visibility= View.VISIBLE
                 tvQuestionDate.text="${tvSpecialistName.context.getString(R.string.questiondate)} : ${
                     DataBinding.convertDateFormat(
                         services.qDate,
                         "yyyy-MM-dd'T'HH:mm:ss",
                         "MMM dd, yyyy"
                     )
                 }"
             }else{
                 holder.binding.tvQuestionDate.visibility= View.GONE
             }
             if (isDoctor){
                 holder.binding.ivDecline.visibility=View.GONE
                 holder.binding.btnAddAnswer.visibility=View.GONE
                 tvPatientname.text="${tvSpecialistName.context.getString(R.string.patientName)} : ${services.patientFirstname}"
             }else{
                 holder.binding.btnAddAnswer.visibility=View.VISIBLE
                 tvPatientname.text="${tvSpecialistName.context.getString(R.string.patientName)} : ${services.patientFulltname}"
             }
         }
        holder.binding.btnAddAnswer.setOnClickListener {
            addAnswer.addAnswer(services)
        }
        holder.binding.ivDecline.setOnClickListener {
            addAnswer.cancelAnswer(services.id)
        }
    }


    class ViewHolder(itemBinding: ItemLayoutQandaBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutQandaBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<Questions>() {
        override fun areItemsTheSame(oldItem: Questions, newItem: Questions): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Questions, newItem: Questions): Boolean {
            return true
        }
    }
    interface IAddAnswer{
        fun addAnswer(questions: Questions)
        fun cancelAnswer(id:Int?)
    }

}