package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.JuleikaService
import de.kordondev.lagermelder.rest.model.RestJuleikaValidation
import de.kordondev.lagermelder.rest.model.request.JuleikaValidationRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class JuleikaController(
    private val juleikaService: JuleikaService,
) {
    @PostMapping("/juleika/validate")
    fun validate(
        @RequestBody request: JuleikaValidationRequest,
    ): RestJuleikaValidation {
        val result = juleikaService.validate(request.cardNumber, request.lastName)
        return RestJuleikaValidation(valid = result.valid, expireDate = result.expireDate?.toString())
    }
}
