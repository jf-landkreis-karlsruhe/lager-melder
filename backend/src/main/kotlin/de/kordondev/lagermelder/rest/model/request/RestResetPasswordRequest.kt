package de.kordondev.lagermelder.rest.model.request

import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

class RestResetPasswordRequest(
    @field:Email(message = "username needs to be an email")
    val username: String,
    @field:Pattern(regexp = "^(http://|https://).+", message = "linkAddress needs to be a valid url")
    val linkAddress: String
)