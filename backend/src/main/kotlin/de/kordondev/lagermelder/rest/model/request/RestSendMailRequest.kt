package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.SendTo
import jakarta.validation.constraints.NotNull

data class RestSendMailRequest(
    @field:NotNull(message = "sendTo cannot be missing")
    val sendTo: SendTo
)
