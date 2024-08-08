package de.kordondev.lagermelder.rest.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RestDeleteTShirtSizeRequest(
    @field:NotNull(message = "size cannot be missing")
    @field:NotBlank(message = "size cannot be blank")
    val size: String,

    @field:NotNull(message = "replacementSize cannot be missing")
    @field:NotBlank(message = "replacementSize cannot be blank")
    val replacementSize: String
)