package com.salamtak.salamtakdoctor.statistics

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.fawry.fawrypay.FawrySdk
import com.fawry.fawrypay.interfaces.FawryPreLaunch
import com.fawry.fawrypay.interfaces.FawrySdkCallbacks
import com.fawry.fawrypay.models.*
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.StatisticsfragmentBinding
import com.salamtak.domain.entities.response.GetClincPacakgesData
import com.salamtak.domain.entities.response.GetClincPacakgesResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.hideLoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.showLoadingDialog
import com.salamtak.salamtakdoctor.statistics.model.ClinksAppointements
import com.salamtak.salamtakdoctor.statistics.model.PacakgesDetails
import com.salamtak.salamtakdoctor.statistics.model.PackageStatics
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class StatisticsFragment : Fragment(), StatisticsMonthAdapter.Action, StatisticsPackgeAdapter.PackageAction {


    private lateinit var binding: StatisticsfragmentBinding
    private lateinit var statisticsMonthAdapter: StatisticsMonthAdapter
    private lateinit var statisticsPackgeAdapter: StatisticsPackgeAdapter
    private lateinit var viewModel: InsuranceViewModel
    private lateinit var loading: Dialog
    private lateinit var mContext: Context
    private lateinit var clinksAppointements: ClinksAppointements
    private lateinit var getClincPacakgesResponse:GetClincPacakgesResponse
    private val args: StatisticsFragmentArgs by navArgs()
    private val list= mutableListOf<PacakgesDetails>()
    private var PackageTotal=""

    //Replace this with Testing/Staging/Production URL
    var baseUrl = "https://www.atfawry.com/"

    //customer info
    var customerName = ""
    var customerMobile = ""
    var customerEmail =
        "" //required in saving cards for payment with card tokenization
    var customerProfileId = "2" //required in saving cards for payment with card tokenization

    //merchant info
    val merchantCode = "400000016268"
    val merchantSecretCode = "791b1e13-e4bc-4026-8f89-2476633b6c71"


    val chargeItems = ArrayList<PayableItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loading = Dialog(requireContext())
        binding = StatisticsfragmentBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(InsuranceViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        StatisticsMonthRecylerview()
        StatisticsPackgeRecylerview()
        statiticsByMonth()
        statisticsPackge(0)
        setupSpinner()
        initButton()
        getPackagesCallback()
    }

    private fun initButton() {


        binding.appointments.setOnClickListener {
            val action = args.clincId?.let {
                StatisticsFragmentDirections.actionStatisticsFragmentToAppointmentFragment(
                    it.toString())
            }
            if (action != null) {
                findNavController().navigate(action)
            }
        }
        binding.statistics.setOnClickListener {
            val action = args.clincId?.let {
                StatisticsFragmentDirections.actionStatisticsFragmentSelf(
                    it.toInt())
            }
            if (action != null) {
                findNavController().navigate(action)
            }
        }

        binding.layoutInclinkPatient.setOnClickListener {
            list.clear()
            getClincPacakgesResponse.data.InclinicAppointments.forEach {
                val pacakgesDetails=PacakgesDetails(it.appointmentDate,it.patientName)
                list.add(pacakgesDetails)
            }
            clinksAppointements= ClinksAppointements(binding.tvNumberofPatient1.text.toString(),binding.tvNumberofPatientInpackage1.text.toString(),
                binding.tvNumberofPatient.text.toString(),binding.tvNumberofPatientInpackage.text.toString(),list)
            val action = StatisticsFragmentDirections.actionStatisticsFragmentToStatisticsDialogFragment(false,clinksAppointements,binding.firstDesc.text.toString())
            findNavController().navigate(action)
        }
        binding.layoutTelecom.setOnClickListener {
           list.clear()
            getClincPacakgesResponse.data.teleCommunicationAppointments.forEach {
                val pacakgesDetails=PacakgesDetails(it.appointmentDate,it.patientName)
                list.add(pacakgesDetails)
            }
            clinksAppointements= ClinksAppointements(binding.tvSecondnumberofPatientInpackage.text.toString(),binding.tvSecondNumberofPatient.text.toString(),
                binding.tvNumberofPatient.text.toString(),binding.tvNumberofPatientInpackage.text.toString(),list)
            val action = StatisticsFragmentDirections.actionStatisticsFragmentToStatisticsDialogFragment(true,clinksAppointements,binding.middelDesc.text.toString())
            findNavController().navigate(action)
        }
        binding.layoutInsuranceall.setOnClickListener {
            list.clear()
            clinksAppointements= ClinksAppointements(binding.tvThirdnumberofPatientInpackage.text.toString(),binding.tvThirdnumberofPatient.text.toString(),
                binding.tvNumberofPatient.text.toString(),binding.tvNumberofPatientInpackage.text.toString(),list)
            val action = StatisticsFragmentDirections.actionStatisticsFragmentToStatisticsDialogFragment(true,clinksAppointements,binding.lastDesc.text.toString())
            findNavController().navigate(action)
        }

        binding.tvOtherPackage.setOnClickListener {

            val action = StatisticsFragmentDirections.actionStatisticsFragmentToOtherPackagesFragment(args.clincId)
            findNavController().navigate(action)
        }

        binding.btnPay.setOnClickListener {
            startPayment()
        }


    }

    private fun StatisticsMonthRecylerview() {
        statisticsMonthAdapter = StatisticsMonthAdapter(this)
        binding.rvMonths.apply {
            adapter = statisticsMonthAdapter
            setHasFixedSize(true)
        }
    }

    private fun StatisticsPackgeRecylerview() {
        statisticsPackgeAdapter = StatisticsPackgeAdapter(this)
        binding.rvStaticsPack.apply {
            adapter = statisticsPackgeAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
        }
    }

    private fun statiticsByMonth() {

        val list = mutableListOf<String>()

        list.add("January")
        list.add("February")
        list.add("March")
        list.add("april")
        statisticsMonthAdapter.submitList(list)
        statisticsMonthAdapter.notifyDataSetChanged()

    }


    fun setupSpinner() {
        val typelist = arrayOf(
            getString(R.string.selectmonth),
            getString(R.string.January),
            getString(R.string.February),
            getString(R.string.March),
            getString(R.string.April),
            getString(R.string.May),
            getString(R.string.June),
            getString(R.string.July),
            getString(R.string.August),
            getString(R.string.September),
            getString(R.string.November),
            getString(R.string.October),
            getString(R.string.December),
        )
        val typeadapter = ArrayAdapter(
            requireActivity(),
            R.layout.spinner_item, typelist
        )
        typeadapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.spStatus.adapter = typeadapter
        binding.spStatus.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                if (position > 0) {
                    Toast.makeText(requireContext(), "${typelist[position]}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action

            }
        }
    }

    private fun statisticsPackge(id: Int) {

        val list = mutableListOf<PackageStatics>()

        when (id) {
            1 -> {
                list.add(
                    PackageStatics(
                        1,
                        5,
                        "insurance"
                    )
                )
            }

            2 -> {
                list.add(
                    PackageStatics(
                        1,
                        5,
                        "insurance"
                    )
                )
                list.add(
                    PackageStatics(
                        3,
                        8,
                        "Cash"
                    )
                )
            }

            else -> {

                list.add(
                    PackageStatics(
                        1,
                        5,
                        "insurance"
                    )
                )
                list.add(
                    PackageStatics(
                        3,
                        8,
                        "Cash"
                    )
                )

                list.add(
                    PackageStatics(
                        8,
                        10,
                        "telecom"
                    )
                )
                list.add(
                    PackageStatics(
                        10,
                        15,
                        "credit"
                    )
                )
                list.add(
                    PackageStatics(
                        12,
                        15,
                        "ok"
                    )
                )
            }
        }

        statisticsPackgeAdapter.submitList(list)
        statisticsPackgeAdapter.notifyDataSetChanged()

    }

    override fun onItemClick(id: Int) {
        statisticsPackge(id)
    }

    override fun onItemClickPackage() {
        Toast.makeText(requireContext(), "ok", Toast.LENGTH_SHORT).show()
    }

    private fun getPackagesCallback() {

        val dateFormat: DateFormat = SimpleDateFormat("M")
        val date = Date()
        viewModel.packageModel.observe(viewLifecycleOwner, Observer {

            if (it.data != null) {
                bindDataToView(it.data)
            }
            getClincPacakgesResponse=it

        })
        viewModel.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        viewModel.getClinicPackage(args.clincId, dateFormat.format(date).toInt(), 2023)
    }

    private fun bindDataToView(data: GetClincPacakgesData) {
        binding.name.text = data.Doctorname
        binding.Mobile.text = data.doctorMobile
        binding.specialist.text = data.specName
        binding.city.text = data.cityName
        binding.area.text = data.areaName
        binding.packagetype.text = data.packagename
        binding.packagePrice.text = data.packagePrice.toString()
        binding.Total.text = data.packagePrice.toString()
        binding.Paid.text = "${data.extraInclinicPatientCount}*${data.extraInclinicPatientPrice}"
        binding.Residual.text = data.extraTeleconsultationPrice.toString()
        binding.insurance.text = data.extraInclinicInsurancePrice.toString()
        binding.tvNumberofPatientInpackage1.text = data.packageInclinicPatient.toString()
        binding.tvNumberofPatient1.text = "${data.completeInclinicPatientCount}"
        binding.tvSecondnumberofPatientInpackage.text = data.packageTeleconsultation.toString()
        binding.tvSecondNumberofPatient.text = data.completeTeleconsultation.toString()
        binding.tvThirdnumberofPatient.text = data.completeInclinicInsurance.toString()
        binding.tvThirdnumberofPatientInpackage.text = data.packageInclinicInsurance.toString()
        binding.tvNumberofPatient.text = data.lastMonthInclinicPatientCount.toString()
        val totalwithouttaxes = (getTotal() + (data.extraInclinicPatientCount * data.extraInclinicPatientPrice) +
                (data.extraInclinicInsuranceCount * data.extraInclinicInsurancePrice) + (data.extraTeleconsultationCount * data.extraTeleconsultationPrice))
        val taxes= (totalwithouttaxes * (.14)).roundToInt()
        binding.PackageTotalTaxes.text=taxes.toString()
        PackageTotal=(totalwithouttaxes+taxes).toString()
        binding.PackageTotal.text=PackageTotal
        val billItem = BillItems(
            itemId = "testId",
            description = "",
            quantity = "1",
            price = PackageTotal
        )
        chargeItems.add(billItem)
    }


    private fun startPayment() {
        var lang = FawrySdk.Languages.ARABIC
        if (Localization.getLanguageName() == "en") {
            lang = FawrySdk.Languages.ENGLISH
        }

        FawrySdk.launchAnonymousSDK(
            requireActivity(), lang, baseUrl,
            FawryLaunchModel(
                launchCustomerModel = LaunchCustomerModel(
                    customerName = customerName,
                    customerEmail = customerEmail,
                    customerMobile = customerMobile,
                    customerProfileId = customerProfileId
                ),
                launchMerchantModel = LaunchMerchantModel(
                    merchantCode = merchantCode,
                    secretCode = merchantSecretCode,
                    merchantRefNum = "${System.currentTimeMillis()}"
                ),
                allow3DPayment = true,
                chargeItems = chargeItems,
                skipReceipt = false,
                skipLogin = true,
                payWithCardToken = true,
                authCaptureMode = false
            ),
            object : FawrySdkCallbacks {
                override fun onPreLaunch(onPreLaunch: FawryPreLaunch) {
                    onPreLaunch.onContinue()
                }

                override fun onInit() {

                }

                override fun onSuccess(msg: String, data: Any?) {
                    Log.d("SDK_Team", "$data")
                    //           Toast.makeText(this@MainActivity, "on success ${msg}", Toast.LENGTH_SHORT)
                    //      .show()
                }

                override fun onPaymentCompleted(msg: String, data: Any?) {

//                    Toast.makeText(
//                        requireContext(),
//                        "on payment completed $data",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

                override fun onFailure(error: String) {
//                    Toast.makeText(
//                        requireContext(),
//                        "on failure ${error}",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            })
    }

    fun getTotal(): Int {

        val total = binding.Total.text.toString().toInt()

        return total
    }


}