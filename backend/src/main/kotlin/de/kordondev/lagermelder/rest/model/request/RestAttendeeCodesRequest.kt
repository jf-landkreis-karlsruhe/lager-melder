package de.kordondev.lagermelder.rest.model.request

import jakarta.validation.constraints.NotNull

data class RestAttendeeCodesRequest(
    @field:NotNull(message = "attendeCodes cannot be missing")
    val attendeeCodes: List<String>
)