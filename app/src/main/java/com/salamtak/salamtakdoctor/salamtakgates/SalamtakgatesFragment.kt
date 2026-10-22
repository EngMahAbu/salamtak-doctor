package com.salamtak.salamtakdoctor.salamtakgates

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salamtak.R
import com.salamtak.databinding.SalamtakgatesfragmentBinding
import com.salamtak.domain.entities.Questions
import com.salamtak.domain.entities.response.AreaData
import com.salamtak.domain.entities.response.Blogs
import com.salamtak.domain.entities.response.CitysData
import com.salamtak.salamtakdoctor.adapter.BlogsGatesAdapter
import com.salamtak.salamtakdoctor.adapter.CapVideoAdapter
import com.salamtak.salamtakdoctor.adapter.MedicalHealthAdapter
import com.salamtak.salamtakdoctor.adapter.QuestionsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.Constant
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage
import androidx.activity.addCallback
import androidx.recyclerview.widget.RecyclerView


class SalamtakgatesFragment : Fragment(), BlogsGatesAdapter.Action, MedicalHealthAdapter.Action,QuestionsAdapter.IAddAnswer {

    private lateinit var binding: SalamtakgatesfragmentBinding
    private lateinit var blogsGatesAdapter: BlogsGatesAdapter
    private lateinit var capsVideoAdapter: CapVideoAdapter
    private lateinit var questionsAdapter: QuestionsAdapter
    private lateinit var polyViewModel: PolyClinicsViewModel
    private lateinit var medicalHealthAdapter: MedicalHealthAdapter
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private lateinit var viewModelinsurance: InsuranceViewModel
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    private val args: SalamtakgatesFragmentArgs by navArgs()
    private var cityId = 0
    private var areaId = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = SalamtakgatesfragmentBinding.inflate(layoutInflater, container, false)
        viewModelinsurance = ViewModelProvider(this).get(InsuranceViewModel::class.java)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.homeFragment)
            }
        }
        bindViews()
        bindDataToUi()
        blogsGateRecylerview()
        capsVideoRecylerview()
        questionsRecylerview()
        medicalServicesRecylerview()
        if (args.gateid != 6) {
            getSalamtakGatesCompanyCallback()
        } else {
            getSalamtakHealthEntityCallback()
        }
        initButton()
    }

    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.homeFragment)
            }
        }
        binding.btnEmergencies.setOnClickListener {
            binding.btnEmergencies.setBackgroundResource(R.drawable.bg_button_sellector)
            binding.btnEmergencies.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.btnQandA.setBackgroundResource(R.drawable.bg_help)
            binding.btnQandA.setTextColor(Color.parseColor("#262D70"))
            binding.layoutFilter.visibility = View.VISIBLE
            binding.rvQuestions.visibility=View.GONE
            binding.rvAngles.visibility=View.VISIBLE
            viewModelinsurance.getHealthEntityPagedList(cityId, areaId, 7, 100, 0)
        }

        binding.btnQandA.setOnClickListener {
            binding.btnQandA.setBackgroundResource(R.drawable.bg_button_sellector)
            binding.btnQandA.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.btnEmergencies.setBackgroundResource(R.drawable.bg_help)
            binding.btnEmergencies.setTextColor(Color.parseColor("#262D70"))
             binding.layoutFilter.visibility = View.GONE
            binding.rvAngles.visibility=View.GONE
            binding.rvQuestions.visibility=View.VISIBLE
            callGetQuestions()
        }

        binding.layoutFilter.setOnClickListener {
            bottomsheetbeahavoir.state =
                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
        }
        binding.layoutBottomsheetpersistant.tvReset.setOnClickListener {
            reset()
        }
        binding.layoutBottomsheetpersistant.btnLayoutChooseCity.setOnClickListener {
            val action =
                SalamtakgatesFragmentDirections.actionSalamtakgatesFragmentToDialogBottomSheetFragment("AllCity")
            findNavController().navigate(action)
            it.isEnabled = false
            Handler().postDelayed({
                it.isEnabled = true
            }, 1000)
        }

        binding.layoutBottomsheetpersistant.btnLayoutChooseArea.setOnClickListener {
            val action =
                SalamtakgatesFragmentDirections.actionSalamtakgatesFragmentToDialogBottomSheetFragment("AllArea")
            findNavController().navigate(action)
            it.isEnabled = false
            Handler().postDelayed({
                it.isEnabled = true
            }, 1000)
        }

        binding.layoutBottomsheetpersistant.btnApply.setOnClickListener {
            viewModelinsurance.getHealthEntityPagedList(cityId, areaId, 7, 100, 0)
            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        }


    }
    private fun reset() {
        sharedDataViewmodel.getCity(CitysData(0, "", 0, false, "", ""))
        sharedDataViewmodel.getArea(AreaData(0, ""))
    }
    private fun bindDataToUi() {
        sharedDataViewmodel.getCity.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.layoutBottomsheetpersistant.etChooseCity.setText(it.name)
            cityId = it.id
        })
        sharedDataViewmodel.getArea.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.layoutBottomsheetpersistant.etChooseArea.setText(it.name)
            areaId = it.id
        })
    }
    private fun blogsGateRecylerview() {
        if (!::blogsGatesAdapter.isInitialized) {
            blogsGatesAdapter = BlogsGatesAdapter(this).apply {
                stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }
        if (binding.rvGates.adapter != blogsGatesAdapter) {
            binding.rvGates.apply {
                adapter = blogsGatesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }
    private fun questionsRecylerview() {
        if (!::questionsAdapter.isInitialized) {
            questionsAdapter = QuestionsAdapter(this, true).apply {
                stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }
        if (binding.rvQuestions.adapter != questionsAdapter) {
            binding.rvQuestions.apply {
                adapter = questionsAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun capsVideoRecylerview() {
        if (!::capsVideoAdapter.isInitialized) {
            capsVideoAdapter = CapVideoAdapter().apply {
                stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }
        if (binding.viewPager.adapter != capsVideoAdapter) {
            binding.viewPager.apply {
                adapter = capsVideoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }

    private fun medicalServicesRecylerview() {
        if (!::medicalHealthAdapter.isInitialized) {
            medicalHealthAdapter = MedicalHealthAdapter(this).apply {
                stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }
        if (binding.rvAngles.adapter != medicalHealthAdapter) {
            binding.rvAngles.apply {
                adapter = medicalHealthAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun getSalamtakGatesCompanyCallback() {
        viewModelinsurance.getBlogsResponse.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                blogsGatesAdapter.submitList(it.data)
                updateUi(it.data)
                capsVideoAdapter.submitList(it.data)
                // Keep every capsule's player alive off-screen so scrolling back
                // doesn't rebind it and reload the video
                binding.viewPager.setItemViewCacheSize(it.data?.size ?: 0)

            }
        })

        viewModelinsurance.error.observe(viewLifecycleOwner, Observer {
            if (!it.Message.isNullOrEmpty()) {
                it.let {
                    it.Message
                }?.let { it1 -> errorMessage(it1) }
            }
        })

        viewModelinsurance.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
            //loading.showLoadingDialog()
                showprogtessbar()
            else
            // loading.hideLoadingDialog()
                hideprogressbar()
        })
        if (viewModelinsurance.loadedGateId != args.gateid || viewModelinsurance.getBlogsResponse.value == null) {
            viewModelinsurance.getBlogs(args.gateid)
        }
    }

    private fun getSalamtakHealthEntityCallback() {
        viewModelinsurance.healthEntityResponse.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                medicalHealthAdapter.submitList(it.data!!.items)
            }
        })

        viewModelinsurance.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        viewModelinsurance.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
            //loading.showLoadingDialog()
                showprogtessbar()
            else
            // loading.hideLoadingDialog()
                hideprogressbar()
        })
        if (viewModelinsurance.healthEntityResponse.value == null) {
            viewModelinsurance.getHealthEntityPagedList(1, 1, 7, 40, 0)
        }
    }

    private fun showprogtessbar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideprogressbar() {
        binding.progressBar.visibility = View.GONE

    }

    override fun onItemGatesClick(gatesDetails: GatesDetails) {
        val action = SalamtakgatesFragmentDirections.actionSalamtakgatesFragmentToGatesDetailsFragment(gatesDetails)
        findNavController().navigate(action)
    }

    private fun bindViews() {
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.MedicalServices)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN

        when (args.gateid) {
            Constant.SalamtakGateTrueOrF -> {
                binding.tvHealth.text = getString(R.string.salamtaktrueorfalse)
                binding.rvGates.visibility = View.VISIBLE
                binding.viewPager.visibility = View.GONE
                binding.rvAngles.visibility = View.GONE
                binding.layoutSalamtakAngel.visibility = View.GONE
                binding.layoutFilter.visibility = View.GONE
            }

            Constant.SalamtakGateCare -> {
                binding.tvHealth.text = getString(R.string.salamtak_care)
                binding.rvGates.visibility = View.VISIBLE
                binding.viewPager.visibility = View.GONE
                binding.rvAngles.visibility = View.GONE
                binding.layoutSalamtakAngel.visibility = View.GONE
                binding.layoutFilter.visibility = View.GONE
            }

            Constant.SalamtakGateScoope -> {
                binding.tvHealth.text = getString(R.string.salamatak_scoope)
                binding.rvGates.visibility = View.VISIBLE
                binding.viewPager.visibility = View.GONE
                binding.rvAngles.visibility = View.GONE
                binding.layoutSalamtakAngel.visibility = View.GONE
                binding.layoutFilter.visibility = View.GONE
            }

            Constant.SalamtakGateCap -> {
                binding.tvHealth.text = getString(R.string.salamatk_cap)
                binding.rvGates.visibility = View.GONE
                binding.viewPager.visibility = View.VISIBLE
                binding.rvAngles.visibility = View.GONE
                binding.layoutSalamtakAngel.visibility = View.GONE
                binding.layoutFilter.visibility = View.GONE
            }

            Constant.SalamtakGatePromotions -> {
                binding.tvHealth.text = getString(R.string.salamtak_promotions)
                binding.rvGates.visibility = View.VISIBLE
                binding.viewPager.visibility = View.GONE
                binding.rvAngles.visibility = View.GONE
                binding.layoutSalamtakAngel.visibility = View.GONE
                binding.layoutFilter.visibility = View.GONE
            }

            Constant.SalamtakGateAngleICO -> {
                binding.tvHealth.text = getString(R.string.salamtak_angle)
                binding.rvGates.visibility = View.GONE
                binding.viewPager.visibility = View.GONE
                binding.rvAngles.visibility = View.VISIBLE
                binding.layoutSalamtakAngel.visibility = View.VISIBLE
                binding.layoutFilter.visibility = View.VISIBLE
            }
        }
    }

    private fun updateUi(it: List<Blogs>) {
        if (it.isNullOrEmpty()) {
//            binding.crdView.visibility = View.VISIBLE
//        } else {
//            binding.crdView.visibility = View.GONE

            if (args.gateid == 5) {
                binding.layoutPromotion.visibility = View.VISIBLE
            } else {
                binding.layoutPromotion.visibility = View.GONE
            }
        }
    }

    override fun onItemClick(phone: String) {
        phone?.let {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", it, null))
            this.startActivity(intent)
        }
    }
    private fun callGetQuestions() {

        polyViewModel.questionsUserResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    binding.progressBar.visibility=View.GONE
                    questionsAdapter.submitList(it.data?.data)
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                }

            }
        })

        if (polyViewModel.questionsUserResponse.value == null) {
            polyViewModel.getAllQandAByDoctorId(2)
        }

    }

    override fun addAnswer(questions: Questions) {
        TODO("Not yet implemented")
    }

    override fun cancelAnswer(id: Int?) {
        TODO("Not yet implemented")
    }
}