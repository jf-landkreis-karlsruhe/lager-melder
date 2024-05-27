package de.kordondev.lagermelder.core.service

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
    fun getAttendees(): List<AttendeeEntry> {
        return getAllAttendees()
            .filter { authorityService.hasAuthorityFilter(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    fun getAttendee(id: Long): AttendeeEntry {
        return attendeeRepository
            .findByIdOrNull(id)
            ?.let { authorityService.hasAuthority(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
            ?: throw NotFoundException("Attendee with id $id not found")
    }

    fun createAttendee(attendee: AttendeeEntry): AttendeeEntry {
        authorityService.hasAuthority(attendee, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        checkCanAttendeeBeEdited()
        checkFirstNameAndLastNameAreUnique(attendee)
        val code = PasswordGenerator.generateCode()
        return attendeeRepository
            .save(attendee.copy(code = code))
    }

    fun saveAttendee(id: Long, attendee: AttendeeEntry): AttendeeEntry {
        checkCanAttendeeBeEdited()
        checkFirstNameAndLastNameAreUnique(attendee, id)

        return attendeeRepository.findByIdOrNull(id)
            ?.let {
                authorityService.hasAuthority(
                    it,
                    listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
                )
                attendeeRepository.save(attendee.copy(code = it.code, id = it.id))
            }
            ?: createAttendee(attendee)
    }

    fun deleteAttendee(id: Long) {
        checkCanAttendeeBeEdited()
        attendeeRepository.findByIdOrNull(id)
            ?.let {
                authorityService.hasAuthority(
                    it,
                    listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
                )
                attendeeRepository.delete(it)
            }
            ?: throw NotFoundException("Attendee with id $id not found and therefore not deleted")
    }

    fun getAttendeesForDepartment(department: DepartmentEntry): List<AttendeeEntry> {
        return attendeeRepository
            .findByDepartment(department)
            .filter { authorityService.hasAuthorityFilter(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    fun getAttendeeByCode(code: String): AttendeeEntry {
        return attendeeRepository.findByCode(code)
            ?: throw NotFoundException("No Attendee for code $code found")
    }

    fun getAllAttendees(): List<AttendeeEntry> {
        return attendeeRepository.findAll().toList()
    }

    fun getAttendeesWithoutYouthPlanRole(): List<AttendeeEntry> {
        return attendeeRepository.findAttendeesWithoutYouthPlanRole()
    }

    fun getDepartmentIdsForAllAttendees(): List<Long> {
        return attendeeRepository.findDistinctDepartmentIdsFromAllAttendees()
    }

    private fun checkFirstNameAndLastNameAreUnique(attendee: AttendeeEntry, id: Long = 0) {
        attendeeRepository.findByDepartmentAndFirstNameAndLastName(
            attendee.department,
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