package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.create_gallery


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.bumptech.glide.Glide
import com.kofigyan.stateprogressbar.StateProgressBar
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentCreateClinicGalleryBinding
import com.salamtak.domain.entities.response.ClinicGalleryData
import com.salamtak.domain.entities.response.HomeAdsData
import com.salamtak.domain.entities.resquest.auth.CreateClinicGalleryModel
import com.salamtak.salamtakdoctor.adapter.ImagesAdapter
import com.salamtak.salamtakdoctor.adapter.VideosAdapter
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.ClinicGalleryAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.showImage.ShowImageActivity
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.UpdateDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.regex.Pattern


class CreateClinicGalleryFragment :
    com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment<SetupClinicViewModel>(R.layout.fragment_create_clinic_gallery),
    DialogsListener,
    RecyclerViewOnClickListener,VideosAdapter.IDelete,ImagesAdapter.ImagesOperation{

    private lateinit var binding: FragmentCreateClinicGalleryBinding
    lateinit var navController: NavController
    lateinit var mContext: Context
    private var partMapImage: Map<String, Any> = mutableMapOf()
    private val sessionManagement: SessionManagement by inject()
    private val REQUEST_VIDEO_CAPTURE = 101
    private val REQUEST_VIDEO_PICK = 102
    private var videoFile: File? = null
    private var videoUri: Uri? = null
    private lateinit var polyViewModel: PolyClinicsViewModel
    //private lateinit var imageFile: File
    private val images: MutableList<File> = mutableListOf()
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var imageVideoAdapter: VideosAdapter
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var clinicGalleryAdapter: ClinicGalleryAdapter
    var descriptionData = arrayOf("Clinic Info.","Location","Schedule","Gallery")
    var descriptionDataAr = arrayOf("بيانات العيادة","الموقع","المواعيد","الصور")
    var descriptionDataNew = arrayOf("Clinic Info.","Location","Gallery")
    var descriptionDataNewAr = arrayOf("بيانات العيادة","الموقع","الصور")
    var isFilled = false
    var assetPosition = 0
    var editClinicgallary: String = ""
    var clinicId: Int = 0
    var addNewClinic=1
    var originalFile: File? = null
    var clinicID: Int? = null
    var appartment: String = ""
    var address: String = ""
    var cityName: String = ""
    var areaName: String = ""
    var street: String = ""
    var editLabel: String = "new"
    var building_no: String? =null
    var floor_no: Int? = null
    var cityId = 0
    var areaId = 0

    override val screenViewModel: SetupClinicViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_Clinic_VIEW_MODEL))

    private val TAG = CreateClinicGalleryFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        arguments?.let {
            editClinicgallary = it.getString("edit", "")
            addNewClinic=it.getInt("add_new_clinic",1)
            clinicID = sessionManagement.getClinicId().toString().toInt()

            editLabel = it.getString("edit", "new")

            appartment = it.getString("appartment", "")
            address = it.getString("address", "")
            cityName = it.getString("city", "")
            areaName = it.getString("area", "")
            areaId = it.getInt("areaId", 0)
            cityId = it.getInt("cityId", 0)
            street = it.getString("street", "")
            building_no = it.getString("building","" )
            floor_no = it.getInt("floor",0)

        }
        clinicId = sessionManagement.getClinicId().toString().toInt()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_clinic_gallery,
            container,
            false
        )
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.uploadBtnVideo.setOnClickListener {
            selectVideo()
        }

        if (!DateUtils.getAdminOwnerId().isNullOrEmpty()){
            binding.scheduleRb.visibility=View.GONE
            binding.vactionRb.visibility=View.GONE
        }else{
            binding.scheduleRb.visibility=View.VISIBLE
            binding.vactionRb.visibility=View.VISIBLE
        }

        if(addNewClinic == 0){
            binding.profileBottomToolbar.contraint2.visibility=View.VISIBLE
            binding.profileBottomToolbar.submitBtn.setText(getString(R.string.savegallery))
            binding.stepsRadio.visibility = View.INVISIBLE
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.visibility = View.VISIBLE
            }else{
                binding.polyYourStateProgressBarId.visibility = View.VISIBLE
            }
            binding.tvClinicprofile.visibility = VISIBLE
            binding.tvSaveedit.visibility =GONE
            binding.help2.visibility
            binding.tvEditClinicstxt.visibility = GONE
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
            }else{
                binding.polyYourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
            }

        }
        else{

            binding.profileBottomToolbar.contraint2.visibility=GONE

        }

        if (Localization.getLanguageName() == "ar") {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.setStateDescriptionData(descriptionDataAr)
            }else{
                binding.polyYourStateProgressBarId.setStateDescriptionData(descriptionDataNewAr)
            }
            binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.backarabic,0)
        } else {
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.setStateDescriptionData(descriptionData)
            }else{
                binding.polyYourStateProgressBarId.setStateDescriptionData(descriptionDataNew)
            }
            binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.group_70328, 0, 0, 0)
        }
        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        loadingDialog = LoadingDialog(mContext)
        clinicGalleryAdapter =
            ClinicGalleryAdapter(mContext, loadingDialog, this, childFragmentManager)

        setUi()
      //  subscribeToObserver()

        if (editClinicgallary.equals("edit")) {
            editClinic()
        }


        binding.tvSaveedit.setOnClickListener {
            if (editClinicgallary.equals("edit")) {
            val sucess = UpdateDialog(findNavController(), "clinic")
            sucess.show(childFragmentManager, "0")
        } else {
            findNavController().navigate(R.id.homeFragment)
        } }

        cameraRequestCode = RequestCode.CAMERA_REQUEST_CODE
        galleryRequestCode = RequestCode.MEDIA_REQUEST_CODE
        editGalleryRequestCode = RequestCode.MEDIA_EDIT_REQUEST_CODE
        clinicsVideosRecylerview()
        initrecylerview()

        binding.tvEditClinicstxt.setOnClickListener {
            findNavController().navigate(R.id.clinicsFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (editClinicgallary.equals("edit")) {
                findNavController().navigate(R.id.clinicsFragment)
            } else {
                if (!findNavController().popBackStack()) {
                    findNavController().navigate(R.id.clinicsFragment)
                }
            }
        }
        initButton()
        //callGetClinicsVideos()
        calldeleteVideos()
        getClinicGallery()
    }
    private fun initButton(){
        binding.tvClinicprofile.setOnClickListener {
            findNavController().navigate(R.id.clinicsFragment)
        }
        binding.ivAdd.setOnClickListener {
//            if (isValidURL(binding.etVideoLink.text.toString())){
               callCreateVideos()
//            }else{
//              Toast.makeText(requireContext(),"invalid Link",Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun clinicsVideosRecylerview() {
        imageVideoAdapter = VideosAdapter(this)
        binding.viewPager.apply {
            adapter = imageVideoAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, VERTICAL,false)
            setHasFixedSize(true)
        }
    }
    fun initrecylerview() {
        imagesAdapter = ImagesAdapter(this)
        binding.rvAddImages.apply {
            adapter = imagesAdapter
            setHasFixedSize(true)
        }
        imagesAdapter.submitList(images)
    }

    private fun editClinic() {

        binding.clinicToolbar.root.visibility = VISIBLE
        binding.clinicToolbar.circularBackground.visibility = View.INVISIBLE
        binding.tvLogout.visibility = GONE
        binding.profileBottomToolbar.submitBtn.setText(R.string.update)
        binding.profileBottomToolbar.saveBtn.setText(R.string.ignore)
        binding.toolbar.toolbarHeader.text = getString(R.string.edit_clinic)
//        binding.editClinicToolbar.root.visibility = View.VISIBLE
        binding.stepsRadio.visibility = View.VISIBLE
        binding.locationRb.setOnClickListener{
            Toast.makeText(requireContext(), "location", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putString("edit", "edit")
            clinicID?.let { it1 -> bundle.putInt("key", it1) }
            bundle.putString("building", building_no.toString())
            bundle.putString("street",street)
            bundle.putString("city",cityName)
            bundle.putString("area",areaName)
            bundle.putInt("areaId",areaId)
            bundle.putInt("cityId",cityId)
            floor_no?.let { it1 -> bundle.putInt("floor", it1) }
            bundle.putString("appartment",appartment)
            clinicID?.let { it1 -> bundle.putInt("clinicId", it1) }
            bundle.putString("address", address)
            findNavController().navigate(R.id.locationFragment,bundle)
        }
        binding.infoRb.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("edit", "edit")
            bundle.putInt(CLINIC_ID, sessionManagement.getClinicId()!!.toInt())
            clinicID?.let { it1 -> bundle.putInt("key", it1) }
            bundle.putString("building", building_no.toString())
            bundle.putString("street",street)
            bundle.putString("city",cityName)
            bundle.putString("area",areaName)
            bundle.putInt("areaId",areaId)
            bundle.putInt("cityId",cityId)
            floor_no?.let { it1 -> bundle.putInt("floor", it1) }
            bundle.putString("appartment",appartment)
            clinicID?.let { it1 -> bundle.putInt("clinicId", it1) }
            bundle.putString("address", address)
            findNavController().navigate(R.id.addNewClinicFragment, bundle)
        }

        binding.scheduleRb.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("edit", "edit")
            clinicID?.let { it1 -> bundle.putInt("key", it1) }
            bundle.putString("building", building_no.toString())
            bundle.putString("street",street)
            bundle.putString("city",cityName)
            bundle.putString("area",areaName)
            bundle.putInt("areaId",areaId)
            bundle.putInt("cityId",cityId)
            floor_no?.let { it1 -> bundle.putInt("floor", it1) }
            bundle.putString("appartment",appartment)
            clinicID?.let { it1 -> bundle.putInt("clinicId", it1) }
            bundle.putString("address", address)
            findNavController().navigate(R.id.clinicScheduleFragment, bundle)
        }

        binding.vactionRb.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("edit", "edit")
            clinicID?.let { it1 -> bundle.putInt("key", it1) }
            bundle.putString("building", building_no.toString())
            bundle.putString("street",street)
            bundle.putString("city",cityName)
            bundle.putString("area",areaName)
            bundle.putInt("areaId",areaId)
            bundle.putInt("cityId",cityId)
            floor_no?.let { it1 -> bundle.putInt("floor", it1) }
            bundle.putString("appartment",appartment)
            clinicID?.let { it1 -> bundle.putInt("clinicId", it1) }
            bundle.putString("address", address)
            findNavController().navigate(R.id.vacationClinicFragment, bundle)
        }

    }

    override fun onClinicGallerySelect(clinicId: Int, position: Int) {
        //todo 29/11/22
        if (isOnline(mContext)) {
         //   loadingDialog.show()
            screenViewModel.deleteClinicGallery(
                clinicId.toString()
            )
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }

        assetPosition = position
    }

    override fun onClinicGalleryURlSelect(imageUrl: String, position: Int) {

        val intent = Intent(requireContext(), ShowImageActivity::class.java)
        intent.putExtra("Image", imageUrl)
        startActivity(intent)

    }

    private fun setUi() {

        binding.clinicToolbar.ivBackBg.setOnClickListener {
            if (editClinicgallary.equals("edit")) {
                findNavController().navigate(R.id.clinicsFragment)
            } else {
                findNavController().popBackStack()
            }
        }

        binding.toolbar.toolbarHeader.text = getString(R.string.new_clinic)

        binding.clinicToolbar.oneTv.text = "4"
        binding.clinicToolbar.circularDeterminativePb.progress = 100
        binding.clinicToolbar.clinicHeaderTv.text = getString(R.string.clinic_gallery)
        binding.clinicToolbar.profileNextTv.text = getString(R.string.next_home)

        binding.uploadLayout.root.setOnClickListener {
           // showDialog(cameraRequestCode, galleryRequestCode)
            selectPic()
        }
        binding.uploadBtnCreate.setOnClickListener {
          //  showDialog(cameraRequestCode, galleryRequestCode)
            selectPic()
        }
        binding.uploadBtn.setTextColor(resources.getColor(R.color.head_color))
        binding.viewBtn.setTextColor(resources.getColor(R.color.black))
        binding.uploadBtn.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_btn_border_gray_transparent_selected))
        binding.viewBtn.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_btn_border_gray_transprent))

        binding.uploadBtn.setOnClickListener {
            binding.itemLabel.visibility = VISIBLE
            binding.uploadLayout.root.visibility = VISIBLE
            binding.galleryRv.visibility = GONE
            binding.uploadLayout.ivAddDoc.setImageDrawable(resources.getDrawable(R.drawable.bg_btn_border_gray_transparent_selected))
            binding.uploadLayout.ivAddPhoto.visibility = VISIBLE
            binding.uploadBtn.setTextColor(resources.getColor(R.color.head_color))
            binding.viewBtn.setTextColor(resources.getColor(R.color.black))
            binding.uploadBtn.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_btn_border_gray_transparent_selected))
            binding.viewBtn.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_btn_border_gray_transprent))
        }

        binding.viewBtn.setOnClickListener {
            getClinicGallery()
            binding.itemLabel.visibility = GONE
            binding.uploadLayout.root.visibility = GONE
            binding.galleryRv.visibility = VISIBLE
            binding.viewBtn.setTextColor(resources.getColor(R.color.head_color))
            binding.uploadBtn.setTextColor(resources.getColor(R.color.black))
            binding.viewBtn.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_btn_border_gray_transparent_selected))
            binding.uploadBtn.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_btn_border_gray_transprent))
        }

        binding.galleryRv.layoutManager = GridLayoutManager(mContext, 2, VERTICAL, false)
        binding.galleryRv.adapter = clinicGalleryAdapter

        binding.tvLogout.setOnClickListener {

            sessionManagement.clearUser()
            navController.navigate(R.id.action_logout_gallery)

        }

        // add new by mansour
        binding.profileBottomToolbar.submitBtn.setOnClickListener {

//            if (clinicGalleryAdapter.clinicGalleryList.isNotEmpty()) {
            if (editClinicgallary.equals("edit")) {
                val sucess = UpdateDialog(findNavController(), "clinic")
                sucess.show(childFragmentManager, "0")
            } else {
                findNavController().navigate(R.id.clinicsFragment)
            }
//            } else {
//                showError(getString(R.string.gallary_required_error))
//            }
        }

    }

    private fun subscribeToObserver() {

        screenViewModel.createGalleryClinicResponse.observe(viewLifecycleOwner) {

            when (it) {

                is Resource.Success -> {
                    Log.e(TAG, "subscribeToObserver: ${it.data.MessageCode}", )
                    if(it.data.MessageCode == 200){
                        Log.e(TAG, "subscribeToObserver: 200", )
                        loadingDialog.dismiss()
                        toast(mContext, it.data.Message)
                        getClinicGallery()
                    }else if (it.data.MessageCode == 201){
                        Log.e(TAG, "subscribeToObserver: 201", )
                        loadingDialog.dismiss()
                        val sucess = UpdateDialog(findNavController(), "")
                        sucess.show(childFragmentManager, "0")
                        Handler(Looper.getMainLooper()).postDelayed(Runnable{
                            sucess.dismiss()
                           //findNavController().navigate(R.id.clinicsFragment)
                        },1000)
                    }else if (it.data.MessageCode == 406){
                        showError(getString(R.string.large_size_error))
                    }

 //                  binding.uploadLayout.ivAddDoc.setImageDrawable(resources.getDrawable(R.drawable.bg_btn_border_gray_transparent_selected))
//                    binding.uploadLayout.ivAddPhoto.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    Log.e(TAG, "onActivityResult: ${it.message}")

                }

                Resource.Loading ->   loadingDialog.show()
                is Resource.SigUpError<*> -> TODO()
            }

        }

        screenViewModel.deleteGalleyResponse.observe(viewLifecycleOwner) {
            if (it.Success) {
                loadingDialog.dismiss()
                clinicGalleryAdapter.deleteAsset(assetPosition)
            }
        }

    }

    private fun getClinicGallery() {
        //todo 29/11/22
        if (isOnline(mContext)) {
         //   loadingDialog.show()
            screenViewModel.getClinicGallery(sessionManagement.getClinicId()!!)
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }


        screenViewModel.clinicGalleyResponse.observe(viewLifecycleOwner) {

            if (it.Success) {
                loadingDialog.dismiss()
                clinicGalleryAdapter.submitData(it.Data as ArrayList<ClinicGalleryData>)
                if(it.Data.isNullOrEmpty()){
                   binding.galleryRv.visibility = View.GONE
                   // binding.nodataTxt.visibility = VISIBLE
                }else{
                    binding.galleryRv.visibility = View.VISIBLE
                  //  binding.nodataTxt.visibility = GONE
                }
            } else {
                loadingDialog.dismiss()
                Log.e(TAG, "subscribeToObserver: ${it.Message}")
            }

        }

    }


    //todo
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    private fun callCreateVideos() {
        if (clinicId != null && clinicId != 0) {
            partMapImage = partMapImage + mapOf("ClinicId" to clinicId )
        }
        videoFile?.let {
            partMapImage = partMapImage + mapOf("VideoFile" to it)
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMapImage) {
            val key = item.key
            if (key.contains("VideoFile")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.mp4",
                    file.asRequestBody("video/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

        polyViewModel.createDoctorClinicVideo(builder.build())

        polyViewModel.createVideoReponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    loadingDialog.show()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    loadingDialog.dismiss()
                    val sucess = UpdateDialog(findNavController(), "")
                    sucess.show(childFragmentManager, "0")
                    Handler(Looper.getMainLooper()).postDelayed(Runnable{
                        sucess.dismiss()
                    },2000)


                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    loadingDialog.dismiss()
                    if (it.data?.messageCode==500){
                        loadingDialog.dismiss()
                    }
                }

            }
        })
    }

    private fun callGetClinicsVideos() {
        polyViewModel.getClinicsVideoResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                   binding.progressBar.visibility=View.VISIBLE
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    binding.progressBar.visibility=View.GONE
                    val listBlogs= mutableListOf<HomeAdsData>()
                    for (i in it.data?.data?.indices!!){
                        val homeAdsData= it.data.data!![i]?.videoURL?.let { it1 ->
                            it.data.data!![i]?.id?.let { it2 ->
                                HomeAdsData(true,
                                    it1, it2
                                )
                            }
                        }
                        if (homeAdsData != null) {
                            listBlogs.add(homeAdsData)
                        }
                    }
                    imageVideoAdapter.submitList(listBlogs)

                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                }

            }
        })
        polyViewModel.getClinicVideoByClinicId(clinicId)
    }

    override fun deleteVideo(videoId: Int) {
        polyViewModel.deleteHealthEntityVideo(videoId)
    }

    override fun playVideo(videoUrl: String) {
        playVideos(videoUrl)
    }

    fun isValidURL(url: String?): Boolean {
        // Regex to check valid URL
        val regex = ("((http|https)://)(www.)?"
                + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%"
                + "._\\+~#?&//=]*)")

        // Compile the ReGex
        val p = Pattern.compile(regex)

        // If the string is empty
        // return false
        if (url == null) {
            return false
        }

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        val m = p.matcher(url)

        // Return if the string
        // matched the ReGex
        return m.matches()
    }
    private fun calldeleteVideos() {
        polyViewModel.deleteDoctorVideoReponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    callGetClinicsVideos()
                    binding.progressBar.visibility=View.GONE
                    imageVideoAdapter.notifyDataSetChanged()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    binding.progressBar.visibility=View.GONE

                }

            }
        })
    }

    private fun selectVideo() {
        val pictureImageDialog = AlertDialog.Builder(requireContext())
            pictureImageDialog.setTitle("Select Action")
            val pictureImageItem = arrayOf("Select From Camera", "Upload Video", "Cancel")
            pictureImageDialog.setItems(pictureImageItem) { dialog, which ->
                when (which) {
                    0 -> captureVideo()
                    1 -> pickVideo()
                    2 -> dialog.dismiss()
                }
            }
        pictureImageDialog.show()

    }


    private fun captureVideo() {
        val captureIntent = Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE)
        if (captureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(captureIntent, REQUEST_VIDEO_CAPTURE)
        }
    }

    private fun pickVideo() {
        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, REQUEST_VIDEO_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_VIDEO_CAPTURE, REQUEST_VIDEO_PICK -> {
                    val videoUri: Uri? = data?.data
                    this.videoUri=videoUri
                    binding.showVideo.visibility=View.VISIBLE
                    binding.showVideo.apply {
                        setVideoURI(videoUri)
                        start()
                    }
                    videoFile=createTemoraryFile(videoUri)

                }
            }
       }
    }



    fun createTemoraryFile(uri: Uri?): File {
        val inputStream = uri?.let { requireActivity().contentResolver.openInputStream(it) }
        val file = createTempFile(
            directory = requireActivity().cacheDir
        )
        inputStream?.copyTo(file.outputStream())
        return file
    }

    private fun playVideos(videoUrl: String){
      //  binding.showVideo.visibility= VISIBLE
        val uri = Uri.parse("https://salamtechapi.azurewebsites.net${videoUrl}")
//       binding.showVideo.setVideoURI(uri)
//        val mediaController = MediaController(requireContext())
//        mediaController.setAnchorView(binding.showVideo)
//        mediaController.setMediaPlayer(binding.showVideo)
//        binding.showVideo.setMediaController(mediaController)
//        binding.showVideo.start()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setDataAndType(uri, "video/mp4")
        startActivity(intent)

    }

    override fun deleteImage(pos: Int) {
        images.remove(images[pos])
        imagesAdapter.submitList(images)
        imagesAdapter.notifyDataSetChanged()
    }

    fun uriToFile(uri: Uri, context: Context): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        var fileName = getFileName(uri, context) ?: "temp_file"

        val validExtensions = listOf("jpg", "jpeg", "png")
        val hasValidExtension = validExtensions.any { fileName.lowercase().endsWith(".$it") }

        if (!hasValidExtension) {
            val mimeType = context.contentResolver.getType(uri)
            val extension = when (mimeType) {
                "image/jpeg" -> "jpg"
                "image/png" -> "png"
                else -> "jpg"
            }
            fileName += ".$extension"
        }

        val tempFile = File(context.cacheDir, fileName)

        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    fun getFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }

    override fun onImageSelected(file: File) {
        images.add(file)
        initrecylerview()
        screenViewModel.createClinicGallery(
            CreateClinicGalleryModel(
                sessionManagement.getClinicId()!!,
                ensureValidImageFormatAndSize(imageFile!!)
            )
        )
        subscribeToObserver()
    }
    fun ensureValidImageFormatAndSize(inputFile: File): File {
        val validFormats = listOf("jpg", "jpeg", "png")
        val maxSizeBytes = 5 * 1024 * 1024 // 5 MB
        val ext = inputFile.extension.lowercase()

        // Decode bitmap
        var bitmap: Bitmap = BitmapFactory.decodeFile(inputFile.absolutePath)
            ?: throw IllegalArgumentException("Cannot read image: ${inputFile.absolutePath}")

        // If format not valid, convert to JPG
        var outputFile = if (ext !in validFormats) {
            val newFile = File(inputFile.parent, inputFile.nameWithoutExtension + "_converted.jpg")
            FileOutputStream(newFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            println("Converted to: ${newFile.absolutePath}")
            newFile
        } else inputFile

        // Check file size
        if (outputFile.length() > maxSizeBytes) {
            println("Compressing image because it’s larger than 5 MB...")
            outputFile = compressImage(outputFile, maxSizeBytes)
        }

        println("✅ Final image ready: ${outputFile.name} (size: ${outputFile.length() / 1024} KB)")
        return outputFile
    }

    fun compressImage(inputFile: File, maxSizeBytes: Int): File {
        val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath)
            ?: throw IllegalArgumentException("Cannot decode file for compression")

        val outputFile = File(inputFile.parent, inputFile.nameWithoutExtension + "_compressed.jpg")
        var quality = 100
        var byteArray: ByteArray
        do {
            val stream = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            byteArray = stream.toByteArray()
            quality -= 5
        } while (byteArray.size > maxSizeBytes && quality > 10)

        FileOutputStream(outputFile).use {
            it.write(byteArray)
        }

        return outputFile
    }

}