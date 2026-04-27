package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.R
import com.salamtak.databinding.BottomSheetFilterBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.domain.entities.resquest.auth.FilterModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.filter.FilterViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicFilterAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicTypesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import java.util.*
import kotlin.collections.ArrayList

class FilterBottomSheetDialog   (
//    val clinicAdapter: ClinicFilterAdapter,
//    val clinicTypeAdapter: ClinicTypesAdapter,
    val filterViewModel: FilterViewModel,
    val clinicsTypeViewModel: SetupServicesViewModel,
    val clinicsViewModel: SetupClinicViewModel,
    val navController: NavController
//    , val clinicTypelId : Int
) : BottomSheetDialogFragment(), RecyclerViewOnClickListener, DialogsListener{

    private lateinit var binding: BottomSheetFilterBinding
    private lateinit var mContext: Context
    var clinicTypelId : Int ?= null
    var clinicId = 0
    var fromDate = ""
    var toDate = ""
    var x = ""

    private lateinit var clinicTypeAdapter: ClinicTypesAdapter
    private lateinit var clinicAdapter: ClinicFilterAdapter
    private val birthDateCalendar = Calendar.getInstance()
    private lateinit var filtersViewModel: FilterViewModel
    private lateinit var  noInternetDialog: NoInternetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_filter, container, false)
        clinicTypeAdapter = ClinicTypesAdapter(requireContext(), this)
        clinicAdapter = ClinicFilterAdapter(this)
        filtersViewModel = ViewModelProvider(this).get(FilterViewModel::class.java)
        mContext = requireContext()
        if(getClinicType() != null){
            getClinicType()
            binding.staticsRv.layoutManager = LinearLayoutManager(mContext)
//            binding.staticsRv.isNestedScrollingEnabled = true;
            binding.staticsRv.adapter = clinicTypeAdapter
        }
        if(getClinics() !=null){
            getClinics()
    //        binding.choicesRv.layoutManager = LinearLayoutManager(mContext)
//            binding.choicesRv.isNestedScrollingEnabled = true;
           // binding.choicesRv.adapter = clinicAdapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext,this)

        setUp()
        initObserve()
    }

    private fun initObserve() {

        filterViewModel.filterResponseModel.observe(viewLifecycleOwner) {
            x = it.Data.Items.toString()
            Log.d(TAG, "initObserve: " + x)
        }

    }

    private fun setUp() {

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog!!.window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog!!.window!!.attributes = wlp

        val bottomSheet = binding.bottomSheetSpinner
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isHideable = false
        dialog!!.setOnShowListener { dialog ->
            val coordinatorLayout = bottomSheet.parent as CoordinatorLayout
            val bottomSheetBehavior: BottomSheetBehavior<*> =
                BottomSheetBehavior.from(binding.bottomSheetSpinner)
            bottomSheetBehavior.peekHeight = bottomSheet.height
            coordinatorLayout.parent.requestLayout()
        }

        binding.chooseLayout.consultationTypeTxt.setOnClickListener {
//            val d = ClinicTypeBottomSheetDialog(clinicTypeAdapter)
//            d.show(childFragmentManager, "")
//            dialog!!.dismiss()
            getClinicType()
            binding.listLayout.headerTitleTxt.setText(R.string.consultation_type)
            binding.chooseLayout.root.visibility = View.GONE
            binding.listLayout.root.visibility = View.VISIBLE
            binding.listLayout.choicesRv.adapter = clinicTypeAdapter
        }

        binding.chooseLayout.datesTxt.setOnClickListener {
//            dialog!!.hide()
//            val d = FilterDateBottomSheetDialog()
//            d.show(childFragmentManager, "")
            binding.chooseLayout.root.visibility = View.GONE
            binding.listLayout.root.visibility = View.GONE
            binding.dateLayout.root.visibility = View.VISIBLE
        }

        binding.chooseLayout.clinicTxt.setOnClickListener {
            getClinics()
            binding.listLayout.headerTitleTxt.setText(R.string.clinics)
            binding.chooseLayout.root.visibility = View.GONE
            binding.listLayout.root.visibility = View.VISIBLE
            binding.listLayout.choicesRv.adapter = clinicAdapter

//            val d = ClinicsBottomSheetDialog(clinicAdapter)
//            d.show(childFragmentManager, "")
//            binding.chooseLayout.descClinicTxt.text = clinicAdapter.getAdditionTexts()
//            dialog!!.dismiss()
        }

        binding.applyBtn1.setOnClickListener {
            filterViewModel.filter(fillFilterRequest(FilterModel()))
//            val bundle = Bundle()
//            bundle.putString("filterResult", x)
//            Log.d(TAG, "mans: " + x)
//            navController.navigate(R.id.appointmentsFragment)
//            initObserve()
            dialog!!.dismiss()

//            filtersViewModel.filterResponseModel.observe(viewLifecycleOwner) {
//                x = it.Data.Items.toString()
//                Log.d(TAG, "initObserve: "+ x)
//            }

//            filterViewModel.filterResponseModel.observe(viewLifecycleOwner) {
////                var x = ArrayList<String>()
//                x = it.Data.Items.toString()
////                val bundle = Bundle()
////                bundle.putString("filterResult", x)
////                navController.navigate(R.id.filterFragment,bundle)
//            }

        }

        //type clinic
        binding.listLayout.choicesRv.layoutManager = LinearLayoutManager(mContext)
//        binding.listLayout.choicesRv.adapter = clinicTypeAdapter

        //clinics name
//        binding.listLayout.choicesRv.adapter = clinicAdapter

        binding.listLayout.submitBtn.setOnClickListener {
            binding.chooseLayout.root.visibility = View.VISIBLE
            binding.listLayout.root.visibility = View.GONE
            binding.dateLayout.root.visibility = View.GONE
//            dialog!!.dismiss()
        }

        binding.dateLayout.doneBtn.setOnClickListener {
            binding.chooseLayout.root.visibility = View.VISIBLE
            binding.listLayout.root.visibility = View.GONE
            binding.dateLayout.root.visibility = View.GONE
//            dialog!!.dismiss()
        }

        binding.dateLayout.fromDateTxt.setOnClickListener {
            openDatePicker()
        }

        binding.dateLayout.toDateTxt.setOnClickListener {
            openToDatePicker()
        }

    }

    private fun fillFilterRequest(filterModel: FilterModel): FilterModel {
//        filterModel.ClinicId = clinicId
        filterModel.MaxResultCount = 10
        filterModel.FromDate = fromDate
        filterModel.ToDate = toDate
        filterModel.MedicalExaminationTypeId = clinicTypelId
        filterModel.PatientName = binding.chooseLayout.patientNameTxt.text.toString()
        filterModel.SkipCount = 0

        return filterModel
    }

    override fun onClinicTypeSelect(clinicId: Int, clinicNameTxt: String, position: Int) {
        Log.d(TAG, "mansourclick: " + clinicTypelId)
        this.clinicTypelId = clinicId
    }


    private fun getClinicType() {
        //TODO 29/11/22
        if(isOnline(mContext))
        {
        clinicsTypeViewModel.getServicesTypes()
        }
        else{
                       noConnectivityDialog.noInternetDialogFragment(requireContext())

        }

        clinicsTypeViewModel.servicesTypesResponse.observe(viewLifecycleOwner) {
            clinicTypeAdapter.submitData(it.Data as ArrayList<ServicesTypesData>)
        }
    }

    private fun getClinics() {

        //TODO 29/11/22
        if(isOnline(mContext)){
            clinicsViewModel.getClinics()
        }
        else{
                       noConnectivityDialog.noInternetDialogFragment(requireContext())

        }

        clinicsViewModel.clinicsResponse.observe(viewLifecycleOwner) {
            clinicAdapter.submitData(it.Data as ArrayList<ClinicData>)
        }
    }

    override fun onClinicSelect(clinicId: Int, clinicNameTxt: String, position: Int) {
        this.clinicId = clinicId
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


    private fun updateBirthDate() {
        binding.dateLayout.fromDateTxt.setText("${birthDateCalendar[Calendar.YEAR]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.DAY_OF_MONTH]}")
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
        if (day >= 10){
            newDay = birthDateCalendar[Calendar.DAY_OF_MONTH].toString()
        }
        if (month >= 10) {
            newMonth = month.toString()
        }

//        this.fromDate =
//            ("${birthDateCalendar[Calendar.YEAR]}-${birthDateCalendar[Calendar.MONTH] + 1}-${birthDateCalendar[Calendar.DAY_OF_MONTH]}")
        this.fromDate = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")
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


    private fun updateToBirthDate() {
        binding.dateLayout.toDateTxt.setText("${birthDateCalendar[Calendar.YEAR]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.DAY_OF_MONTH]}")
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
        if (day >= 10){
            newDay = birthDateCalendar[Calendar.DAY_OF_MONTH].toString()
        }
        if (month >= 10) {
            newMonth = month.toString()
        }

//        this.toDate =
//            ("${birthDateCalendar[Calendar.YEAR]}-${birthDateCalendar[Calendar.MONTH] + 1}-${birthDateCalendar[Calendar.DAY_OF_MONTH]}")
        this.toDate = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")

    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }


}