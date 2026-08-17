package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.kofigyan.stateprogressbar.StateProgressBar
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentLocationBinding
import com.salamtak.domain.entities.response.CityData
import com.salamtak.domain.entities.response.CountriesData
import com.salamtak.domain.entities.resquest.auth.DoctorLocation
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.help.HelpBottomSheetFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.NewClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.AreasAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.CitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.CountriesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.UpdateDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.AreasBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.CitiesBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.NationalitiesBottomSheetDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import kotlin.collections.ArrayList

class LocationFragment : BaseFragment<SetupClinicViewModel>(R.layout.fragment_location),
    RecyclerViewOnClickListener, View.OnClickListener {
    lateinit var binding: FragmentLocationBinding
    private val sessionManagement: SessionManagement by inject()
    private lateinit var citiesAdapter: CitiesAdapter
    private lateinit var areasAdapter: AreasAdapter
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    var countriesList = java.util.ArrayList<CountriesData>()
    private lateinit var countriesAdapter: CountriesAdapter
    private var doctorLocation: DoctorLocation.Location? = null
    private lateinit var loadingDialog: LoadingDialog
    var nationalityId = 1
    var cityId = 0
    var areaId = 0
    lateinit var mContext: Context
    lateinit var navController: NavController
    var english_name: String = " "
    var addNewClinic = 1
    var arabicname: String = " "
    var emailID: String = " "
    var phoneno1: String = " "
    var phoneno2: String = " "
    var phoneno3: String = " "
    var service: String = " "
    var clinicID: Int? = null
    private var polyInfoId=""
    var appartment: String = ""
    var address: String = ""
    var cityName: String = ""
    var areaName: String = ""
    var street: String = ""

    var editLabel: String = "new"
    var building_no: String? =null
    var floor_no: Int? = null
    var selectedAddress = ""


    var descriptionData = arrayOf("Clinic Info.","Location","Schedule","Gallery")
    var descriptionDataAr = arrayOf("بيانات العيادة","الموقع","المواعيد","الصور")
    var descriptionDataNew = arrayOf("Clinic Info.","Location","Gallery")
    var descriptionDataNewAr = arrayOf("بيانات العيادة","الموقع","الصور")
    private lateinit var loading: Dialog
    override val screenViewModel: SetupClinicViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_Clinic_VIEW_MODEL))
    val screenViewModels: SetupProfileViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_VIEW_MODEL))
    private lateinit var viewModel: NewClinicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            english_name = it.getString("english", "")
            arabicname = it.getString("arabic", "")
            emailID = it.getString("email", "")
            phoneno1 = it.getString("phone1", "")
            phoneno2 = it.getString("phone2", "")
            phoneno3 = it.getString("phone3", "")
            service = it.getString("service", "")
            addNewClinic = it.getInt("add_new_clinic", 1)
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedDataViewmodel.healthEntityId.observe(viewLifecycleOwner, Observer {
            polyInfoId=it.toString()
        })

        if (!DateUtils.getAdminOwnerId().isNullOrEmpty()){
            binding.scheduleRb.visibility=View.GONE
            binding.vactionRb.visibility=View.GONE
        }else{
            binding.scheduleRb.visibility=View.VISIBLE
            binding.vactionRb.visibility=View.VISIBLE
        }

        mContext = binding.root.context
        viewModel = ViewModelProvider(this).get(NewClinicViewModel::class.java)
        navController = Navigation.findNavController(binding.root)
        citiesAdapter = CitiesAdapter(false, this, mContext)
        areasAdapter = AreasAdapter(false, this, mContext)
        countriesAdapter = CountriesAdapter(false, this, mContext)
        binding.tvEditClinicstxt.setOnClickListener(this)
        binding.areaSelect.setOnClickListener(this)
        if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
            binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
        }else{
            binding.polyYourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
        }
        loadingDialog = LoadingDialog(mContext)

        if (addNewClinic == 0) {
            binding.llRadioGroup.visibility = View.GONE
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.visibility = View.VISIBLE
            }else{
                binding.polyYourStateProgressBarId.visibility = View.VISIBLE
            }
            binding.tvClinicprofile.visibility = View.VISIBLE
            binding.saveedit.visibility = View.GONE
         //   binding.help2.visibility = View.VISIBLE
            binding.tvEditClinicstxt.visibility = View.GONE
            binding.submitlocation.visibility = View.VISIBLE
        }else{
            if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
                binding.yourStateProgressBarId.visibility = View.GONE
            }else{
                binding.polyYourStateProgressBarId.visibility = View.GONE
            }
        }
//        else{
//            binding.tvEditClinicstxt.text = name
//        }
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
        binding.help2.setOnClickListener(this)
        binding.submitlocation.setOnClickListener(this)
        binding.citySelect.setOnClickListener(this)
        binding.pickLocation.setOnClickListener(this)
        binding.submitlocation.setOnClickListener(this)
        binding.tvEditClinicstxt.setOnClickListener(this)
        initObserve()
        initData()
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }
        validity()

    }

    private fun initData() {
        if (editLabel == "edit") {
            binding.citySelect.text = cityName
            binding.areaSelect.text = areaName

            bindAreas()
            if (Localization.getLanguageName()=="ar"){
                binding.nationalitySelect.text = "مصر"
            }else{
                binding.nationalitySelect.text = "Egypt"
            }
            binding.streetEt.setText(address)
            binding.buildingEt.setText(building_no.toString())
            binding.floorEt.setText(floor_no.toString())
            binding.apartmentEt.setText(appartment.toString())

            binding.submitlocation.setText(getString(R.string.update))
            binding.submitlocation.visibility = View.VISIBLE

            binding.saveedit.setOnClickListener {
                checkValidation()
            }
        }else{
            binding.submitlocation.visibility = View.VISIBLE
            binding.submitlocation.text = mContext.getString(R.string.next)

        }
    }

    private fun initObserve() {
        if (isOnline(mContext)) {
            screenViewModel.getCities("1")
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }
        if (isOnline(mContext)) {
            screenViewModels.getCountries()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        screenViewModels.countriesResponse.observe(viewLifecycleOwner) {
            countriesList.clear()
            it.Data.forEach { item ->
                countriesList.add(item)
            }
            countriesAdapter.submitData(countriesList)
        }

        screenViewModel.citiesResponse.observe(viewLifecycleOwner) {
            if (it.Success) {
                val list = ArrayList<CityData>()
                list.clear()
                it.Data.forEach { item ->
                    list.add(item)
                }
                citiesAdapter.submitData(list)
            }
        }
        screenViewModel.locationResponse.observe(viewLifecycleOwner) {
            if (it.Success) {
                loadingDialog.dismiss()
                if (editLabel == "edit") {
                    if(it.MessageCode == 200){
//                        Toast.makeText(requireContext(), "Edit successfully...", Toast.LENGTH_SHORT)
//                            .show()
                    }else if (it.MessageCode == 201){
                        //show the review popup as the account is approved
                        val sucess = UpdateDialog(findNavController(), "")
                        sucess.show(childFragmentManager, "0")
                    }

                } else {
                    val bundle = Bundle()
                    bundle.putInt("add_new_clinic", 0)
                    if (!DateUtils.getAdminOwnerId().isNullOrEmpty()){
                        if (doctorLocation != null)
                        navController.navigate(R.id.createClinicGalleryFragment, bundle)
                    }else{
                        if (doctorLocation != null)
                        navController.navigate(R.id.clinicScheduleFragment, bundle)
                    }
                }

            } else {
                loadingDialog.dismiss()
            }
        }
        binding.infoRb.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("edit", "edit")
            bundle.putInt(CLINIC_ID, sessionManagement.getClinicId()!!.toInt())
            findNavController().navigate(R.id.addNewClinicFragment, bundle)
        }

//        binding.locationRb.setOnClickListener {
////            Toast.makeText(requireContext(), "location", Toast.LENGTH_SHORT).show()
//            val bundle = Bundle()
//            bundle.putString("edit", "edit")
//            findNavController().navigate(R.id.locationFragment)
//        }

        binding.locationRb.setOnClickListener{

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
//            Toast.makeText(requireContext(), "schedule", Toast.LENGTH_SHORT).show()
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
//            Toast.makeText(requireContext(), "vacation", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.vacationClinicFragment, bundle)

        }

        binding.galleryRb.setOnClickListener {
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
//            Toast.makeText(requireContext(), "vahhhation", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.createClinicGalleryFragment, bundle)
        }
        binding.mcardNationalityLayout.setOnClickListener {
            val d = NationalitiesBottomSheetDialog(countriesAdapter, countriesList)
            d.show(childFragmentManager, "")
        }

    }


    override fun onAreSelect(areaId: Int, areaTxt: String, position: Int) {
        binding.areaSelect.text = areaTxt
        areaName = areaTxt
        this.areaId = areaId
    }

    override fun onCitySelect(cityId: Int, cityTxt: String, position: Int) {
        if (nationalityId==1){
            binding.citySelect.text = cityTxt
            cityName = cityTxt
            binding.areaSelect.text = ""
            this.cityId = cityId

            //TODO 29/11/22
            if (isOnline(mContext)) {
                screenViewModel.getAreas(cityId.toString())
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
            }

            bindAreas()
        }

    }

    fun bindAreas() {


        screenViewModel.areasResponse.observe(viewLifecycleOwner) {

            if (it.Success) {
                if (it.Data != null) {
                    val list = ArrayList<CityData>()
                    list.clear()
                    it.Data.forEach { item ->
                        list.add(item)
                    }
                    areasAdapter.submitData(list)
                } else {
                    toast(mContext, it.Message)
                }
            }

        }


    }


    override fun onClick(p0: View) {
        when (p0!!.id) {
            R.id.tv_editClinicstxt -> {
               findNavController().navigate(R.id.clinicsFragment)
            }
            R.id.submitlocation -> {
                checkValidation()
            }
            R.id.city_select -> {
                if (nationalityId==1) {
                    val cityBottomSheetDialog = CitiesBottomSheetDialog(citiesAdapter)
                    cityBottomSheetDialog.show(childFragmentManager, "")
                }
            }
            R.id.area_select -> {
                if (cityId!=0) {
                    screenViewModel.getAreas(cityId.toString())
                    val areaBottomSheetDialog = AreasBottomSheetDialog(areasAdapter)
                    areaBottomSheetDialog.show(childFragmentManager, "")
                    binding.cityErrorTxt.visibility = View.GONE
                }
            }

            R.id.help2 -> {
                val bottomSheet = HelpBottomSheetFragment()
                bottomSheet.show(parentFragmentManager, "ModalBottomSheet")

            }
            R.id.pick_location -> {
                pickLocation()
            }

        }
    }

    private fun checkValidation() {
        isvalidateFeilds()
        if (binding.streetEt.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please add street", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.citySelect.text.isEmpty()) {
        //    binding.citySelect.error = "City Required"

        } else if (binding.areaSelect.text.isEmpty()) {
          //  binding.areaSelect.error = "Area Required"
        } else if (binding.streetEt.text!!.isEmpty()) {
         //   binding.streetField.error = "Street Required"
        } else if (binding.buildingEt.text!!.isEmpty()) {
         //   binding.buildingField.error = "Building Required"

        } else if (binding.floorEt.text!!.isEmpty()) {
         //   binding.floorField.error = "Floor Required"

        } else if (binding.apartmentEt.text!!.isEmpty()) {
          //  binding.appartmentField.error = "Appartment Required"

        } else if (doctorLocation?.latitude == null && doctorLocation?.longitude == null) {
            Toast.makeText(requireContext(), "please select your location in map...", Toast.LENGTH_SHORT).show()
        } else {
            loadingDialog.show()
            screenViewModel.setLocation(
                street = "",
                clinicId = clinicID.toString(),
                cityId = cityId.toString(),
                areaId = areaId.toString(),
                latitude = doctorLocation!!.latitude.toString(),
                longitude = doctorLocation!!.longitude.toString(),
                floorNumber = binding.floorEt.text.toString(),
                blockNumber = binding.buildingEt.text.toString(),
                countryId = sessionManagement.getCountry().toString(),
                appartmeneNumber = binding.apartmentEt.text.toString(),
                address = binding.streetEt.text.toString(),
                cityName = cityName,
                areaName = areaName
            )
        }
    }


    override fun onCountrySelect(countryId: Int, countryTxt: String, position: Int) {
        nationalityId = countryId
        binding.nationalitySelect.text = countryTxt

        //remove error message
        if (nationalityId != 0) {
           // binding.nationalityErrorTxt.visibility = View.GONE
        }
    }
    private fun pickLocation() {
        val intent = Intent(requireContext(), SelectLocationActivity::class.java)
        startActivityForResult(intent, RequestCode.CLINIC_LOCATION_RC)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.CLINIC_LOCATION_RC) {

                doctorLocation =
                    data!!.extras!!.getSerializable(DOCTOR_LOCATION) as DoctorLocation.Location
                selectedAddress = doctorLocation!!.details
//                binding.pickLocation.setText(doctorLocation!!.details)
                Log.e(TAG, "onActivityResult: farmLocation, $doctorLocation")

            }

        }
    }

    private fun isvalidateFeilds(): Boolean {
        var value = false
        if (binding.nationalitySelect.text?.trim().isNullOrEmpty()) {
            binding.tvCountryNameError.visibility = View.VISIBLE
            binding.mcardNationalityLayout.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.nationalitySelect.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )
        } else {
            binding.tvCountryNameError.visibility = View.GONE
            binding.mcardNationalityLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.nationalitySelect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        if (binding.citySelect.text?.trim().isNullOrEmpty()) {
            binding.cityErrorTxt.visibility = View.VISIBLE
            binding.citySelect.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.citySelect.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )


        } else {
            binding.cityErrorTxt.visibility = View.GONE
            binding.citySelect.setBackgroundResource(R.drawable.bg_edittext)
            binding.citySelect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

        }

        if (binding.areaSelect.text?.trim().isNullOrEmpty()) {
            binding.tvAreaNameError.visibility = View.VISIBLE
            binding.areaSelect.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.areaSelect.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )


        } else {
            binding.tvAreaNameError.visibility = View.GONE
            binding.areaSelect.setBackgroundResource(R.drawable.bg_edittext)
            binding.areaSelect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

        }

        if (binding.buildingEt.text?.trim().isNullOrEmpty()) {
            binding.buildingNumErrorTxt.visibility = View.VISIBLE
            binding.mCardBuildingName.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.streetEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )


        } else {
            binding.buildingNumErrorTxt.visibility = View.GONE
            binding.mCardBuildingName.setBackgroundResource(R.drawable.bg_edittext)
            binding.buildingEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

        }

        if (binding.streetEt.text?.trim().isNullOrEmpty()) {
            binding.streetErrorTxt.visibility = View.VISIBLE
            binding.mCardStreetName.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.streetEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )


        } else {
            binding.streetErrorTxt.visibility = View.GONE
            binding.mCardStreetName.setBackgroundResource(R.drawable.bg_edittext)
            binding.streetEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

        }

        if (binding.floorEt.text?.trim().isNullOrEmpty()) {
            binding.floorErrorTxt.visibility = View.VISIBLE
            binding.mCardFloorName.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.floorEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )


        } else {
            binding.floorErrorTxt.visibility = View.GONE
            binding.mCardFloorName.setBackgroundResource(R.drawable.bg_edittext)
            binding.floorEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

        }

        if (binding.apartmentEt.text?.trim().isNullOrEmpty()) {
            binding.apartmentErrorTxt.visibility = View.VISIBLE
            binding.mCardAppartmentName.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.apartmentEt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_error,
                0
            )


        } else {
            binding.apartmentErrorTxt.visibility = View.GONE
            binding.mCardAppartmentName.setBackgroundResource(R.drawable.bg_edittext)
            binding.apartmentEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
          value=true
        }

        return value
    }
    private fun validity() {

        binding.nationalitySelect.doOnTextChanged { text, start, before, count ->
           if (binding.nationalitySelect.text?.trim().isNullOrEmpty()) {
                binding.tvCountryNameError.visibility = View.VISIBLE
                binding.mcardNationalityLayout.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.nationalitySelect.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )
            } else {
            binding.tvCountryNameError.visibility = View.GONE
            binding.mcardNationalityLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.nationalitySelect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        }


        binding.citySelect.doOnTextChanged { text, start, before, count ->
            if (binding.citySelect.text?.trim().isNullOrEmpty()) {
                binding.cityErrorTxt.visibility = View.VISIBLE
                binding.citySelect.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.citySelect.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )


            } else {
                binding.cityErrorTxt.visibility = View.GONE
                binding.citySelect.setBackgroundResource(R.drawable.bg_edittext)
                binding.citySelect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }

        }

        binding.areaSelect.doOnTextChanged { text, start, before, count ->
            if (binding.areaSelect.text?.trim().isNullOrEmpty()) {
                binding.tvAreaNameError.visibility = View.VISIBLE
                binding.areaSelect.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.areaSelect.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )


            } else {
                binding.tvAreaNameError.visibility = View.GONE
                binding.areaSelect.setBackgroundResource(R.drawable.bg_edittext)
                binding.areaSelect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }
        }
        binding.buildingEt.doOnTextChanged { text, start, before, count ->
            if (binding.buildingEt.text?.trim().isNullOrEmpty()) {
                binding.buildingNumErrorTxt.visibility = View.VISIBLE
                binding.mCardBuildingName.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.streetEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )


            } else {
                binding.buildingNumErrorTxt.visibility = View.GONE
                binding.mCardBuildingName.setBackgroundResource(R.drawable.bg_edittext)
                binding.buildingEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }
        }
        binding.streetEt.doOnTextChanged { text, start, before, count ->
            if (binding.streetEt.text?.trim().isNullOrEmpty()) {
                binding.streetErrorTxt.visibility = View.VISIBLE
                binding.mCardStreetName.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.streetEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )


            } else {
                binding.streetErrorTxt.visibility = View.GONE
                binding.mCardStreetName.setBackgroundResource(R.drawable.bg_edittext)
                binding.streetEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }
        }
        binding.floorEt.doOnTextChanged { text, start, before, count ->
            if (binding.floorEt.text?.trim().isNullOrEmpty()) {
                binding.floorErrorTxt.visibility = View.VISIBLE
                binding.mCardFloorName.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.floorEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )


            } else {
                binding.floorErrorTxt.visibility = View.GONE
                binding.mCardFloorName.setBackgroundResource(R.drawable.bg_edittext)
                binding.floorEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }
        }
        binding.apartmentEt.doOnTextChanged { text, start, before, count ->
            if (binding.apartmentEt.text?.trim().isNullOrEmpty()) {
                binding.apartmentErrorTxt.visibility = View.VISIBLE
                binding.mCardAppartmentName.setBackgroundResource(R.drawable.bg_edittext_error)
                binding.apartmentEt.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_error,
                    0
                )


            } else {
                binding.apartmentErrorTxt.visibility = View.GONE
                binding.mCardAppartmentName.setBackgroundResource(R.drawable.bg_edittext)
                binding.apartmentEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }

    }



}