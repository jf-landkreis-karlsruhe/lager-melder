package de.kordondev.lagermelder.core.juleika

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["application.juleika.use-mock"], havingValue = "true")
class MockJuleikaValidationService : JuleikaValidationService {

    private val logger: Logger = LoggerFactory.getLogger(MockJuleikaValidationService::class.java)

    override fun validate(cardNumber: String, lastName: String): JuleikaValidationResult {
        val valid = cardNumber.isNotBlank() && lastName.isNotBlank() && cardNumber.endsWith("0")
        val expireDate = if (valid) "08.12.2025" else null
        logger.info("Mock Juleika validation for card $cardNumber / lastName $lastName → valid=$valid expireDate=$expireDate")
        return JuleikaValidationResult(valid = valid, expireDate = expireDate)
    }
}
