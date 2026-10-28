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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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
             "helperDays": []}
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

    @Test
    fun juleikaValidationAsSentByTheFrontend() {
        mockMvc
            .perform(
                post("/juleika/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"cardNumber": "12340", "lastName": "Leiter"}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.expireDate").value("2099-12-31"))
    }

    @Test
    fun youthLeaderGetsExpireDateOfJuleikaApiInsteadOfTheSentOne() {
        val department = webTestHelper.createDepartment(mockMvc, setOf(DepartmentFeatures.YOUTH_GROUPS))

        mockMvc
            .perform(post("/attendees").contentType(MediaType.APPLICATION_JSON).content(youthLeaderJson(department.id, "12340")))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.juleikaNumber").value("12340"))
            .andExpect(jsonPath("$.juleikaExpireDate").value("2099-12-31"))
    }

    @Test
    fun youthLeaderWithInvalidJuleikaGetsNoExpireDate() {
        val department = webTestHelper.createDepartment(mockMvc, setOf(DepartmentFeatures.YOUTH_GROUPS))

        mockMvc
            .perform(post("/attendees").contentType(MediaType.APPLICATION_JSON).content(youthLeaderJson(department.id, "12345")))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.juleikaExpireDate").doesNotExist())
    }

    @Test
    fun registrationDatesAreSentAndReturnedAsDays() {
        val settingsJson =
            """
            {"id": 1, "registrationEnd": "2099-07-01", "childGroupsRegistrationEnd": "2099-07-02",
             "helpersRegistrationEnd": "2099-07-03", "hostCity": "Ettlingen", "eventStart": "2099-08-01",
             "eventEnd": "2099-08-05", "eventName": "Zeltlager", "eventAddress": "Platz", "organizer": "KJF",
             "organisationAddress": "Adresse", "moneyPerYouthLoader": "8,99",
             "startDownloadRegistrationFiles": "2099-07-10", "numberOfDuties": 2}
            """.trimIndent()

        mockMvc
            .perform(put("/settings").contentType(MediaType.APPLICATION_JSON).content(settingsJson))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.registrationEnd").value("2099-07-01"))
            .andExpect(jsonPath("$.startDownloadRegistrationFiles").value("2099-07-10"))

        mockMvc
            .perform(webTestHelper.get("/settings/registration-end"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.registrationEnd").value("2099-07-01"))
            .andExpect(jsonPath("$.childGroupsRegistrationEnd").value("2099-07-02"))
            .andExpect(jsonPath("$.helpersRegistrationEnd").value("2099-07-03"))
    }

    // the mocked Juleika API accepts numbers ending in 0 and returns 2099-12-31, the client sends its own date
    private fun youthLeaderJson(
        departmentId: Long,
        juleikaNumber: String,
    ) = """
        {"firstName": "Lea", "lastName": "Leiter", "departmentId": $departmentId, "birthday": "2000-01-01",
         "food": "MEAT", "tShirtSize": "S", "additionalInformation": "", "role": "YOUTH_LEADER",
         "juleikaNumber": "$juleikaNumber", "juleikaExpireDate": "2030-01-01", "helperDays": []}
        """.trimIndent()
}
