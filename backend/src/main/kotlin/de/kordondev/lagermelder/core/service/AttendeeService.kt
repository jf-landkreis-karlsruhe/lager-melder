package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.model.Attendee
import de.kordondev.lagermelder.core.model.Department
import de.kordondev.lagermelder.core.model.NewAttendee
import de.kordondev.lagermelder.core.persistence.entry.AttendeeEntry
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.repository.AttendeeRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.security.PasswordGenerator
import de.kordondev.lagermelder.exception.NotFoundException
import de.kordondev.lagermelder.exception.UniqueException
import de.kordondev.lagermelder.exception.WrongTimeException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AttendeeService(
    private val attendeeRepository: AttendeeRepository,
    private val authorityService: AuthorityService,
    private val settingsService: SettingsService
) {
    private val logger: Logger = LoggerFactory.getLogger(AttendeeService::class.java)
    fun getAttendees(): List<Attendee> {
        return getAllAttendees()
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
        checkCanAttendeeBeEdited()
        checkFirstNameAndLastNameAreUnique(attendee)
        val code = PasswordGenerator.generateCode()
        return attendeeRepository
            .save(AttendeeEntry.of(attendee, code))
            .let { savedAttendee -> AttendeeEntry.to(savedAttendee) }
    }

    fun saveAttendee(id: Long, attendee: NewAttendee): Attendee {
        checkCanAttendeeBeEdited()
        checkFirstNameAndLastNameAreUnique(attendee, id)

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
        checkCanAttendeeBeEdited()
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

    fun getAllAttendees(): List<Attendee> {
        return attendeeRepository.findAll()
            .map { AttendeeEntry.to(it) }
    }

    fun getAttendeesWithoutYouthPlanRole(): List<AttendeeEntry> {
        return attendeeRepository.findAttendeesWithoutYouthPlanRole()
    }

    private fun checkFirstNameAndLastNameAreUnique(attendee: NewAttendee, id: Long = 0) {
        attendeeRepository.findByDepartmentAndFirstNameAndLastName(
            DepartmentEntry.of(attendee.department),
            attendee.firstName,
            attendee.lastName
        )
            ?.let {
                if (it.id != id) {
                    throw UniqueException("Vorname (${attendee.firstName}) und Nachname (${attendee.lastName}) müssen pro Feuerwehr einmalig sein")
                }
            }
    }

    private fun checkCanAttendeeBeEdited() {
        if (!settingsService.canAttendeesBeEdited()) {
            throw WrongTimeException("Registrierungsende wurde überschritten")
        }
    }
}