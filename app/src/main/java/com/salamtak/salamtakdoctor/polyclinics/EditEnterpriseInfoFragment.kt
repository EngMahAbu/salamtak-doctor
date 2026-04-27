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
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.transform.CircleCropTransformation
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
import com.salamtak.core.localDataBase.UserData
import com.salamtak.databinding.EditEnterpriseInfofragmentBinding
import com.salamtak.databinding.EnterpriseInfofragmentBinding
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.OffersViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.getRandomString
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.resizePic
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.showRationalePermission
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toFile
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditEnterpriseInfoFragment : NewBaseFragment(R.layout.edit_enterprise_infofragment) {

    private lateinit var binding:EditEnterpriseInfofragmentBinding
    private var FrontImageFile: File? = null
    private var backImageFile: File? = null
    private  var type:Int?=null
    private var imageFound=0
    private var partMap: Map<String, Any> = mutableMapOf()
    private var partMapImage: Map<String, Any> = mutableMapOf()
    private lateinit var polyViewModel: PolyClinicsViewModel
    private lateinit var viewModel: OffersViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= EditEnterpriseInfofragmentBinding.inflate(layoutInflater,container,false)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        viewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback { }
        bindViews()
        initButton()
        callGetPolyclinicId()
    }
    private fun bindViews(){
        binding.frontView.tvPhoto.text=getString(R.string.national_id)
        binding.backView.tvPhoto.text=getString(R.string.national_id)
        binding.frontView.editFrontIv.visibility=View.VISIBLE
        binding.backView.editFrontIv.visibility=View.VISIBLE
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
            findNavController().navigate(R.id.editEnterpriseInfoFragment)

        }
        binding.layoutCanceled.setOnClickListener {
            binding.layoutCanceled.setBackgroundResource(R.drawable.bg_button_sellector)
            binding.tvCanceled.setTextColor(android.graphics.Color.parseColor("#FFFFFFFF"))
            binding.layoutUpcoming.setBackgroundResource(R.drawable.bg_help)
            binding.tvUpcoming.setTextColor(android.graphics.Color.parseColor("#262D70"))
            binding.layoutFinished.setBackgroundResource(R.drawable.bg_help)
            binding.tvFinished.setTextColor(android.graphics.Color.parseColor("#262D70"))
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
            }else{
                validity()
            }
        }
    }

    private fun sendData() {
        if (!binding.etFullNameEn.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("Name" to binding.etFullNameEn.text.toString())
        }
        if (!binding.etFullNameAR.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("NameAr" to binding.etFullNameAR.text.toString())
        }
        if (!binding.adminIdEt.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("IdNumber" to binding.adminIdEt.text.toString())
        }
        if (!binding.emailEt.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("Email" to binding.emailEt.text.toString())
        }
        if (!binding.phoneNumberEt.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("Phone" to binding.phoneNumberEt.text.toString())
        }
        if (!binding.mobile1NumberEt.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("Mobile1" to binding.mobile1NumberEt.text.toString())
        }
        if (!binding.mobile2NumberEt.text.isNullOrEmpty()) {
            partMap = partMap + mapOf("Mobile2" to binding.mobile2NumberEt.text.isNullOrEmpty().toString())
        }
        partMap = partMap + mapOf("UserType" to 1)
        partMap = partMap + mapOf("ID" to DateUtils.getAdminOwnerId().toString())
        partMap = partMap + mapOf("UserID" to DateUtils.getUserId().toString())
        FrontImageFile?.let {
            partMap = (partMap + mapOf("ID_Front" to it))
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMap) {
            val key = item.key
            if (key.contains("ID_Front")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

        polyViewModel.updatePolyClinicOwner(builder.build())
        polyViewModel.updatePolyOwnerIdResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is Resource.Loading -> {
                  binding.progressBar.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                    val action= it.data?.data?.iD?.let { it1 ->
                        EnterpriseInfoFragmentDirections.actionEnterpriseInfoFragmentToPolyClinicAdminFragment(
                            it1
                        )
                    }
                        if (backImageFile==null){
                            if (action != null) {
                                binding.progressBar.visibility=View.GONE
                            }
                        }else{
                            it.data?.data?.let { it1 -> sendData(it1.iD) }
                        }

                    }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    private fun sendData(id: Int) {
        backImageFile?.let {
            partMapImage = partMapImage + mapOf("ID_Back" to it)
        }
        partMapImage = partMapImage + mapOf("OwnerID" to id)
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMapImage) {
            val key = item.key
            if (key.contains("ID_Back")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

        viewModel.createPolyClinicOwnerBackImg(builder.build())
        viewModel.polyClinicAdminBackImgResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.progressBar.visibility = View.GONE
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
        if (binding.etFullNameEn.text?.trim().isNullOrEmpty()) {
            binding.tvFullNameError.visibility = View.VISIBLE
        }else if (binding.etFullNameEn.text?.trim()?.split(" ")!!.size < 3) {
            binding.tvFullNameError.visibility = View.VISIBLE
            binding.tvFullNameError.text = getString(R.string.Enter_fullName_english)
        }
        else {
            binding.tvFullNameError.visibility = View.GONE
        }
        if (binding.etFullNameAR.text?.trim().isNullOrEmpty()) {
            binding.tvFullNameARError.visibility = View.VISIBLE
        }else {
            binding.tvFullNameARError.visibility = View.GONE
        }
        if (binding.adminIdEt.text?.trim().isNullOrEmpty()) {
            binding.tvNationalidError.visibility = View.VISIBLE
        }else {
            binding.tvNationalidError.visibility = View.GONE
        }

        if (binding.phoneNumberEt.text?.trim()!!.isEmpty()) {
            binding.tvPhoneError.visibility = View.VISIBLE
        }
        else {
            binding.tvPhoneError.visibility = View.GONE
        }
        if (binding.emailEt.text?.trim()!!.isEmpty()) {
            binding.tvEmailError.visibility = View.VISIBLE

        } else if (!(binding.emailEt.text?.toString()?.isValidEmail() == true)) {
            binding.tvEmailError.text = getString(R.string.match_email)
            binding.tvEmailError.visibility = View.VISIBLE

        } else {
            binding.tvEmailError.visibility = View.GONE
            value = true
        }

        return value
    }
    private fun validity(){
        binding.etFullNameEn.doOnTextChanged { text, start, before, count ->

            if (binding.etFullNameEn.text?.trim().isNullOrEmpty()) {
                binding.tvFullNameError.visibility = View.VISIBLE

            } else if (binding.etFullNameEn.text?.trim()?.split(" ")!!.size < 3) {
                binding.tvFullNameError.visibility = View.VISIBLE
                binding.tvFullNameError.text = getString(R.string.Enter_fullName_english)
            } else {
                binding.tvFullNameError.visibility = View.GONE
            }
        }
        binding.etFullNameAR.doOnTextChanged { text, start, before, count ->

            if (binding.etFullNameAR.text?.trim().isNullOrEmpty()) {
                binding.tvFullNameARError.visibility = View.VISIBLE

            } else if (binding.etFullNameAR.text?.trim()?.split(" ")!!.size < 3) {
                binding.tvFullNameARError.visibility = View.VISIBLE
                binding.tvFullNameARError.text = getString(R.string.Enter_fullName_Arabic)
            } else {
                binding.tvFullNameARError.visibility = View.GONE
            }
        }
        binding.adminIdEt.doOnTextChanged { text, start, before, count ->

            if (binding.adminIdEt.text?.trim().isNullOrEmpty()) {
                binding.tvNationalidError.visibility = View.VISIBLE
            }else if (binding.adminIdEt.text?.length!! < 14) {
                binding.tvNationalidError.visibility = View.VISIBLE
                binding.tvNationalidError.text = getString(R.string.enter14number)
            } else {
                binding.tvNationalidError.visibility = View.GONE
            }
        }
        binding.phoneNumberEt.doOnTextChanged { text, start, before, count ->
            if (binding.phoneNumberEt.text?.trim()!!.isEmpty()) {
                binding.tvPhoneError.visibility = View.VISIBLE
            } else if (binding.phoneNumberEt.text?.length!! < 11) {
                binding.tvPhoneError.visibility = View.VISIBLE
                binding.tvPhoneError.text = getString(R.string.phonedigits)
            }
            else {
                binding.tvPhoneError.visibility = View.GONE
            }
        }
        binding.emailEt.doOnTextChanged { text, start, before, count ->
            if (binding.emailEt.text?.trim()!!.isEmpty()) {
                binding.tvEmailError.visibility = View.VISIBLE

            } else if (!(binding.emailEt.text?.toString()?.isValidEmail() == true)) {
                binding.tvEmailError.text = getString(R.string.match_email)
                binding.tvEmailError.visibility = View.VISIBLE

            } else {
                binding.tvEmailError.visibility = View.GONE
            }
        }
    }

    fun String.isValidEmail() =
        !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()



    private fun callGetPolyclinicId() {
        polyViewModel.polyOwnerIdResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                  binding.progressBar1.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.progressBar1.visibility=View.GONE

                    binding.etFullNameEn.setText(it.data?.data?.name)
                    binding.etFullNameAR.setText(it.data?.data?.nameAr)
                    binding.emailEt.setText(it.data?.data?.email)
                    binding.adminIdEt.setText(it.data?.data?.idNumber)
                    binding.phoneNumberEt.setText(it.data?.data?.phone)
                    binding.mobile1NumberEt.setText(it.data?.data?.mobile1.toString())
                    binding.mobile2NumberEt.setText(it.data?.data?.mobile2.toString())
                    if (!it.data?.data?.IdImageFront.isNullOrEmpty()){
                        binding.frontView.ivAddPhoto.visibility=View.GONE
                        binding.frontView.tvPhoto.visibility=View.GONE
                        imageFound=+1
                    }
                    if (!it.data?.data?.IdImageBack.isNullOrEmpty()){
                        binding.backView.ivAddPhoto.visibility=View.GONE
                        binding.backView.tvPhoto.visibility=View.GONE
                    }
                    Glide.with(requireContext())
                        .load(it.data?.data?.IdImageFront)
                        .into(binding.frontView.ivAddDoc)
                    Glide.with(requireContext())
                        .load(it.data?.data?.IdImageBack)
                        .into(binding.backView.ivAddDoc)
                }

                is Resource.Error -> {
                  binding.progressBar1.visibility=View.GONE
                }

            }
        })
        DateUtils.getAdminOwnerId()?.toInt()?.let { polyViewModel.getpolyOwnerByID(it) }
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