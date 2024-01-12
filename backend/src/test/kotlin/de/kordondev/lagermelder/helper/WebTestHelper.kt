package de.kordondev.lagermelder.helper

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.nio.charset.Charset
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class WebTestHelper(
    private val objectMapper: ObjectMapper
) {
    companion object {

        val CONTENT_TYPE_JSON = MediaType(
            MediaType.APPLICATION_JSON.type,
            MediaType.APPLICATION_JSON.subtype,
            Charset.forName("utf8")
        )
    }

    fun toJSON(inputObject: Any?): ByteArray {
        return objectMapper.writeValueAsBytes(inputObject)
    }

    fun <T> toObject(requestResult: ResultActions, valueTypeRef: Class<T>): T {
        val result: MvcResult = requestResult.andReturn()
        val json = result.response.contentAsString
        return objectMapper.readValue(json, valueTypeRef)
    }

    fun <T> post(url: String, body: T): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post(url).contentType(CONTENT_TYPE_JSON)
            .content(toJSON(body))
    }

    fun <T> put(url: String, body: T): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.put(url).contentType(CONTENT_TYPE_JSON)
            .content(toJSON(body))
    }

    fun delete(url: String): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.delete(url).contentType(CONTENT_TYPE_JSON)
    }

    fun get(url: String): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.get(url).contentType(CONTENT_TYPE_JSON)
    }

    fun formatDate(date: ZonedDateTime): String {
        val utcDate = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"))
        return utcDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"))

    }
}