package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.repository.SettingsRepository
import de.kordondev.lagermelder.core.security.SecurityConstants
import de.kordondev.lagermelder.core.service.SettingsService
import de.kordondev.lagermelder.exception.ErrorConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.IntegrationTest
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestAttendee
import de.kordondev.lagermelder.rest.model.RestEvent
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
class EventControllerTest(
    val context: WebApplicationContext,
) {
    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    @Autowired
    lateinit var settingsService: SettingsService

    @Autowired
    lateinit var settingsRepository: SettingsRepository

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addEvent() {
        val event = Entities.event()

        restMockMvc
            .perform(webTestHelper.post("/events", event))
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
        restMockMvc
            .perform(webTestHelper.put("/events/${createdEvent.id}", createdEvent))
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
        restMockMvc
            .perform(webTestHelper.put("/events/${createdEvent.id}", createdEvent))
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

        restMockMvc
            .perform(webTestHelper.get("/events/by-code/${createdEvent.code}"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdEvent.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(createdEvent.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(createdEvent.code))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addAttendeeToEvent() {
        allowCheckIn()
        val event = createEvent()
        val attendee = createAttendee()

        restMockMvc
            .perform(webTestHelper.post("/events/by-code/${event.code}/${attendee.code}", null))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.attendeeFirstName").value(attendee.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.attendeeLastName").value(attendee.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.eventName").value(event.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.time").isNotEmpty)
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addUnknownAttendeeToEvent() {
        allowCheckIn()
        val event = createEvent()

        restMockMvc
            .perform(webTestHelper.post("/events/by-code/${event.code}/unknown-attendee", null))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.key").value(ErrorConstants.NOT_FOUND_ERROR))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addAttendeeToUnknownEvent() {
        allowCheckIn()
        val attendee = createAttendee()

        restMockMvc
            .perform(webTestHelper.post("/events/by-code/unknown-event/${attendee.code}", null))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.key").value(ErrorConstants.NOT_FOUND_ERROR))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addAttendeeToEventIsNotPossibleEarlierThanOneWeekBeforeTheEvent() {
        settingsRepository.save(settingsService.getSettings().copy(eventStart = LocalDate.now().plusDays(8)))
        val event = createEvent()
        val attendee = createAttendee()

        restMockMvc
            .perform(webTestHelper.post("/events/by-code/${event.code}/${attendee.code}", null))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.key").value(ErrorConstants.WRONG_TIME_EXCEPTION))
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun deleteEvent() {
        val event = Entities.event()

        val createdEvent =
            webTestHelper.toObject(
                restMockMvc
                    .perform(webTestHelper.post("/events", event))
                    .andExpect(MockMvcResultMatchers.status().isOk),
                RestEvent::class.java,
            )

        restMockMvc
            .perform(webTestHelper.get("/events/${createdEvent.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc
            .perform(webTestHelper.delete("/events/${createdEvent.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc
            .perform(webTestHelper.get("/events/${createdEvent.id}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    // add attendeeToEvent, delete event and attendeeToEvent need to be still there
    // errror unknown event code
    // error unknown att code

    private fun allowCheckIn() {
        settingsRepository.save(settingsService.getSettings().copy(eventStart = LocalDate.now()))
    }

    private fun createEvent(): RestEvent =
        webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/events", Entities.event())).andExpect(MockMvcResultMatchers.status().isOk),
            RestEvent::class.java,
        )

    private fun createAttendee(): RestAttendee {
        val department = webTestHelper.createDepartment(restMockMvc, setOf(DepartmentFeatures.YOUTH_GROUPS))
        val attendee =
            webTestHelper.toObject(
                restMockMvc
                    .perform(webTestHelper.post("/attendees", Entities.restAttendeeRequest(department.id)))
                    .andExpect(MockMvcResultMatchers.status().isOk),
                RestAttendee::class.java,
            )
        webTestHelper.flushAndClear()
        return attendee
    }
}
