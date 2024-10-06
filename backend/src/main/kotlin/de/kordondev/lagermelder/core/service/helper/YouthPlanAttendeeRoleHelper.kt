package de.kordondev.lagermelder.core.service.helper

import de.kordondev.lagermelder.Helper
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.BaseAttendeeEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthPlanAttendeeRoleEntry
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class YouthPlanAttendeeRoleHelper {

    fun getOptimizedLeaderAndAttendeeIds(
        youthPlanAttendeeRoles: List<YouthPlanAttendeeRoleEntry>,
        undistributedAttendees: List<Attendee>,
        eventStart: LocalDate
    ): List<YouthPlanAttendeeRoleEntry> {
        if (undistributedAttendees.isNotEmpty()) {
            val leader = youthPlanAttendeeRoles.filter { it.youthPlanRole === AttendeeRole.YOUTH_LEADER }
            return distributeNewAttendees(undistributedAttendees, eventStart, youthPlanAttendeeRoles.size, leader.size)
        }
        return listOf()
    }

    private val oldFirst = compareBy<Attendee> { Helper.getBirthday(it) }
    private val oldFirstThenFirstname = oldFirst.thenByDescending { it.firstName }
    private fun distributeNewAttendees(
        newAttendees: List<Attendee>,
        eventStart: LocalDate,
        fixedDistributedAttendeesSize: Int,
        fixedLeaderSize: Int
    ): List<YouthPlanAttendeeRoleEntry> {

        val (attendees, leaderWithoutJuleika) = newAttendees
            .partition { leaderWithValidJuleika(it, eventStart) }

        // birthday: "2020-03-30" "yyyy-MM-dd"
        var (youth, leader) = attendees
            .sortedWith(oldFirstThenFirstname)
            .filter { Helper.ageAtEvent(it, eventStart) >= 6 }
            .partition { Helper.ageAtEvent(it, eventStart) <= 26 }

        youth = youth + leaderWithoutJuleika
        val allLeaderSize = leader.size + fixedLeaderSize
        val correctDistributedAttendees = allLeaderSize + allLeaderSize * 5
        val toMuchYouths = (fixedDistributedAttendeesSize + newAttendees.size) - correctDistributedAttendees
        val possibleLeaderCount = toMuchYouths / 6
        if (possibleLeaderCount > 0) {
            // move x first of attendees, who are at least 18, to the end of leader
            val newLeader = youth
                .subList(0, possibleLeaderCount)
                .filter { Helper.ageAtEvent(it, eventStart) >= 18 && leaderWithValidJuleika(it, eventStart) }
            leader = leader.plus(newLeader)
            youth = youth.subList(newLeader.size, youth.size)
        }
        var newYouthPlanRoles = listOf<YouthPlanAttendeeRoleEntry>()
        for (l in leader) {
            newYouthPlanRoles = newYouthPlanRoles.plus(
                YouthPlanAttendeeRoleEntry(
                    attendeeId = l.id,
                    attendee = BaseAttendeeEntry.of(l),
                    departmentId = l.department.id,
                    youthPlanRole = AttendeeRole.YOUTH_LEADER,
                )
            )
        }
        for (y in youth) {
            newYouthPlanRoles = newYouthPlanRoles.plus(
                YouthPlanAttendeeRoleEntry(
                    attendeeId = y.id,
                    attendee = BaseAttendeeEntry.of(y),
                    departmentId = y.department.id,
                    youthPlanRole = AttendeeRole.YOUTH,
                )
            )
        }
        return newYouthPlanRoles
    }

    private fun leaderWithValidJuleika(attendee: Attendee, eventStart: LocalDate): Boolean {
        if (attendee !is YouthLeaderEntry) {
            return true
        }
        return attendee.juleikaNumber.isNotEmpty() && attendee.juleikaExpireDate?.isAfter(eventStart) ?: false
    }
}
