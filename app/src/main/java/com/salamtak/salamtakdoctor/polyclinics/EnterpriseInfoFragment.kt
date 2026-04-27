package com.salamtak.salamtakdoctor.polyclinics

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.core.localDataBase.UserData
import com.salamtak.databinding.EnterpriseInfofragmentBinding
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.OffersViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.getRandomString
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.resizePic
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toFile
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EnterpriseInfoFragment  :
    NewBaseFragment(R.layout.enterprise_infofragment) {

    private lateinit var binding:EnterpriseInfofragmentBinding
    private var FrontImageFile: File? = null
    private var backImageFile: File? = null
    private  var type:Int?=null
    private var partMap: Map<String, Any> = mutableMapOf()
    private var partMapImage: Map<String, Any> = mutableMapOf()
    private lateinit var viewModel: OffersViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= EnterpriseInfofragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        initButton()
    }
    private fun bindViews(){
        binding.frontView.tvPhoto.text=getString(R.string.national_id)
        binding.backView.tvPhoto.text=getString(R.string.national_id)
        UserData.setUserName(binding.etFullNameEn.text.toString())
        UserData.setUserNameAR(binding.etFullNameEn.text.toString())
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
        binding.etFullNameEn.doOnTextChanged { text, start, before, count ->
            validity()
        }

        binding.btnNext.setOnClickListener {

            if (checkData()){
                sendData()
            }else{
                validity()
            }
        }
    }


    private var galleryOpening =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount
                    for (i in 0 until count!!) {
                        val imageUri =
                            data.clipData?.getItemAt(i)?.uri ?: return@registerForActivityResult
                        val imageStream = imageUri.let {
                            requireActivity().contentResolver.openInputStream(it)
                        }
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        val resizedBitmap = selectedImage.resizePic(1000)
                        val file = resizedBitmap.toFile(requireContext(), 20.getRandomString())
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
                } else if (data?.data != null) {
                    val imageUri = data.data ?: return@registerForActivityResult
                    val imageStream = imageUri.let {
                        requireActivity().contentResolver.openInputStream(it)
                    }
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val resizedBitmap = selectedImage.resizePic(1000)
                    val file = resizedBitmap.toFile(requireContext(), 20.getRandomString())
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
        }
    private var cameraOpening =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val bitmap = data.extras?.get("data") as Bitmap
                    val resizedBitmap = bitmap.resizePic(2000)
                    val file = resizedBitmap.toFile(requireContext(), 20.getRandomString())
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

        viewModel.createPolyClinicOwner(builder.build())
        viewModel.polyClinicAdminResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is Resource.Loading -> {
                  binding.progressBar.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                    DateUtils.setAdminOwnerId(it.data?.data?.ownerID.toString())
                    val action= it.data?.data?.ownerID?.let { it1 ->
                        EnterpriseInfoFragmentDirections.actionEnterpriseInfoFragmentToPolyClinicAdminFragment(
                            it1
                        )
                    }
                    if (FrontImageFile==null){

                        if (action != null) {
                            findNavController().navigate(action)
                        }
                    }else{
                        if (backImageFile==null){
                            if (action != null) {
                                findNavController().navigate(action)
                            }
                        }else{
                            it.data?.data?.let { it1 -> sendData(it1.ownerID) }
                        }

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
                       val action=EnterpriseInfoFragmentDirections.actionEnterpriseInfoFragmentToPolyClinicAdminFragment(id)
                        findNavController().navigate(action)
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
        }
        if (FrontImageFile==null){
            binding.frontView.tvPhoto.visibility=View.VISIBLE
            binding.frontView.tvPhoto.setTextColor(ContextCompat.getColor(requireContext(), `in`.aabhasjindal.otptextview.R.color.red))
        }else{
            binding.frontView.tvPhoto.visibility=View.GONE
        }
        if (backImageFile==null){
            value=false
            binding.backView.tvPhoto.visibility=View.VISIBLE
            binding.backView.tvPhoto.setTextColor(ContextCompat.getColor(requireContext(), `in`.aabhasjindal.otptextview.R.color.red))
        }else{
            binding.backView.tvPhoto.visibility=View.GONE
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