package com.salamtak.salamtakdoctor.offers


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.databinding.AddOffersfragmentBinding
import com.salamtak.domain.entities.response.offers.OffersData
import com.salamtak.salamtakdoctor.adapter.CategoryAdapter
import com.salamtak.salamtakdoctor.adapter.ChooseClinksDoctorsAdapter
import com.salamtak.salamtakdoctor.adapter.ImageAdapter
import com.salamtak.salamtakdoctor.adapter.ImagesAdapter
import com.salamtak.salamtakdoctor.adapter.OfferServicesAdapter
import com.salamtak.salamtakdoctor.adapter.OfferSubServicesAdapter
import com.salamtak.salamtakdoctor.adapter.SubCategoryAdapter
import com.salamtak.salamtakdoctor.baseFragment.NewBaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.OffersViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding.convertDateFormat
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone



class AddOffersFragment : NewBaseFragment(R.layout.add_offersfragment), ChooseClinksDoctorsAdapter.Booking, CategoryAdapter.Action,
    SubCategoryAdapter.Action, ImagesAdapter.ImagesOperation,
    OfferServicesAdapter.SelectInsuranceCard, OfferSubServicesAdapter.SelectSubServices,
    ImageAdapter.ImagesOperation {

    private lateinit var binding: AddOffersfragmentBinding
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    private val args: AddOffersFragmentArgs by navArgs()
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomsheetbeahavoirservices: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomsheetbeahavoirsubservices: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomsheetbeahavoirComments: BottomSheetBehavior<ConstraintLayout>
    private lateinit var chooseClinksDoctorsAdapter: ChooseClinksDoctorsAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private lateinit var offerServicesAdapter: OfferServicesAdapter
    private lateinit var offerSubServicesAdapter: OfferSubServicesAdapter
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var viewModel: OffersViewModel
    private var partMap: Map<String, Any> = mutableMapOf()
    private var partMapImage: Map<String, Any> = mutableMapOf()
    val now = Calendar.getInstance(TimeZone.getTimeZone("CST"))
    private val images: MutableList<File> = mutableListOf()
    private var clinkId = 0
    private var serviceId = 2
    private var subCategoryId = 0
    private var subCategoryServicesId = 0
    private var subCategorySubServicesId = 0
    private var sessionCount = 1
    private var startDate = ""
    private var endDate = ""
    private var offerId = 0
    private var offerDiscount = 1
    private var name=""
    private var subCatName=""
    private var cssComment=""
    private var offerStatus=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddOffersfragmentBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheetServices()
        initButton()
        clinksRecylerview()
        categoryRecylerview()
        subCategory2Recylerview()
        subCategory3Recylerview()
        callBackGetClinicSchedualByClinicDayId()
        initrecylerview2()
        initrecylerview()
        if (args.offerId > 0) {
            callGetGallaryByOfferId()
            callgetHealthEntityOfferById()
        }else{
            callCategory()
        }
        callSubCategory()
        callDeleteImage()
        subCategoryRecylerview()
        bindViews()
        bindFields()

    }

    private fun initButton() {
        binding.etOfferDiscount.doOnTextChanged { text, start, before, count ->
            addPriceOffer()
        }
        binding.etOfferPrice.doOnTextChanged { text, start, before, count ->
            addPriceOffer()
        }

        binding.btnConfirm.setOnClickListener {
            if (isvalidateFeilds()) {
                sendingOffer()
            }
        }
        binding.layoutBottomsheetpersistantcommet.btnChoose.setOnClickListener {
            bottomsheetbeahavoirComments.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.layoutAddphoto.setOnClickListener {
            selectPic()
        }

        binding.ivArrow.setOnClickListener {
            backToOffers()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            backToOffers()
        }

        binding.btnLayoutChooseClinic.setOnClickListener {
            callBackGetClinicSchedualByClinicDayId()
            bottomsheetbeahavoir.state =
                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
        }

        binding.btnLayoutUnit.setOnClickListener {
            val action =
                AddOffersFragmentDirections.actionAddOffersFragmentToDialogBottomSheetFragment("session")
            findNavController().navigate(action)
        }

        binding.btnLayoutYourServices.setOnClickListener {
            if (this.subCategoryId == 0) {
                return@setOnClickListener
            } else {
                callSubCategory2()
                bottomsheetbeahavoirservices.state =
                    if (bottomsheetbeahavoirservices.state == BottomSheetBehavior.STATE_EXPANDED)
                        BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
            }

        }
        binding.btnLayoutChoosesubServices.setOnClickListener {
            if (this.subCategoryServicesId == 0) {
                return@setOnClickListener
            } else {
                callSubCategory3()
                bottomsheetbeahavoirsubservices.state =
                    if (bottomsheetbeahavoirsubservices.state == BottomSheetBehavior.STATE_EXPANDED)
                        BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
            }

        }

        binding.layoutBottomsheetpersistant.btnApply.setOnClickListener {
            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.layoutBottomsheetpersistant1.btnChoose.setOnClickListener {
            bottomsheetbeahavoirservices.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.layoutBottomsheetpersistant2.btnChoose.setOnClickListener {
            bottomsheetbeahavoirsubservices.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.ivAdd.setOnClickListener {
            sessionCount++
            bindViews()
        }
        binding.ivMinus.setOnClickListener {
            if (sessionCount > 1) {
                sessionCount--
                bindViews()
            }

        }

        binding.btnLayoutStartDate.setOnClickListener {
            val action =
                AddOffersFragmentDirections.actionAddOffersFragmentToDailogDatePickerFragment("date")
            findNavController().navigate(action)
        }
        binding.btnLayoutEndDate.setOnClickListener {
            val action =
                AddOffersFragmentDirections.actionAddOffersFragmentToDailogDatePickerFragment("endDate")
            findNavController().navigate(action)
        }


    }

    private fun clinksRecylerview() {
        chooseClinksDoctorsAdapter = ChooseClinksDoctorsAdapter(this)
        binding.layoutBottomsheetpersistant.rvClinks.apply {
            adapter = chooseClinksDoctorsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)

        }
    }

    private fun categoryRecylerview() {
        categoryAdapter = CategoryAdapter(this,name)
        binding.rvCatagory.apply {
            adapter = categoryAdapter
            setHasFixedSize(true)
        }

    }

    private fun subCategoryRecylerview() {
        subCategoryAdapter = this.subCatName?.let { SubCategoryAdapter(this.subCatName,this,) }!!
        binding.rvSubCatagory.apply {
            adapter = subCategoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun subCategory2Recylerview() {
        offerServicesAdapter = OfferServicesAdapter(this)
        binding.layoutBottomsheetpersistant1.rvOfferServices.apply {
            adapter = offerServicesAdapter
            setHasFixedSize(true)
        }
    }

    private fun subCategory3Recylerview() {
        offerSubServicesAdapter = OfferSubServicesAdapter(this)
        binding.layoutBottomsheetpersistant2.rvOfferServices.apply {
            adapter = offerSubServicesAdapter
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
        imagesAdapter.notifyDataSetChanged()

    }

    fun initrecylerview2() {
        imageAdapter = ImageAdapter(this)
        binding.rveditImages.apply {
            adapter = imageAdapter
            setHasFixedSize(true)
        }
    }

    private fun callBackGetClinicSchedualByClinicDayId() {
        viewModel.clinicsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    showprogtessbar()
                }

                is Resource.sucess -> {
                    hideprogressbar()
                    chooseClinksDoctorsAdapter.submitList(it.data?.data)
                    chooseClinksDoctorsAdapter.notifyDataSetChanged()

                }

                is Resource.Error -> {
                    hideprogressbar()
                }

            }
        })
        viewModel.getDoctorClinics()
    }

    private fun callCategory() {
        viewModel.categoryResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.progressBar.visibility = View.GONE
                    categoryAdapter.submitList(it.data?.category)
                    categoryAdapter.notifyDataSetChanged()
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }

            }
        })
        viewModel.getOfferMainCategory()
    }

    private fun callSubCategory() {
        viewModel.subCategoryResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.progressBar.visibility = View.GONE
                    subCategoryAdapter.submitList(it.data?.data)
                    subCategoryAdapter.notifyDataSetChanged()

                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }

            }
        })
    }

    private fun callSubCategory2() {
        viewModel.subCategory2Response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.layoutBottomsheetpersistant1.rvProgressBar.visibility = View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.layoutBottomsheetpersistant1.rvProgressBar.visibility = View.GONE
                    offerServicesAdapter.submitList(it.data?.data)
                    offerServicesAdapter.notifyDataSetChanged()
                    it.data?.data?.forEach {
                        if (it.id==this.subCategoryServicesId){
                            binding.etChooseYourServices.setText(it.name)
                        }
                    }

                }

                is Resource.Error -> {
                    binding.layoutBottomsheetpersistant1.rvProgressBar.visibility = View.GONE
                }

            }
        })
        viewModel.getOfferSubCategoryL2(this.subCategoryId)
    }

    private fun callGetGallaryByOfferId() {
        viewModel.getofferGallaryResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.sucess -> {
                    for (i in 0..(it.data?.data?.size?.minus(1) ?: 1)) {

                        lifecycleScope.launch() {
                            DataBinding.getImageBitmap(
                                "${
                                    it.data?.data?.get(
                                        i
                                    )?.imageUrl
                                }"
                            )?.let { it1 ->
                                DataBinding.getImageFile(
                                    it1, requireContext()
                                )
                            }?.let { it2 ->
                                withContext(Dispatchers.Main) {
                                   // images.add(it2)
                                }
                            }
                        }
                    }
                    imageAdapter.submitList(it.data?.data)
                    imageAdapter.notifyDataSetChanged()
                }

                is Resource.Error -> {
                }

            }
        })
        viewModel.getClinicOfferGalleryByOfferId(args.offerId)
    }

    private fun callgetHealthEntityOfferById() {
        viewModel.getOfferResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                  binding.progressBar.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.progressBar.visibility=View.GONE
                    this.offerStatus= it.data?.data?.offerStatus!!
                     if( it.data?.data?.offerMainCategoryName==null){
                        this.name=""
                    }else{
                         this.name= it.data?.data?.offerMainCategoryName.toString()
                     }
                    if( it.data?.data?.offerSubCategoryL1Name==null){
                        this.subCatName=""
                    }else{
                        this.subCatName= it.data?.data?.offerSubCategoryL1Name.toString()
                    }
                    this.subCategoryServicesId= it.data?.data?.offerCategoriesid!!
                    this.subCategorySubServicesId=it.data?.data?.offerSubCategoryID!!
                    this.subCategoryId= it.data?.data?.offerSubCategoryL1ID ?: 0
                    callSubCategory2()
                    callSubCategory3()
                    subCategoryRecylerview()
                    categoryRecylerview()
                    callCategory()
                    it.data?.data?.let { it1 -> viewModel.getOfferSubCategoryL1(it1?.offerMainCategoryID) }
                    it.data?.let { it1 -> fetchData(it1.data) }
                    if (it.data?.data?.offerStatus==1){
                        binding.btnConfirm.visibility=View.GONE
                        binding.btnLayoutChooseClinic.isEnabled=false
                        binding.btnLayoutYourServices.isEnabled=false
                        binding.btnLayoutChoosesubServices.isEnabled=false
                        binding.btnLayoutUnit.isEnabled=false

                    }
                    else if (it.data?.data?.offerStatus==2 &&  it.data?.data?.doctorComment=="null" &&  it.data?.data?.doctorComment.toString().isNullOrEmpty()){
                        binding.layoutBottomsheetpersistantcommet.tvComment.text=it.data.data.cSComment.toString()
                        bottomsheetbeahavoirComments.state =
                            if (bottomsheetbeahavoirComments.state == BottomSheetBehavior.STATE_EXPANDED)
                                BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
                        this.cssComment= it.data?.data?.cSComment.toString()
                    }

                }

                is Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                }

            }
        })
        viewModel.getHealthEntityOfferById(args.offerId)
    }

    private fun callSubCategory3() {
        viewModel.subCategory3Response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.layoutBottomsheetpersistant2.rvProgressBar.visibility = View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.layoutBottomsheetpersistant2.rvProgressBar.visibility = View.GONE
                    offerSubServicesAdapter.submitList(it.data?.data)
                    offerSubServicesAdapter.notifyDataSetChanged()
                    it.data?.data?.forEach {
                        if (it.id==this.subCategorySubServicesId){
                            binding.etSubservices.setText(it.name)
                        }
                    }
                }

                is Resource.Error -> {
                    binding.layoutBottomsheetpersistant2.rvProgressBar.visibility = View.GONE
                }

            }
        })
        viewModel.getOfferSubCategoryL3(this.subCategoryServicesId)
    }
    private fun callDeleteImage() {
        viewModel.deleteGalleryResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.sucess -> {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.image_deleted_successfully),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    callGetGallaryByOfferId()
                }

                is Resource.Error -> {

                }

            }
        })

    }

    private fun showprogtessbar() {
        binding.layoutBottomsheetpersistant.progressBar.visibility = View.VISIBLE
    }

    private fun hideprogressbar() {
        binding.layoutBottomsheetpersistant.progressBar.visibility = View.GONE

    }

    override fun onItemClick(clinkid: Int, clinkname: String, list: List<String>) {
        this.clinkId = clinkid
        binding.etChooseClinic.setText(clinkname)
    }

    override fun onItemClick(servicesId: Int) {
        this.serviceId = servicesId
        viewModel.getOfferSubCategoryL1(this.serviceId)
    }

    override fun onItemClickSubCategoryId(subCategoryId: Int) {
        this.subCategoryId = subCategoryId
    }

    override fun deleteImage(pos: Int) {
        images.remove(images[pos])
        imagesAdapter.submitList(images)
        imagesAdapter.notifyDataSetChanged()
    }

    private fun bindViews() {
        binding.tvSession.text = "${getString(R.string.session_acount)} ${sessionCount}"
    }

    private fun bindFields() {

        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).apply {
            this.timeZone = TimeZone.getTimeZone("CST")
        }
        sharedDataViewmodel.session.observe(viewLifecycleOwner) {
            if (it.id == 1) {
                binding.layoutSession.visibility = View.VISIBLE
                binding.etChooseUnit.setText(it.session)
            }
        }
        sharedDataViewmodel.birthDate.observe(viewLifecycleOwner) {
            binding.tvStartDate.text = convertDateFormat(it, "yyyy-M-d", "MMM dd, yyyy")
            startDate = convertDateFormat(it, "yyyy-M-d", "yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        }
        sharedDataViewmodel.endDate.observe(viewLifecycleOwner) {
            binding.tvEndDate.text = convertDateFormat(it, "yyyy-M-d", "MMM dd, yyyy")
            endDate = convertDateFormat(it, "yyyy-M-d", "yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        }

        binding.tvStartDate.text = formatter.format(now.time)
        binding.tvEndDate.text = formatter.format(now.time)
    }

    private fun initBottomSheetServices() {
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.clinksBottomsheet)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN

        bottomsheetbeahavoirservices =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant1.chooseServices)
        bottomsheetbeahavoirservices.state = BottomSheetBehavior.STATE_HIDDEN

        bottomsheetbeahavoirsubservices =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant2.chooseServices)
        bottomsheetbeahavoirsubservices.state = BottomSheetBehavior.STATE_HIDDEN

        bottomsheetbeahavoirComments =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistantcommet.cssComments)
        bottomsheetbeahavoirComments.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onItemSelected(id: Int, name: String) {
        this.subCategoryServicesId = id
        binding.etChooseYourServices.setText(name)
    }

    override fun onItemSubServiceSelected(id: Int, name: String) {
        this.subCategorySubServicesId = id
        binding.etSubservices.setText(name)
    }

    private fun addPriceOffer() {

        if (!binding.etOfferPrice.text.toString()
                .isNullOrEmpty() && !binding.etOfferDiscount.text.toString().isNullOrEmpty()
        ) {
            offerDiscount = binding.etOfferDiscount.text.toString().toInt()
            val taxes =
                (binding.etOfferPrice.text.toString().toInt() * (offerDiscount * .01)).toInt()
            val total = (binding.etOfferPrice.text.toString().toInt() - taxes)
            binding.etOfferPriceAfter.setText(total.toString())
        }

    }

    private fun sendingOffer() {

        if (clinkId != null && clinkId != 0) {
            partMap = partMap + mapOf("HealthEntityId" to clinkId)
        }
        if (subCategoryServicesId != null && subCategoryServicesId != 0) {
            partMap = partMap + mapOf("Offer_Categoriesid" to subCategoryServicesId)
        }
        if (subCategorySubServicesId != null && subCategorySubServicesId != 0) {
            partMap = partMap + mapOf("OfferSubCategoryID" to subCategorySubServicesId)
        }
        if (!binding.etOfferTiltle.text.toString().trim().isNullOrEmpty()) {
            partMap = partMap + mapOf("ServiceNameEn" to binding.etOfferTiltle.text.toString())
        }
        if (!binding.etOfferDescription.text.toString().trim().isNullOrEmpty()) {
            partMap = partMap + mapOf("ServiceDescEn" to binding.etOfferDescription.text.toString())
        }
        if (!binding.etOfferTitleAr.text.toString().trim().isNullOrEmpty()) {
            partMap = partMap + mapOf("ServiceNameAr" to binding.etOfferTitleAr.text.toString())
        }
        if (!binding.etOfferDescriptionAr.text.toString().trim().isNullOrEmpty()) {
            partMap =
                partMap + mapOf("ServiceDescAr" to binding.etOfferDescriptionAr.text.toString())
        }
        if (sessionCount != null && sessionCount != 0) {
            partMap = partMap + mapOf("SessionCount" to sessionCount)
        }
        if (!binding.etOfferPrice.text.toString().trim().isNullOrEmpty()) {
            partMap = partMap + mapOf(
                "PriceBefore" to binding.etOfferPrice.text.toString().toInt()
            )
        }
        if (!binding.etOfferPriceAfter.text.toString().trim().isNullOrEmpty()) {
            partMap =
                partMap + mapOf("PriceAfter" to binding.etOfferPriceAfter.text.toString().toInt())
        }
        if (!binding.etOfferDiscount.text.toString().trim().isNullOrEmpty()) {
            partMap =
                partMap + mapOf("DiscountPerc" to binding.etOfferDiscount.text.toString().toInt())
        }
        if (!binding.tvStartDate.text.toString().trim().isNullOrEmpty()) {
            partMap = partMap + mapOf("StartDate" to startDate)
        }
        if (!binding.tvEndDate.text.toString().trim().isNullOrEmpty()) {
            partMap = partMap + mapOf("EndDate" to endDate)
        }
        if (!binding.layoutBottomsheetpersistantcommet.etComment.text.toString().trim().isNullOrEmpty()) {
            partMap = partMap + mapOf("Doctor_Comment" to binding.layoutBottomsheetpersistantcommet.etComment.text.toString())
        }
        partMap = partMap + mapOf("CS_Comment" to this.cssComment)
        partMap = partMap + mapOf("OfferStatus" to this.offerStatus)
        partMap = partMap + mapOf("OfferId" to args.offerId)
        if (args.offerId==0){
            viewModel.createClinicOffer(partMap as HashMap<String, String>)
            viewModel.offerResponse.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.sucess -> {
                        binding.offerprogressBar.visibility = View.GONE
                        response.data?.let {
                            this.offerId = response.data.data.offerId
                        }
                        if (images.isNullOrEmpty()) {
                            backToOffers()
                        } else {
                            for (i in 0 until images.size) {
                                sendData(offerId, i)
                            }
                        }
                    }

                    is Resource.Error -> {
                        binding.offerprogressBar.visibility = View.GONE
                        response.message?.let {
                            Snackbar.make(
                                requireView(),
                                "${response.message}",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    is Resource.Loading -> {
                        binding.offerprogressBar.visibility = View.VISIBLE
                    }
                }
            })
        }else{
            viewModel.updateHealthEntityOfferByOfferId(partMap as HashMap<String, String>)
            viewModel.offerUpdateResponse.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.sucess -> {
                        binding.offerprogressBar.visibility = View.GONE
                        response.data?.let {
                            this.offerId = response.data.data.offerId
                        }
                        if (images.isNullOrEmpty()) {
                            backToOffers()
                        } else {
                            for (i in 0 until images.size) {
                                sendData(args.offerId, i)
                            }
                        }
                    }

                    is Resource.Error -> {
                        binding.offerprogressBar.visibility = View.GONE
                        response.message?.let {
                            Snackbar.make(
                                requireView(),
                                "${response.message}",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    is Resource.Loading -> {
                        binding.offerprogressBar.visibility = View.VISIBLE
                    }
                }
            })
        }

    }

    private fun backToOffers() {
        if (!findNavController().popBackStack(R.id.offersFragment, false)) {
            findNavController().navigate(R.id.offersFragment)
        }
    }

    private fun sendData(offerId: Int, index: Int) {
        if (offerId != null && offerId != 0) {
            partMapImage = partMapImage + mapOf("ClinicOfferId" to offerId)
        }
        images.let {
            partMapImage = partMapImage + mapOf("clinicGallery" to images[index])
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (item in partMapImage) {
            val key = item.key
            if (key.contains("clinicGallery")) {
                val file = item.value as File
                builder.addFormDataPart(
                    key,
                    "${file.name}.jpeg",
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart(key, item.value.toString())
        }

        viewModel.createOfferGallery(builder.build())
        viewModel.offerGallaryResponse.observe(viewLifecycleOwner, Observer {

            when (it) {

                is Resource.Loading -> {
                    binding.offerprogressBar.visibility = View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.offerprogressBar.visibility = View.GONE
                    Snackbar.make(
                        requireView(),
                        getString(R.string.datasentsuccessfully),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    if (index == images.size - 1) {
                        backToOffers()
                    }
                }

                is Resource.Error -> {
                    binding.offerprogressBar.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    private fun isvalidateFeilds(): Boolean {

        if (binding.etOfferTiltle.text.isNullOrEmpty() || binding.etOfferPriceAfter.text.toString()
                .isNullOrEmpty()
            || binding.etOfferPrice.text.toString()
                .isNullOrEmpty() || binding.etOfferDiscount.text.toString().isNullOrEmpty()
            || binding.etOfferDescription.text.toString()
                .isNullOrEmpty() || startDate.isNullOrEmpty() || endDate.isNullOrEmpty() || clinkId == 0
        ) {
            Snackbar.make(
                requireView(),
                getString(R.string.fields_required),
                Snackbar.LENGTH_SHORT
            ).show()
            return false
        } else {
            return true
        }

    }
    private fun fetchData(data:OffersData){
        data.let {
            binding.tvFilter.text=getString(R.string.editoffer)
            binding.btnConfirm.text=getString(R.string.update)
            binding.etChooseClinic.setText(it.healthEntityName)
            binding.etOfferTiltle.setText(it.serviceNameEn)
            binding.etOfferTitleAr.setText(it.serviceNameAr)
            binding.etOfferDescription.setText(it.serviceDescEn)
            binding.etOfferDescriptionAr.setText(it.serviceDescAr)
            binding.etOfferPrice.setText(it.priceBefore.toString())
            binding.etOfferDiscount.setText(it.discountPerc.toString())
            binding.etOfferPriceAfter.setText(it.priceAfter.toString())
            binding.layoutSession.visibility=View.VISIBLE
            binding.etChooseUnit.setText(getString(R.string.session))
            binding.tvSession.text="${getString(R.string.session_acount)} ${data.sessionCount}"
            sessionCount=data.sessionCount
            binding.tvStartDate.text = convertDateFormat(it.startDate, "yyyy-MM-dd'T'HH:mm:ss", "MMM dd, yyyy", Locale.getDefault())
            binding.tvEndDate.text = convertDateFormat(it.endDate, "yyyy-MM-dd'T'HH:mm:ss", "MMM dd, yyyy", Locale.getDefault())
        }
    }

    override fun deleteImageFromGallery(pos: Int, imageId: Int) {
        viewModel.deleteOfferGallery(imageId)
    }
    override fun onImageSelected(file: File) {
        images.add(file)
        initrecylerview()

    }

}