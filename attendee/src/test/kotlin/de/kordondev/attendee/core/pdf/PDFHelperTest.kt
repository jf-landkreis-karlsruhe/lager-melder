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
            createAttendee(1L, "1995-05-01"),
            createAttendee(2L, "1996-03-01"),
            createAttendee(6L, "1990-03-01"),
            createAttendee(3L, "2005-05-01"),
            createAttendee(4L, "2011-05-01"),
            createAttendee(5L, "2022-05-01"),
        )

        val result = pdfHelper.getOptimizedLeaderAndAttendees(allAttendees, LocalDate.of(2023, 5,5))
        val leader = result.first
        Assertions.assertThat(leader.map { it.id }).isEqualTo(listOf(6L, 1L, 2L))

        val attendees = result.first
        Assertions.assertThat(attendees.map { it.id }).isEqualTo(listOf(6L, 1L, 2L))
    }

    fun createAttendee(id: Long, birthday: String): Attendee {
        return Attendee(
            id= id,
            firstName= "first",
            lastName= "name",
            birthday= birthday,
            food= Food.MEAT,
            tShirtSize= TShirtSize.L,
            additionalInformation= "",
            role= AttendeeRole.YOUTH,
            department = Department(4L, "", "", ""),
            code = "",
            specialLeave= false,
            status = AttendeeStatus.ENTERED
        )
    }
}