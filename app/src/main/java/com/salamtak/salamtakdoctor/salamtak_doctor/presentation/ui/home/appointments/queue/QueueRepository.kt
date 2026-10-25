package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.queue

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.response.queue.CancelQueueTicketRequest
import com.salamtak.domain.entities.response.queue.QueueTicketActionRequest

class QueueRepository {

    private val client = RetrofitBuilder().instant

    suspend fun getQueueBoard(clinicId: Int, queueDate: String) =
        client.getQueueBoard(Localization.getLanguageName(), clinicId, queueDate)

    suspend fun completeTicket(ticketId: Int) =
        client.completeQueueTicket(Localization.getLanguageName(), QueueTicketActionRequest(ticketId))

    suspend fun cancelTicket(ticketId: Int) =
        client.cancelQueueTicket(Localization.getLanguageName(), CancelQueueTicketRequest(ticketId))
}
