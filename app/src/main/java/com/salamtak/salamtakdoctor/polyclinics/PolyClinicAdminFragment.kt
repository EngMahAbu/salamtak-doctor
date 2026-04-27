package com.salamtak.salamtakdoctor.polyclinics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.PolyClinicAdminFragmentBinding
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule.SETUP_VIEW_MODEL
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.OffersViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.io.File
import kotlin.getValue

class PolyClinicAdminFragment: NewBaseFragment(R.layout.poly_clinic_admin_fragment) {

    private lateinit var binding:PolyClinicAdminFragmentBinding
    private val args:PolyClinicAdminFragmentArgs by navArgs()
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    private var FrontImageFile: File? = null
    private var backImageFile: File? = null
    private  var type:Int?=null

    private var partMap: Map<String, Any> = mutableMapOf()
    private var partMapImage: Map<String, Any> = mutableMapOf()
    private lateinit var viewModel: OffersViewModel
    private val sessionManagement: SessionManagement by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= PolyClinicAdminFragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        initButton()
    }

    private fun bindViews(){
        binding.frontView.tvPhoto.text=getString(R.string.fronttaxnumberimage)
        binding.backView.tvPhoto.text=getString(R.string.backtaxnumberimage)
    }
    private fun initButton(){
        binding.frontView.ivAddPhoto.setOnClickListener {
            type=1
            selectPic()
        }
        binding.backView.ivAddPhoto.setOnClickListener {
            type=2
            selectPic()
        }
        binding.btnNext.setOnClickListener {
            if (checkData()){
                sendData()
            }else{
                validity()
            }
        }
    }

    private fun sendData() {
        if (!binding.nameEnEt.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("Name" to binding.nameEnEt.text.toString())
        }
        if (!binding.nameAREt.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("NameAr" to binding.nameAREt.text.toString())
        }
        if (!binding.CommercialRecNo.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("CommercialRecNo" to binding.CommercialRecNo.text.toString())
        }
        if (!binding.TaxCertificateNo.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("TaxCertificateNo" to binding.TaxCertificateNo.text.toString())
        }
        if (!binding.bioggraphy.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("BioEn" to binding.bioggraphy.text.toString())
        }
        if (!binding.bioggraphyAr.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("BioAr" to binding.bioggraphyAr.text.toString())
        }
        partMap = partMap + mapOf("OwnerID" to args.ownerId)
        FrontImageFile?.let {
            partMap = (partMap + mapOf("TaxCertificateImage" to it))
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMap) {
            val key = item.key
            if (key.contains("TaxCertificateImage")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

        viewModel.createPolyClinicInfo(builder.build())
        viewModel.polyClinicInfoResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                   if (FrontImageFile==null){
                       sessionManagement.setClinicId("0")
                       findNavController().navigate(R.id.addNewClinicFragment)
                   }else{
                       DateUtils.setAdminInfoId(it.data?.data?.iD.toString())
                       if (backImageFile==null){
                           sessionManagement.setClinicId("0")
                           findNavController().navigate(R.id.addNewClinicFragment)
                       }else {
                           it.data?.data?.let { it1 -> sendData(it1.iD) }
                       }
                   }

                    it.data?.data?.let { it1 -> sharedDataViewmodel.setHealthEntityId(it1.iD) }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    private fun sendData(id: Int) {
        backImageFile?.let {
            partMapImage = partMapImage + mapOf("TaxCertificateImage2" to it)
        }
        partMapImage = partMapImage + mapOf("ID" to id)
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMapImage) {
            val key = item.key
            if (key.contains("TaxCertificateImage2")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

        viewModel.createPolyClinicInfoImg2(builder.build())
        viewModel.polyClinicInfoBackImgResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.progressBar.visibility = View.GONE
                 //123456   DateUtils.setAdminStep("2")
                    Snackbar.make(
                        requireView(),
                        getString(R.string.datasentsuccessfully),
                        Snackbar.LENGTH_SHORT
                    ).show()
                   // val action=EnterpriseInfoFragmentDirections.actionEnterpriseInfoFragmentToPolyClinicAdminFragment(id)
                    sessionManagement.setClinicId("0")
                    findNavController().navigate(R.id.addNewClinicFragment)
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    private fun checkData():Boolean{

        var value = false

        if (binding.nameEnEt.text?.trim().isNullOrEmpty()) {
            binding.tvFullNameError.visibility = View.VISIBLE
        }else {
            binding.tvFullNameError.visibility = View.GONE
        }
        if (binding.nameAREt.text?.trim().isNullOrEmpty()) {
            binding.tvFullNameARError.visibility = View.VISIBLE
        }else {
            binding.tvFullNameARError.visibility = View.GONE
        }

//        if (binding.CommercialRecNo.text?.trim()!!.isEmpty()) {
//            binding.tvCommercialRecNoError.visibility = View.VISIBLE
//        }
//        else {
//            binding.tvCommercialRecNoError.visibility = View.GONE
//        }
        if (binding.TaxCertificateNo.text?.trim()!!.isEmpty()) {
            binding.tvTaxCertificateNoError.visibility = View.VISIBLE

        } else {
            binding.tvTaxCertificateNoError.visibility = View.GONE

        }
        if (FrontImageFile==null){
            binding.frontView.tvPhoto.visibility=View.VISIBLE
            binding.frontView.tvPhoto.setTextColor(ContextCompat.getColor(requireContext(), R.color.cancel_appointment))
        }else{
            binding.frontView.tvPhoto.visibility=View.GONE
            value = true
        }

        return value
    }
    private fun validity(){
        binding.nameEnEt.doOnTextChanged { text, start, before, count ->
            if (binding.nameEnEt.text?.trim().isNullOrEmpty()) {
                binding.tvFullNameError.visibility = View.VISIBLE
            }else {
                binding.tvFullNameError.visibility = View.GONE
            }
        }
        binding.nameAREt.doOnTextChanged { text, start, before, count ->
            if (binding.nameAREt.text?.trim().isNullOrEmpty()) {
                binding.tvFullNameARError.visibility = View.VISIBLE
            }else {
                binding.tvFullNameARError.visibility = View.GONE
            }
        }
//        binding.CommercialRecNo.doOnTextChanged { text, start, before, count ->
//            if (binding.CommercialRecNo.text?.trim()!!.isEmpty()) {
//                binding.tvCommercialRecNoError.visibility = View.VISIBLE
//            }
//            else {
//                binding.tvCommercialRecNoError.visibility = View.GONE
//            }
//        }
        binding.TaxCertificateNo.doOnTextChanged { text, start, before, count ->
            if (binding.TaxCertificateNo.text?.trim()!!.isEmpty()) {
                binding.tvTaxCertificateNoError.visibility = View.VISIBLE

            } else {
                binding.tvTaxCertificateNoError.visibility = View.GONE

            }
        }

    }

     override fun onImageSelected(file: File) {
        when(type){
            1->{
                FrontImageFile=file
                Glide.with(requireContext())
                    .load(file)
                    .into(binding.frontView.ivAddDoc)
                binding.frontView.tvPhoto.visibility=View.GONE
                binding.frontView.ivAddPhoto.visibility=View.GONE
            }
            2->{
                backImageFile=file
                Glide.with(requireContext())
                    .load(file)
                    .into(binding.backView.ivAddDoc)
                binding.backView.tvPhoto.visibility=View.GONE
                binding.backView.ivAddPhoto.visibility=View.GONE
            }
        }
    }


}