package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.SendTo
import javax.validation.constraints.NotNull

data class RestSendMailRequest(
    @field:NotNull(message = "sendTo cannot be missing")
    val sendTo: SendTo
)
