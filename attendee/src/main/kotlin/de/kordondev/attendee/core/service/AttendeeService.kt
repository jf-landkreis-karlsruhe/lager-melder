package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Attendee
import org.springframework.stereotype.Component

@Component
class AttendeeService {
    private val attendeesMock = listOf<Attendee>(
        Attendee(
            id = 1,
            firstName = "Iris",
            lastName = "Muller"
        ), Attendee(
            id = 2,
            firstName = "Karl",
            lastName = "Smith"
    ))

    fun getAttendees() : List<Attendee> {
        return attendeesMock
    }

    fun getAttendee(id: Long) : Attendee {
        return attendeesMock.find { attendee -> attendee.id == id }.
    }
}