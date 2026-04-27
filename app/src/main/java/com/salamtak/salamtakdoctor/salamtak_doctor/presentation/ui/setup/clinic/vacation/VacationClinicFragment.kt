package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.vacation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentVacationClinicBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.domain.entities.response.VactionData
import com.salamtak.domain.entities.resquest.auth.VactionModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding.convertDateFormat
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.UpdateDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.ClinicsBottomSheetDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.text.SimpleDateFormat
import java.util.*

class VacationClinicFragment : BaseFragment<SetupClinicViewModel>(R.layout.fragment_vacation_clinic),
    DialogsListener,
    RecyclerViewOnClickListener, View.OnClickListener, VacationAdapter.OnEditClickListener,
    VacationAdapter.OnDeleteClickListener, VacationClinicAdapter.onRadio {

    private lateinit var binding: FragmentVacationClinicBinding
    private lateinit var viewModel: VacationViewModel
    lateinit var loading: Dialog
    var clinicId: Int = -1
    var editClinicScedule: String = ""
    private val sessionManagement: SessionManagement by inject()
    private val birthDateCalendar = Calendar.getInstance()
    var fromDate = ""
    var toDate = ""
    private lateinit var mContext: Context
    private lateinit var vacationClinicAdapter: VacationClinicAdapter
    override val screenViewModel: SetupClinicViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_Clinic_VIEW_MODEL))
    var clinicListId: Int = 0
    var vacationId: Int = 0
    var doctorId: Int = 0
   // var dateFrom = ""
   // var vactoionEt = ""
    var area=""
    var vactoionFromEt = ""
    var vactoionToEt = ""
    lateinit var clinicEt : TextView
    var vactoionFromEtD:TextView?=null
    var vactoionToEtD:TextView?=null
    var vactoionFromEtED :TextView?=null
    var vactoionToEtED :TextView?=null
   // var vactoionEt1 = ""
    var vactoionFromEt1 = ""
    var vactoionToEt1 = ""
    var clinicEt1 = ""
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

   // private var clinicData = ArrayList<ClinicData>()

    private lateinit var noInternetDialog: NoInternetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
//            clinicId = it.getLong(PATIENT_ID, 0)
            editClinicScedule = it.getString("edit", "")
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

        clinicId = sessionManagement.getClinicId()!!.toInt()

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_vacation_clinic, container, false)
        viewModel = ViewModelProvider(this).get(VacationViewModel::class.java)
        loading = Dialog(requireContext())

        binding.vactoionFromEt.setOnClickListener {
            openDatePicker(it.context)
        }

        binding.vactoionToEt.setOnClickListener {
            openToDatePicker(it.context)
        }

        mContext = binding.root.context
        vacationClinicAdapter = VacationClinicAdapter(this, mContext, this)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        initObservers()
        initListener()
        if (Localization.getLanguageName()== "ar") {
            binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.backarabic,0)
        } else {
            binding.tvEditClinicstxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.group_70328, 0, 0, 0)

        }
        binding.addNewVactions.setOnClickListener(this)

        binding.clinicToolbar.oneTv.text = "3"

        if (editClinicScedule.equals("edit")) {
            editVacation()
        }

//        Toast.makeText(requireContext(), "${VacationClinicAdapter.clinicName}", Toast.LENGTH_SHORT)
//            .show()

        binding.tvEditClinicstxt.setOnClickListener {
            findNavController().navigate(R.id.clinicsFragment)
        }
        binding.tvSaveedit.setOnClickListener{
            if ( binding.vactoionFromEt.text.toString()
                    .isNotEmpty() && binding.vactoionToEt.text.toString().isNotEmpty()
//                && binding.clinicEt.text.toString().isNotEmpty()
            ) {
              //  var dt = Date.parse(binding.vactoionFromEt.text.toString())
              //  val today = Date()
         //       val now = Date(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/mm/yyyy", Locale.US)
              //  val vactoionFromEt = sdf.parse(binding.vactoionFromEt.text.toString())
              //  val vactoionToEt = sdf.parse(binding.vactoionToEt.text.toString())
              //  val t = sdf.format(today)


//                if (vactoionFromEt.after(now)) {
//                   binding.vactoionFromEt.error="Vacation Date must be future dates"
//                } else if (vactoionToEt.after(now)) {
//                    binding.vactoionToEt.error="Vacation Date must be future dates"
//                }
////                else if (!vactoionToEt.after(vactoionFromEt)) {
////                    binding.vactoionToEt.error="Vacation Date in correct"
////                }
//                else {
                binding.vactoionFromEt.error = null
                binding.vactoionToEt.error = null

                //TODO
                if (isOnline(mContext)) {
                    viewModel.addVaction(fillRequest(VactionModel()))
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }

//                    val sucess = UpdateDialog(findNavController(), "vacationDone")
//                    sucess.show(childFragmentManager, "0")

                //TODO
                if (isOnline(mContext)) {
                    viewModel.getVaction(clinicId)
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }


                binding.vactoionEt.setText("")
                binding.vactoionFromEt.text = ""
                binding.vactoionToEt.text = ""
                binding.clinicEt.text = ""
//                }
            } else {
                errorMessage("Please Complete All Data")
                loading.hideLoadingDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }

    }


    private fun editVacation() {

        binding.stepsRadio.visibility = View.VISIBLE
        binding.clinicToolbar.circularBackground.visibility = View.INVISIBLE

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

    private fun initListener() {

        binding.vactionDataRv.layoutManager = LinearLayoutManager(requireContext())

        binding.profileBottomToolbar.saveBtn.visibility = View.VISIBLE

        binding.profileBottomToolbar.saveBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clinicEt.setOnClickListener(this)

        binding.saveBtn.setOnClickListener {
//            binding.vactoionEt.text.toString()
//                .isNotEmpty() &&
            if ( binding.vactoionFromEt.text.toString()
                    .isNotEmpty() && binding.vactoionToEt.text.toString().isNotEmpty()
//                && binding.clinicEt.text.toString().isNotEmpty()
            ) {
                //var dt = Date.parse(binding.vactoionFromEt.text.toString())
                val today = Date()
              //  val now = Date(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/mm/yyyy", Locale.US)
             //   val vactoionFromEt = sdf.parse(binding.vactoionFromEt.text.toString())
               // val vactoionToEt = sdf.parse(binding.vactoionToEt.text.toString())
              //  val t = sdf.format(today)


//                if (vactoionFromEt.after(now)) {
//                   binding.vactoionFromEt.error="Vacation Date must be future dates"
//                } else if (vactoionToEt.after(now)) {
//                    binding.vactoionToEt.error="Vacation Date must be future dates"
//                }
////                else if (!vactoionToEt.after(vactoionFromEt)) {
////                    binding.vactoionToEt.error="Vacation Date in correct"
////                }
//                else {
                binding.vactoionFromEt.error = null
                binding.vactoionToEt.error = null

                //TODO
                if (isOnline(mContext)) {
                    viewModel.addVaction(fillRequest(VactionModel()))
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }

//                    val sucess = UpdateDialog(findNavController(), "vacationDone")
//                    sucess.show(childFragmentManager, "0")

                //TODO
                if (isOnline(mContext)) {
                    viewModel.getVaction(clinicId)
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }


                binding.vactoionEt.setText("")
                binding.vactoionFromEt.text = ""
                binding.vactoionToEt.text = ""
                binding.clinicEt.text = ""
//                }
            } else {
                errorMessage("Please Complete All Data")
            }
        }

        binding.clinicToolbar.clinicHeaderTv.text = getString(R.string.clinic_vacations)

        binding.clinicToolbar.ivBackBg.setOnClickListener {
            if (editClinicScedule.equals("edit")) {
                findNavController().navigate(R.id.clinicsFragment)
            } else {
                findNavController().popBackStack()
            }
        }

        binding.profileBottomToolbar.submitBtn.setOnClickListener {

            if (editClinicScedule.equals("edit")) {
                val sucess = UpdateDialog(findNavController(), "")
                sucess.show(childFragmentManager, "0")
            } else {
                findNavController().navigate(R.id.createClinicGalleryFragment)
            }
        }

    }

    override fun onClinicSelect(clinicId: Int, clinicTxt: String, position: Int) {
        binding.clinicEt.text = clinicTxt
        this.clinicListId = clinicId
    }

    private fun fillRequest(vactionModel: VactionModel): VactionModel {

        vactionModel.VacationName ="done"
//        vactionModel.FromDate = binding.vactoionFromEt.text.toString()
        vactionModel.FromDate = fromDate
        vactionModel.ToDate = toDate
//        vactionModel.ToDate = binding.vactoionToEt.text.toString()
//        vactionModel.ClinicID = binding.clinicEt.text.toString().toInt()
        vactionModel.ClinicID = sessionManagement.getClinicId()!!.toInt()

        return vactionModel
    }

    private fun initObservers() {

        //TODO
        if (isOnline(mContext)) {
            viewModel.getVaction(clinicId)
            screenViewModel.getClinics()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())


        }


        viewModel.vactionModel.observe(viewLifecycleOwner) {

            if (!it.Data.isNullOrEmpty()){
                binding.vactionDataRv.adapter =
                    VacationAdapter(it.Data as MutableList<VactionData>, this, this)
                vacationClinicAdapter.notifyDataSetChanged()

            }
        }

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
//            binding.vactionDataRv.adapter!!.notifyItemRemoved()
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
//            if (it == null) {
//                it?.Message?.let { it1 -> errorMessage(it1) }
//                Toast.makeText(requireContext(),
//                    getString(R.string.choose_date_from_first),Toast.LENGTH_SHORT).show()
//                loading.hideLoadingDialog()
//            }
            loading.hideLoadingDialog()
        })

        screenViewModel.clinicsResponse.observe(viewLifecycleOwner) {
            if (it.Success) {
                vacationClinicAdapter.submitData(it.Data as ArrayList<ClinicData>)
            } else
                showError(it.Message)
              loading.hideLoadingDialog()
        }
    }


    private fun openDatePicker(context: Context) {

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
//        minDateCalender.set(currentYear - 100, 0, 1)
        minDateCalender.set(currentYear, (Calendar.MONTH) + 1, Calendar.DAY_OF_MONTH)

        datePicker.datePicker.maxDate = maxDateCalender.timeInMillis
        datePicker.datePicker.minDate = minDateCalender.timeInMillis

        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()

    }

    private fun openDailogDatePicker(context: Context) {
        var date=""
        val datePickerDialog = DatePickerDialog(
            context,R.style.DatePickerTheme_Dark, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                date=formattedDate
                vactoionFromEtD?.text=date
                vactoionFromEtED?.text=date
                this.fromDate=date
            },
            birthDateCalendar.get(Calendar.YEAR),
            birthDateCalendar.get(Calendar.MONTH),
            birthDateCalendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()

    }
    private fun openToDailogDatePicker(context: Context) {
        var date=""
        val datePickerDialog = DatePickerDialog(
            context,R.style.DatePickerTheme_Dark, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                date=formattedDate
                vactoionToEtD?.text =date
                vactoionToEtED?.text=date
                this.toDate=date
                diffInDates(this.fromDate, this.toDate)
            },
            birthDateCalendar.get(Calendar.YEAR),
            birthDateCalendar.get(Calendar.MONTH),
            birthDateCalendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }

    private fun openToDatePicker(context: Context) {

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
        datePicker.datePicker.minDate = System.currentTimeMillis()


        datePicker.show()

    }

    private fun updateBirthDate() {
        binding.vactoionFromEt.text = "${birthDateCalendar[Calendar.DAY_OF_MONTH]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.YEAR]}"
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
        this.fromDate = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")

    }

    private fun updateToBirthDate() {
        binding.vactoionToEt.text = "${birthDateCalendar[Calendar.DAY_OF_MONTH]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.YEAR]}"
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
        this.toDate = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")

        diffInDates(this.fromDate, this.toDate)

    }

    fun diffInDates(fDate: String, tDate: String) {
        var from = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fDate)
        var to = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(tDate)

        var diff = to.time - from.time
        val numOfDays = (diff / (1000 * 60 * 60 * 24)).toInt()
        val hours = (diff / (1000 * 60 * 60)).toInt()
        val minutes = (diff / (1000 * 60)).toInt()
        val seconds = (diff / 1000).toInt()

        if (numOfDays < 0) {
            //That means the user didn't selected the same date
            this.toDate = ""
            binding.vactoionToEt.text = ""
            Toast.makeText(requireActivity(), "Vacation Date in correct", Toast.LENGTH_LONG).show()
        }


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onClick(v: View?) {

        when (v?.id) {

//            R.id.clinic_et -> {
//
//                val cityBottomSheetDialog = ClinicsBottomSheetDialog(vacationClinicAdapter)
//                cityBottomSheetDialog.show(childFragmentManager, "")
//
//            }

            R.id.add_new_vactions -> {
//                findNavController().navigate(R.id.addNewClinicFragment)

                val bottomSheetDialog = BottomSheetDialog(requireContext())
                bottomSheetDialog.setContentView(R.layout.add_new_vacation_bottom_sheet)

              //  val vactoionEt = bottomSheetDialog.findViewById<TextInputEditText>(R.id.vactoionEt)!!
                 vactoionFromEtD = bottomSheetDialog.findViewById<TextView>(R.id.vactoionFromEt)
                vactoionToEtD = bottomSheetDialog.findViewById<TextView>(R.id.vactoionToEt)
                clinicEt = bottomSheetDialog.findViewById<TextView>(R.id.clinicEt)!!
                val tvPageTitle = bottomSheetDialog.findViewById<TextView>(R.id.tv_pageTitle)!!


                clinicEt.setOnClickListener {
                    val cityBottomSheetDialog = ClinicsBottomSheetDialog(vacationClinicAdapter)
                    cityBottomSheetDialog.show(childFragmentManager, "")
                }

                // done add
                vactoionFromEtD?.setOnClickListener {
                    openDailogDatePicker(it.context)
                    //    vactoionFromEt.text = fromDate
                }
                vactoionToEtD?.setOnClickListener {
                    openToDailogDatePicker(it.context)
                   // vactoionToEt.text = toDate
                }

                val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.saveVacation_btn)


                btnDone?.setOnClickListener {
                  //  this.vactoionEt = vactoionEt.text.toString()
                    this.vactoionFromEt = vactoionFromEtD?.text.toString()
                    this.vactoionToEt = vactoionToEtD?.text.toString()


//                        viewModel.editVaction(fillEditRequest(VactionModel()))
                    if (vactoionFromEtD?.text.toString()
                            .isNotEmpty() && vactoionToEtD?.text.toString().isNotEmpty()
                    ) {
                        viewModel.addVaction(fillRequest(VactionModel()))

                        val sucess = UpdateDialog(findNavController(), "vacationDone")
                        sucess.show(childFragmentManager, "0")

                        binding.vactoionEt.setText("")
                        binding.vactoionFromEt.text = ""
                        binding.vactoionToEt.text = ""
                        binding.clinicEt.text = ""

                    } else {
                        errorMessage("Please Complete All Data")
                        loading.hideLoadingDialog()
                    }
//                        viewModel.addVaction(VactionModel())
//                        viewModel.getVaction(clinicId)
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()

            }

        }

    }

    override fun onEditClicked(vactionData: VactionData) {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.add_new_vacation_bottom_sheet)

       // val vactoionEt = bottomSheetDialog.findViewById<TextInputEditText>(R.id.vactoionEt)!!
         vactoionFromEtED = bottomSheetDialog.findViewById<TextView>(R.id.vactoionFromEt)!!
         vactoionToEtED = bottomSheetDialog.findViewById<TextView>(R.id.vactoionToEt)!!
          clinicEt = bottomSheetDialog.findViewById<TextView>(R.id.clinicEt)!!
        val tvPageTitle = bottomSheetDialog.findViewById<TextView>(R.id.tv_pageTitle)!!

        val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.saveVacation_btn)


        bottomSheetDialog.show()

        tvPageTitle.setText(R.string.editVacation)
        btnDone?.setText(R.string.update)
      //  vactoionEt.setText(vactionData.VacationName)
        vactoionFromEtED?.text = "${convertDateFormat(
                vactionData.FromDate,
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd"
            )
        }"
        vactoionToEtED?.text = "${convertDateFormat(
            vactionData.ToDate,
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
        )
        }"
        clinicEt.text = vactionData.ClinicName
        vacationId = vactionData.id
        doctorId = vactionData.DoctorID
        clinicListId = vactionData.ClinicID
        this.fromDate= vactoionFromEtED?.text.toString()
        this.toDate= vactoionToEtED?.text.toString()
        vactoionFromEtED?.setOnClickListener {
            openDailogDatePicker(it.context)
   //      vactoionFromEt.text = fromDate
        }
        vactoionToEtED?.setOnClickListener {
            openToDailogDatePicker(it.context)
      // vactoionToEt.text = toDate
        }

        clinicEt.setOnClickListener {
            val cityBottomSheetDialog = ClinicsBottomSheetDialog(vacationClinicAdapter)
            cityBottomSheetDialog.show(childFragmentManager, "")
        }



        btnDone?.setOnClickListener {
            this.clinicEt1 = clinicEt.text.toString()
         //   this.vactoionEt1 = vactoionEt.text.toString()
            this.vactoionFromEt1 = vactoionFromEtED?.text.toString()
            this.vactoionToEt1 = vactoionToEtED?.text.toString()

            if (isOnline(mContext)) {

                if (vactoionFromEtED?.text.toString()
                        .isNotEmpty() && vactoionToEtED?.text.toString().isNotEmpty()
                ) {
                    if (isOnline(mContext)) {
                        viewModel.editVaction(fillEditRequest(VactionModel()))
                        bottomSheetDialog.dismiss()

                    } else {
                        noConnectivityDialog.noInternetDialogFragment(requireContext())
                    }
                } else {

                    bottomSheetDialog.dismiss()
                    loading.hideLoadingDialog()
                }

            }
        }


// todo 01/01/2023 pending
//        fun fillEditRequest(vactionModel: VactionModel): VactionModel {
//
//            vactionModel.VacationName = vactoionEt.text.toString()
//            vactionModel.FromDate = vactoionFromEt.text.toString()
//            vactionModel.ToDate = vactoionToEt.text.toString()
//            vactionModel.ClinicID = clinicListId
//            vactionModel.id = vacationId
//            vactionModel.DoctorID = doctorId
//
//            return vactionModel
//        }

    }

    private fun fillEditRequest(vactionModel: VactionModel): VactionModel {

        vactionModel.VacationName ="done"
        vactionModel.FromDate = vactoionFromEt1
        vactionModel.ToDate = vactoionToEt1
        vactionModel.ClinicID = sessionManagement.getClinicId()!!.toInt()
        vactionModel.id = vacationId
        vactionModel.DoctorID = doctorId

        return vactionModel
    }

    override fun onDeleteClicked(vactionData: VactionData) {

        //TODO
        if (isOnline(mContext)) {
            viewModel.deleteVaction(vactionData.id)
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    override fun onRadioClicked(Id: Int, Area: String, position: Int) {
     //   Toast.makeText(requireContext(), Area, Toast.LENGTH_SHORT).show()
        area=Area
        clinicEt.text = Area
        if(!Area.isNullOrEmpty() && this::clinicEt.isInitialized){
            clinicEt.text = Area
        }
    }



}
