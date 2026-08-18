package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.kofigyan.stateprogressbar.StateProgressBar
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentSetupServicesBinding
import com.salamtak.domain.entities.GeneralResponse
import com.salamtak.domain.entities.response.DeleteSchduleRequest
import com.salamtak.domain.entities.response.MedicalExaminationDurationData
import com.salamtak.domain.entities.response.ServiceData
import com.salamtak.domain.entities.response.ServiceScheduleData
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.domain.entities.resquest.auth.DoctorServiceSchedule
import com.salamtak.domain.entities.resquest.auth.ScheduleModel
import com.salamtak.salamtakdoctor.ServiceTypeAdapter
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.help.HelpBottomSheetFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters.ServiceDurationsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters.ServicesScheduleAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters.ServicesTypesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.ServicesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SalamtakViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.CLINIC_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.UpdateDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.ScheduleServicesBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.showError
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toast
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import retrofit2.HttpException


class SetupServicesFragment : BaseFragment<SetupServicesViewModel>(R.layout.fragment_setup_services), DialogsListener,
    RecyclerViewOnClickListener, View.OnClickListener,ServicesScheduleAdapter.ItemClick ,ServicesScheduleAdapter.IDeleteSchedule{

    private lateinit var binding: FragmentSetupServicesBinding
    private lateinit var servicesTypeAdapter: ServicesTypesAdapter
    private lateinit var serviceTypeAdapter: ServiceTypeAdapter
    private lateinit var navController: NavController
    private lateinit var mContext: Context
    private lateinit var servicesDurationsAdapter: ServiceDurationsAdapter
    private lateinit var serviceScheduleAdapter: ServicesScheduleAdapter
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var viewmodel: SalamtakViewModel
    val clinicsTypeViewModel: SetupServicesViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_SERVICE_VIEW_MODEL))
    var selectedServiceId = 0
    var dayId: Int? = null
    var durationId: Int? = null
    var workingFrom: String? = null
    var appointmentsData: ArrayList<ServiceScheduleData>? = null
    var workingTo: String? = null
    var fees: Double? = null
    var workDate:String?=null
    lateinit var adapter: ServicesAdapter
    var listServiceTypeData: ArrayList<ServicesTypesData>? = null
    var serviceId: Int? = null
    var followUpFees: Double? = null
    var addNewClinic = 1
    var descriptionData = arrayOf("Clinic Info.","Location","Schedule","Gallery")
    var descriptionDataAr = arrayOf("بيانات العيادة","الموقع","المواعيد","الصور")
    var periodSwitch: Boolean? = null
    var overlapping: Boolean? = null
    var scheduleUpdate: ScheduleModel? = null
    var serviceTypeList = arrayListOf<ServiceData>()
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
    private val sessionManagement: SessionManagement by inject()

    override val screenViewModel: SetupServicesViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_SERVICE_VIEW_MODEL))

    private val TAG = SetupServicesFragment::class.java.simpleName
    var editClinicScedule: String = ""
    var MaxNoOfPatients: Int = 0

    var clinicId: Int? = null
    private var isCreated = false

    private lateinit var noInternetDialog: NoInternetDialog

    companion object{
        var selectedPeriodPosition =0
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        arguments?.let {
            editClinicScedule = it.getString("edit", "")
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

        clinicId = sessionManagement.getClinicId()!!.toString().toInt()
        Log.d(TAG, "onCreateView3: " + clinicId)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_setup_services, container, false)
        viewmodel = ViewModelProvider(this).get(SalamtakViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ServicesAdapter(false, this, requireContext())
        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        servicesTypeAdapter = ServicesTypesAdapter(mContext, this)
        serviceTypeAdapter = ServiceTypeAdapter(false,this, mContext)
        loadingDialog = LoadingDialog(mContext)
        binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)

        if (addNewClinic == 0) {
            binding.stepsRadio.visibility = View.INVISIBLE
            binding.yourStateProgressBarId.visibility = View.VISIBLE
            binding.tvClinicprofile.visibility = View.VISIBLE
            binding.tvSaveedit.visibility = GONE
            binding.help2.visibility = View.VISIBLE
            binding.tvEditClinicstxt.visibility = GONE

        } else {
            binding.submitSchedule.visibility = GONE
        }

        if (Localization.getLanguageName() == "ar") {
            binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.backarabic,
                0
            )
            binding.yourStateProgressBarId.setStateDescriptionData(descriptionDataAr)

        } else {
            binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.group_70328,
                0,
                0,
                0
            )
            binding.yourStateProgressBarId.setStateDescriptionData(descriptionData)
        }
        binding.help2.setOnClickListener(this)
        binding.tvEditClinicstxt.setOnClickListener(this)

        setUi()
        subscribeToObserver()

        if (editClinicScedule.equals("edit")) {
            editClinic()
        }
        binding.submitSchedule.setOnClickListener {
            if (!appointmentsData.isNullOrEmpty()) {
                val totalPeriods =appointmentsData?.sumOf { it.DayPeriodCount } ?: 0
                if (totalPeriods > 0) {
                    val bundle = Bundle()
                    bundle.putInt("add_new_clinic", 0)
                    navController.navigate(R.id.createClinicGalleryFragment, bundle)
                }
                else{
                    toast(mContext, getString(R.string.appointment_required))

                }
            }
            else{
                toast(mContext, getString(R.string.appointment_required))
            }
        }
        binding.tvSaveedit.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("add_new_clinic", 0)
            navController.navigate(R.id.createClinicGalleryFragment, bundle)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (editClinicScedule.equals("edit")) {
                findNavController().navigate(R.id.clinicsFragment)
            } else {
                if (!findNavController().popBackStack()) {
                    findNavController().navigate(R.id.clinicsFragment)
                }
            }
        }
        initButton()

    }

    private fun initButton(){
        binding.tvHelp.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }
    }

    private fun setUi() {

        binding.toolbar.toolbarHeader.text = getString(R.string.services)

        binding.servicesTb.servicesRv.layoutManager = LinearLayoutManager(
            mContext, LinearLayoutManager.VERTICAL, false
        )

        binding.servicesTb.servicesRv.adapter = servicesTypeAdapter
        binding.servicesTb.servicesRv.adapter = serviceTypeAdapter
        servicesDurationsAdapter = ServiceDurationsAdapter(this)

        binding.toolbar.ivBack.setOnClickListener {
            navController.popBackStack()
        }

        getServiceSchedule("", clinicId.toString())

        binding.tvLogout.setOnClickListener {

            sessionManagement.clearUser()
            navController.navigate(R.id.action_logout_3)

        }
        binding.tvLogout.visibility = GONE

        binding.clinicToolbar.clinicHeaderTv.text = getString(R.string.schedule)

        binding.clinicToolbar.oneTv.text = "2"

        binding.profileBottomToolbar.saveBtn.visibility = View.VISIBLE
        binding.profileBottomToolbar.saveBtn.setText(R.string.previous)

        binding.profileBottomToolbar.submitBtn.setOnClickListener {

            if (editClinicScedule.equals("edit")) {
                val sucess = UpdateDialog(findNavController(), "")
                sucess.show(childFragmentManager, "0")
            } else {
                if (isCreated) {
                    navController.navigate(R.id.vacationClinicFragment)
                } else {
                    errorMessage(getString(R.string.schedule_time_required))
                }
            }

        }

        binding.profileBottomToolbar.saveBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clinicToolbar.ivBackBg.setOnClickListener {
            if (editClinicScedule.equals("edit")) {
                findNavController().navigate(R.id.clinicsFragment)
            } else {
                findNavController().popBackStack()
            }
        }

    }

    private fun editClinic() {

//        binding.clinicToolbar.root.visibility = View.VISIBLE
//        binding.clinicToolbar.circularBackground.visibility = View.INVISIBLE
//        binding.tvLogout.visibility = View.GONE
//        binding.clinicToolbar.clinicHeaderTv.text = getString(R.string.schedule)
//        binding.profileBottomToolbar.submitBtn.setText(R.string.update)
//        binding.profileBottomToolbar.saveBtn.visibility = View.GONE

        binding.stepsRadio.visibility = View.VISIBLE

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
            findNavController().navigate(R.id.createClinicGalleryFragment, bundle)
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
        binding.locationRb.setOnClickListener {
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
            findNavController().navigate(R.id.locationFragment, bundle)
        }
    }

    override fun onServiceSelect(serviceId: Int, serviceTxt: String, position: Int) {
        val intent = Intent("onServiceSelect")
        intent.putExtra("serviceId", serviceId)
        intent.putExtra("serviceTxt", serviceTxt)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        selectedServiceId = serviceId
//        getServiceSchedule(serviceId.toString(), clinicId.toString())
        Log.e(TAG, "onServiceSelect: $selectedServiceId $serviceTxt ")
    }

    override fun onScheduleSubmit(
        dayId: Int,
        durationId: Int,
        workingFrom: String,
        workingTo: String,
        fees: Double,
        position: Int,
        serviceId: Int,
        MaxNoOfPatients: Int,
        followUpFees: Double,
        periodSwitch: Boolean,
        overlapping: Boolean,
        workDate: String?
    ) {
        scheduleUpdate = null
        loadingDialog.show()
        this.dayId = dayId
        this.durationId = durationId
        this.workingFrom = workingFrom
        this.workingTo = workingTo
        this.fees = fees
        this.serviceId = serviceId
        this.MaxNoOfPatients = MaxNoOfPatients
        this.followUpFees = followUpFees
        this.periodSwitch = periodSwitch
        this.overlapping = overlapping
        this.workDate=workDate
        selectedPeriodPosition = position

        //TODO 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.createServiceSchedule(
                DoctorServiceSchedule(
                    dayId,
                    workingFrom,
                    workingTo,
                    fees,
                    selectedServiceId,
                    clinicId!!.toInt(),
                    null,
                     MaxNoOfPatients,
                    followUpFees,
                    true,
                    this.overlapping,
                    workDate
                )
            )
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }

    }

    override fun onUpdate(scheduleUpdate: ScheduleModel) {
        this.scheduleUpdate = scheduleUpdate
        this.dayId=scheduleUpdate.DayId
        this.workingFrom=scheduleUpdate.TimeFrom
        this.workingTo=scheduleUpdate.TimeTo
        this.fees=scheduleUpdate.Fees
        this.selectedServiceId=scheduleUpdate.MedicalExaminationTypeId
        this.clinicId=scheduleUpdate.ClinicId
        this.MaxNoOfPatients=scheduleUpdate.MaxNoOfPatients
        this.followUpFees=scheduleUpdate.FollowUp_Fees
        this.overlapping=scheduleUpdate.OverlapApproved
        this.workDate=scheduleUpdate.workDate
        if (isOnline(mContext)) {
            loadingDialog.show()
            screenViewModel.editClinicSchedule(scheduleUpdate)
        } else {
            noInternetDialog = NoInternetDialog(mContext, this)
            noConnectivityDialog.noInternetDialogFragment(mContext)

        }
     //   Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
        super.onUpdate(scheduleUpdate)
    }

    override fun onTypeService() {
        val serviceBottomSheet = ScheduleServicesBottomSheetDialog(adapter, serviceTypeAdapter,
            serviceTypeList
        )
        serviceBottomSheet.show(childFragmentManager, "")
        super.onTypeService()
    }

    private fun subscribeToObserver() {
        //TODO 29/11/22

        if (isOnline(mContext)) {
            screenViewModel.getServicesTypes()
            clinicsTypeViewModel.getServicesTypes()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }

        screenViewModel.servicesTypesResponse.observe(viewLifecycleOwner) {

//            val list = ArrayList<ServicesTypesData>()
//            old for list of 1
//            for (i in 1 until it.Data.size)
//            for (i in 0 until it.Data.size)
//                list.add(it.Data[i])

//            Log.e(TAG, "subscribeToObserver: $list" )
//            servicesTypesAdapter.submitData(list)
            getServiceSchedule("", clinicId.toString())


            servicesTypeAdapter.submitData(it.Data as ArrayList<ServicesTypesData>)
            serviceTypeList.clear()
            for(i in it.Data.indices){
                var data = it.Data.get(i)
                serviceTypeList.add(ServiceData(data.Id, data.Name, data.Image, data.isSelected))
            }
            serviceTypeAdapter.submitData(serviceTypeList)



        }

        //TODO 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getMedicalDurations(selectedServiceId.toString())
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }
        screenViewModel.serviceDurationScheduleResponse.observe(viewLifecycleOwner) {

            if (it.Success) {
                loadingDialog.dismiss()
                servicesDurationsAdapter?.submitData(it.Data as ArrayList<MedicalExaminationDurationData>)

            }
        }

        clinicsTypeViewModel.servicesTypesResponse.observe(viewLifecycleOwner) {
            listServiceTypeData = it.Data
            adapter.list = it.Data
            adapter.notifyDataSetChanged()
            adapter.submitData(it.Data as ArrayList<ServicesTypesData>)
            Log.d("getData", "in bottomsheet: ${it.Data.size} && ${it.Data[0].Name}")
        }

        screenViewModel.createScheduleResponse.observe(viewLifecycleOwner) {
            if (it.Success) {
                loadingDialog.dismiss()
                isCreated = true
                Log.e(TAG, "subscribeToObserver:Schedule ${it.Data.toString()}")
                lifecycleScope.launch{
                    getServiceSchedule("", clinicId.toString())
                }

            }
//            else {
         //   loadingDialog.dismiss()
            if (it.MessageCode == 220) {
                    showDialog(it.Message, it.MessageCode)
                }
            if (it.MessageCode == 201) {
                showDialog("Update will be reviewed within 24-48 hours", it.MessageCode)
            }
//                Log.e(TAG, "subscribeToObserver:Schedule error ${it.Message} ")
//            }

        }
        screenViewModel.editScheduleResponse.observe(viewLifecycleOwner) {

            if (it.Success) {
                    getServiceSchedule(selectedServiceId.toString(), clinicId.toString())
                    loadingDialog.dismiss()
                 //  toast(mContext, mContext.getString(R.string.updated_successfully))
               // Log.e(TAG, "editService: ${it.Message} ")
                if (it.MessageCode == 220) {
                  //  showDialog(it.Message, it.MessageCode)
                    toast(mContext, mContext.getString(R.string.overlapp))
                }
                if (it.MessageCode == 201) {
                    loadingDialog.dismiss()
                    showDialog("Update will be reviewed within 24-48 hours", it.MessageCode)
                }
            } else {
                loadingDialog.dismiss()
                Log.e(TAG, "editService: ${it.Message} ")
            }

        }
        screenViewModel.failure.observe(viewLifecycleOwner) {

            loadingDialog.dismiss()
            when (it) {
                is GeneralResponse.Failure.JSonServerError -> {

                    Log.d("getScheduleError", "subscribeToObserver: ${it.errorCode}")
                    when (it.errorCode) {
                        401 -> {
//                            showDialog()
                            showError(
                                getString(R.string.error_unauthorized)
                            )
                        }

                        400 -> {
                            if (scheduleUpdate == null) {
                                showDialog(it.errorMessage!!, 0)
                            } else {
                                showError(
                                    it.errorMessage!!
                                )
                            }

                        }

                        else -> {
                            Log.e("TAG BaseFragmentss", "onCreate: ${it.errorMessage}  ")
                        }
                    }
                    // handel manual to hide dialog error
                    it.errorCode = 200
                }

                is GeneralResponse.Failure.Error -> {
                    when (it.ex) {
                        is HttpException -> if (canShowNoNetworkDialog) {
                            Log.e("Tag", "${it.ex.localizedMessage} ")
                        }

                        is JsonSyntaxException -> {
                            Log.e("TAGJson BaseFragment123", "onCreate: ${it.ex.printStackTrace()}")
                        }

                        else -> {
                            Log.e(
                                "TAG BaseFragment",
                                "onCreate: ${it.ex.cause?.message}  == ${it.ex.message}"
                            )
                        }
                    }
                }
            }

        }

    }

    //TODO 25-11-2022
    private fun showDialog(title: String, messageCode: Int) {

        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_schedule)
        val dailogCancel = dialog.findViewById<TextView>(R.id.dialog_cancel)
        val dailogOk = dialog.findViewById<TextView>(R.id.dialog_ok)
        val dailogtext1 = dialog.findViewById<TextView>(R.id.dialog_txt_1)
        dailogtext1.text = title
        if (!title.isNullOrEmpty() && messageCode != 220) {
            dailogCancel.visibility = View.VISIBLE
            dailogOk.visibility = View.VISIBLE
            dailogOk.setText(getString(R.string.confirm))
            dailogCancel.setText(getString(R.string.cancel))
            dailogtext1.text = title
        }else if (messageCode == 400){
            dailogCancel.visibility = View.GONE
            dailogOk.visibility = View.VISIBLE
            dailogOk.setText(getString(R.string.ok))
        } else if (messageCode == 220) {
            dailogtext1.text = "${title}Are you sure you want to complete anyway?"
        }

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        dailogCancel.setOnClickListener {
            dialog.dismiss()
        }
        dailogOk.setOnClickListener {
//            dialog.dismiss()
            //TODO 29/11/22
            if (messageCode == 220) {
                this.overlapping=true
                if (isOnline(mContext)) {
                    screenViewModel.createServiceSchedule(
                        DoctorServiceSchedule(
                            dayId!!,
                            workingFrom!!,
                            workingTo!!,
                            fees!!,
                            selectedServiceId,
                            clinicId!!.toInt(),
                            null,
                            MaxNoOfPatients,
                            followUpFees,
                            false,
                             overlapping,
                            workDate
                        )
                    )

                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }
            }

            dialog.dismiss()
        }
    }

    private fun getServiceSchedule(serviceId: String, clinicId: String) {

        serviceScheduleAdapter = servicesDurationsAdapter?.let {
            ServicesScheduleAdapter(
                this,
                this,
                mContext,
                screenViewModel,
                childFragmentManager,
                it,
                viewLifecycleOwner,
                serviceId,
                loadingDialog,
                clinicId,
            )
        }!!


        binding.scheduleRv.layoutManager = LinearLayoutManager(mContext)
        binding.scheduleRv.adapter = serviceScheduleAdapter


        loadingDialog.show()

//        screenViewModel.getServiceSchedule(serviceId)
        // TODO INDER
        if (isOnline(mContext)) {
            screenViewModel.getServiceSchedule(serviceId, clinicId)
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }


        screenViewModel.serviceScheduleResponse.observe(viewLifecycleOwner) {
            if (it.Success) {
                loadingDialog.dismiss()
                Log.d("kjashdkjshdkjshkjashd",it.Data.toString())
                appointmentsData = it.Data as ArrayList<ServiceScheduleData>
                serviceScheduleAdapter.submitData(it.Data as ArrayList<ServiceScheduleData>)
            }

        }

    }

    //TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.tv_editClinicstxt -> {
                findNavController().navigate(R.id.clinicsFragment)
            }

            R.id.help2 -> {
                val bottomSheet = HelpBottomSheetFragment()
                bottomSheet.show(parentFragmentManager, "ModalBottomSheet")
            }

        }
    }
    private fun deleteScheduleCallBack(deleteSchdeule:DeleteSchduleRequest) {

        viewmodel.deleteSchduleResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                hideprogressbar()
                if (it.messageCode==200){
                    Snackbar.make(
                        requireView(),
                        getString(R.string.your_schedule_deleted_successfully),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.setupServicesFragment)
                }
            }
        })
        viewmodel.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        viewmodel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                showprogtessbar()
            else
                hideprogressbar()
        })

        viewmodel.deleteDoctorClinicSchedual(deleteSchdeule)

    }
    override fun OnItemClick() {
        findNavController().navigate(R.id.dailogDatePickerFragment)
    }
    private fun showprogtessbar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideprogressbar() {
      binding.progressBar.visibility = View.GONE
    }

    override fun onItemDeleted(scheduleId: Int) {
        val delSchedule=DeleteSchduleRequest(false,scheduleId)
        deleteScheduleCallBack(delSchedule)
    }


}

