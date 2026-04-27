package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemLayoutQandaBinding
import com.salamtak.domain.entities.QuestionsAnswer
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding


class AnswersAdapter(val book:IBook):ListAdapter<QuestionsAnswer,AnswersAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutQandaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
         holder.binding.apply {
             if (!services.qDate.isNullOrEmpty() || services.qDate!=null) {
                 tvChronicDisease.text = "${tvSpecialistName.context.getString(R.string.date)} : ${
                     DataBinding.convertDateFormat(
                         services.qDate,
                         "yyyy-MM-dd'T'HH:mm:ss",
                         "MMM dd, yyyy"
                     )
                 }"
             }
             tvSpecialistName.text="${tvSpecialistName.context.getString(R.string.patientName)} : ${services.patientFulltname}"
             //tvPatientname.text="${tvSpecialistName.context.getString(R.string.doctorname)} : ${services.getAnswers?.get(0)?.doctorName}"
             tvQuestions.text="${tvSpecialistName.context.getString(R.string.your_question)} : ${services.question}"
             tvAge.text="${tvSpecialistName.context.getString(R.string.patient_age)} : ${services.age}"
             tvPrevSurgery.text="${tvSpecialistName.context.getString(R.string.questionanswered)} : ${services.getAnswers?.get(0)?.answer}"
             tvDate.text = "${tvSpecialistName.context.getString(R.string.date)} : ${
                 DataBinding.convertDateFormat(
                     services.getAnswers?.get(0)?.answerDate,
                     "yyyy-MM-dd'T'HH:mm:ss",
                     "MMM dd, yyyy"
                 )
             }"
             btnAddAnswer.visibility=View.GONE
             ivDecline.visibility=View.GONE

         }

    }


    class ViewHolder(itemBinding: ItemLayoutQandaBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutQandaBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<QuestionsAnswer>() {
        override fun areItemsTheSame(oldItem: QuestionsAnswer, newItem: QuestionsAnswer): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: QuestionsAnswer, newItem: QuestionsAnswer): Boolean {
            return true
        }
    }

    interface IBook{
        fun onItemClick(doctorId:Int,clinicId:Int)
    }

}