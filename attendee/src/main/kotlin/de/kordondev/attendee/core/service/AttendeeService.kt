package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class AttendeeService (
        private val attendeeRepository: AttendeeRepository
) {

    fun getAttendees() : Iterable<Attendee> {
        return attendeeRepository.findAll()
                .map{ attendee -> AttendeeEntry.to(attendee) }
    }

    fun getAttendee(id: Long) : Attendee {
        return attendeeRepository
                .findByIdOrNull(id)?.let { attendee -> AttendeeEntry.to(attendee) }
            ?: throw NotFoundException("Attendee with id $id not found")
    }

    fun createAttendee(attendee: NewAttendee) : Attendee {
        return attendeeRepository
                .save(AttendeeEntry.of(attendee))
                .let { savedAttendee -> AttendeeEntry.to(savedAttendee) }
    }

    fun saveAttendee(id: Long, attendee: NewAttendee): Attendee {
        val attendeeToSave = AttendeeEntry.of(attendee, id)
        return attendeeRepository
                .save(attendeeToSave)
                .let { savedAttendee -> AttendeeEntry.to(savedAttendee) }
    }

    fun deleteAttendee(id: Long) {
         attendeeRepository.findByIdOrNull(id)
                 ?.let { attendeeRepository.delete(it) }
             ?: throw NotFoundException("Attendee with id $id not found and therefore not deleted")
    }

    fun getAttendeesForDepartment(department: Department): Iterable<Attendee> {
        return attendeeRepository
                .findByDepartment(DepartmentEntry.of(department))
                .map{ attendee -> AttendeeEntry.to(attendee) };

    }
}