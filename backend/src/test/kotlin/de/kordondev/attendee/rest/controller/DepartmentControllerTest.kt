package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.security.SecurityConstants
import de.kordondev.attendee.helper.Entities
import de.kordondev.attendee.helper.WebTestHelper
import de.kordondev.attendee.rest.model.RestDepartment
import org.hamcrest.Matchers.hasSize
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
class DepartmentControllerTest(val context: WebApplicationContext) {
    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addDepartment() {

        val department = Entities.restDepartmentRequest()

        restMockMvc.perform(webTestHelper.post("/departments", department))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(department.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.leaderName").value(department.leaderName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.leaderEMail").value(department.leaderEMail))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun updateDepartment() {

        val department = Entities.restDepartmentRequest()

        var createdDepartment = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/departments", department))
                .andExpect(MockMvcResultMatchers.status().isOk), RestDepartment::class.java
        )

        val newName = "new name"
        createdDepartment = createdDepartment.copy(name = newName)
        restMockMvc.perform(webTestHelper.put("/departments/${createdDepartment.id}", createdDepartment))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newName))

        val newLeaderName = "new leader name"
        createdDepartment = createdDepartment.copy(leaderName = newLeaderName)
        restMockMvc.perform(webTestHelper.put("/departments/${createdDepartment.id}", createdDepartment))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.leaderName").value(newLeaderName))

        val newLeaderEMail = "new leader mail"
        createdDepartment = createdDepartment.copy(leaderName = newLeaderEMail)
        restMockMvc.perform(webTestHelper.put("/departments/${createdDepartment.id}", createdDepartment))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.leaderName").value(newLeaderEMail))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun deleteDepartment() {

        val department = Entities.restDepartmentRequest()

        var createdDepartment = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/departments", department))
                .andExpect(MockMvcResultMatchers.status().isOk), RestDepartment::class.java
        )

        restMockMvc.perform(webTestHelper.get("/departments/${createdDepartment.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.delete("/departments/${createdDepartment.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.get("/departments/${createdDepartment.id}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun getAttendeesForDepartment() {

        val department = Entities.restDepartmentRequest()

        var createdDepartment = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/departments", department))
                .andExpect(MockMvcResultMatchers.status().isOk), RestDepartment::class.java
        )

        val attendee1 = Entities.restAttendeeRequest().copy(departmentId = createdDepartment.id)
        val attendee2 = Entities.restAttendeeRequest().copy(departmentId = createdDepartment.id, firstName = "first")

        restMockMvc.perform(webTestHelper.post("/attendees", attendee1))
            .andExpect(MockMvcResultMatchers.status().isOk)
        restMockMvc.perform(webTestHelper.post("/attendees", attendee2))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.get("/departments/${createdDepartment.id}/attendees"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize<Any>(2)))
    }
}