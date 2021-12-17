package de.kordondev.attendee.helper

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.nio.charset.Charset

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
}