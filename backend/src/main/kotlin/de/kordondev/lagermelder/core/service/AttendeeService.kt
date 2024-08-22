package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.persistence.repository.BaseAttendeeRepository
import de.kordondev.lagermelder.core.persistence.repository.YouthLeadersRepository
import de.kordondev.lagermelder.core.persistence.repository.YouthsRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.security.PasswordGenerator
import de.kordondev.lagermelder.core.service.helper.TShirtSizeValidator
import de.kordondev.lagermelder.core.service.models.Attendees
import de.kordondev.lagermelder.exception.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass

@Service
class AttendeeService(
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
        return getAttendeeOrNull(id)
            ?: throw NotFoundException("Attendee with id $id not found")
    }

    fun createAttendee(attendee: Attendee): Attendee {
        authorityService.hasAuthority(attendee, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        checkCanAttendeeBeEdited()
        checkFirstNameAndLastNameAreUnique(attendee)
        tShirtSizeValidator.validate(attendee.tShirtSize)

        val code = PasswordGenerator.generateCode()
        val id = UUID.randomUUID().toString()
        return saveAttendeeToDB(attendee, attendee::class, id, code, Instant.now())
    }

    fun saveAttendee(id: String, attendee: Attendee): Attendee {
        checkCanAttendeeBeEdited()
        checkFirstNameAndLastNameAreUnique(attendee, id)
        tShirtSizeValidator.validate(attendee.tShirtSize)

        return getAttendeeOrNull(id)
            ?.let {
                authorityService.hasAuthority(
                    it,
                    listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
                )
            }
            ?.let { saveAttendeeToDB(attendee, it::class, it.id, it.code, it.createdAt) }
            ?: createAttendee(attendee)
    }

    fun deleteAttendee(id: String) {
        checkCanAttendeeBeEdited()
        getAttendeeOrNull(id)
            ?.let {
                authorityService.hasAuthority(
                    it,
                    listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
                )
                when (it) {
                    is YouthEntry -> youthRepository.delete(it)
                    is YouthLeaderEntry -> youthLeaderRepository.delete(it)
                }
            }
            ?: throw NotFoundException("Attendee with id $id not found and therefore not deleted")
    }

    fun getAttendeesForDepartment(department: DepartmentEntry): Attendees {
        return Attendees(
            youths = getYouthsByDepartmentId(department.id),
            youthLeaders = getYouthLeadersByDepartmentId(department.id),
        )
    }

    fun getAttendeeByCode(code: String): Attendee {
        return baseAttendeeRepository.findByCode(code)
            ?.let { getAttendee(it.id) }
            ?: throw NotFoundException("No Attendee for code $code found")
    }

    fun getAllAttendees(): Attendees {
        return Attendees(
            youths = youthRepository.findAll().toList(),
            youthLeaders = youthLeaderRepository.findAll().toList(),
        )
    }

    fun getAttendeesWithoutYouthPlanRole(): List<Attendee> {
        return (youthRepository.findAttendeesWithoutYouthPlanRole() + youthLeaderRepository.findAttendeesWithoutYouthPlanRole())
    }

    fun getAllAttendeesWithIds(ids: List<String>): List<Attendee> {
        val attendees = baseAttendeeRepository.findAllById(ids)
        val youthIds = attendees.filter { it.role == AttendeeRole.YOUTH }.map { it.id }
        val youthLeaderIds = attendees.filter { it.role == AttendeeRole.YOUTH_LEADER }.map { it.id }

        val youths = youthRepository.findAllByIds(youthIds)
        val youthLeaders = youthLeaderRepository.findAllByIds(youthLeaderIds)
        return (youths + youthLeaders)

    }

    fun getDepartmentIdsForAllAttendees(): List<Long> {
        return baseAttendeeRepository.findDistinctDepartmentIdsFromAllAttendees()
    }

    fun updateAttendeeStatus(attendee: Attendee, status: AttendeeStatus) {
        when (attendee) {
            is YouthEntry -> youthRepository.save(attendee.copy(status = status))
            is YouthLeaderEntry -> youthLeaderRepository.save(attendee.copy(status = status))
            else -> throw UnexpectedTypeException("Attendee from db is not of expected type")
        }
    }

    private fun checkFirstNameAndLastNameAreUnique(attendee: Attendee, id: String = UUID.randomUUID().toString()) {
        baseAttendeeRepository.findByDepartmentAndFirstNameAndLastName(
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
        baseAttendeeRepository.findAllBytShirtSize(oldSize).forEach {
            baseAttendeeRepository.save(it.copy(tShirtSize = newSize))
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

    private fun getAttendeeOrNull(id: String): Attendee? {
        return baseAttendeeRepository.findByIdOrNull(id)
            ?.let {
                when (it.role) {
                    AttendeeRole.YOUTH -> youthRepository.findByIdOrNull(it.id)
                    AttendeeRole.YOUTH_LEADER -> youthLeaderRepository.findByIdOrNull(it.id)
                }
            }
            ?.let { authorityService.hasAuthority(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    private fun saveAttendeeToDB(
        toSave: Attendee,
        dbAttendeeClass: KClass<out Attendee>,
        id: String,
        code: String,
        createdAt: Instant
    ): Attendee {
        if (toSave::class != dbAttendeeClass) {
            throw ChangedRoleException("The role of the attendee to save (${toSave::class}) is not the same as the stored role ($dbAttendeeClass).")
        }
        return when (toSave) {
            is YouthEntry -> youthRepository.save(toSave.copy(code = code, id = id, createdAt = createdAt))
            is YouthLeaderEntry -> youthLeaderRepository.save(toSave.copy(code = code, id = id, createdAt = createdAt))
            else -> throw UnexpectedTypeException("Attendee from db is not of expected type")
        }
    }

}