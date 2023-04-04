package de.kordondev.attendee.core.pdf

import de.kordondev.attendee.Helper
import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


@Service
class PDFHelper {
    companion object {
        var germanDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        var germanDateShort = DateTimeFormatter.ofPattern("dd.MM.yy")
    }

    fun fillField(form: PDAcroForm, fieldName: String, fieldText: String, page: Number): PDField? {
        val field = form.getField(fieldName)
        field?.setValue(fieldText)
        field?.partialName = "$page$fieldName"
        field?.isReadOnly = true
        return field
    }

    fun checkField(form: PDAcroForm, fieldName: String, page: Number): PDField? {
        val field = form.getField(fieldName)
        if (field != null) {
            (field as PDCheckBox).check()
            field?.partialName = "$page$fieldName"
            field?.isReadOnly = true
        }
        return field
    }

    fun formatBirthday(birthday: String, formatter: DateTimeFormatter): String {
        return Helper.birthdayToDate(birthday).format(formatter)
    }

    private val oldFirst = compareBy<Attendee> {it.birthday}
    private val oldFirstThenFirstname = oldFirst.thenByDescending {it.firstName}
    // TODO: Save afterwards
    fun getOptimizedLeaderAndAttendees(allAttendees: List<Attendee>, eventStart: LocalDate): Pair<List<Attendee>, List<Attendee>> {
        // birthday: "2020-03-30" "yyyy-MM-dd"
        val fixedAttendees = allAttendees.groupBy { it.youthPlanRole }
        val fixedLeader = fixedAttendees[AttendeeRole.YOUTH_LEADER] ?: listOf()
        val fixedYouth = fixedAttendees[AttendeeRole.YOUTH] ?: listOf()

        val undistributedAttendees = fixedAttendees[null]
        if (undistributedAttendees != null) {
            val (youth, leader) = distributeNewAttendees(undistributedAttendees,eventStart, allAttendees.size, fixedAttendees[AttendeeRole.YOUTH_LEADER]?.size)
            return Pair(fixedYouth + youth, fixedLeader + leader)
        }

        return Pair(fixedYouth, fixedLeader)
    }

    private fun distributeNewAttendees(newAttendees: List<Attendee>, eventStart: LocalDate, allAttendeesSize: Int, fixedLeaderSize: Int?): Pair<List<Attendee>, List<Attendee>> {
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
            for (l in leader) {
                l.youthPlanRole = AttendeeRole.YOUTH_LEADER
            }
            for (y in youth) {
                y.youthPlanRole = AttendeeRole.YOUTH
            }
        }
        return Pair(youth, leader)
    }

}