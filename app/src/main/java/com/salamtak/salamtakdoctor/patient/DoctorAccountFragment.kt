package com.salamtak.salamtakdoctor.patient

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.databinding.DoctorAccountfragmentBinding
import com.salamtak.domain.entities.response.doctoraccount.invoice.Data
import com.salamtak.domain.entities.response.doctoraccount.invoice.Item
import com.salamtak.domain.entities.response.doctoraccount.invoice.PatientData
import com.salamtak.salamtakdoctor.adapter.DoctorAccountAdapter
import com.salamtak.salamtakdoctor.adapter.MainDoctorAccountAdapter
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class DoctorAccountFragment : Fragment(),DoctorAccountAdapter.IUpdate,MainDoctorAccountAdapter.IAddItems,
    DialogsListener {

private lateinit var binding:DoctorAccountfragmentBinding
    private lateinit var polyViewModel: PolyClinicsViewModel

    private  val doctorAccountAdapter:DoctorAccountAdapter by lazy {
        DoctorAccountAdapter(this)
    }
    private val maindoctorAccountAdapter: MainDoctorAccountAdapter by lazy {
        MainDoctorAccountAdapter(this,false)
    }
    lateinit var mContext: Context
    private lateinit var noInternetDialog: NoInternetDialog
    var today: String? = null
    var toDate: String? =null
    var patientName: String? =null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DoctorAccountfragmentBinding.inflate(layoutInflater,container,false)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        doctorAccountRecylerview()
        callBackGetAllAccount()
        getSearchDoctorsAccountant()
        initButton()
    }

    private fun initButton(){
        binding.dateTv.setOnClickListener {
            showBottomSheetDialog()
        }
        binding.ivArrow.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.ivDel.setOnClickListener {
            binding.textdate.text=""
            today = null
            toDate=null
            polyViewModel.getAllPatientAccount(dateFrom = today,dateTo = toDate,patientName)
        }
    }

    private fun doctorAccountRecylerview() {
        binding.patientDataRv.apply {
            adapter = maindoctorAccountAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    private fun callBackGetAllAccount(){

        polyViewModel.doctorAccountResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.sucess -> {
                    binding.progressBar.visibility = View.GONE
                    maindoctorAccountAdapter.submitList(response.data?.data?.data)
                    maindoctorAccountAdapter.notifyDataSetChanged()
                    response.data?.data?.let { bindViews(it) }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE

                }

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })

        polyViewModel.getAllPatientAccount()

    }

    private fun bindViews(toTalAccount: Data){
        binding.balanceTv.text=toTalAccount.totalServiceFee.toString()
        binding.paidTv.text=toTalAccount.totalPaid.toString()
        binding.finalTv.text="${toTalAccount.totalServiceFee!! - toTalAccount.totalPaid!!}"

    }

    override fun update(item: Item) {
        Log.e("TAG", "update: ", )
    }

    override fun delete(invoiceId: Int) {
        Log.e("TAG", "update: ", )
    }

    override fun onItemClick(invoiceId: Int) {
        Log.e("TAG", "update: ", )
    }

    override fun edit(item: Item) {
        Log.e("TAG", "update: ", )
    }

    override fun editAllItems(patientData: PatientData,invoiceId: Int) {
        Log.e("TAG", "update: ", )
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_calendar_view)

        val calendar = bottomSheetDialog.findViewById<DateRangeCalendarView>(R.id.calendar)
        val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.done_btn)

        val sdf = SimpleDateFormat("EEE, dd MMM")

        calendar?.setCalendarListener(object : CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {

                Log.d(TAG, "onFirstDateSelected: ${sdf.format(startDate.time)}")
                val sdfFrom = SimpleDateFormat("yyyy-MM-dd")
                if (!sdfFrom.format(startDate.time)
                        .equals(sdfFrom.format(Calendar.getInstance().time))
                ) {
                    if (Calendar.getInstance().time.before(startDate.time)) {
                        toDate = sdfFrom.format(startDate.time)
                        today = sdfFrom.format(Calendar.getInstance().time)
                    } else {
                        toDate = sdfFrom.format(Calendar.getInstance().time)
                        today = sdfFrom.format(startDate.time)
                    }

                } else {
                    toDate = sdfFrom.format(Calendar.getInstance().time)
                    today = sdfFrom.format(Calendar.getInstance().time)
                }

            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                val fromD = sdf.format(startDate.time)
                val toD = sdf.format(endDate.time)
                binding.textdate.text = "$fromD - $toD"
                val sdfFrom = SimpleDateFormat("yyyy-MM-dd")
                today = sdfFrom.format(startDate.time)
                toDate = sdfFrom.format(endDate.time)
            }
        })

        btnDone?.setOnClickListener {
            if (isOnline(mContext)) {
//                viewModel.getHome(fromDate, toDate)
                polyViewModel.getAllPatientAccount(dateFrom = today,dateTo = toDate,patientName)
                bottomSheetDialog.dismiss()
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    private fun getSearchDoctorsAccountant() {
        var job: Job? = null
        binding.search.addTextChangedListener {
            patientName=it.toString()
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                it?.let {
                    if (it.isNotEmpty()) {
                        polyViewModel.getAllPatientAccount(dateFrom = today,dateTo = toDate,patientName)
                    }else{
                        polyViewModel.getAllPatientAccount(dateFrom = today,dateTo = toDate)
                    }
                }
            }
    }}
}