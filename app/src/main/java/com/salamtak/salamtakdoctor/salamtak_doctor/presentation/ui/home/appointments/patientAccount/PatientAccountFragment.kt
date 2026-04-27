package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.patientAccount

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.databinding.FragmentPatientAccountBinding
import com.salamtak.domain.entities.response.DataAccount
import com.salamtak.domain.entities.response.PatientAccountData
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.domain.entities.response.doctoraccount.DoctorInvoiceRequest
import com.salamtak.domain.entities.response.doctoraccount.Item
import com.salamtak.domain.entities.response.doctoraccount.invoice.AddInvoiceItemRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.EditInvoiceRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.ItemRequest
import com.salamtak.domain.entities.response.doctoraccount.invoice.PatientData
import com.salamtak.domain.entities.resquest.auth.PatientAccountModel
import com.salamtak.salamtakdoctor.adapter.DoctorAccountAdapter
import com.salamtak.salamtakdoctor.adapter.MainDoctorAccountAdapter
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.ServicesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.DoctorAccountViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding.convertDateFormat
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.util.*
import kotlin.collections.ArrayList

class PatientAccountFragment : Fragment(), PatientAccountAdapter.OnEditClickListener,
    PatientAccountAdapter.OnDeleteClickListener, DialogsListener, RecyclerViewOnClickListener,
    MainDoctorAccountAdapter.IAddItems, DoctorAccountAdapter.IUpdate {

    private lateinit var binding: FragmentPatientAccountBinding
    private lateinit var viewModel: PatientAccountViewModel
    private lateinit var doctorAccountviewModel: DoctorAccountViewModel
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetDialog: BottomSheetDialog? = null
    private val doctorAccountAdapter: DoctorAccountAdapter by lazy {
        DoctorAccountAdapter(this)
    }
    private val maindoctorAccountAdapter: MainDoctorAccountAdapter by lazy {
        MainDoctorAccountAdapter(this,true)
    }
    val list = mutableListOf<DataAccount>()
    val listOfDoctorAccount = mutableListOf<Item>()
    val listOfAddItemAccount = mutableListOf<ItemRequest>()
    lateinit var adapter: ServicesAdapter
    lateinit var loading: Dialog
    var patientId: Long = -1
    var accountId: Int = 0
    var invoiceId = 0
    lateinit var mContext: Context
    lateinit var noInternetDialog: NoInternetDialog
    var serviceTypeList = ArrayList<ServicesTypesData>()

    private val birthDateCalendar = Calendar.getInstance()
    var date = ""
    val clinicsTypeViewModel: SetupServicesViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_SERVICE_VIEW_MODEL))

    lateinit var dateEt: TextView
    lateinit var doctorName: EditText
    lateinit var service: TextView
    lateinit var fee: EditText
    lateinit var paid: EditText
    lateinit var btnDone: MaterialButton
    var selectedServiceId = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            patientId = it.getLong(PATIENT_ID, 0)
//            patientId = AppointmentsFragment.patientId
        }
//        patientId = AppointmentsFragment.patientId

        Log.d("getData", "onCreateView Frag: $patientId")
        binding = FragmentPatientAccountBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[PatientAccountViewModel::class.java]
        doctorAccountviewModel = ViewModelProvider(this)[DoctorAccountViewModel::class.java]
        adapter = ServicesAdapter(false, this, requireContext())
        loading = Dialog(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)

        // initObservers()

        createDoctorInvoiceRecylerview()
        initListener()
        initBottomSheetServices()
        initButton()
        callBackGetAllAccount()
        callBackGetAllPatientInvoice()
    }


    private fun createDoctorInvoiceRecylerview() {
        binding.patientDataRv.apply {
            adapter = maindoctorAccountAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun initButton() {
//        binding.floatingActionButton.setOnClickListener {
//            bottomsheetbeahavoir.state =
//                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
//                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
//        }
//        binding.layoutBottomsheetpersistant.btnApply.setOnClickListener {
//            if (listOfDoctorAccount.isNotEmpty()){
//                doctorAccountviewModel.createInvoice(DoctorInvoiceRequest(date,
//                    DateUtils.getDoctorId()?.toInt(),listOfDoctorAccount,patientId))
//            }
//            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
//        }

        binding.floatingActionButton.setOnClickListener {
            list.clear()
            listOfDoctorAccount.clear()
            createBottomSheetDialog(0, "")
        }
    }

    private fun initListener() {
//        binding.saveBtn.setOnClickListener {
//            //TODO 28/11/22
//            if (isOnline(mContext)) {
//                // TODO 23/11/22 13:31
//                if (PatientAccountModel() != null) {
//
//                    viewModel.addPatientAccount(fillRequest(PatientAccountModel()))
//
//                } else {
//                    Toast.makeText(requireActivity(), "data is null", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                noConnectivityDialog.noInternetDialogFragment(requireContext())
//
//            }
//        }
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
        this.date = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")
        //  dateEt.setText("${birthDateCalendar[Calendar.DAY_OF_MONTH]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.YEAR]}")
        dateEt.text = this.date
    }

    private fun fillRequest(patientAccountModel: PatientAccountModel): PatientAccountModel {

//        patientAccountModel.Date = binding.dateEt.text.toString()
        patientAccountModel.Date = date

        // TODO 23/11/22  13:29
        if (!doctorName.text.toString().isNullOrEmpty()) {
            patientAccountModel.DoctorName = doctorName.text.toString()
        }
        if (!dateEt.text.toString().isNullOrEmpty()) {
            patientAccountModel.Date = dateEt.text.toString()
        }
        if (!service.text.toString().isNullOrEmpty()) {
            patientAccountModel.Service = service.text.toString()
        }

        if (!fee.text.toString().isNullOrEmpty()) {
            patientAccountModel.ServiceFee = fee.text.toString().toInt()
        }
        if (!paid.text.toString().isNullOrEmpty()) {
            patientAccountModel.Paid = paid.text.toString().toInt()
        }
        if (!patientId.toString().isNullOrEmpty()) {
            patientAccountModel.PatientId = patientId.toInt()
        }

        Log.d("getData", "fillRequest: ${patientId}")

        patientAccountModel.Isdeleted = false

        return patientAccountModel
    }

    private fun initObservers() {
        //TODO
        if (isOnline(mContext)) {
            viewModel.getPatientAccount(patientId.toInt())
            clinicsTypeViewModel.getServicesTypes()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }



        clinicsTypeViewModel.servicesTypesResponse.observe(viewLifecycleOwner) {
            serviceTypeList.clear()
            serviceTypeList.addAll(it.Data)
            adapter.list = serviceTypeList
            adapter.notifyDataSetChanged()
//            clinicTypeAdapter.submitData(it.Data as ArrayList<ServicesTypesData>)
            Log.d("getData", "in bottomsheet: ${it.Data.size} && ${it.Data[0].Name}")
        }

        viewModel.getPatientAccountResponse.observe(viewLifecycleOwner) {

            binding.patientDataRv.layoutManager = LinearLayoutManager(requireContext())
            binding.patientDataRv.adapter = PatientAccountAdapter(it.Data, this, this)
            Log.d(TAG, "initObservers: ${it.Data}")
        }

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        viewModel.needRefresh.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("getData", "initObservers: Hitting Get API")
                viewModel.getPatientAccount(patientId.toInt())
                selectedServiceId = ""
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null)

                if (it.Success) {
                    viewModel.getPatientAccount(patientId.toInt())
                } else {
                    if (it.Message != null) {
                        errorMessage(it.Message!!)
                    }
                }
            else
                Log.d("resp", "initObservers: it is null")
            loading.dismiss()
        })

    }

    override fun onEditClicked(patientAccountData: PatientAccountData) {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.account_bottom_sheet)

        dateEt = bottomSheetDialog.findViewById<TextView>(R.id.et_date)!!
        //   doctorName = bottomSheetDialog.findViewById<EditText>(R.id.et_doctor)!!
        service = bottomSheetDialog.findViewById<TextView>(R.id.et_service)!!
        fee = bottomSheetDialog.findViewById<EditText>(R.id.et_fee)!!
        paid = bottomSheetDialog.findViewById<EditText>(R.id.et_paid)!!
        btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.done_btn)!!
        val titleName = bottomSheetDialog.findViewById<TextView>(R.id.title_tv)!!
        titleName.text = getString(R.string.editinvoice)
        dateEt.text = convertDateFormat(patientAccountData.Date, "yyyy-MM-dd'T'HH:mm:ss")
        doctorName.setText(patientAccountData.DoctorName)
        service.setText(patientAccountData.Service)
        fee.setText(patientAccountData.ServiceFee.toString())
        paid.setText(patientAccountData.Paid.toString())
        btnDone.text = getString(R.string.edit)
        //  binding.saveBtn.setText(R.string.update)

//        service.setOnClickListener {
//            val serviceBottomSheet = ServicesBottomSheetDialog(adapter,null, arrayListOf())
//            serviceBottomSheet.show(childFragmentManager, "")
//        }

        dateEt.setOnClickListener {
            openDatePicker()
        }

//        binding.dateEt.setText(patientAccountData.Date)
//        binding.doctorNameEt.setText(patientAccountData.DoctorName)
//        binding.serviceEt.setText(patientAccountData.Service)
//        binding.feesEt.setText(patientAccountData.ServiceFee.toString())
//        binding.paidEt.setText(patientAccountData.Paid.toString())

        accountId = patientAccountData.Id

        btnDone!!.setOnClickListener {
            if (isOnline(mContext)) {
                viewModel.editPatientAccount(fillEditRequest(PatientAccountModel()))
                bottomSheetDialog.dismiss()
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())

            }
        }

        bottomSheetDialog.show()
    }

    private fun fillEditRequest(patientAccountModel: PatientAccountModel): PatientAccountModel {

        patientAccountModel.Date = dateEt.text.toString() ?: date
        patientAccountModel.DoctorName = doctorName.text.toString()
        patientAccountModel.Service = service.text.toString()
        patientAccountModel.ServiceFee = fee.text.toString().toInt()
        patientAccountModel.Paid = paid.text.toString().toInt()
        patientAccountModel.PatientId = patientId.toInt()
        patientAccountModel.Id = accountId
        patientAccountModel.Isdeleted = false

        return patientAccountModel
    }

    override fun onDeleteClicked(patientAccountData: PatientAccountData) {
        //TODO
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.cancelappointmentbottomsheet)

        val textView = bottomSheetDialog.findViewById<TextView>(R.id.tv_display)!!
        val yesBtn = bottomSheetDialog.findViewById<MaterialButton>(R.id.yes_btn)!!
        val noBtn = bottomSheetDialog.findViewById<MaterialButton>(R.id.No_button)!!

        textView.text = "Are you sure you want to delete\n" +
                "this "

        yesBtn.setOnClickListener {
            if (isOnline(mContext)) {
                viewModel.deletePatientAccount(patientAccountData.Id)
                bottomSheetDialog.dismiss()
//        findNavController().navigate(R.id.patientAccountFragment)
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
            }
        }
        noBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()


    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    override fun onServiceSelect(serviceId: Int, serviceTxt: String, position: Int) {
        super.onServiceSelect(serviceId, serviceTxt, position)
        if (service != null) {
            service.text = serviceTxt
        }
        selectedServiceId = serviceId.toString()
    }

    private fun initBottomSheetServices() {
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.invoiceBottomsheet)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun callBackGetAllAccount() {

        doctorAccountviewModel.doctorsCountResponse.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.sucess -> {
                        // binding.progressBar.visibility = View.GONE
//                    doctorAccountAdapter.submitList(response.data?.data?.data)
//                    doctorAccountAdapter.notifyDataSetChanged()
//                    response.data?.data?.let { bindViews(it) }
                        callBackGetAllPatientInvoice()
                    }

                    is Resource.Error -> {
                        // binding.progressBar.visibility = View.GONE

                    }

                    is Resource.Loading -> {
                        // binding.progressBar.visibility = View.VISIBLE
                    }
                }
            })

    }

    private fun callBackGetAllPatientInvoice() {

        doctorAccountviewModel.patientCountResponse.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.sucess -> {
                        binding.progressBar.visibility = View.GONE
                        maindoctorAccountAdapter.submitList(response.data?.data?.data)
                        maindoctorAccountAdapter.notifyDataSetChanged()
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE

                    }

                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            })
        doctorAccountviewModel.getInvoicesByPatientId(patientId.toInt())

    }

    override fun onItemClick(invoiceId: Int) {
        this.invoiceId = invoiceId
        list.clear()
        listOfAddItemAccount.clear()
        createBottomSheetDialog(invoiceId, "")
    }

    override fun edit(item: com.salamtak.domain.entities.response.doctoraccount.invoice.Item) {
        list.clear()
        list.add(
            DataAccount(
                item.date,
                false,
                "edit",
                item.id,
                item.paid?.toDouble(),
                "",
                0,
                item.service,
                item.serviceFee?.toDouble()
            )
        )
        createBottomSheetDialog(0, "edit")
    }

    override fun editAllItems(patientData: PatientData,invoiceId: Int) {
        list.clear()
        listOfAddItemAccount.clear()
            patientData.items?.forEach { item ->
                list.add(
                    DataAccount(
                        item?.date,
                        false,
                        "edit",
                        item?.id,
                        item?.paid?.toDouble(),
                        "",
                        0,
                        item?.service,
                        item?.serviceFee?.toDouble()
                    )
                )

        }
        createBottomSheetDialog(invoiceId, "edit")
    }


    private fun createBottomSheetDialog(invoiceId: Int, edit: String) {

        fun createInvoice(rvInvoice: RecyclerView) {
            rvInvoice.apply {
                adapter = doctorAccountAdapter
                layoutManager = LinearLayoutManager(requireContext())
                doctorAccountAdapter.submitList(list)
                doctorAccountAdapter.notifyDataSetChanged()
                setHasFixedSize(true)
            }
        }
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(R.layout.account_bottom_sheet)
        val bottomSheet =
            bottomSheetDialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val layoutParams = bottomSheet?.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.height = 3000
        bottomSheet?.layoutParams = layoutParams

        dateEt = bottomSheetDialog?.findViewById<TextView>(R.id.et_date)!!
        //  doctorName = bottomSheetDialog.findViewById<EditText>(R.id.et_doctor)!!
        service = bottomSheetDialog?.findViewById<EditText>(R.id.et_service)!!
        fee = bottomSheetDialog?.findViewById<EditText>(R.id.et_fee)!!
        paid = bottomSheetDialog?.findViewById<EditText>(R.id.et_paid)!!
        btnDone = bottomSheetDialog?.findViewById<MaterialButton>(R.id.done_btn)!!
        val titleName = bottomSheetDialog?.findViewById<TextView>(R.id.title_tv)!!
        val btnAddInvoice = bottomSheetDialog?.findViewById<Button>(R.id.btn_AddInvoice)!!
        val layout = bottomSheetDialog?.findViewById<ConstraintLayout>(R.id.layout_offerPrice)!!
        val btnAddItem = bottomSheetDialog?.findViewById<Button>(R.id.btn_AddItem)!!
        val rvInvoice = bottomSheetDialog?.findViewById<RecyclerView>(R.id.rvSubItem)!!
        titleName.text = getString(R.string.add_another)
        if (edit == "edit") {
           // btnAddInvoice.visibility = View.GONE
           // btnDone.visibility = View.GONE
            dateEt.setText(
                convertDateFormat(
                    list[0].date,
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-M-d",
                    Locale.US
                )
            )
        }

        createInvoice(rvInvoice)
        bottomSheetDialog?.show()

        dateEt.setOnClickListener {
            openDatePicker()
        }

        //            service.setOnClickListener {
//                val serviceBottomSheet = ServicesBottomSheetDialog(adapter,null, arrayListOf())
//                serviceBottomSheet.show(childFragmentManager, "")
//            }

        btnAddInvoice.setOnClickListener {
            layout.visibility = if (layout.isShown) View.GONE else View.VISIBLE
        }
        btnDone.setOnClickListener {

            if (listOfDoctorAccount.isNotEmpty()) {
                if (invoiceId == 0) {
                    doctorAccountviewModel.createInvoice(
                        DoctorInvoiceRequest(
                            date,
                            DateUtils.getDoctorId()?.toInt(), listOfDoctorAccount, patientId
                        )
                    )
                    bottomSheetDialog?.dismiss()
                } else {
                    doctorAccountviewModel.addInvoiceItems(
                        AddInvoiceItemRequest(
                            invoiceId,
                            listOfAddItemAccount
                        )
                    )
                    bottomSheetDialog?.dismiss()
                }

            }else{
                bottomSheetDialog?.dismiss()
            }

        }

        btnAddItem.setOnClickListener {
            if (isOnline(mContext)) {
                if (date.isEmpty()) {
                    toast(requireContext(), "please select date")
                } else if (service.text.toString().isEmpty()) {
                    toast(requireContext(), "please choose service")
                } else if (fee.text.toString().isEmpty()) {
                    fee.setError("Fees can't be empty")
                } else if (paid.text.toString().isEmpty()) {
                    paid.setError("Paid can't be empty")
                } else {

                    list.add(
                        DataAccount(
                            date,
                            false,
                            "",
                            0,
                            paid.text.toString().toDouble(),
                            "",
                            0,
                            service.text.toString(),
                            fee.text.toString().toDouble()
                        )
                    )
                    Log.e(TAG, "initButton:${list} ")
                    listOfDoctorAccount.add(
                        Item(
                            date,
                            paid.text.toString().toInt(),
                            service.text.toString(),
                            fee.text.toString().toInt()
                        )
                    )
                    listOfAddItemAccount.add(
                        ItemRequest(
                            date,
                            invoiceId,
                            invoiceId,
                            paid.text.toString().toInt(),
                            service.text.toString(),
                            fee.text.toString().toInt()
                        )
                    )
                    service.setText("")
                    fee.setText("")
                    paid.setText("")
                    layout.visibility = View.GONE
                    createInvoice(rvInvoice)
                    //   bottomSheetDialog.dismiss()
                }
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())

            }
        }
    }

    override fun update(item: com.salamtak.domain.entities.response.doctoraccount.invoice.Item) {
        doctorAccountviewModel.editInvoiceItem(
            EditInvoiceRequest(
                item.date,
                item.id,
                item.paid,
                item.service,
                item.serviceFee
            )
        )
        bottomSheetDialog?.dismiss()
    }

    override fun delete(invoiceId: Int) {
        doctorAccountviewModel.deleteInvoiceItem(invoiceId)
        bottomSheetDialog?.dismiss()
    }


}