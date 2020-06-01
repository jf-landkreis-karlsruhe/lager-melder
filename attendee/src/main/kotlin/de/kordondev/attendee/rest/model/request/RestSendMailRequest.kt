package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.SendTo

data class RestSendMailRequest (
        val sendTo: SendTo
)
