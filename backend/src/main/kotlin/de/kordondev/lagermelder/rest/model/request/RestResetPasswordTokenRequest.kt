package de.kordondev.lagermelder.rest.model.request

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class RestResetPasswordTokenRequest(
    @field:NotNull(message = "token needs to be not defined")
    val token: String,
    @field:Size(min = 8, max = 20, message = "password needs to be between 8 and 20 chars long")
    val password: String
)