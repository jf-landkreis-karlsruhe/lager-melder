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

        val result = pdfHelper.getOptimizedLeaderAndAttendees(allAttendees, LocalDate.of(2023, 5,5))
        val leader = result.first
        Assertions.assertThat(leader.map { it.id }).isEqualTo(listOf(6L, 1L, 2L))

        val attendees = result.first
        Assertions.assertThat(attendees.map { it.id }).isEqualTo(listOf(6L, 1L, 2L))
    }

    @Test
    fun toOldLeader() {
      val eventDate = LocalDate.of(2023, 5,5)
      val allAttendees = listOf(
        createAttendeeAge(1L, 15, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(2L, 15, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(3L, 15, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(4L, 15, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(5L, 35, eventDate, AttendeeRole.YOUTH_LEADER),
        createAttendeeAge(6L, 35, eventDate, AttendeeRole.YOUTH_LEADER),
        createAttendeeAge(7L, 35, eventDate, AttendeeRole.YOUTH_LEADER)
      )

        val result = pdfHelper.getOptimizedLeaderAndAttendees(allAttendees, eventDate)
        val leader = result.first
        Assertions.assertThat(leader.map { it.id }).isEqualTo(listOf(5L, 6L, 7L))

        val attendees = result.first
        Assertions.assertThat(attendees.map { it.id }).isEqualTo(listOf(1L, 2L, 3L, 4L))
    }

    @Test
    fun toYoungLeader() {
      val eventDate = LocalDate.of(2023, 5,5)
      val allAttendees = listOf(
        createAttendeeAge(1L, 15, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(2L, 15, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(3L, 15, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(4L, 15, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(5L, 17, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(6L, 17, eventDate, AttendeeRole.YOUTH),
        createAttendeeAge(7L, 17, eventDate, AttendeeRole.YOUTH_LEADER)
      )

        val result = pdfHelper.getOptimizedLeaderAndAttendees(allAttendees, eventDate)
        val leader = result.first
        Assertions.assertThat(leader.map { it.id }).isEqualTo(listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L))

        val attendees = result.first
        Assertions.assertThat(attendees.map { it.id }).isEqualTo(emptyList<Attendee>())
    }


    @Test
    fun moveWithManyPeople() {
      val eventDate = LocalDate.of(2023, 5,5)
      val youths = createAttendees(30, eventDate)
      val leaderMovable = createLeaderBelow27(5, eventDate)
      val leaderStable = createLeaderAtLeast27(10, eventDate)

      val result = pdfHelper.getOptimizedLeaderAndAttendees(youths + leaderMovable + leaderStable, eventDate)
      val leader = result.first
      Assertions.assertThat(leader).hasSize(10)

      val attendees = result.first
      Assertions.assertThat(attendees).hasSize(35)
    }
    
    @Test
    fun toOldToMoveAtEventStart() {
      val eventDate = LocalDate.of(2023, 5,5)
      val youths = createAttendees(9, eventDate)
      val birthdayAtEventStart = createAttendee(100L, eventDate.minusYears(27).toString(), AttendeeRole.YOUTH_LEADER)
      val leaderStable = createLeaderAtLeast27(2, eventDate)

      val result = pdfHelper.getOptimizedLeaderAndAttendees(youths + birthdayAtEventStart + leaderStable, eventDate)
      val leader = result.first
      Assertions.assertThat(leader.contains(birthdayAtEventStart)).isEqualTo(true)

      val attendees = result.first
      Assertions.assertThat(attendees.contains(birthdayAtEventStart)).isEqualTo(false)
    }

    @Test
    fun justNOTToOldToMove() {
      val eventDate = LocalDate.of(2023, 5,5)
      val youths = createAttendees(9, eventDate)
      val birthdayAtEventStart = createAttendee(100L, eventDate.minusYears(27).plusDays(1).toString(), AttendeeRole.YOUTH_LEADER)
      val leaderStable = createLeaderAtLeast27(2, eventDate)

      val result = pdfHelper.getOptimizedLeaderAndAttendees(youths + birthdayAtEventStart + leaderStable, eventDate)
      val leader = result.first
      Assertions.assertThat(leader.contains(birthdayAtEventStart)).isEqualTo(false)

      val attendees = result.first
      Assertions.assertThat(attendees.contains(birthdayAtEventStart)).isEqualTo(true)
    }

    @Test
    fun oldEnougthToMoveAtEventStart() {
      val eventDate = LocalDate.of(2023, 5,5)
      val youths = createAttendees(9, eventDate)
      val birthdayAtEventStart = createAttendee(100L, eventDate.minusYears(18).toString(), AttendeeRole.YOUTH_LEADER)
      val leaderStable = createLeaderAtLeast27(2, eventDate)

      val result = pdfHelper.getOptimizedLeaderAndAttendees(youths + birthdayAtEventStart + leaderStable, eventDate)
      val leader = result.first
      Assertions.assertThat(leader.contains(birthdayAtEventStart)).isEqualTo(true)

      val attendees = result.first
      Assertions.assertThat(attendees.contains(birthdayAtEventStart)).isEqualTo(false)
    }


    fun createLeaderAtLeast27(count: Int, eventDate: LocalDate): List<Attendee> {
      var id = 1L
      var attendees = listOf<Attendee>()
      for (i in 1..count) {
        attendees.plus(createAttendeeAge(id, 36, eventDate, AttendeeRole.YOUTH_LEADER))
        id++
      }
      return attendees
    }

    fun createLeaderBelow27(count: Int, eventDate: LocalDate): List<Attendee> {
      var id = 1L
      var attendees = listOf<Attendee>()
      for (i in 1..count) {
        attendees.plus(createAttendeeAge(id, 26, eventDate, AttendeeRole.YOUTH_LEADER))
        id++
      }
      return attendees
    }
    
    fun createAttendees(count: Int, eventDate: LocalDate): List<Attendee> {
      var id = 1L
      var attendees = listOf<Attendee>()
      for (i in 1..count) {
        attendees.plus(createAttendeeAge(id, 16, eventDate, AttendeeRole.YOUTH))
        id++
      }
      return attendees
    }

    fun createAttendeeAge(id: Long, age: Int, eventDate: LocalDate, role: AttendeeRole): Attendee {
      val birthday = "${eventDate.year - age}-${eventDate.monthValue}-${eventDate.dayOfMonth-1}"
      return createAttendee(id, birthday, role)
    }

    fun createAttendee(id: Long, birthday: String, role: AttendeeRole): Attendee {
        return Attendee(
            id= id,
            firstName= "first",
            lastName= "name",
            birthday= birthday,
            food= Food.MEAT,
            tShirtSize= TShirtSize.L,
            additionalInformation= "",
            role= role,
            department = Department(4L, "", "", ""),
            code = "",
            specialLeave= false,
            status = AttendeeStatus.ENTERED
        )
    }
}
