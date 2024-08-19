package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.persistence.repository.AttendeeRepository
import de.kordondev.lagermelder.core.persistence.repository.BaseAttendeeRepository
import de.kordondev.lagermelder.core.persistence.repository.YouthLeadersRepository
import de.kordondev.lagermelder.core.persistence.repository.YouthsRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.security.PasswordGenerator
import de.kordondev.lagermelder.core.service.helper.TShirtSizeValidator
import de.kordondev.lagermelder.core.service.models.Attendees
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
    private val youthRepository: YouthsRepository,
    private val youthLeaderRepository: YouthLeadersRepository,
    private val baseAttendeeRepository: BaseAttendeeRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(AttendeeService::class.java)

    fun getAttendees(): Attendees {
        val allAttendees = getAllAttendees()
        return Attendees(
            youths = allAttendees.youths.filter { byAuthority(it) },
            youthLeaders = allAttendees.youthLeaders.filter { byAuthority(it) },
        )
    }

    fun getAttendee(id: String): Attendee {
        val baseAttendee = baseAttendeeRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Attendee with id $id not found")

        return when (baseAttendee.role) {
            AttendeeRole.YOUTH -> youthRepository.findByIdOrNull(baseAttendee.id)
            AttendeeRole.YOUTH_LEADER -> youthLeaderRepository.findByIdOrNull(baseAttendee.id)
        }
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

    fun getAttendeesForDepartment(department: DepartmentEntry): Attendees {
        return Attendees(
            youths = getYouthsByDepartmentId(department.id),
            youthLeaders = getYouthLeadersByDepartmentId(department.id),
        )
    }

    fun getAttendeeByCode(code: String): AttendeeEntry {
        return attendeeRepository.findByCode(code)
            ?: throw NotFoundException("No Attendee for code $code found")
    }

    fun getAllAttendees(): Attendees {
        return Attendees(
            youths = youthRepository.findAll().toList(),
            youthLeaders = youthLeaderRepository.findAll().toList(),
        )
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

    private fun getYouthsByDepartmentId(departmentId: Long): List<YouthEntry> {
        return youthRepository.findByDepartment(departmentId)
            .filter { byAuthority(it) }
            .toList()
    }

    private fun getYouthLeadersByDepartmentId(departmentId: Long): List<YouthLeaderEntry> {
        return youthLeaderRepository.findByDepartment(departmentId)
            .filter { byAuthority(it) }
            .toList()
    }

    private fun byAuthority(attendee: Attendee): Boolean {
        return authorityService.hasAuthorityFilter(attendee, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
    }
}