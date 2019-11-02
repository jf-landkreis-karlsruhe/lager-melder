package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
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
        return attendeeRepository.findByIdOrNull(id)?.let { attendee -> Attendee(
                id = attendee.id,
                firstName = attendee.firstName,
                lastName = attendee.lastName
        )} ?: throw NotFoundException("Attendee with id $id not found")
    }

    fun saveAttendee(attendee: NewAttendee) : Attendee {
        return attendeeRepository.save(AttendeeEntry(
                firstName = attendee.firstName,
                lastName = attendee.lastName
        )).let { savedAttendee -> Attendee(
                id = savedAttendee.id,
                firstName = savedAttendee.firstName,
                lastName = savedAttendee.lastName
        ) }
    }

    fun deleteAttendee(id: Long) {
         attendeeRepository.findByIdOrNull(id)?.let {
             attendeeRepository.delete(it)
         } ?: throw NotFoundException("Attendee with id $id not found and therefore not deleted")
    }
}