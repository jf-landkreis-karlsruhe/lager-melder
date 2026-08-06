package de.kordondev.lagermelder.core.juleika

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
@ConditionalOnProperty(name = ["application.juleika.use-mock"], havingValue = "false", matchIfMissing = true)
class RealJuleikaValidationService(
    private val juleikaConfig: JuleikaConfig,
    private val objectMapper: ObjectMapper
) : JuleikaValidationService {

    private val logger: Logger = LoggerFactory.getLogger(RealJuleikaValidationService::class.java)
    private val JULEIKA_API_URL = "https://app.juleica.de/api/card-is-valid"

    override fun validate(cardNumber: String, lastName: String): JuleikaValidationResult {
        if (cardNumber.isBlank() || lastName.isBlank()) return JuleikaValidationResult(valid = false, expireDate = null)
        return try {
            val uri: URI = UriComponentsBuilder.fromHttpUrl(JULEIKA_API_URL)
                .queryParam("card_number", cardNumber)
                .queryParam("lastname", lastName)
                .build()
                .toUri()

            val request = HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer ${juleikaConfig.token}")
                .GET()
                .build()

            val response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())

            logger.info("Juleika validation for card $cardNumber: HTTP ${response.statusCode()}")

            if (response.statusCode() != 200) {
                return JuleikaValidationResult(valid = false, expireDate = null)
            }

            val body = objectMapper.readTree(response.body())
            val valid = body.path("status")?.asText() == "valid"
            val expireDate = body.path("valid_till").takeIf { !it.isMissingNode && !it.isNull }?.asText()

            JuleikaValidationResult(valid = valid, expireDate = expireDate)
        } catch (e: Exception) {
            logger.error("Juleika API call failed for card $cardNumber: ${e.message}")
            JuleikaValidationResult(valid = false, expireDate = null)
        }
    }
}
