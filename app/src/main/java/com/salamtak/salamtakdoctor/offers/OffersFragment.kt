package com.salamtak.salamtakdoctor.offers

import android.os.Bundle
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.salamtak.R
import com.salamtak.databinding.OffersfragmentBinding
import com.salamtak.domain.entities.response.offers.Offers
import com.salamtak.salamtakdoctor.adapter.CategoryAdapter
import com.salamtak.salamtakdoctor.adapter.OffersAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.OffersViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class OffersFragment : Fragment(),OffersAdapter.SelectOfferId{

   private lateinit var binding:OffersfragmentBinding
   private lateinit var offersAdapter: OffersAdapter
    private lateinit var viewModel: OffersViewModel
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    val now = Calendar.getInstance(TimeZone.getTimeZone("CST"))
    val formatter = SimpleDateFormat("yyyy-M-d", Locale.ENGLISH).apply {
        this.timeZone = TimeZone.getTimeZone("CST")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= OffersfragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
        categoryRecylerview()
        callBackGetOffers()
    }

    private fun initButton(){
        binding.btnApply.setOnClickListener {
            sharedDataViewmodel.getBirthDate(formatter.format(now.time))
            sharedDataViewmodel.getEdndDate(formatter.format(now.time))
            val action=OffersFragmentDirections.actionOffersFragmentToAddOffersFragment(0)
            findNavController().navigate(action)
        }
        binding.ivArrow.setOnClickListener {
            backToMore()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            backToMore()
        }
    }

    private fun backToMore() {
        if (!findNavController().popBackStack()) {
            findNavController().navigate(R.id.moreFragment)
        }
    }

    private fun categoryRecylerview() {
        offersAdapter = OffersAdapter(this)
        binding.rvOffers.apply {
            adapter = offersAdapter
            setHasFixedSize(true)
        }

    }

    private fun callBackGetOffers() {
        viewModel.offersResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.sucess -> {
                    it.data?.data?.let { it1 -> bindViews(it1) }
                    offersAdapter.submitList(it.data?.data)
                    offersAdapter.notifyDataSetChanged()

                }

                is Resource.Error -> {
                }

            }
        })
        viewModel.getDoctorswithOffers()
    }

    private fun bindViews(list:List<Offers>){
        if (!list.isNullOrEmpty()){
            binding.ivOffers.visibility=View.GONE
            binding.tvAddyouroffer.visibility=View.GONE
        }
    }

    override fun onItemSelected(offerId: Int) {
       val action=OffersFragmentDirections.actionOffersFragmentToAddOffersFragment(offerId)
        findNavController().navigate(action)
    }

}