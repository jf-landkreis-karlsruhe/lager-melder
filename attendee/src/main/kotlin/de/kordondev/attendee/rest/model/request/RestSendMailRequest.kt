package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.SendTo
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestSendMailRequest(
    @field:NotNull(message = "sendTo cannot be missing")
    @field:NotBlank(message = "sendTo cannot be blank")
    val sendTo: SendTo
)
