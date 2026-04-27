package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.certificates

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.NewItemCertificateTableBinding
import com.salamtak.domain.entities.response.CertificatesData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.profile.SetupProfileFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.DeleteCertificateDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import java.text.NumberFormat
import java.util.Locale


class NewCertificatesAdapter(
    val mContext: Context, val loadingDialog: LoadingDialog,
    val listener: RecyclerViewOnClickListener, val childFragmentManager: FragmentManager,
    val onEditClickListener: OnEditClickListener,
    val onSaveClickListener: OnSaveClickListener,
    val onImageClickListener: OnImageClickListener,

//    ,val onImageClickListener: OnImageClickListener
) : RecyclerView.Adapter<NewCertificatesAdapter.ViewHolder>() , YearAdapter.AdapterOnClick{

    var selectedYear = ""
    var currentPosition = 0

    var list = mutableListOf<CertificatesData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val TAG = NewCertificatesAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.new_item_certificate_table, parent, false
            )
        )

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

    inner class ViewHolder(val binding: NewItemCertificateTableBinding) :
        RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(item: CertificatesData, position: Int) {



            Log.d(TAG, "bind: $item")
            binding.certificatesSave.text = mContext.getString(R.string.update_cert)
            if (Localization.getLanguageName()=="ar"){
                binding.headerTitle.setText(item.TitleAr)
                binding.certificateLabel.text = item.TitleAr
                binding.tvTittleAr.setText(item.TitleAr)
                binding.tvTittleEn.setText(item.Title)
                binding.tvDescriptionAr.setText(item.DescriptionAr)
                binding.tvDescriptionEn.setText(item.Description)
            }else{
                binding.certificateLabel.text = item.Title
                binding.headerTitle.setText(item.Title)
                binding.tvTittleEn.setText(item.Title)
                binding.tvTittleAr.setText(item.TitleAr)
                binding.tvDescriptionAr.setText(item.DescriptionAr)
                binding.tvDescriptionEn.setText(item.Description)
            }
            binding.tvYear.text = item.Year.toString()

            if (item.CertificateFile == null)
                Glide.with(mContext).load("${item.CertificateUrl}").placeholder(R.drawable.certificate_image).into(binding.ivCertificate)
            else{
                val imageUri = Uri.fromFile(item.CertificateFile)
                Glide.with(mContext).load(imageUri).placeholder(R.drawable.certificate_image).into(binding.ivCertificate)

            }

//            binding.ivCertificate



//            binding.certificateTitleEt.setText(item.Title)
//            binding.certificateTitleArEt.setText(item.TitleAr)
//            binding.certificateBodyEt.setText(item.Description)
//            binding.certificateBodyArEt.setText(item.DescriptionAr)
//            binding.certificateLabel.text = item.Title
            binding.certificateYearTv.text = item.Year.toString()
//            binding.uploadLayout.ivView.visibility = View.VISIBLE

            binding.cardCalender.setOnClickListener {

                val y= mutableListOf<String>()
                val nf: NumberFormat = NumberFormat.getInstance(Locale("AR"))
                val yearList = mContext.resources.getStringArray(R.array.year_list)
                yearList.forEach {
                    y.add(nf.parse(it).toString())
                }

                showYearBottomSheet(mContext, y, binding)


            }

            binding.iconDelete.setOnClickListener {
//                loadingDialog.show()
//                listener.onAssetDelete(item.Id,position)

                if (item.Id == 0){
                    deleteAsset(position)
                }else{
                    val success = DeleteCertificateDialog(loadingDialog, listener, item, position)
                    success.show(childFragmentManager, "0")
                }

            }

            binding.ivCertification.setOnClickListener {
                binding.linearLayout5.visibility = View.VISIBLE
                if (binding.llCertification.visibility == View.VISIBLE) {
                    binding.llCertification.visibility = View.GONE
                    binding.ivCertification.rotation = 180F
                } else {
                    binding.llCertification.visibility = View.VISIBLE
                    binding.ivCertification.rotation = 180F
                }
            }
        }

    }

    fun showYearBottomSheet(
        context: Context,
        yearList: MutableList<String>,
        binding: NewItemCertificateTableBinding
    ) {
        val bottomSheetDialog = BottomSheetDialog(mContext)
        bottomSheetDialog.setContentView(R.layout.yearpicker)

        val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.btnDone)
        val pickerYear = bottomSheetDialog.findViewById<RecyclerView>(R.id.rvYear)

        val adapter = YearAdapter(context, yearList, this)
        pickerYear?.adapter = adapter

        btnDone?.setOnClickListener {
            binding.tvYear.text = selectedYear
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()

    }

    interface OnImageClickListener {
        fun onImageClicked(binding: NewItemCertificateTableBinding,certificatesData: CertificatesData, position: Int)
    }

    override fun onBindViewHolder(holder: NewCertificatesAdapter.ViewHolder, position: Int) {

        currentPosition = position

        holder.bind(list[position], position)

        if (SetupProfileFragment.certificatesEdit == "edit" && list.size >= 1){
        //    holder.binding.iconDelete.visibility = View.VISIBLE

            holder.binding.certificatesSave.visibility = View.VISIBLE
        }

//        if(position != list.size-1){
            holder.binding.llCertification.visibility = View.GONE
//        }

        holder.binding.iconEdit.setOnClickListener {
            onEditClickListener.onEditClicked(list[position])
        }

        var certificateUrl = ""
        if ( list[position].CertificateUrl != null && list[position].CertificateUrl.length != 0 ){
            var imageUrl =list[position].CertificateUrl
            if (imageUrl.contains("http")){
                certificateUrl = imageUrl
            }else{
                certificateUrl = "${imageUrl}"
            }
        }

        holder.binding.certificatesSave.setOnClickListener {
            onSaveClickListener.onUpdateCertificate(holder.binding, position, list[position].Id,certificateUrl)
        }

        holder.binding.ivCertificate.setOnClickListener {
            onImageClickListener.onImageClicked(holder.binding,list[position], position)
        }

        holder.binding.tvTittleEn.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {
                if(position < list.size){
                    list[position].Title = s.toString()
                }

            }
        })
        holder.binding.tvTittleAr.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {
                list[position].TitleAr = s.toString()
            }
        })
        holder.binding.tvDescriptionEn.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {
                list[position].Description = s.toString()
            }
        })
        holder.binding.tvDescriptionAr.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {
                list[position].DescriptionAr = s.toString()
            }
        })

//        notifyDataSetChanged()

    }

    interface OnEditClickListener {
        fun onEditClicked(certificatesData: CertificatesData)
    }

    interface OnSaveClickListener {
        fun onSaveClicked(certificatesData: CertificatesData)
        fun onUpdateCertificate(binding: NewItemCertificateTableBinding, position: Int, id: Int, certificateUrl : String)
    }

    override fun YearOnClick(year: String) {
        selectedYear = year
        list[currentPosition].Year = year.toInt()
    }

}