package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.repository.SettingsRepository
import de.kordondev.lagermelder.core.security.SecurityConstants.ROLE_PREFIX
import de.kordondev.lagermelder.core.service.SettingsService
import de.kordondev.lagermelder.exception.ErrorConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.IntegrationTest
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestDepartment
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Smoke tests for the generated PDFs/CSV: every endpoint must produce a valid file for realistic data
 * (one department with a youth and a youth leader, plus the Liquibase mock data).
 */
@Transactional
@IntegrationTest
@WithMockUser(authorities = [ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
class GeneratedFilesControllerTest(
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
        val youth = Entities.restAttendeeRequest(department.id)
        mockMvc.perform(webTestHelper.post("/attendees", youth)).andExpect(status().isOk)
        mockMvc
            .perform(
                webTestHelper.post(
                    "/attendees",
                    youth.copy(firstName = "leader", role = AttendeeRole.YOUTH_LEADER, birthday = "1990-01-01"),
                ),
            ).andExpect(status().isOk)
        webTestHelper.flushAndClear()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/planning-files/events?frontendBaseUrl=https://lager.example/",
            "/planning-files/batches",
            "/planning-files/batches-ordered-by-creation-date",
            "/planning-files/t-shirts",
            "/planning-files/food",
            "/planning-files/additionalInformation",
            "/planning-files/overviewForDepartment",
            "/planning-files/contactOverview",
            "/planning-files/tentMarkings",
            "/planning-files/missing-juleika",
        ],
    )
    fun planningFileIsPdf(url: String) {
        assertIsPdf(url)
    }

    @Test
    fun tentsAndDutiesIsCsv() {
        val content =
            mockMvc
                .perform(webTestHelper.get("/planning-files/tents-and-duties-csv"))
                .andExpect(status().isOk)
                .andReturn()
                .response.contentAsString

        assertThat(content).contains(department.name)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "attendeesKarlsruhe/{id}?group=teilnehmer",
            "stateYouthPlanLeader/{id}?group=teilnehmer",
            "stateYouthPlanAttendees/{id}?group=teilnehmer",
            "attendeesCommunal/{id}",
        ],
    )
    fun registrationFileIsPdfAfterDownloadStart(path: String) {
        allowRegistrationFileDownload()

        assertIsPdf("/registrationFiles/" + path.replace("{id}", department.id.toString()))
    }

    @Test
    fun registrationFilesCannotBeDownloadedBeforeDownloadStart() {
        settingsRepository.save(
            settingsService.getSettings().copy(startDownloadRegistrationFiles = Instant.now().plus(1, ChronoUnit.DAYS)),
        )

        mockMvc
            .perform(webTestHelper.get("/registrationFiles/attendeesKarlsruhe/${department.id}?group=teilnehmer"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.key").value(ErrorConstants.WRONG_TIME_EXCEPTION))
    }

    private fun allowRegistrationFileDownload() {
        val now = Instant.now()
        settingsRepository.save(
            settingsService.getSettings().copy(
                registrationEnd = now.minus(2, ChronoUnit.DAYS),
                startDownloadRegistrationFiles = now.minus(1, ChronoUnit.DAYS),
            ),
        )
    }

    private fun assertIsPdf(url: String) {
        val bytes =
            mockMvc
                .perform(webTestHelper.get(url))
                .andExpect(status().isOk)
                .andReturn()
                .response.contentAsByteArray

        assertThat(String(bytes.take(4).toByteArray())).isEqualTo("%PDF")
    }
}
