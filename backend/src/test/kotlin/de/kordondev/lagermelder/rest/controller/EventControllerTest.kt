package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.RestDepartmentWithUser
import de.kordondev.lagermelder.rest.model.RestEvent
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@Transactional
@SpringBootTest
class EventControllerTest(val context: WebApplicationContext) {

    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addEvent() {

        val event = Entities.event()

        restMockMvc.perform(webTestHelper.post("/events", event))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(event.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").isNotEmpty)
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun updateEventName() {
        val event = Entities.event()
        val createdResponse = restMockMvc.perform(webTestHelper.post("/events", event))
        var createdEvent = webTestHelper.toObject(createdResponse, RestEvent::class.java)

        createdEvent = createdEvent.copy(name = "new name")
        restMockMvc.perform(webTestHelper.put("/events/${createdEvent.id}", createdEvent))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdEvent.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(createdEvent.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(createdEvent.code))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun updateEventCodeButKeepsOld() {
        val event = Entities.event()
        val createdResponse = restMockMvc.perform(webTestHelper.post("/events", event))
        var createdEvent = webTestHelper.toObject(createdResponse, RestEvent::class.java)

        val oldCode = createdEvent.code
        createdEvent = createdEvent.copy(code = "new code")
        restMockMvc.perform(webTestHelper.put("/events/${createdEvent.id}", createdEvent))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdEvent.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(createdEvent.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(oldCode))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun getEventByCode() {
        val event = Entities.event()
        val createdResponse = restMockMvc.perform(webTestHelper.post("/events", event))
        var createdEvent = webTestHelper.toObject(createdResponse, RestEvent::class.java)

        restMockMvc.perform(webTestHelper.get("/events/by-code/${createdEvent.code}"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdEvent.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(createdEvent.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(createdEvent.code))
    }

    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addAttendeeToEvent() {
        val event = Entities.event()
        val createdResponse = restMockMvc.perform(webTestHelper.post("/events", event))
        var createdEvent = webTestHelper.toObject(createdResponse, RestEvent::class.java)

        var createdAttendee = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/attendees", Entities.attendee())),
            RestAttendee::class.java
        )

        restMockMvc.perform(
            webTestHelper.post(
                "/events-by-code/${createdEvent.code}/${createdAttendee.code}",
                null
            )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.attendeeFirstName").value(createdAttendee.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.attendeeLastName").value(createdAttendee.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.eventName").value(createdEvent.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.time").isNotEmpty)
    }

    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addInvalidAttendeeToEvent() {
        val event = Entities.event()
        restMockMvc.perform(webTestHelper.post("/events", event))

        val attendee = Entities.attendee()
        var createdAttendee = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/attendees", attendee)),
            RestAttendee::class.java
        )

        restMockMvc.perform(
            webTestHelper.post(
                "/events-by-code/invalid-attendee/${createdAttendee.code}",
                null
            )
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addAttendeeToInvalidEvent() {
        val event = Entities.event()
        val createdResponse = restMockMvc.perform(webTestHelper.post("/events", event))
            .andExpect(MockMvcResultMatchers.status().isOk)
        var createdEvent = webTestHelper.toObject(createdResponse, RestEvent::class.java)

        val department = webTestHelper.toObject(
            restMockMvc.perform(
                webTestHelper.post("/register", Entities.restDepartmentWithUserRequest()),
            ).andExpect(MockMvcResultMatchers.status().isOk),
            RestDepartmentWithUser::class.java
        )
        var attendee = Entities.restAttendeeRequest()
        attendee = attendee.copy(departmentId = department.departmentId, firstName = "")
        val response = restMockMvc.perform(webTestHelper.post("/attendees", attendee))

        restMockMvc.perform(
            webTestHelper.post(
                "/events-by-code/${createdEvent.code}/invalid-event",
                null
            )
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)

    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun deleteEvent() {

        val event = Entities.event()

        val createdEvent = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/events", event))
                .andExpect(MockMvcResultMatchers.status().isOk),
            RestEvent::class.java
        )

        restMockMvc.perform(webTestHelper.get("/events/${createdEvent.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.delete("/events/${createdEvent.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.get("/events/${createdEvent.id}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }


    //add attendeeToEvent, delete event and attendeeToEvent need to be still there
    // errror unknown event code
    //error unknown att code
}