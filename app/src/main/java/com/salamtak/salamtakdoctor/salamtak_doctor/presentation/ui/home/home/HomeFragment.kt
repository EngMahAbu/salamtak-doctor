package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.ebling.goldenrose.network.ConnectionLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentHomeBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.domain.entities.response.GetMedicalEximationDtos
import com.salamtak.domain.entities.response.poly.ListOfDoctors
import com.salamtak.salamtakdoctor.adapter.GatesAdapter
import com.salamtak.salamtakdoctor.adapter.PollyClinicsDoctorsAdapter
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RequestCode.UNAUTHORIZED
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), HomeAdapter.OnAppointmentClickListener,
    ClinicHomeAdapter.OnClinicClickListener, DialogsListener,PollyClinicsDoctorsAdapter.ISelectDoctor,GatesAdapter.Action{

    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var insuranceViewModel: InsuranceViewModel
    private lateinit var polyclinicsviewModel: PolyClinicsViewModel
    private lateinit var pollyClinicsDoctorsAdapter: PollyClinicsDoctorsAdapter
    lateinit var loading: Dialog
    private val birthDateCalendar = Calendar.getInstance()
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    val now = Calendar.getInstance(TimeZone.getTimeZone("CST"))
    var fromDate:String? = null
    var toDate:String? = null
    var clinicId: Int? = null
    private var doctorID: Int? = null
    private var polyInfoID: Int? = null
    private val sessionManagement: SessionManagement by inject()
    private lateinit var imageList: ArrayList<Int>
    val list = ArrayList<String>()
    lateinit var clinicRV: RecyclerView
    private var clinicData = ArrayList<ClinicData>()
    private var formattedDate = ""
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var gatesAdapter: GatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loading = Dialog(requireContext())
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        polyclinicsviewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
       insuranceViewModel = ViewModelProvider(this).get(InsuranceViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        ImageUtil.loadPicture(binding.profileDoctorImage, UserData.getImage())
        if (Localization.getLanguageName() == "ar") {
            UserData.getUserNameAR()?.let {
                binding.userNameTv.text = it
            }
        } else {
            UserData.getUserName()?.let {
                binding.userNameTv.text = it
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val formatter = SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH).apply {
            this.timeZone = TimeZone.getTimeZone("CST")
        }
        formattedDate = formatter.format(now.time)
        fromDate = formatter.format(now.time)
        toDate== formatter.format(now.time)
        bindViews()
        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        callCheckUser()
        setUi()
        initListner()
        initObserve()
        doctorsRecylerview()
        getSalamtakGatesCompanyCallback()
      //  binding.tabLayout.setupWithViewPager(binding.viewPager)
        initButton()
        initBottomSheetServices()
        callBack()
        callGetQuestions()
    }

    private fun initButton(){
        binding.ivNotification.setOnClickListener {
            findNavController().navigate(R.id.notificationListFragment)
        }
        binding.ivMessage.setOnClickListener {
            findNavController().navigate(R.id.myQuestionsFragment)
        }

        binding.ivDel.setOnClickListener {
            binding.dateTv.text=""
        }

        binding.tvListofDoctors.setOnClickListener {
          callGetDoctorsClinicDayId()
            bottomsheetbeahavoir.state =
                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
        }
        binding.layoutBottomsheetpersistant.btnApply.setOnClickListener {
            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun doctorsRecylerview() {
        pollyClinicsDoctorsAdapter = PollyClinicsDoctorsAdapter(this)
        binding.layoutBottomsheetpersistant.rvClinks.apply {
            adapter = pollyClinicsDoctorsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun initObserve() {

        viewModel.homeModel.observe(viewLifecycleOwner, Observer {

            if (it.Message.isEmpty()) {
                findNavController().navigate(R.id.loginFragment)
            }

            binding.firstCircel.text = it.Data.TodayAppointmentCount.toString()
            binding.lastCircel.text =it.Data.UpComingAppointmentCount.toString()
            binding.middelCircel.text =
                it.Data.TotalAppointmentCount.toString()

            binding.staticsRv.adapter = HomeAdapter(
                it.Data.getMedicalEximationListDtos, this
            )
        })

        ConnectionLiveData(mContext).observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "happen: " + it)
            if (!it) {
//                noInternetDialog.show(childFragmentManager, "")
            }
        })


        viewModel.clinicModel.observe(viewLifecycleOwner, Observer {
            binding.clinicsHomeRv.adapter = ClinicHomeAdapter(it.Data, this)

            clinicData.addAll(it.Data)

            Log.d(TAG, "initObserve: ${it.Data}")
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
//            errorMessage(it.Message)
            if (it.Message != null) {
              //  errorMessage(it.Message!!)
            } else {
                if (it.code == UNAUTHORIZED) {
                    //send to login scren
                    sessionManagement.clearUser()
                    UserData.logout()
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        })
        viewModel.doctorProfileModel.observe(viewLifecycleOwner, Observer {

            DateUtils.setYear(it.Data.Birthday.substring(0,4))
            DateUtils.setDoctorId(it.Data.Id.toString())
            DateUtils.setSpecDoctor(it.Data.SpecialistName)
            if (it.Data.StatusId == 1) {
                binding.viewMsg.visibility = View.GONE
                binding.imageView10.visibility = View.GONE
                binding.tv1.visibility = View.GONE
                binding.tv2.visibility = View.GONE
                //binding.ivMessage.visibility=View.VISIBLE
            } else if (it.Data.StatusId == 4) {
                binding.viewMsg.visibility = View.VISIBLE
                binding.imageView10.visibility = View.VISIBLE
                binding.tv1.visibility = View.VISIBLE
                binding.tv2.visibility = View.VISIBLE
               // binding.ivMessage.visibility=View.GONE
            }
        })

        //todo 29/11/22
        if (isOnline(mContext)) {
            viewModel.getHome(formattedDate,formattedDate,clinicId,doctorID,polyInfoID)
//        viewModel.getHome(fromDate, toDate)
            viewModel.getHomeClinics()
               if (DateUtils.getAdminOwnerId().isNullOrEmpty()){
                   viewModel.getDoctorProfile()
               }
      //      viewModel.getWhatsUpAds()
        //    viewModel.getHomeAds()        video adds
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

//        viewModel.sliderCall().observe(viewLifecycleOwner, Observer {
//            onSliderCall()
//        })

//        list.clear()

        viewModel.homeAdsModel.observe(viewLifecycleOwner, Observer {

        })

//        viewModel.homeAdsModel.observe(viewLifecycleOwner, Observer {
//
//            it.Data.forEach { ddModel ->
//                list.add("https://salamtechapi.azurewebsites.net" + ddModel.VideoLink)
//            }
//
//            Log.d(TAG, "AdsImages: " + list)
//
//            binding.viewPager.adapter =
//               list?.let { SliderAdapter((activity as FragmentActivity), it) }
//
//        })

        imageList = ArrayList(2)
        imageList.add(R.drawable.app_logo)
        imageList.add(R.drawable.ic_new_logo)

        // slider
//        binding.viewPager.adapter = list?.let { SliderAdapter((activity as FragmentActivity), it) }

    }


    private fun onSliderCall() {

        activity?.runOnUiThread(Runnable {
            if (binding.viewPager.currentItem < imageList.size - 1) {
                binding.viewPager.currentItem = binding.viewPager.currentItem + 1
            } else {
                binding.viewPager.currentItem = 0
            }
        })

    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }


    private fun setUi() {
        binding.tvGreeting.text = setGreeting()

//        binding.toolbar.ivBack.setOnClickListener {
//            navController.popBackStack()
//        }
//
//        binding.toolbar.toolbarHeader.text = getString(R.string.home)
//        binding.toolbar.ivBack.visibility = View.GONE
//        binding.toolbar.ivBackBg.visibility = View.GONE
        binding.dateTv.setOnClickListener {
//            openDatePicker()

            showBottomSheetDialog()
        }

        binding.toDateTv.setOnClickListener {
            openToDatePicker()
        }


//        binding.doneTv.setOnClickListener {
//            if (isOnline(mContext)) {
//                viewModel.getHome(fromDate, toDate)
//            } else {
//                noConnectivityDialog.noInternetDialogFragment(requireContext())
//            }
//
//        }

    }

    private fun initListner() {

        binding.staticsRv.layoutManager = GridLayoutManager(requireContext(), 2)

//        val layoutManager = FlexboxLayoutManager(context).apply {
//            justifyContent = JustifyContent.CENTER
//            alignItems = AlignItems.CENTER
//            flexDirection = FlexDirection.ROW
//            flexWrap = FlexWrap.WRAP
//        }

//        val layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
//
//
//        binding.staticsRv.layoutManager = layoutManager

        binding.tvClinics.setOnClickListener {
            showClinicsBottomSheetDialog()
        }


//        binding.clinicsHomeRv.layoutManager = LinearLayoutManager(requireContext(),
//            LinearLayoutManager.HORIZONTAL, false)
    }

    private fun showClinicsBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.clinics_bottom_sheet)

        clinicRV = bottomSheetDialog.findViewById<RecyclerView>(R.id.clinics_home_rv)!!
        val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.done_btn)

        clinicRV.adapter = ClinicHomeAdapter(clinicData, this)

        clinicRV.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        btnDone!!.setOnClickListener {
            callGetDoctorsClinicDayId()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }


    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_calendar_view)

        val calendar = bottomSheetDialog.findViewById<DateRangeCalendarView>(R.id.calendar)
        val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.done_btn)

        val sdf = SimpleDateFormat("EEE, dd MMM")

        calendar?.setCalendarListener(object : CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {
//                Toast.makeText(
//                    requireContext(),
//                    "Start Date: " + startDate.time.toString(),
//                    Toast.LENGTH_SHORT
//                ).show()
                Log.d(TAG, "onFirstDateSelected: ${sdf.format(startDate.time)}")
            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
//                Toast.makeText(
//                    requireContext(),
//                    "Start Date: " + startDate.time.toString() + " End date: " + endDate.time.toString(),
//                    Toast.LENGTH_SHORT
//                ).show()
                val fromD = sdf.format(startDate.time)
                val toD = sdf.format(endDate.time)

                binding.dateTv.text = "${getString(R.string.from)} $fromD - ${getString(R.string.to)} $toD"
                val sdfFrom = SimpleDateFormat("yyyy-MM-dd")
                fromDate = sdfFrom.format(startDate.time)
                toDate = sdfFrom.format(endDate.time)
            }
        })

        btnDone?.setOnClickListener {
            if (isOnline(mContext)) {
                viewModel.getHome(fromDate,toDate,clinicId,doctorID,polyInfoID)
                bottomSheetDialog.dismiss()
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
                bottomSheetDialog.dismiss()
            }

        }
        bottomSheetDialog.show()
    }

    override fun onAppointmentClicked(static: GetMedicalEximationDtos) {
        val bundle = Bundle()
        bundle.putInt(APPOINTMENT_ID, static.MedicalTypeId)
        bundle.putString(TODAY_DATE, fromDate)
        bundle.putString(TO_DATE, toDate)
        clinicId?.let { bundle.putInt(CLINIC_ID, it) }

        findNavController().navigate(R.id.appointmentsFragment, bundle)
    }

    override fun onClinicClicked(clinicData: ClinicData) {
        clinicId = clinicData.ClinicId!!
        if (Localization.getLanguageName()=="ar"){
            binding.tvClinics.text=clinicData.NameAr
        }else{
            binding.tvClinics.text=clinicData.Name
        }
        if (isOnline(mContext)) {
            viewModel.getHome(fromDate,toDate,clinicId,doctorID,polyInfoID)
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

    }

    private fun openDatePicker() {

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                birthDateCalendar[Calendar.YEAR] = year
                birthDateCalendar[Calendar.MONTH] = monthOfYear
                birthDateCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateBirthDate()
            }

        val datePicker = DatePickerDialog(
            requireContext(),
            date,
            birthDateCalendar[Calendar.YEAR],
            birthDateCalendar[Calendar.MONTH],
            birthDateCalendar[Calendar.DAY_OF_MONTH]
        )

        val maxDateCalender = Calendar.getInstance()
        maxDateCalender.set(currentYear, 11, 31)

        val minDateCalender = Calendar.getInstance()
        minDateCalender.set(currentYear - 100, 0, 1)

        datePicker.datePicker.maxDate = maxDateCalender.timeInMillis
        datePicker.datePicker.minDate = minDateCalender.timeInMillis

        datePicker.show()

    }

    private fun openToDatePicker() {

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                birthDateCalendar[Calendar.YEAR] = year
                birthDateCalendar[Calendar.MONTH] = monthOfYear
                birthDateCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateToBirthDate()
            }

        val datePicker = DatePickerDialog(
            requireContext(),
            date,
            birthDateCalendar[Calendar.YEAR],
            birthDateCalendar[Calendar.MONTH],
            birthDateCalendar[Calendar.DAY_OF_MONTH]
        )

        val maxDateCalender = Calendar.getInstance()
        maxDateCalender.set(currentYear, 11, 31)

        val minDateCalender = Calendar.getInstance()
        minDateCalender.set(currentYear - 100, 0, 1)

        datePicker.datePicker.maxDate = maxDateCalender.timeInMillis
        datePicker.datePicker.minDate = minDateCalender.timeInMillis

        datePicker.show()

    }

    private fun updateBirthDate() {
//        binding.dateTv.setText("${birthDateCalendar[Calendar.DAY_OF_MONTH]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.YEAR]}")
        var month = birthDateCalendar[Calendar.MONTH] + 1
        var day = birthDateCalendar[Calendar.DAY_OF_MONTH]
        var newMonth = ""
        var newDay = ""

        if (month < 10) {
            newMonth = "0$month"
        }
        if (day < 10) {
            newDay = "0$day"
        }
        if (day >= 10) {
            newDay = birthDateCalendar[Calendar.DAY_OF_MONTH].toString()
        }
        if (month >= 10) {
            newMonth = month.toString()
        }

//        this.fromDate = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")
    }

    private fun updateToBirthDate() {

        binding.toDateTv.setText("${birthDateCalendar[Calendar.DAY_OF_MONTH]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.YEAR]}")
        var month = birthDateCalendar[Calendar.MONTH] + 1
        var day = birthDateCalendar[Calendar.DAY_OF_MONTH]
        var newMonth = ""
        var newDay = ""

        if (month < 10) {
            newMonth = "0$month"
        }
        if (day < 10) {
            newDay = "0$day"
        }
        if (day >= 10) {
            newDay = birthDateCalendar[Calendar.DAY_OF_MONTH].toString()
        }
        if (month >= 10) {
            newMonth = month.toString()
        }

//        this.toDate = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")
//        viewModel.getHome(fromDate, toDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (loading.isShowing) {
            loading.dismiss()
        }
    }

    fun setGreeting(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c[Calendar.HOUR_OF_DAY]

        var greeting = ""

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = getString(R.string.good_morning)
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = getString(R.string.good_afternoon)
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greeting = getString(R.string.good_evening)
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            greeting = getString(R.string.good_night)
        }
        return greeting
    }

    private fun initBottomSheetServices() {
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.doctorsBottomsheet)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun callGetDoctorsClinicDayId() {
        polyclinicsviewModel.polyClinicDoctorsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility=View.GONE
                    pollyClinicsDoctorsAdapter.submitList(it.data?.data)
                    pollyClinicsDoctorsAdapter.notifyDataSetChanged()

                }

                is Resource.Error -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility=View.GONE
                }

            }
        })
        clinicId?.let { polyclinicsviewModel.getDoctorByClinicId(it) }
    }

    private fun bindViews(){
        healthTopicsRecylerview()
       if (!DateUtils.getAdminOwnerId().isNullOrEmpty()) {
           polyInfoID= DateUtils.getAdminInfoId()?.toInt()
           binding.tvListofDoctors.visibility=View.VISIBLE
           binding.profileDoctorImage.visibility=View.GONE
           binding.descNameTv.visibility=View.GONE
           binding.ivMessage.visibility=View.GONE
       }else{
          // binding.tvClinics.text=DateUtils.getClinicName()
           if (DateUtils.getClinicId().isNullOrEmpty()){
               clinicId=null
           }else{
               clinicId=DateUtils.getClinicId()?.toInt()
           }
       }
    }

    override fun onDoctorSelected(listOfDoctors: ListOfDoctors) {
        this.doctorID=listOfDoctors.id
        viewModel.getHome(formattedDate,formattedDate,null,doctorID,polyInfoID)
        if (DefaultLocaleHelper.getInstance(requireContext()).getCurrentLocale()=="en"){
            binding.tvListofDoctors.text=listOfDoctors.fullName
        }else{
            binding.tvListofDoctors.text=listOfDoctors.fullNameAr
        }
    }
    private fun callBack() {

        insuranceViewModel.notificationsResponse.observe(viewLifecycleOwner, Observer {

            val listIsNotSeen = it.data.filter {
                it.isSeen==false
            }
            if (listIsNotSeen.isEmpty()){
                binding.tvNumberOfNotification.visibility=View.GONE
                binding.ivRoundbg.visibility=View.GONE
            }else{
                binding.tvNumberOfNotification.text= listIsNotSeen.size.toString()
            }

        })


        viewModel.error.observe(viewLifecycleOwner, Observer {
          //  it.Message?.let { it1 -> errorMessage(it1) }
        })


        insuranceViewModel.getUserNotifications()

    }
    private fun callGetQuestions() {

        polyclinicsviewModel.questionsUserResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {

                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    if (it.data?.data.isNullOrEmpty()){
                       // binding.ivMessage.cancelAnimation()
                    }else{
                       // binding.ivMessage.playAnimation()
                    }
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                }

            }
        })

        polyclinicsviewModel.getAllQandAByDoctorId(1)

    }
    private fun callCheckUser() {

        polyclinicsviewModel.checkUserResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {


                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    findNavController().navigate(R.id.loginFragment)
                }

            }
        })

        polyclinicsviewModel.checkUser()

    }
    private fun healthTopicsRecylerview() {
        gatesAdapter = GatesAdapter(this)
        binding.whatsUpRv.apply {
            adapter = gatesAdapter
            layoutManager = GridLayoutManager(requireContext(),3)
            setHasFixedSize(true)
        }
    }
    private fun getSalamtakGatesCompanyCallback() {
        insuranceViewModel.gateResponse.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                gatesAdapter.submitList(it.data)
            }
        })

        insuranceViewModel.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        insuranceViewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
              showprogtessbar()
            else
                hideprogressbar()
        })
        insuranceViewModel.getSalamtakGate()
    }
    private fun showprogtessbar() {
        binding.progressBar.visibility = View.GONE
    }
    private fun hideprogressbar() {
        binding.progressBar.visibility = View.GONE

    }

    override fun onItemGatesClick(gateId: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToSalamtakgatesFragment(gateId)
        findNavController().navigate(action)
    }

}