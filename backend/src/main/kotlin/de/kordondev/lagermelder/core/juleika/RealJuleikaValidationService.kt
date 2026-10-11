package de.kordondev.lagermelder.core.juleika

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import tools.jackson.core.JacksonException
import tools.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@JsonIgnoreProperties(ignoreUnknown = true)
data class JuleikaApiResponse(
    val status: String?,
    @JsonProperty("valid_till") val validTill: String?,
)

@Service
@ConditionalOnProperty(name = ["application.juleika.use-mock"], havingValue = "false", matchIfMissing = true)
class RealJuleikaValidationService(
    private val juleikaConfig: JuleikaConfig,
    private val objectMapper: ObjectMapper,
) : JuleikaValidationService {
    private val logger: Logger = LoggerFactory.getLogger(RealJuleikaValidationService::class.java)

    override fun validate(
        cardNumber: String,
        lastName: String,
    ): JuleikaValidationResult =
        if (cardNumber.isBlank() || lastName.isBlank()) {
            INVALID
        } else {
            requestValidation(cardNumber, lastName)
        }

    private fun requestValidation(
        cardNumber: String,
        lastName: String,
    ): JuleikaValidationResult =
        try {
            val uri: URI =
                UriComponentsBuilder
                    .fromUriString(JULEIKA_API_URL)
                    .queryParam("card_number", cardNumber)
                    .queryParam("lastname", lastName)
                    .build()
                    .toUri()

            val request =
                HttpRequest
                    .newBuilder(uri)
                    .header("Authorization", "Bearer ${juleikaConfig.token}")
                    .GET()
                    .build()

            val response =
                HttpClient
                    .newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString())

            logger.info("Juleika validation for card $cardNumber: HTTP ${response.statusCode()}")

            if (response.statusCode() == HttpStatus.OK.value()) {
                val body = objectMapper.readValue(response.body(), JuleikaApiResponse::class.java)
                JuleikaValidationResult(valid = body.status == "valid", expireDate = body.validTill)
            } else {
                INVALID
            }
        } catch (e: IOException) {
            logger.error("Juleika API call failed for card $cardNumber", e)
            INVALID
        } catch (e: JacksonException) {
            logger.error("Juleika API response could not be read for card $cardNumber", e)
            INVALID
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            logger.error("Juleika API call was interrupted for card $cardNumber", e)
            INVALID
        }

    companion object {
        private const val JULEIKA_API_URL = "https://app.juleica.de/api/card-is-valid"
        private val INVALID = JuleikaValidationResult(valid = false, expireDate = null)
    }
}
