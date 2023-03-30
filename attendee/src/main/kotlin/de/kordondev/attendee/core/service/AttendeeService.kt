package de.kordondev.attendee.core.service

import de.kordondev.attendee.Helper
import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.core.security.PasswordGenerator
import de.kordondev.attendee.exception.NotFoundException
import de.kordondev.attendee.exception.UniqueException
import de.kordondev.attendee.exception.WrongTimeException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AttendeeService(
    private val attendeeRepository: AttendeeRepository,
    private val authorityService: AuthorityService,
    private val settingsService: SettingsService
) {
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

    private val oldFirst = compareBy<Attendee> {it.birthday}
    private val oldFirstThenFirstname = oldFirst.thenByDescending {it.firstName}
    fun distributeForYouthPlan(allAttendees: List<Attendee>, eventStart: LocalDate): Pair<List<Attendee>, List<Attendee>> {
        // birthday: "2020-03-30" "yyyy-MM-dd"
        var fixedAttendees = allAttendees.groupBy { it.youthPlanRole }

        var undistributedAttendees = fixedAttendees[null]
        if (undistributedAttendees != null) {
            var (youth, leader) = undistributedAttendees
                .sortedWith(oldFirstThenFirstname)
                .filter { Helper.ageAtEvent(it.birthday, eventStart) >= 6 }
                .partition { Helper.ageAtEvent(it.birthday, eventStart) <= 26 }

            val allLeader = leader + fixedAttendees[AttendeeRole.YOUTH_LEADER]
            val correctDistributedAttendees = allLeader.size + allLeader.size * 5
            val toMuchYouths = allAttendees.size - correctDistributedAttendees
            val possibleLeaderCount = toMuchYouths / 6
            if (possibleLeaderCount > 0) {
                // move x first of attendees, who are at least 18, to the end of leader
                val newLeader = youth
                    .subList(0, possibleLeaderCount)
                    .filter { Helper.ageAtEvent(it.birthday, eventStart) >= 18 }
                leader = leader.plus(newLeader)
                youth = youth.subList(newLeader.size, youth.size)
                for (l in leader) {
                    l.youthPlanRole = AttendeeRole.YOUTH_LEADER
                }
                for (y in youth) {
                    y.youthPlanRole = AttendeeRole.YOUTH
                }
                attendeeRepository.saveAll((leader + youth).map { AttendeeEntry.of(it, it.status) })
            }
        }

        return Pair(youth, leader)
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