package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants.DEPARTMENT_ID_PREFIX
import de.kordondev.lagermelder.core.security.SecurityConstants.ROLE_PREFIX
import de.kordondev.lagermelder.core.security.SecurityConstants.USER_ID_PREFIX
import de.kordondev.lagermelder.exception.ErrorConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.IntegrationTest
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.RestDepartment
import de.kordondev.lagermelder.rest.model.request.RestSettingsRequest
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Department-level authorization enforced by AuthorityService in the services:
 * a USER may only access data of its own department, LK_KARLSRUHE may read all departments,
 * only SPECIALIZED_FIELD_DIRECTOR/ADMIN may change global data.
 *
 * Test data is created as specialized field director (class-level @WithMockUser) without the security filter chain,
 * the requests under test run through the filter chain as the given user.
 */
@Transactional
@IntegrationTest
@WithMockUser(authorities = [ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
class DepartmentAccessTest(
    val context: WebApplicationContext,
) {
    lateinit var setupMockMvc: MockMvc
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    lateinit var ownDepartment: RestDepartment
    lateinit var otherDepartment: RestDepartment
    lateinit var otherAttendee: RestAttendee

    @BeforeEach
    fun setUp() {
        setupMockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply<DefaultMockMvcBuilder>(springSecurity()).build()

        // settings are created lazily on first read, which requires the specialized field director role
        setupMockMvc.perform(webTestHelper.get("/settings")).andExpect(status().isOk)
        ownDepartment = webTestHelper.createDepartment(setupMockMvc, setOf(DepartmentFeatures.YOUTH_GROUPS), name = "own")
        otherDepartment = webTestHelper.createDepartment(setupMockMvc, setOf(DepartmentFeatures.YOUTH_GROUPS), name = "other")
        otherAttendee = createAttendee(otherDepartment.id, "other")
        createAttendee(ownDepartment.id, "own")
        webTestHelper.flushAndClear()
    }

    @Test
    fun userCanReadOwnDepartment() {
        mockMvc
            .perform(webTestHelper.get("/departments/${ownDepartment.id}").with(departmentUser()))
            .andExpect(status().isOk)
    }

    @Test
    fun userCannotReadOtherDepartment() {
        mockMvc
            .perform(webTestHelper.get("/departments/${otherDepartment.id}").with(departmentUser()))
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.key").value(ErrorConstants.ACCESS_DENIED_ERROR))
    }

    @Test
    fun userCannotReadAttendeesOfOtherDepartment() {
        mockMvc
            .perform(webTestHelper.get("/departments/${otherDepartment.id}/attendees").with(departmentUser()))
            .andExpect(status().isForbidden)
    }

    @Test
    fun userCannotReadSingleAttendeeOfOtherDepartment() {
        mockMvc
            .perform(webTestHelper.get("/attendees/${otherAttendee.id}").with(departmentUser()))
            .andExpect(status().isForbidden)
    }

    @Test
    fun userOnlySeesAttendeesOfOwnDepartment() {
        mockMvc
            .perform(webTestHelper.get("/attendees").with(departmentUser()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.youths", hasSize<Any>(1)))
            .andExpect(jsonPath("$.youths[0].departmentId").value(ownDepartment.id))
    }

    @Test
    fun userCanCreateAttendeeInOwnDepartment() {
        val attendee = Entities.restAttendeeRequest(ownDepartment.id).copy(firstName = "new")

        mockMvc
            .perform(webTestHelper.post("/attendees", attendee).with(departmentUser()))
            .andExpect(status().isOk)
    }

    @Test
    fun userCannotCreateAttendeeInOtherDepartment() {
        val attendee = Entities.restAttendeeRequest(otherDepartment.id).copy(firstName = "new")

        mockMvc
            .perform(webTestHelper.post("/attendees", attendee).with(departmentUser()))
            .andExpect(status().isForbidden)
    }

    @Test
    fun userCannotDeleteAttendeeOfOtherDepartment() {
        mockMvc
            .perform(webTestHelper.delete("/attendees/${otherAttendee.id}").with(departmentUser()))
            .andExpect(status().isForbidden)
    }

    @Test
    fun userCannotCreateDepartments() {
        mockMvc
            .perform(webTestHelper.post("/departments", Entities.restDepartmentRequest().copy(name = "new")).with(departmentUser()))
            .andExpect(status().isForbidden)
    }

    @Test
    fun userCannotChangeSettings() {
        mockMvc
            .perform(webTestHelper.put("/settings", settingsRequest()).with(departmentUser()))
            .andExpect(status().isForbidden)
    }

    @Test
    fun lkKarlsruheCanReadOtherDepartment() {
        mockMvc
            .perform(webTestHelper.get("/departments/${otherDepartment.id}").with(roleUser(Roles.LK_KARLSRUHE)))
            .andExpect(status().isOk)
    }

    @Test
    fun lkKarlsruheCannotChangeSettings() {
        mockMvc
            .perform(webTestHelper.put("/settings", settingsRequest()).with(roleUser(Roles.LK_KARLSRUHE)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun specializedFieldDirectorCanChangeSettings() {
        mockMvc
            .perform(webTestHelper.put("/settings", settingsRequest()).with(roleUser(Roles.SPECIALIZED_FIELD_DIRECTOR)))
            .andExpect(status().isOk)
    }

    private fun departmentUser(): RequestPostProcessor =
        user("department-user").authorities(
            SimpleGrantedAuthority(USER_ID_PREFIX + "999"),
            SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + ownDepartment.id),
            SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER),
        )

    private fun roleUser(role: String): RequestPostProcessor =
        user("role-user").authorities(
            SimpleGrantedAuthority(USER_ID_PREFIX + "998"),
            SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
            SimpleGrantedAuthority(ROLE_PREFIX + role),
        )

    private fun createAttendee(
        departmentId: Long,
        firstName: String,
    ): RestAttendee =
        webTestHelper.toObject(
            setupMockMvc
                .perform(webTestHelper.post("/attendees", Entities.restAttendeeRequest(departmentId).copy(firstName = firstName)))
                .andExpect(status().isOk),
            RestAttendee::class.java,
        )

    private fun settingsRequest(): RestSettingsRequest {
        val now = Instant.now()
        return RestSettingsRequest(
            registrationEnd = now.plus(10, ChronoUnit.DAYS),
            hostCity = "city",
            eventStart = LocalDate.now().plusDays(60),
            eventEnd = LocalDate.now().plusDays(65),
            eventName = "event",
            eventAddress = "address",
            organizer = "organizer",
            organisationAddress = "organisation",
            moneyPerYouthLoader = "8,99",
            startDownloadRegistrationFiles = now.plus(20, ChronoUnit.DAYS),
            childGroupsRegistrationEnd = now.plus(10, ChronoUnit.DAYS),
            helpersRegistrationEnd = now.plus(10, ChronoUnit.DAYS),
            numberOfDuties = 0,
        )
    }
}
