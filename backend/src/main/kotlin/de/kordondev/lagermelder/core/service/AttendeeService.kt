package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.AttendeeEntry
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.YouthEntry
import de.kordondev.lagermelder.core.persistence.repository.AttendeeRepository
import de.kordondev.lagermelder.core.persistence.repository.YouthRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.security.PasswordGenerator
import de.kordondev.lagermelder.core.service.helper.TShirtSizeValidator
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
    private val settingsService: SettingsService,
    private val tShirtSizeValidator: TShirtSizeValidator,
    private val youthRepository: YouthRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(AttendeeService::class.java)
    fun getYouth(): List<YouthEntry> {
        return youthRepository
            .findAll()
            .toList()
    }

    fun youthToAttendee(youth: YouthEntry): AttendeeEntry {
        return AttendeeEntry(
            id = kotlin.random.Random.nextLong(),
            firstName = youth.firstName,
            lastName = youth.lastName,
            birthday = youth.birthday,
            food = youth.food,
            tShirtSize = youth.tShirtSize,
            additionalInformation = youth.additionalInformation,
            code = youth.code,
            role = youth.role,
            department = youth.department,
            status = youth.status
        )
    }
    fun getAttendees(): List<AttendeeEntry> {
        return getYouth()
            .map { youthToAttendee(it) }
            .toList()
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
        tShirtSizeValidator.validate(attendee.tShirtSize)

        val code = PasswordGenerator.generateCode()
        return attendeeRepository
            .save(attendee.copy(code = code))
    }

    fun saveAttendee(id: Long, attendee: AttendeeEntry): AttendeeEntry {
        checkCanAttendeeBeEdited()
        checkFirstNameAndLastNameAreUnique(attendee, id)
        tShirtSizeValidator.validate(attendee.tShirtSize)

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
        return youthRepository.findByDepartment(department.id)
            .map { youthToAttendee(it) }
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

    fun replaceTShirtSize(oldSize: String, newSize: String) {
        attendeeRepository.findAllBytShirtSize(oldSize).forEach {
            attendeeRepository.save(it.copy(tShirtSize = newSize))
        }

    }
}