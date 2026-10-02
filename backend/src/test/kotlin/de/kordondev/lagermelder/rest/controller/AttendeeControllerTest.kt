package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures
import de.kordondev.lagermelder.core.persistence.entry.Food
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants
import de.kordondev.lagermelder.exception.ErrorConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.IntegrationTest
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.RestDepartment
import de.kordondev.lagermelder.rest.model.request.RestAttendeeRequest
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate

@Transactional
@IntegrationTest
class AttendeeControllerTest(
    val context: WebApplicationContext,
) {
    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    lateinit var department: RestDepartment

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()

        department = webTestHelper.createDepartment(restMockMvc, setOf(DepartmentFeatures.YOUTH_GROUPS))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addAttendee() {
        val attendee = Entities.restAttendeeRequest(department.id)

        restMockMvc
            .perform(webTestHelper.post("/attendees", attendee))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(attendee.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(attendee.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value(attendee.birthday))
            .andExpect(MockMvcResultMatchers.jsonPath("$.food").value(attendee.food.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tShirtSize").value(attendee.tShirtSize))
            .andExpect(MockMvcResultMatchers.jsonPath("$.additionalInformation").value(attendee.additionalInformation))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(attendee.role.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").isNotEmpty)
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun updateAttendee() {
        val attendee = Entities.restAttendeeRequest(department.id)

        val createdAttendee =
            webTestHelper.toObject(
                restMockMvc
                    .perform(webTestHelper.post("/attendees", attendee))
                    .andExpect(MockMvcResultMatchers.status().isOk),
                RestAttendee::class.java,
            )
        webTestHelper.flushAndClear()

        val updatedAttendee =
            RestAttendeeRequest(
                firstName = "att1",
                lastName = "endee1",
                departmentId = department.id,
                birthday = "05-09-2006",
                food = Food.VEGETARIAN,
                tShirtSize = Entities.TShirtSizeMock.M.size,
                additionalInformation = "no",
                role = AttendeeRole.YOUTH,
                juleikaNumber = "12345678",
                juleikaExpireDate = LocalDate.of(2099, 5, 5).toString(),
                partOfDepartmentId = department.id,
                helperDays = emptySet(),
            )

        restMockMvc
            .perform(webTestHelper.put("/attendees/${createdAttendee.id}", updatedAttendee))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdAttendee.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(updatedAttendee.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(updatedAttendee.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value(updatedAttendee.birthday))
            .andExpect(MockMvcResultMatchers.jsonPath("$.food").value(updatedAttendee.food.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tShirtSize").value(updatedAttendee.tShirtSize))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalInformation").value(updatedAttendee.additionalInformation),
            ).andExpect(MockMvcResultMatchers.jsonPath("$.role").value(updatedAttendee.role.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").isNotEmpty)
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun deleteAttendee() {
        val attendee = Entities.restAttendeeRequest(department.id)

        val createdAttendee =
            webTestHelper.toObject(
                restMockMvc
                    .perform(webTestHelper.post("/attendees", attendee))
                    .andExpect(MockMvcResultMatchers.status().isOk),
                RestAttendee::class.java,
            )
        webTestHelper.flushAndClear()

        restMockMvc
            .perform(webTestHelper.get("/attendees/${createdAttendee.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc
            .perform(webTestHelper.delete("/attendees/${createdAttendee.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
        webTestHelper.flushAndClear()

        restMockMvc
            .perform(webTestHelper.get("/attendees/${createdAttendee.id}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun updateAttendeeCannotChangeRole() {
        val attendee = Entities.restAttendeeRequest(department.id)
        val createdAttendee =
            webTestHelper.toObject(
                restMockMvc
                    .perform(webTestHelper.post("/attendees", attendee))
                    .andExpect(MockMvcResultMatchers.status().isOk),
                RestAttendee::class.java,
            )
        webTestHelper.flushAndClear()

        restMockMvc
            .perform(webTestHelper.put("/attendees/${createdAttendee.id}", attendee.copy(role = AttendeeRole.YOUTH_LEADER)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.key").value(ErrorConstants.CHANGED_ROLE))
    }
}
