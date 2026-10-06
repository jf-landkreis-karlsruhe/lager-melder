package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.SendTo
import de.kordondev.lagermelder.core.persistence.repository.SettingsRepository
import de.kordondev.lagermelder.core.security.SecurityConstants.ROLE_PREFIX
import de.kordondev.lagermelder.core.service.SettingsService
import de.kordondev.lagermelder.exception.ErrorConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.IntegrationTest
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.RestDepartment
import de.kordondev.lagermelder.rest.model.RestTents
import de.kordondev.lagermelder.rest.model.request.RestSendMailRequest
import de.kordondev.lagermelder.rest.model.request.RestTShirtSizeRequest
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Settings, T-shirt sizes, tents, mails and the read-only lookup endpoints, used by the specialized field director.
 */
@Transactional
@IntegrationTest
@WithMockUser(authorities = [ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
class AdministrationControllerTest(
    val context: WebApplicationContext,
) {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    @Autowired
    lateinit var settingsService: SettingsService

    @Autowired
    lateinit var settingsRepository: SettingsRepository

    lateinit var department: RestDepartment

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        department = webTestHelper.createDepartment(mockMvc, setOf(DepartmentFeatures.YOUTH_GROUPS))
    }

    @Test
    fun updateSettings() {
        val request = Entities.restSettingsRequest().copy(eventName = "Kreiszeltlager")

        mockMvc
            .perform(webTestHelper.put("/settings", request))
            .andExpect(status().isOk)

        mockMvc
            .perform(webTestHelper.get("/settings"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.eventName").value("Kreiszeltlager"))
    }

    @Test
    fun settingsRejectRegistrationEndAfterDownloadStart() {
        val request = Entities.restSettingsRequest()
        val invalid = request.copy(registrationEnd = request.startDownloadRegistrationFiles.plus(1, ChronoUnit.DAYS))

        mockMvc
            .perform(webTestHelper.put("/settings", invalid))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.key").value(ErrorConstants.BAD_REQUEST_ERROR))
    }

    @Test
    fun settingsValidateRequiredFields() {
        mockMvc
            .perform(webTestHelper.put("/settings", Entities.restSettingsRequest().copy(hostCity = "")))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.key").value(ErrorConstants.VALIDATION_ERROR))
            .andExpect(jsonPath("$.messages[0].fieldName").value("hostCity"))
    }

    @Test
    fun deletingTShirtSizeReplacesItAtAttendees() {
        mockMvc
            .perform(webTestHelper.post("/tShirtSizes", RestTShirtSizeRequest("XXL")))
            .andExpect(status().isOk)
        val attendee =
            webTestHelper.toObject(
                mockMvc
                    .perform(webTestHelper.post("/attendees", Entities.restAttendeeRequest(department.id).copy(tShirtSize = "XXL")))
                    .andExpect(status().isOk),
                RestAttendee::class.java,
            )
        webTestHelper.flushAndClear()

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete("/tShirtSizes/XXL")
                    .contentType(WebTestHelper.CONTENT_TYPE_JSON)
                    .content(webTestHelper.toJSON(RestTShirtSizeRequest("M"))),
            ).andExpect(status().isOk)
        webTestHelper.flushAndClear()

        mockMvc
            .perform(webTestHelper.get("/tShirtSizes"))
            .andExpect(jsonPath("$", not(hasItem("XXL"))))
        mockMvc
            .perform(webTestHelper.get("/attendees/${attendee.id}"))
            .andExpect(jsonPath("$.tShirtSize").value("M"))
    }

    @Test
    fun tentCountsAreNeverNegative() {
        val tents = RestTents(id = 0, departmentId = department.id, sg200 = 2, sg20 = -1, sg30 = 0, sg40 = 0, sg50 = 1)

        mockMvc
            .perform(webTestHelper.put("/tents/department/${department.id}", tents))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.sg200").value(2))
            .andExpect(jsonPath("$.sg20").value(0))

        mockMvc
            .perform(webTestHelper.get("/departments/${department.id}/tents"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.sg200").value(2))
    }

    @Test
    @WithMockUser(authorities = [ROLE_PREFIX + Roles.ADMIN])
    fun adminCanSendReminderMail() {
        mockMvc
            .perform(webTestHelper.post("/mail/reminder", RestSendMailRequest(SendTo.ALL_DEPARTMENTS)))
            .andExpect(status().isOk)
    }

    @Test
    fun specializedFieldDirectorCannotSendMails() {
        mockMvc
            .perform(webTestHelper.post("/mail/reminder", RestSendMailRequest(SendTo.ALL_DEPARTMENTS)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun youthPlanDistributionIsOnlyAvailableAfterDownloadStart() {
        mockMvc
            .perform(webTestHelper.get("/youth-plan-attendees/distribution"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.key").value(ErrorConstants.WRONG_TIME_EXCEPTION))

        settingsRepository.save(
            settingsService.getSettings().copy(
                registrationEnd = Instant.now().minus(2, ChronoUnit.DAYS),
                startDownloadRegistrationFiles = Instant.now().minus(1, ChronoUnit.DAYS),
            ),
        )

        mockMvc
            .perform(webTestHelper.get("/youth-plan-attendees/distribution"))
            .andExpect(status().isOk)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/event-days", "/evacuation-groups", "/events/global/summary"])
    fun lookupEndpointsRespond(url: String) {
        mockMvc
            .perform(webTestHelper.get(url))
            .andExpect(status().isOk)
    }
}
