package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.queue

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.FragmentQueueBinding
import com.salamtak.domain.entities.response.queue.QueueBoardData
import com.salamtak.domain.entities.response.queue.QueueTicket
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toast
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class QueueFragment : Fragment(), QueueAdapter.Action {

    private lateinit var binding: FragmentQueueBinding
    private lateinit var viewModel: QueueViewModel
    private lateinit var queueAdapter: QueueAdapter
    private val args: QueueFragmentArgs by navArgs()
    private val queueDate = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQueueBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(QueueViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivArrow.setOnClickListener { backToAppointments() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            backToAppointments()
        }
        binding.cardDate.setOnClickListener { openDatePicker() }
        binding.tvServingClinic.text = args.clinicName.orEmpty()

        queueRecyclerview()
        initObserve()
        bindDate()
        getQueueBoard()
    }

    private fun backToAppointments() {
        if (!findNavController().popBackStack()) {
            findNavController().navigate(R.id.appointmentsFragment)
        }
    }

    private fun queueRecyclerview() {
        queueAdapter = QueueAdapter(args.clinicName.orEmpty(), this)
        binding.rvQueue.adapter = queueAdapter
    }

    private fun initObserve() {
        viewModel.queueBoardResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }

                is Resource.sucess -> {
                    binding.progressBar.visibility = View.GONE
                    showBoard(it.data?.data)
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    it.message?.let { message -> errorMessage(message) }
                }
            }
        }

        viewModel.queueActionResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE

                is Resource.sucess -> {
                    binding.progressBar.visibility = View.GONE
                    it.data?.message?.let { message -> toast(requireContext(), message) }
                    viewModel.queueActionResponse.value = null
                    getQueueBoard()
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    it.message?.let { message -> errorMessage(message) }
                    viewModel.queueActionResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun showBoard(board: QueueBoardData?) {
        binding.tvServingNumber.text = board?.currentlyServingPosition?.toString() ?: "-"
        binding.tvServingPatient.text = board?.currentlyServingPatientName ?: "--"

        val tickets = board?.waitingList.orEmpty().sortedBy { it.ticketPosition }
        queueAdapter.submitList(tickets)
        queueAdapter.currentlyServingTicketId = board?.currentlyServingTicketId
        binding.tvEmpty.visibility = if (tickets.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun getQueueBoard() {
        if (isOnline(requireContext())) {
            val apiDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(queueDate.time)
            viewModel.getQueueBoard(args.clinicId, apiDate)
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }
    }

    private fun bindDate() {
        val locale = Locale(Localization.getLanguageName())
        binding.tvDate.text = SimpleDateFormat("d MMM yyyy", locale).format(queueDate.time)
        binding.tvDay.text = SimpleDateFormat("EEEE", locale).format(queueDate.time)
    }

    private fun openDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                queueDate.set(year, month, dayOfMonth)
                bindDate()
                getQueueBoard()
            },
            queueDate[Calendar.YEAR],
            queueDate[Calendar.MONTH],
            queueDate[Calendar.DAY_OF_MONTH]
        ).show()
    }

    override fun onCompleteClick(ticket: QueueTicket) {
        if (isOnline(requireContext())) {
            viewModel.completeTicket(ticket.id)
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }
    }

    override fun onCancelClick(ticket: QueueTicket) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.queue_cancel_confirm, ticket.patientName.orEmpty()))
            .setPositiveButton(R.string.queue_yes) { _, _ ->
                if (isOnline(requireContext())) {
                    viewModel.cancelTicket(ticket.id)
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }
            }
            .setNegativeButton(R.string.queue_no, null)
            .show()
    }
}
