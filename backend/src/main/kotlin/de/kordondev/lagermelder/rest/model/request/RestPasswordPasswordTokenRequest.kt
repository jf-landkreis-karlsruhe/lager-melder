package de.kordondev.lagermelder.rest.model.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern


class RestPasswordPasswordTokenRequest(
    @field:Email(message = "username needs to be an email")
    val username: String,
    @field:Pattern(regexp = "^(http://|https://).+", message = "linkAddress needs to be a valid url")
    val linkAddress: String
)