package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import de.kordondev.attendee.core.security.SecurityConstants
import de.kordondev.attendee.helper.Entities
import de.kordondev.attendee.helper.WebTestHelper
import de.kordondev.attendee.rest.model.RestAttendee
import de.kordondev.attendee.rest.model.RestDepartment
import de.kordondev.attendee.rest.model.request.RestAttendeeRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import javax.transaction.Transactional

@Transactional
@SpringBootTest
class AttendeeControllerTest(val context: WebApplicationContext) {
    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    lateinit var department: RestDepartment

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()

        val departmentRequest = Entities.restDepartmentRequest()
        department = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/departments", departmentRequest))
                .andExpect(MockMvcResultMatchers.status().isOk), RestDepartment::class.java
        )
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addAttendee() {
        val attendee = Entities.restAttendeeRequest(department.id)

        restMockMvc.perform(webTestHelper.post("/attendees", attendee))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(attendee.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(attendee.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value(attendee.birthday))
            .andExpect(MockMvcResultMatchers.jsonPath("$.food").value(attendee.food))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tShirtSize").value(attendee.tShirtSize))
            .andExpect(MockMvcResultMatchers.jsonPath("$.additionalInformation").value(attendee.additionalInformation))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(attendee.role))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").isNotEmpty)
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun updateAttendee() {
        val attendee = Entities.restAttendeeRequest(department.id)

        val createdAttendee = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/attendees", attendee))
                .andExpect(MockMvcResultMatchers.status().isOk),
            RestAttendee::class.java
        )

        val updatedAttendee = RestAttendeeRequest(
            firstName = "att1",
            lastName = "endee1",
            departmentId = department.id,
            birthday = "05-09-2006",
            food = Food.VEGETARIAN,
            tShirtSize = TShirtSize.M.toString(),
            additionalInformation = "no",
            role = AttendeeRole.YOUTH_LEADER,
            specialLeave = false
        )

        restMockMvc.perform(webTestHelper.put("/attendees/${createdAttendee.id}", updatedAttendee))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdAttendee.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(updatedAttendee.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(updatedAttendee.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value(updatedAttendee.birthday))
            .andExpect(MockMvcResultMatchers.jsonPath("$.food").value(updatedAttendee.food))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tShirtSize").value(updatedAttendee.tShirtSize))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalInformation").value(updatedAttendee.additionalInformation)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(updatedAttendee.role))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").isNotEmpty)

    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun deleteAttendee() {
        val attendee = Entities.restAttendeeRequest(department.id)

        val createdAttendee = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/attendees", attendee))
                .andExpect(MockMvcResultMatchers.status().isOk),
            RestAttendee::class.java
        )

        restMockMvc.perform(webTestHelper.get("/attendees/${createdAttendee.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.delete("/attendees/${createdAttendee.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.get("/attendees/${createdAttendee.id}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}
