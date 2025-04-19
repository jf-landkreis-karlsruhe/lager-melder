package de.kordondev.lagermelder.core.service.helper

import de.kordondev.lagermelder.Helper
import de.kordondev.lagermelder.Helper.Companion.ageAtEvent
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.BaseAttendeeEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthPlanAttendeeRoleEntry
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.math.ceil

// https://jugendarbeitsnetz.de/landesjugendplan/ii-zur-foerderung-der-kinder-und-jugenderholung
@Service
class AttendeeRoleHelper {

    private final val minAge = 6
    private final val maxAgeYouth = 27
    private final val youthPerLeader = 5

    fun getOptimizedLeaderAndAttendeeIds(
        youthPlanAttendeeRoles: List<YouthPlanAttendeeRoleEntry>,
        undistributedAttendees: List<Attendee>,
        eventStart: LocalDate
    ): List<YouthPlanAttendeeRoleEntry> {
        if (undistributedAttendees.isNotEmpty()) {
            val groupedAttendeeRoles = youthPlanAttendeeRoles.groupBy { it.youthPlanRole }
            return distributeNewAttendees(
                undistributedAttendees.filter { ageAtEvent(it, eventStart) >= minAge },
                eventStart,
                groupedAttendeeRoles[AttendeeRole.YOUTH]?.size ?: 0,
                groupedAttendeeRoles[AttendeeRole.YOUTH_LEADER]?.size ?: 0,
            )
        }
        return listOf()
    }

    private fun distributeNewAttendees(
        newAttendees: List<Attendee>,
        eventStart: LocalDate,
        fixedYouthsSize: Int,
        fixedLeaderSize: Int
    ): List<YouthPlanAttendeeRoleEntry> {

        val (validJuleika, invalidJuleika) = newAttendees
            .partition { leaderWithValidJuleika(it, eventStart) }

        val (validJuleikaMaxAge27, validJuleikaOver27) = validJuleika
            .partition { Helper.ageAtEvent(it, eventStart) <= maxAgeYouth }
        val (invalidJuleikaMaxAge27, invalidJuleikaOver27) = invalidJuleika
            .partition { Helper.ageAtEvent(it, eventStart) <= maxAgeYouth }

        val totalYouths = fixedYouthsSize + invalidJuleikaMaxAge27.size
        val totalYouthLeader = fixedLeaderSize + validJuleikaOver27.size

        var distributableAttendees = validJuleikaMaxAge27
        val setAsLeader = mutableListOf<Attendee>()
        val setAsYouth = mutableListOf<Attendee>()

        // more leader that youths, fill up youths
        if (totalYouths < youthsFor(totalYouthLeader)) {
            val moreYouths = youthsFor(totalYouthLeader) - totalYouths
            setAsYouth.addAll(distributableAttendees.take(moreYouths))
            distributableAttendees = distributableAttendees.drop(moreYouths)
        }

        // more youths that leader
        if (totalYouths > youthsFor(totalYouthLeader)) {
            val moreLeader = leaderFor(totalYouths) - totalYouthLeader
            setAsLeader.addAll(distributableAttendees.take(moreLeader))
            distributableAttendees = distributableAttendees.drop(moreLeader)

            // fill up until youths
            val moreYouths = youthsFor(totalYouthLeader + moreLeader)
            setAsYouth.addAll(distributableAttendees.take(moreYouths))
            distributableAttendees = distributableAttendees.drop(moreYouths)
        }

        // distribute the rest of the attendees 1:5
        val maxLeader = leaderOutOfDistributableAttendees(distributableAttendees)
        setAsLeader.addAll(distributableAttendees.take(maxLeader))
        distributableAttendees = distributableAttendees.drop(maxLeader)
        setAsYouth.addAll(distributableAttendees)

        var newYouthPlanRoles = listOf<YouthPlanAttendeeRoleEntry>()
        for (l in setAsLeader + validJuleikaOver27) {
            newYouthPlanRoles = newYouthPlanRoles.plus(
                YouthPlanAttendeeRoleEntry(
                    attendeeId = l.id,
                    attendee = BaseAttendeeEntry.of(l),
                    departmentId = l.department.id,
                    youthPlanRole = AttendeeRole.YOUTH_LEADER,
                )
            )
        }
        for (y in setAsYouth + invalidJuleikaMaxAge27) {
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

    fun leaderWithValidJuleika(attendee: Attendee, eventStart: LocalDate): Boolean {
        if (attendee !is YouthLeaderEntry) {
            return false
        }
        return attendee.juleikaNumber.isNotEmpty() && (attendee.juleikaExpireDate?.isAfter(eventStart) ?: false)
    }

    fun youthsFor(youthLeaderCount: Int) = youthLeaderCount * youthPerLeader
    fun leaderFor(youths: Int) = ceil(youths / youthPerLeader.toDouble()).toInt()
    fun leaderOutOfDistributableAttendees(attendees: List<Attendee>) =
        ceil(attendees.size / (youthPerLeader + 1.0)).toInt()
}
