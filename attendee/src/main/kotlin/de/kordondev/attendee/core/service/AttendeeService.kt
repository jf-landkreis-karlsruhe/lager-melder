package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.exception.NotFoundException
import org.springframework.stereotype.Component

@Component
class AttendeeService (
        private val attendeeRepository: AttendeeRepository
) {

    fun getAttendees() : List<Attendee> {
        return attendeeRepository.findAll().map{ attendee -> Attendee(
            id = attendee.id,
            firstName = attendee.firstName,
            lastName = attendee.lastName
        ) }.toList()
    }

    fun getAttendee(id: Long) : Attendee {
        val attendeeById = attendeeRepository.findById(id)?.map { attendee -> Attendee(
                id = attendee.id,
                firstName = attendee.firstName,
                lastName = attendee.lastName
            )}
        if (attendeeById.isPresent) {
            return attendeeById.get()
        }
        throw NotFoundException("Attendee with id $id not found")
    }
}