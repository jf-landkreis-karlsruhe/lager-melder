package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.core.security.PasswordGenerator
import de.kordondev.attendee.exception.NotFoundException
import de.kordondev.attendee.exception.UniqueException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AttendeeService(
    private val attendeeRepository: AttendeeRepository,
    private val authorityService: AuthorityService
) {
    fun getAttendees(): Iterable<Attendee> {
        return attendeeRepository.findAll()
            .map { AttendeeEntry.to(it) }
            .filter { authorityService.hasAuthorityFilter(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    fun getAttendee(id: Long): Attendee {
        return attendeeRepository
            .findByIdOrNull(id)
            ?.let { AttendeeEntry.to(it) }
            ?.let { authorityService.hasAuthority(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
            ?: throw NotFoundException("Attendee with id $id not found")
    }

    fun createAttendee(attendee: NewAttendee): Attendee {
        authorityService.hasAuthority(attendee, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        checkFirstNameAndLastNameAreUnique(attendee)
        val code = PasswordGenerator.generateCode()
        return attendeeRepository
            .save(AttendeeEntry.of(attendee, code))
            .let { savedAttendee -> AttendeeEntry.to(savedAttendee) }
    }

    fun saveAttendee(id: Long, attendee: NewAttendee): Attendee {
        checkFirstNameAndLastNameAreUnique(attendee)
        return attendeeRepository.findByIdOrNull(id)
            ?.let {
                authorityService.hasAuthority(
                    AttendeeEntry.to(it),
                    listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
                )
                attendeeRepository.save(AttendeeEntry.of(attendee, it.code, it.id))
            }
            ?.let { AttendeeEntry.to(it) }
            ?: createAttendee(attendee)
    }

    fun deleteAttendee(id: Long) {
        attendeeRepository.findByIdOrNull(id)
            ?.let {
                authorityService.hasAuthority(
                    AttendeeEntry.to(it),
                    listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
                )
                attendeeRepository.delete(it)
            }
            ?: throw NotFoundException("Attendee with id $id not found and therefore not deleted")
    }

    fun getAttendeesForDepartment(department: Department): List<Attendee> {
        return attendeeRepository
            .findByDepartment(DepartmentEntry.of(department))
            .map { attendee -> AttendeeEntry.to(attendee) }
            .filter { authorityService.hasAuthorityFilter(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    fun getAttendeeByCode(code: String): Attendee {
        return attendeeRepository.findByCode(code)
            ?.let { AttendeeEntry.to(it) }
            ?: throw NotFoundException("No Attendee for code $code found")
    }

    private fun checkFirstNameAndLastNameAreUnique(attendee: NewAttendee) {
        attendeeRepository.findByDepartmentAndFirstNameAndLastName(
            DepartmentEntry.of(attendee.department),
            attendee.firstName,
            attendee.lastName
        )
            ?.let { throw UniqueException("Vorname (${attendee.firstName}) und Nachname (${attendee.lastName}) müssen pro Feuerwehr einmalig sein") }
    }
}