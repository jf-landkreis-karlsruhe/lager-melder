package de.kordondev.attendee.core.service

import de.kordondev.attendee.Helper
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.YouthPlanAttendeeRoleEntry
import de.kordondev.attendee.core.persistence.repository.YouthPlanAttendeeRoleRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class YouthPlanAttendeeRoleService(
    private val youthPlanAttendeeRoleRepository: YouthPlanAttendeeRoleRepository,
    private val attendeeService: AttendeeService,
    private val settingsService: SettingsService
) {

    fun saveAll(attendeeRoles: List<YouthPlanAttendeeRoleEntry>): List<YouthPlanAttendeeRoleEntry> {
        return youthPlanAttendeeRoleRepository.saveAll(attendeeRoles).toList()
    }

    fun getAll(): List<YouthPlanAttendeeRoleEntry> {
        return youthPlanAttendeeRoleRepository.findAll().toList()
    }

    fun deleteById(attendeeId: Long) {
        youthPlanAttendeeRoleRepository.deleteById(attendeeId)
    }


    fun getOptimizedLeaderAndAttendeeIds(): List<YouthPlanAttendeeRoleEntry> {
        val undistributedAttendees = attendeeService.getAttendeesWithoutYouthPlanRole()
        val distributedAttendees = getAll()
        val newDistributed = getOptimizedLeaderAndAttendeeIdsInner(
            distributedAttendees,
            undistributedAttendees,
            settingsService.getSettings().eventStart
        )
        saveAll(newDistributed)
        return distributedAttendees + newDistributed
    }

    private fun getOptimizedLeaderAndAttendeeIdsInner(
        youthPlanAttendeeRoles: List<YouthPlanAttendeeRoleEntry>,
        undistributedAttendees: List<AttendeeEntry>,
        eventStart: LocalDate
    ): List<YouthPlanAttendeeRoleEntry> {
        if (undistributedAttendees.isNotEmpty()) {
            val leader = youthPlanAttendeeRoles.filter { it.youthPlanRole === AttendeeRole.YOUTH_LEADER }
            return distributeNewAttendees(undistributedAttendees, eventStart, leader.size, youthPlanAttendeeRoles.size)
        }
        return listOf()
    }

    private val oldFirst = compareBy<AttendeeEntry> { it.birthday }
    private val oldFirstThenFirstname = oldFirst.thenByDescending { it.firstName }
    private fun distributeNewAttendees(
        newAttendees: List<AttendeeEntry>,
        eventStart: LocalDate,
        allAttendeesSize: Int,
        fixedLeaderSize: Int?
    ): List<YouthPlanAttendeeRoleEntry> {
        // birthday: "2020-03-30" "yyyy-MM-dd"
        var (youth, leader) = newAttendees
            .sortedWith(oldFirstThenFirstname)
            .filter { Helper.ageAtEvent(it.birthday, eventStart) >= 6 }
            .partition { Helper.ageAtEvent(it.birthday, eventStart) <= 26 }

        val allLeaderSize = leader.size + (fixedLeaderSize ?: 0)
        val correctDistributedAttendees = allLeaderSize + allLeaderSize * 5
        val toMuchYouths = allAttendeesSize - correctDistributedAttendees
        val possibleLeaderCount = toMuchYouths / 6
        if (possibleLeaderCount > 0) {
            // move x first of attendees, who are at least 18, to the end of leader
            val newLeader = youth
                .subList(0, possibleLeaderCount)
                .filter { Helper.ageAtEvent(it.birthday, eventStart) >= 18 }
            leader = leader.plus(newLeader)
            youth = youth.subList(newLeader.size, youth.size)
        }
        var newYouthPlanRoles = listOf<YouthPlanAttendeeRoleEntry>()
        for (l in leader) {
            newYouthPlanRoles = newYouthPlanRoles.plus(
                YouthPlanAttendeeRoleEntry(
                    attendeeId = l.id,
                    attendee = l,
                    departmentId = l.department.id,
                    youthPlanRole = AttendeeRole.YOUTH_LEADER,
                )
            )
        }
        for (y in youth) {
            newYouthPlanRoles = newYouthPlanRoles.plus(
                YouthPlanAttendeeRoleEntry(
                    attendeeId = y.id,
                    attendee = y,
                    departmentId = y.department.id,
                    youthPlanRole = AttendeeRole.YOUTH,
                )
            )
        }
        return newYouthPlanRoles
    }
}
