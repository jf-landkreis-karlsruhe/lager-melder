package de.kordondev.lagermelder.core.juleika

data class JuleikaValidationResult(val valid: Boolean, val expireDate: String?)

interface JuleikaValidationService {
    fun validate(cardNumber: String, lastName: String): JuleikaValidationResult
}
