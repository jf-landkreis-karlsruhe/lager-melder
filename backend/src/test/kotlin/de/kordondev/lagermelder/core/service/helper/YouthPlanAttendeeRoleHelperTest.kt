package de.kordondev.lagermelder.core.service.helper

import de.kordondev.lagermelder.core.persistence.entry.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class YouthPlanAttendeeRoleHelperTest {
    private lateinit var youthPlanAttendeeRoleHelper: YouthPlanAttendeeRoleHelper

    @BeforeEach
    fun setup() {
        youthPlanAttendeeRoleHelper = YouthPlanAttendeeRoleHelper()
    }


    @Test
    fun getOptimizedLeaderAndAttendees() {
        val allAttendees = listOf(
            createAttendee(1L, "1995-05-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(2L, "1996-03-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(6L, "1990-03-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(3L, "2005-05-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(4L, "2011-05-01", AttendeeRole.YOUTH),
            createAttendee(5L, "2022-05-01", AttendeeRole.YOUTH),
        )

        val youthPlanAttendees = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            allAttendees,
            LocalDate.of(2023, 5, 5)
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId })
            .isEqualTo(listOf(6L, 1L, 2L))
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId })
            .isEqualTo(listOf(3L, 4L))
    }

    @Test
    fun toOldLeader() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val allAttendees = listOf(
            createAttendeeAge(1L, 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(2L, 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(3L, 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(4L, 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(5L, 35, eventDate, AttendeeRole.YOUTH_LEADER),
            createAttendeeAge(6L, 35, eventDate, AttendeeRole.YOUTH_LEADER),
            createAttendeeAge(7L, 35, eventDate, AttendeeRole.YOUTH_LEADER)
        )

        val youthPlanAttendees =
            youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(listOf(), allAttendees, eventDate)
                .groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId })
            .isEqualTo(listOf(5L, 6L, 7L))
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId })
            .isEqualTo(listOf(1L, 2L, 3L, 4L))
    }

    @Test
    fun toYoungLeader() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val allAttendees = listOf(
            createAttendeeAge(1L, 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(2L, 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(3L, 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(4L, 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(5L, 17, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(6L, 17, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(7L, 17, eventDate, AttendeeRole.YOUTH_LEADER)
        )

        val youthPlanAttendees =
            youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(listOf(), allAttendees, eventDate)
                .groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId }).isNull()
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId })
            .isEqualTo(listOf(5L, 6L, 7L, 1L, 2L, 3L, 4L))
    }


    @Test
    fun moveWithManyPeople() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val youths = createAttendees(30, eventDate)
        val leaderMovable = createLeaderBelow27(5, eventDate)
        val leaderStable = createLeaderAtLeast27(10, eventDate)

        val youthPlanAttendees = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            youths + leaderMovable + leaderStable,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]).hasSize(10)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]).hasSize(35)
    }

    @Test
    fun toOldToMoveAtEventStart() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(9, eventDate)
        val birthdayAtEventStart =
            createAttendee(100L, eventDate.minusYears(27).toString(), AttendeeRole.YOUTH_LEADER)
        val leaderStable = createLeaderAtLeast27(2, eventDate)

        val youthPlanAttendees = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            registeredYouths + birthdayAtEventStart + leaderStable,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId }
            ?.contains(birthdayAtEventStart.id))
            .isEqualTo(true)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId }
            ?.contains(birthdayAtEventStart.id))
            .isEqualTo(false)
    }

    @Test
    fun justNOTToOldToMove() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val youths = createAttendees(9, eventDate)
        val birthdayAtEventStart =
            createAttendee(100L, eventDate.minusYears(27).plusDays(1).toString(), AttendeeRole.YOUTH_LEADER)
        val leaderStable = createLeaderAtLeast27(2, eventDate)

        val youthPlanAttendees =
            youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
                listOf(),
                youths + listOf(birthdayAtEventStart) + leaderStable,
                eventDate
            ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId }
            ?.contains(birthdayAtEventStart.id))
            .isEqualTo(false)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId }
            ?.contains(birthdayAtEventStart.id))
            .isEqualTo(true)
    }

    @Test
    fun oldEnoughToMoveAtEventStart() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(9, eventDate)
        val birthdayAtEventStart =
            createAttendee(100L, eventDate.minusYears(18).toString(), AttendeeRole.YOUTH_LEADER)
        val leaderStable = createLeaderAtLeast27(2, eventDate)

        val youthPlanAttendees = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            registeredYouths + birthdayAtEventStart + leaderStable,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId }
            ?.contains(birthdayAtEventStart.id))
            .isEqualTo(false)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId }
            ?.contains(birthdayAtEventStart.id))
            .isEqualTo(true)
    }

    @Test
    fun canNOTDistributeYoungLeader() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(12, eventDate)
        val youngLeader = createLeaderBelow27(6, eventDate)
        val oldLeader = createLeaderAtLeast27(3, eventDate)

        val youthPlanAttendees = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            registeredYouths + youngLeader + oldLeader,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]).hasSize(18)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]).hasSize(3)
    }

    @Test
    fun distributeYoungLeader() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(14, eventDate)
        val youngLeader = createLeaderBelow27(7, eventDate)
        val oldLeader = createLeaderAtLeast27(3, eventDate)

        val youthPlanAttendees = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            registeredYouths + youngLeader + oldLeader,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]).hasSize(20)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]).hasSize(4)
    }

    @Test
    fun moveWithFixed() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val fixedYouths = createAttendees(10, eventDate).map { attendeeToYouthPlanAttendeeRole(it, AttendeeRole.YOUTH) }
        val fixedLeader =
            createAttendees(3, eventDate).map { attendeeToYouthPlanAttendeeRole(it, AttendeeRole.YOUTH_LEADER) }
        val youths = createAttendees(7, eventDate)
        val leaderMovable = createLeaderBelow27(5, eventDate)
        val leaderStable = createLeaderAtLeast27(1, eventDate)

        val youthPlanAttendees = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            fixedLeader + fixedYouths,
            youths + leaderMovable + leaderStable,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]).hasSize(1)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]).hasSize(12)
    }


    private fun attendeeToYouthPlanAttendeeRole(
        attendee: AttendeeEntry,
        role: AttendeeRole
    ): YouthPlanAttendeeRoleEntry {
        return YouthPlanAttendeeRoleEntry(
            attendeeId = attendee.id,
            attendee = attendee,
            departmentId = attendee.department.id,
            youthPlanRole = role
        )
    }

    private fun createLeaderAtLeast27(
        count: Int,
        eventDate: LocalDate,
    ): List<AttendeeEntry> {
        var id = 1L
        var attendees = listOf<AttendeeEntry>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(id, 36, eventDate, AttendeeRole.YOUTH_LEADER))
            id++
        }
        return attendees
    }

    private fun createLeaderBelow27(count: Int, eventDate: LocalDate): List<AttendeeEntry> {
        var id = 1L
        var attendees = listOf<AttendeeEntry>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(id, 26, eventDate, AttendeeRole.YOUTH_LEADER))
            id++
        }
        return attendees
    }

    private fun createAttendees(count: Int, eventDate: LocalDate): List<AttendeeEntry> {
        var id = 1L
        var attendees = listOf<AttendeeEntry>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(id, 16, eventDate, AttendeeRole.YOUTH))
            id++
        }
        return attendees
    }

    private fun createAttendeeAge(
        id: Long,
        age: Int,
        eventDate: LocalDate,
        role: AttendeeRole,
    ): AttendeeEntry {
        val birthday = "${eventDate.year - age}-${eventDate.monthValue}-${eventDate.dayOfMonth - 1}"
        return createAttendee(id, birthday, role)
    }

    private fun createAttendee(id: Long, birthday: String, role: AttendeeRole): AttendeeEntry {
        return AttendeeEntry(
            id = id,
            firstName = "first",
            lastName = "name",
            birthday = birthday,
            food = Food.MEAT,
            tShirtSize = TShirtSize.L,
            additionalInformation = "",
            role = role,
            department = DepartmentEntry(4L, "", "", ""),
            code = "",
            status = AttendeeStatus.ENTERED,
        )
    }
}