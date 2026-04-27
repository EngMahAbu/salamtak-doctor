package com.salamtak.salamtakdoctor.polyclinics

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.EditEnterpriseInfofragmentBinding
import com.salamtak.databinding.EditpolyClinicAdminFragmentBinding
import com.salamtak.databinding.EnterpriseInfofragmentBinding
import com.salamtak.databinding.PolyClinicAdminFragmentBinding
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.OffersViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.getRandomString
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.resizePic
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.showRationalePermission
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toFile
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import java.io.File

class EditPolyClinicAdminFragment : NewBaseFragment(R.layout.editpoly_clinic_admin_fragment) {

    private lateinit var binding:EditpolyClinicAdminFragmentBinding
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    private var FrontImageFile: File? = null
    private var backImageFile: File? = null
    private var imageFound=0
    private  var type:Int?=null
    private var partMap: Map<String, Any> = mutableMapOf()
    private var partMapImage: Map<String, Any> = mutableMapOf()
    private lateinit var viewModel: OffersViewModel
    private lateinit var polyViewModel: PolyClinicsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= EditpolyClinicAdminFragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {}
        bindViews()
        initButton()
        callGetPolyInfoId()
    }

    private fun bindViews(){
        binding.frontView.tvPhoto.text=getString(R.string.fronttaxnumberimage)
        binding.backView.tvPhoto.text=getString(R.string.backtaxnumberimage)
        binding.backView.editFrontIv.visibility=View.VISIBLE
        binding.frontView.editFrontIv.visibility=View.VISIBLE
    }
    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            findNavController().navigate(R.id.moreFragment)
        }
        binding.layoutUpcoming.setOnClickListener {
            binding.layoutUpcoming.setBackgroundResource(R.drawable.bg_button_sellector)
            binding.tvUpcoming.setTextColor(android.graphics.Color.parseColor("#FFFFFFFF"))
            binding.layoutFinished.setBackgroundResource(R.drawable.bg_help)
            binding.tvFinished.setTextColor(android.graphics.Color.parseColor("#262D70"))
            binding.layoutCanceled.setBackgroundResource(R.drawable.bg_help)
            binding.tvCanceled.setTextColor(android.graphics.Color.parseColor("#262D70"))
            //  callback()
            findNavController().navigate(R.id.editEnterpriseInfoFragment)

        }

        binding.layoutCanceled.setOnClickListener {
            binding.layoutCanceled.setBackgroundResource(R.drawable.bg_button_sellector)
            binding.tvCanceled.setTextColor(android.graphics.Color.parseColor("#FFFFFFFF"))
            binding.layoutUpcoming.setBackgroundResource(R.drawable.bg_help)
            binding.tvUpcoming.setTextColor(android.graphics.Color.parseColor("#262D70"))
            binding.layoutFinished.setBackgroundResource(R.drawable.bg_help)
            binding.tvFinished.setTextColor(android.graphics.Color.parseColor("#262D70"))
            callGetPolyInfoId()
            findNavController().navigate(R.id.editPolyClinicAdminFragment)
        }

        binding.frontView.editFrontIv.setOnClickListener {
            type=1
            selectPic()
        }
        binding.backView.editFrontIv.setOnClickListener {
            type=2
            selectPic()
        }
        binding.backView.ivAddPhoto.setOnClickListener {
            type=2
            selectPic()
        }
        binding.btnNext.setOnClickListener {
            if (checkData()){
                sendData()
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
        partMap = (partMap + mapOf("OwnerID" to DateUtils.getAdminOwnerId()!!))
        partMap = (partMap + mapOf("ID" to DateUtils.getAdminInfoId().toString()))
        partMap = (partMap + mapOf("UserID" to DateUtils.getUserId().toString()))
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

        polyViewModel.updatePolyClinicInfo(builder.build())
        polyViewModel.updatePolyInfoIdResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }

                is Resource.sucess -> {

                       if (backImageFile==null){
                           binding.progressBar.visibility=View.GONE
                       }else {
                           it.data?.data?.let { it1 -> sendData(it1.iD) }
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
        if (imageFound==0) {
            if (FrontImageFile == null) {
                binding.frontView.tvPhoto.visibility = View.VISIBLE
                binding.frontView.tvPhoto.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        `in`.aabhasjindal.otptextview.R.color.red
                    )
                )
            }
        }

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
            value = true
        }

        return value
    }



    private fun callGetPolyInfoId() {
        polyViewModel.polyInfoIdResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar1.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.progressBar1.visibility=View.GONE

                    binding.nameEnEt.setText(it.data?.data?.name)
                    binding.nameAREt.setText(it.data?.data?.nameAr.toString())
                    binding.CommercialRecNo.setText(it.data?.data?.commercialRecNo)
                    binding.TaxCertificateNo.setText(it.data?.data?.taxCertificateNo)
                    binding.bioggraphy.setText(it.data?.data?.bioEn)
                    binding.bioggraphyAr.setText(it.data?.data?.bioAr)
                    if (!it.data?.data?.TaxCertificateImage1.isNullOrEmpty()){
                        binding.frontView.ivAddPhoto.visibility=View.GONE
                        binding.frontView.tvPhoto.visibility=View.GONE
                        imageFound=+1
                    }
                    if (!it.data?.data?.TaxCertificateImage2.isNullOrEmpty()){
                        binding.backView.ivAddPhoto.visibility=View.GONE
                        binding.backView.tvPhoto.visibility=View.GONE
                    }
                    Glide.with(requireContext())
                        .load(it.data?.data?.TaxCertificateImage1)
                        .into(binding.frontView.ivAddDoc)
                    Glide.with(requireContext())
                        .load(it.data?.data?.TaxCertificateImage2)
                        .into(binding.backView.ivAddDoc)
                }
                is Resource.Error -> {
                    binding.progressBar1.visibility=View.VISIBLE
                }

            }
        })
        DateUtils.getAdminInfoId()?.toInt()?.let { polyViewModel.getPolyClinicInfoByID(it) }

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