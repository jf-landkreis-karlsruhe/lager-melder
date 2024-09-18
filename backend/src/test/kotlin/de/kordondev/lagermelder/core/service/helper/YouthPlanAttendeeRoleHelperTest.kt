package de.kordondev.lagermelder.core.service.helper

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.helper.Entities
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class YouthPlanAttendeeRoleHelperTest {
    private lateinit var youthPlanAttendeeRoleHelper: YouthPlanAttendeeRoleHelper

    @BeforeEach
    fun setup() {
        youthPlanAttendeeRoleHelper = YouthPlanAttendeeRoleHelper()
    }


    @Test
    fun getOptimizedLeaderAndAttendees() {
        val selectedYouthLeader = listOf(randomId(), randomId(), randomId())
        val selectedYouth = listOf(randomId(), randomId())
        val allAttendees = listOf(
            createAttendee(selectedYouthLeader[1], "1995-05-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(selectedYouthLeader[2], "1996-03-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(selectedYouthLeader[0], "1990-03-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(selectedYouth[0], "2005-05-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(selectedYouth[1], "2011-05-01", AttendeeRole.YOUTH),
            createAttendee(randomId(), "2022-05-01", AttendeeRole.YOUTH),
        )

        val youthPlanAttendees = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            allAttendees,
            LocalDate.of(2023, 5, 5)
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId })
            .isEqualTo(selectedYouthLeader)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId })
            .isEqualTo(selectedYouth)
    }

    @Test
    fun toOldLeader() {
        val selectedYouthLeader = listOf(randomId(), randomId(), randomId())
        val selectedYouth = listOf(randomId(), randomId(), randomId(), randomId())
        val eventDate = LocalDate.of(2023, 5, 5)
        val allAttendees = listOf(
            createAttendeeAge(selectedYouth[0], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[1], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[2], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[3], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouthLeader[0], 35, eventDate, AttendeeRole.YOUTH_LEADER),
            createAttendeeAge(selectedYouthLeader[1], 35, eventDate, AttendeeRole.YOUTH_LEADER),
            createAttendeeAge(selectedYouthLeader[2], 35, eventDate, AttendeeRole.YOUTH_LEADER)
        )

        val youthPlanAttendees =
            youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(listOf(), allAttendees, eventDate)
                .groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId })
            .isEqualTo(selectedYouthLeader)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId })
            .isEqualTo(selectedYouth)
    }

    @Test
    fun toYoungLeader() {
        val selectedYouth = listOf(randomId(), randomId(), randomId(), randomId(), randomId(), randomId(), randomId())
        val eventDate = LocalDate.of(2023, 5, 5)
        val allAttendees = listOf(
            createAttendeeAge(selectedYouth[3], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[4], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[5], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[6], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[0], 17, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[1], 17, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[2], 17, eventDate, AttendeeRole.YOUTH_LEADER)
        )

        val youthPlanAttendees =
            youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(listOf(), allAttendees, eventDate)
                .groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId }).isNull()
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId })
            .isEqualTo(selectedYouth)
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
            createAttendee(randomId(), eventDate.minusYears(27).toString(), AttendeeRole.YOUTH_LEADER)
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
            createAttendee(randomId(), eventDate.minusYears(27).plusDays(1).toString(), AttendeeRole.YOUTH_LEADER)
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
            createAttendee(randomId(), eventDate.minusYears(18).toString(), AttendeeRole.YOUTH_LEADER)
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
        attendee: Attendee,
        role: AttendeeRole
    ): YouthPlanAttendeeRoleEntry {
        return YouthPlanAttendeeRoleEntry(
            attendeeId = attendee.id,
            attendee = BaseAttendeeEntry.of(attendee),
            departmentId = attendee.department.id,
            youthPlanRole = role
        )
    }

    private fun createLeaderAtLeast27(
        count: Int,
        eventDate: LocalDate,
    ): List<Attendee> {
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(randomId(), 36, eventDate, AttendeeRole.YOUTH_LEADER))
        }
        return attendees
    }

    private fun createLeaderBelow27(count: Int, eventDate: LocalDate): List<Attendee> {
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(randomId(), 26, eventDate, AttendeeRole.YOUTH_LEADER))
        }
        return attendees
    }

    private fun createAttendees(count: Int, eventDate: LocalDate): List<Attendee> {
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(randomId(), 16, eventDate, AttendeeRole.YOUTH))
        }
        return attendees
    }

    private fun createAttendeeAge(
        id: String,
        age: Int,
        eventDate: LocalDate,
        role: AttendeeRole,
    ): Attendee {
        val birthday = "${eventDate.year - age}-${eventDate.monthValue}-${eventDate.dayOfMonth - 1}"
        return createAttendee(id, birthday, role)
    }

    private fun createAttendee(id: String, birthday: String, role: AttendeeRole): Attendee {
        return when (role) {
            AttendeeRole.YOUTH -> YouthEntry(
                id = id,
                firstName = "first",
                lastName = "name",
                birthday = birthday,
                food = Food.MEAT,
                tShirtSize = Entities.TShirtSizeMock.L.size,
                additionalInformation = "",
                role = role,
                department = DepartmentEntry(4L, "", "", "", "", ""),
                code = "",
                status = AttendeeStatus.ENTERED,
            )

            AttendeeRole.YOUTH_LEADER -> YouthLeaderEntry(
                id = id,
                firstName = "first",
                lastName = "name",
                birthday = birthday,
                food = Food.MEAT,
                tShirtSize = Entities.TShirtSizeMock.L.size,
                additionalInformation = "",
                role = role,
                department = DepartmentEntry(4L, "", "", "", "", ""),
                code = "",
                status = AttendeeStatus.ENTERED,
            )

            AttendeeRole.CHILD -> ChildEntry(
                id = id,
                firstName = "first",
                lastName = "name",
                birthday = birthday,
                food = Food.MEAT,
                tShirtSize = Entities.TShirtSizeMock.L.size,
                additionalInformation = "",
                role = role,
                department = DepartmentEntry(4L, "", "", "", "", ""),
                code = "",
                status = AttendeeStatus.ENTERED,
            )

            AttendeeRole.CHILD_LEADER -> ChildLeaderEntry(
                id = id,
                firstName = "first",
                lastName = "name",
                birthday = birthday,
                food = Food.MEAT,
                tShirtSize = Entities.TShirtSizeMock.L.size,
                additionalInformation = "",
                role = role,
                department = DepartmentEntry(4L, "", "", "", "", ""),
                code = "",
                status = AttendeeStatus.ENTERED,
            )
        }
    }

    private fun randomId(): String {
        return UUID.randomUUID().toString()
    }
}