package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.RestDepartment
import de.kordondev.lagermelder.rest.model.RestPCRTestSeries
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@Transactional
@SpringBootTest
class PCRTestControllerTest(val context: WebApplicationContext) {
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

    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.USER])
    fun addAttendeeToPcrTestSeries() {
        val pcrTestSeriesEntity = Entities.restPCRTestSeriesRequest()
        val createdPcrTestSeries = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/pcr-test-series", pcrTestSeriesEntity))
                .andExpect(MockMvcResultMatchers.status().isOk), RestPCRTestSeries::class.java
        )
        val pcrTestCode = createdPcrTestSeries.testCodes[0]

        val attendee = Entities.restAttendeeRequest(department.id)
        val createdAttendee = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/attendees", attendee))
                .andExpect(MockMvcResultMatchers.status().isOk),
            RestAttendee::class.java
        )
        val attendee2 = Entities.restAttendeeRequest(department.id)
        val createdAttendee2 = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/attendees", attendee2))
                .andExpect(MockMvcResultMatchers.status().isOk),
            RestAttendee::class.java
        )

        restMockMvc.perform(
            webTestHelper.post("/pcr-tests/${pcrTestCode}/${createdAttendee.code}", null)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)


        // create
        restMockMvc.perform(webTestHelper.get("/pcr-tests/by-code/${pcrTestCode}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(pcrTestCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees[0].attendeeId").value(createdAttendee.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.testedAttendees[0].attendeeFirstName")
                    .value(createdAttendee.firstName)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.testedAttendees[0].attendeeLastName").value(createdAttendee.lastName)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees[0].departmentName").value(department.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees", hasSize<Any>(1)))

        // update
        restMockMvc.perform(
            webTestHelper.post("/pcr-tests/${pcrTestCode}/${createdAttendee2.code}", null)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
        restMockMvc.perform(webTestHelper.get("/pcr-tests/by-code/${pcrTestCode}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(pcrTestCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees[1].attendeeId").value(createdAttendee2.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.testedAttendees[1].attendeeFirstName")
                    .value(createdAttendee2.firstName)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.testedAttendees[1].attendeeLastName").value(createdAttendee2.lastName)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees[1].departmentName").value(department.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees", hasSize<Any>(2)))

        // delete
        restMockMvc.perform(
            webTestHelper.delete("/pcr-tests/${pcrTestCode}/${createdAttendee2.code}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
        restMockMvc.perform(webTestHelper.get("/pcr-tests/by-code/${pcrTestCode}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(pcrTestCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees[0].attendeeId").value(createdAttendee.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.testedAttendees[0].attendeeFirstName")
                    .value(createdAttendee.firstName)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.testedAttendees[0].attendeeLastName").value(createdAttendee.lastName)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees[0].departmentName").value(department.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.testedAttendees", hasSize<Any>(1)))
    }
}