package de.kordondev.lagermelder.rest.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RestTShirtSizeRequest(
    @field:NotNull(message = "size cannot be missing")
    @field:NotBlank(message = "size name cannot be blank")
    val size: String
)