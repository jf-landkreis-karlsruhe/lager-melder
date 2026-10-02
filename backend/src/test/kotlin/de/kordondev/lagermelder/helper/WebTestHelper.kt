package de.kordondev.lagermelder.helper

import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures
import de.kordondev.lagermelder.rest.model.RestDepartment
import jakarta.persistence.EntityManager
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import tools.jackson.databind.ObjectMapper
import java.nio.charset.Charset
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class WebTestHelper(
    private val objectMapper: ObjectMapper,
    private val entityManager: EntityManager,
) {
    companion object {
        val CONTENT_TYPE_JSON =
            MediaType(
                MediaType.APPLICATION_JSON.type,
                MediaType.APPLICATION_JSON.subtype,
                Charset.forName("utf8"),
            )
    }

    fun toJSON(inputObject: Any?): ByteArray = objectMapper.writeValueAsBytes(inputObject)

    fun <T> toObject(
        requestResult: ResultActions,
        valueTypeRef: Class<T>,
    ): T {
        val result: MvcResult = requestResult.andReturn()
        val json = result.response.contentAsString
        return objectMapper.readValue(json, valueTypeRef)
    }

    fun <T> post(
        url: String,
        body: T,
    ): MockHttpServletRequestBuilder =
        MockMvcRequestBuilders
            .post(url)
            .contentType(CONTENT_TYPE_JSON)
            .content(toJSON(body))

    fun <T> put(
        url: String,
        body: T,
    ): MockHttpServletRequestBuilder =
        MockMvcRequestBuilders
            .put(url)
            .contentType(CONTENT_TYPE_JSON)
            .content(toJSON(body))

    fun delete(url: String): MockHttpServletRequestBuilder = MockMvcRequestBuilders.delete(url).contentType(CONTENT_TYPE_JSON)

    fun get(url: String): MockHttpServletRequestBuilder = MockMvcRequestBuilders.get(url).contentType(CONTENT_TYPE_JSON)

    /**
     * Creates a department with the given features. Features are added with a second request because
     * POST /departments cannot persist features of a not yet existing department (same workaround as POST /register).
     */
    fun createDepartment(
        mockMvc: MockMvc,
        features: Set<DepartmentFeatures>,
        name: String = Entities.restDepartmentRequest().name,
    ): RestDepartment {
        val request = Entities.restDepartmentRequest().copy(name = name)
        val created =
            toObject(
                mockMvc.perform(post("/departments", request)).andExpect(MockMvcResultMatchers.status().isOk),
                RestDepartment::class.java,
            )
        flushAndClear()
        return toObject(
            mockMvc
                .perform(put("/departments/${created.id}", request.copy(features = features)))
                .andExpect(MockMvcResultMatchers.status().isOk),
            RestDepartment::class.java,
        )
    }

    /**
     * Tests annotated with @Transactional run all requests in one transaction, while production uses one per request.
     * Attendee entities share the table base_attendees, so a lookup via BaseAttendeeEntry does not see a not yet
     * flushed YouthEntry. Call this between requests to behave like separate transactions.
     */
    fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    fun formatDate(date: ZonedDateTime): String {
        val utcDate = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"))
        return utcDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"))
    }
}
