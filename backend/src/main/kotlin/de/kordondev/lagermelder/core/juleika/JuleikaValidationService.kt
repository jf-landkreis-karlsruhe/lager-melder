package de.kordondev.lagermelder.core.juleika

import java.time.LocalDate

data class JuleikaValidationResult(
    val valid: Boolean,
    val expireDate: LocalDate?,
)

interface JuleikaValidationService {
    fun validate(
        cardNumber: String,
        lastName: String,
    ): JuleikaValidationResult
}
