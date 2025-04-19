package de.kordondev.lagermelder.core.service.helper

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.helper.Entities
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import java.util.*

class AttendeeRoleHelperTest {
    private lateinit var attendeeRoleHelper: AttendeeRoleHelper

    @BeforeEach
    fun setup() {
        attendeeRoleHelper = AttendeeRoleHelper()
    }


    @Test
    fun getOptimizedLeaderAndAttendees() {
        val selectedYouthLeader = listOf(randomId(), randomId(), randomId())
        val selectedYouth = listOf(randomId(), randomId())
        val allAttendees = listOf(
            createAttendee(selectedYouthLeader[0], "1994-05-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(selectedYouthLeader[1], "1995-03-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(selectedYouthLeader[2], "1990-03-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(selectedYouth[0], "2005-05-01", AttendeeRole.YOUTH_LEADER),
            createAttendee(selectedYouth[1], "2011-05-01", AttendeeRole.YOUTH),
            createAttendee(randomId(), "2022-05-01", AttendeeRole.YOUTH),
        )

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
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
            attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(listOf(), allAttendees, eventDate)
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
            createAttendeeAge(selectedYouth[0], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[1], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[2], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[3], 15, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[4], 17, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[5], 17, eventDate, AttendeeRole.YOUTH),
            createAttendeeAge(selectedYouth[6], 17, eventDate, AttendeeRole.YOUTH_LEADER)
        )

        val youthPlanAttendees =
            attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(listOf(), allAttendees, eventDate)
                .groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]?.map { it.attendeeId })
            .isEqualTo(selectedYouth.drop(6))
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]?.map { it.attendeeId })
            .isEqualTo(selectedYouth.take(6))
    }


    @Test
    fun moveWithManyPeople() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val youths = createAttendees(30, eventDate)
        val leaderMovable = createLeaderMaxAge27(5, eventDate)
        val leaderStable = createLeaderOver27(10, eventDate)

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
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
            createAttendee(randomId(), eventDate.minusYears(28).toString(), AttendeeRole.YOUTH_LEADER)
        val leaderStable = createLeaderOver27(2, eventDate)

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
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
        val leaderStable = createLeaderOver27(2, eventDate)

        val youthPlanAttendees =
            attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
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
        val leaderStable = createLeaderOver27(2, eventDate)

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
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
        val youngLeader = createLeaderMaxAge27(6, eventDate)
        val oldLeader = createLeaderOver27(3, eventDate)

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            registeredYouths + youngLeader + oldLeader,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]).hasSize(17)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]).hasSize(4)
    }

    @Test
    fun distributeYoungLeader() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(14, eventDate)
        val youngLeader = createLeaderMaxAge27(7, eventDate)
        val oldLeader = createLeaderOver27(3, eventDate)

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
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
        val leaderMovable = createLeaderMaxAge27(5, eventDate)
        val leaderStable = createLeaderOver27(1, eventDate)

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            fixedLeader + fixedYouths,
            youths + leaderMovable + leaderStable,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]).hasSize(2)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]).hasSize(11)
    }

    @Test
    fun invalidYouthLeaderAreYouth() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val youths = createAttendees(10, eventDate)
        val leaderMovable = createLeaderMaxAge27(1, eventDate)
        val leaderWithoutJuleika = createLeaderWithoutJuleika(3, eventDate, 27)

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            leaderWithoutJuleika + youths + leaderMovable,
            eventDate
        ).groupBy { it.youthPlanRole }
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]).hasSize(1)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]).hasSize(13)
    }

    @Test
    fun leaderWithoutJuleikaAndToOld() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val youths = createAttendees(10, eventDate)
        val leaderMovable = createLeaderMaxAge27(1, eventDate)
        val leaderWithoutJuleika = createLeaderWithoutJuleika(3, eventDate, 27)
        val leaderWithoutJuleikaToOld = createLeaderWithoutJuleika(5, eventDate, 99)

        val youthPlanAttendees = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            listOf(),
            leaderWithoutJuleika + youths + leaderMovable + leaderWithoutJuleikaToOld,
            eventDate
        ).groupBy { it.youthPlanRole }

        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH_LEADER]).hasSize(1)
        Assertions.assertThat(youthPlanAttendees[AttendeeRole.YOUTH]).hasSize(13)
        val allDistributedAttendeeIds =
            youthPlanAttendees[AttendeeRole.YOUTH_LEADER]!! + youthPlanAttendees[AttendeeRole.YOUTH]!!
                .map { it.attendeeId }
        Assertions.assertThat(allDistributedAttendeeIds)
            .doesNotContainAnyElementsOf(leaderWithoutJuleikaToOld.map { it.id })
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "1, 5",
        "2, 10",
        "3, 15",
    )
    fun testMinYouthsFor(numberOfLeaders: Int, expectedNumberOfYouths: Int) {
        Assertions.assertThat(
            attendeeRoleHelper.youthsFor(numberOfLeaders)
        ).isEqualTo(expectedNumberOfYouths)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "1, 1",
        "5, 1",
        "6, 2",
        "7, 2",
    )
    fun testLeaderFor(numberOfYouths: Int, expectedNumberOfLeaders: Int) {
        Assertions.assertThat(
            attendeeRoleHelper.leaderFor(numberOfYouths)
        ).isEqualTo(expectedNumberOfLeaders)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "1, 1",
        "5, 1",
        "6, 1",
        "7, 2",
    )
    fun testLeaderOutOfDistributableAttendees(numberOfAttendees: Int, expectedNumberOfLeaders: Int) {
        val attendees = (1..numberOfAttendees).map { createAttendee(randomId(), "2000-01-01", AttendeeRole.YOUTH) }
        Assertions.assertThat(
            attendeeRoleHelper.leaderOutOfDistributableAttendees(attendees)
        ).isEqualTo(expectedNumberOfLeaders)
    }

    @ParameterizedTest
    @CsvSource(
        "0",
        "1",
        "2",
        "5",
        "6",
        "1354"
    )
    fun testNumberOfLeaderFitsNumberOfAttendees(youths: Int) {
        val leader = attendeeRoleHelper.leaderFor(youths)
        val calculatedYouths = attendeeRoleHelper.youthsFor(leader)
        Assertions.assertThat(
            attendeeRoleHelper.leaderFor(calculatedYouths)
        ).isEqualTo(leader)
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

    private fun createLeaderOver27(
        count: Int,
        eventDate: LocalDate,
    ): List<Attendee> {
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(randomId(), 36, eventDate, AttendeeRole.YOUTH_LEADER))
        }
        return attendees
    }

    private fun createLeaderMaxAge27(count: Int, eventDate: LocalDate): List<Attendee> {
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(randomId(), 27, eventDate, AttendeeRole.YOUTH_LEADER))
        }
        return attendees
    }

    private fun createLeaderWithoutJuleika(count: Int, eventDate: LocalDate, age: Int): List<Attendee> {
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            var attendee = createAttendeeAge(randomId(), 27, eventDate, AttendeeRole.YOUTH_LEADER) as YouthLeaderEntry
            attendee = if ((count % 2) == 0) {
                attendee.copy(juleikaNumber = "")
            } else {
                attendee.copy(juleikaExpireDate = eventDate.minusDays(1))
            }

            attendees = attendees.plus(attendee)
        }
        return attendees
    }

    private fun createAttendees(count: Int, eventDate: LocalDate) =
        (1..count).map { createAttendeeAge(randomId(), 16, eventDate, AttendeeRole.YOUTH) }

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
                department = DepartmentEntry(4L, "", "", "", "", "", emptySet(), "", false, emptySet(), null),
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
                department = DepartmentEntry(4L, "", "", "", "", "", emptySet(), "", false, emptySet(), null),
                code = "",
                status = AttendeeStatus.ENTERED,
                juleikaNumber = "12345678",
                juleikaExpireDate = LocalDate.of(2099, 5, 5)
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
                department = DepartmentEntry(4L, "", "", "", "", "", emptySet(), "", false, emptySet(), null),
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
                department = DepartmentEntry(4L, "", "", "", "", "", emptySet(), "", false, emptySet(), null),
                code = "",
                status = AttendeeStatus.ENTERED,
                juleikaNumber = "12345678",
                juleikaExpireDate = LocalDate.of(2099, 5, 5)
            )

            AttendeeRole.HELPER -> throw IllegalArgumentException("Helper are not allowed")
            AttendeeRole.Z_KID -> throw IllegalArgumentException("Z-Kids are not allowed")
        }
    }

    private fun randomId(): String {
        return UUID.randomUUID().toString()
    }
}