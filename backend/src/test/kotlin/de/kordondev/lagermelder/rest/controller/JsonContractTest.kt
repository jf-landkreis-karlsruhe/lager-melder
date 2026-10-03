package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants.ROLE_PREFIX
import de.kordondev.lagermelder.helper.IntegrationTest
import de.kordondev.lagermelder.helper.WebTestHelper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * Uses raw JSON exactly as the frontend sends it, independent of the server's ObjectMapper configuration.
 * Enums are exchanged by name (see frontend/src/services/attendee.ts Food, department.ts DepartmentFeatures), and
 * optional fields like partOfDepartmentId are left out (undefined in TypeScript).
 */
@Transactional
@IntegrationTest
@WithMockUser(authorities = [ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
class JsonContractTest(
    val context: WebApplicationContext,
) {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun attendeeAsSentByTheFrontend() {
        val department = webTestHelper.createDepartment(mockMvc, setOf(DepartmentFeatures.YOUTH_GROUPS))
        val attendeeJson =
            """
            {"firstName": "Anna", "lastName": "Schmidt", "departmentId": ${department.id}, "birthday": "2012-04-03",
             "food": "MEAT", "tShirtSize": "S", "additionalInformation": "", "role": "YOUTH", "juleikaNumber": "",
             "juleikaExpireDate": "", "helperDays": []}
            """.trimIndent()

        mockMvc
            .perform(post("/attendees").contentType(MediaType.APPLICATION_JSON).content(attendeeJson))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.food").value("MEAT"))
            .andExpect(jsonPath("$.role").value("YOUTH"))

        mockMvc
            .perform(webTestHelper.get("/departments/${department.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.features[0]").value("YOUTH_GROUPS"))
    }
}
