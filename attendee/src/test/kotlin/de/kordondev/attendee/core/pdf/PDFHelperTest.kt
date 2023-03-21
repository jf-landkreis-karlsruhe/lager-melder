package de.kordondev.attendee.core.pdf

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.AttendeeStatus
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PDFHelperTest {

    private lateinit var pdfHelper: PDFHelper

    @BeforeEach
    fun setup() {
        pdfHelper = PDFHelper()

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

        val (youth, leader) = pdfHelper.getOptimizedLeaderAndAttendees(allAttendees, LocalDate.of(2023, 5, 5))
        Assertions.assertThat(leader.map { it.id }).isEqualTo(listOf(6L, 1L, 2L))
        Assertions.assertThat(youth.map { it.id }).isEqualTo(listOf(3L, 4L))
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

        val (youth, leader) = pdfHelper.getOptimizedLeaderAndAttendees(allAttendees, eventDate)
        Assertions.assertThat(leader.map { it.id }).isEqualTo(listOf(5L, 6L, 7L))
        Assertions.assertThat(youth.map { it.id }).isEqualTo(listOf(1L, 2L, 3L, 4L))
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

        val (youth, leader) = pdfHelper.getOptimizedLeaderAndAttendees(allAttendees, eventDate)
        Assertions.assertThat(leader.map { it.id }).isEqualTo(emptyList<Attendee>())
        Assertions.assertThat(youth.map { it.id }).isEqualTo(listOf(5L, 6L, 7L, 1L, 2L, 3L, 4L))
    }


    @Test
    fun moveWithManyPeople() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val youths = createAttendees(30, eventDate)
        val leaderMovable = createLeaderBelow27(5, eventDate)
        val leaderStable = createLeaderAtLeast27(10, eventDate)

        val (youth, leader)  = pdfHelper.getOptimizedLeaderAndAttendees(youths + leaderMovable + leaderStable, eventDate)
        Assertions.assertThat(leader).hasSize(10)
        Assertions.assertThat(youth).hasSize(35)
    }

    @Test
    fun toOldToMoveAtEventStart() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(9, eventDate)
        val birthdayAtEventStart = createAttendee(100L, eventDate.minusYears(27).toString(), AttendeeRole.YOUTH_LEADER)
        val leaderStable = createLeaderAtLeast27(2, eventDate)

        val (youth, leader) = pdfHelper.getOptimizedLeaderAndAttendees(registeredYouths + birthdayAtEventStart + leaderStable, eventDate)
        Assertions.assertThat(leader.contains(birthdayAtEventStart)).isEqualTo(true)
        Assertions.assertThat(youth.contains(birthdayAtEventStart)).isEqualTo(false)
    }

    @Test
    fun justNOTToOldToMove() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val youths = createAttendees(9, eventDate)
        val birthdayAtEventStart =
            createAttendee(100L, eventDate.minusYears(27).plusDays(1).toString(), AttendeeRole.YOUTH_LEADER)
        val leaderStable = createLeaderAtLeast27(2, eventDate)

        val (youth, leader) =
            pdfHelper.getOptimizedLeaderAndAttendees(youths + listOf(birthdayAtEventStart) + leaderStable, eventDate)
        Assertions.assertThat(leader.contains(birthdayAtEventStart)).isEqualTo(false)
        Assertions.assertThat(youth.contains(birthdayAtEventStart)).isEqualTo(true)
    }

    @Test
    fun oldEnoughToMoveAtEventStart() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(9, eventDate)
        val birthdayAtEventStart = createAttendee(100L, eventDate.minusYears(18).toString(), AttendeeRole.YOUTH_LEADER)
        val leaderStable = createLeaderAtLeast27(2, eventDate)

        val (youths, leader) = pdfHelper.getOptimizedLeaderAndAttendees(registeredYouths + birthdayAtEventStart + leaderStable, eventDate)
        Assertions.assertThat(leader.contains(birthdayAtEventStart)).isEqualTo(false)
        Assertions.assertThat(youths.contains(birthdayAtEventStart)).isEqualTo(true)
    }

    @Test
    fun canNOTDistributeYoungLeader() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(12, eventDate)
        val youngLeader = createLeaderBelow27(6, eventDate)
        val oldLeader = createLeaderAtLeast27(3, eventDate)

        val (youths, leader) = pdfHelper.getOptimizedLeaderAndAttendees(registeredYouths + youngLeader + oldLeader, eventDate)
        Assertions.assertThat(youths.size).isEqualTo(18)
        Assertions.assertThat(leader.size).isEqualTo(3)
    }

    @Test
    fun distributeYoungLeader() {
        val eventDate = LocalDate.of(2023, 5, 5)
        val registeredYouths = createAttendees(14, eventDate)
        val youngLeader = createLeaderBelow27(7, eventDate)
        val oldLeader = createLeaderAtLeast27(3, eventDate)

        val (youths, leader) = pdfHelper.getOptimizedLeaderAndAttendees(registeredYouths + youngLeader + oldLeader, eventDate)
        Assertions.assertThat(youths.size).isEqualTo(20)
        Assertions.assertThat(leader.size).isEqualTo(4)
    }

    fun createLeaderAtLeast27(count: Int, eventDate: LocalDate): List<Attendee> {
        var id = 1L
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(id, 36, eventDate, AttendeeRole.YOUTH_LEADER))
            id++
        }
        return attendees
    }

    fun createLeaderBelow27(count: Int, eventDate: LocalDate): List<Attendee> {
        var id = 1L
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(id, 26, eventDate, AttendeeRole.YOUTH_LEADER))
            id++
        }
        return attendees
    }

    fun createAttendees(count: Int, eventDate: LocalDate): List<Attendee> {
        var id = 1L
        var attendees = listOf<Attendee>()
        for (i in 1..count) {
            attendees = attendees.plus(createAttendeeAge(id, 16, eventDate, AttendeeRole.YOUTH))
            id++
        }
        return attendees
    }

    fun createAttendeeAge(id: Long, age: Int, eventDate: LocalDate, role: AttendeeRole): Attendee {
        val birthday = "${eventDate.year - age}-${eventDate.monthValue}-${eventDate.dayOfMonth - 1}"
        return createAttendee(id, birthday, role)
    }

    fun createAttendee(id: Long, birthday: String, role: AttendeeRole): Attendee {
        return Attendee(
            id = id,
            firstName = "first",
            lastName = "name",
            birthday = birthday,
            food = Food.MEAT,
            tShirtSize = TShirtSize.L,
            additionalInformation = "",
            role = role,
            department = Department(4L, "", "", ""),
            code = "",
            status = AttendeeStatus.ENTERED
        )
    }
}
