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
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
    private val httpClient: HttpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build()

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
                    .timeout(TIMEOUT)
                    .GET()
                    .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            logger.info("Juleika validation for card $cardNumber: HTTP ${response.statusCode()}")

            if (response.statusCode() == HttpStatus.OK.value()) {
                toResult(objectMapper.readValue(response.body(), JuleikaApiResponse::class.java), cardNumber)
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

    // without an expire date a leader does not count as having a valid Juleika, so a valid card without one is not confirmed
    internal fun toResult(
        body: JuleikaApiResponse,
        cardNumber: String,
    ): JuleikaValidationResult {
        val expireDate = parseExpireDate(body.validTill)
        if (body.status == "valid" && expireDate == null) {
            logger.warn("Juleika API returned valid card $cardNumber with unknown expire date '${body.validTill}'")
            return INVALID
        }
        return JuleikaValidationResult(valid = body.status == "valid", expireDate = expireDate)
    }

    companion object {
        private val TIMEOUT: Duration = Duration.ofSeconds(10)
        private const val JULEIKA_API_URL = "https://app.juleica.de/api/card-is-valid"
        private val INVALID = JuleikaValidationResult(valid = false, expireDate = null)
        private val GERMAN_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")

        // the API documents no date format, so accept ISO (2026-12-31) and German (31.12.2026) dates
        internal fun parseExpireDate(date: String?): LocalDate? {
            if (date.isNullOrBlank()) return null
            val trimmed = date.trim()
            return listOf(DateTimeFormatter.ISO_LOCAL_DATE, GERMAN_DATE).firstNotNullOfOrNull { format ->
                try {
                    LocalDate.parse(trimmed, format)
                } catch (_: DateTimeParseException) {
                    null
                }
            }
        }
    }
}
