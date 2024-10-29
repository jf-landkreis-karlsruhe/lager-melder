package de.kordondev.lagermelder.rest.model.request

import jakarta.validation.constraints.NotNull

data class RestDepartmentPauseRequest (
    @field:NotNull(message = "pause cannot be missing")
    val paused: Boolean
)