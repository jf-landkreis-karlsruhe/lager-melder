package de.kordondev.attendee.core.pdf

import de.kordondev.attendee.core.model.Attendee
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
        return birthdayToDate(birthday).format(formatter)
    }

    fun birthdayToDate(birthday: String) : LocalDate {
        var dateList = birthday.split("-")
        return LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt())
    }

    fun getOptimizedLeaderAndAttendees(allAttendees: List<Attendee>, eventStart: LocalDate): Pair<List<Attendee>, List<Attendee>> {
        // birthday: "2020-03-30" "yyyy-MM-dd"
        val birthdayComparator = compareBy<Attendee> {it.birthday}
        val birthdayAndNameComparator = birthdayComparator.thenByDescending {it.firstName}
        val splitList = allAttendees
            .sortedWith(birthdayAndNameComparator)
            .filter { ageAtEvent(it.birthday, eventStart) >= 6 }
            .groupBy { ageAtEvent(it.birthday, eventStart) <= 26 }.values
            .toList()
        var leader = splitList[0]
        var attendees = splitList[1]

        val toMuchAttendees = allAttendees.size - (leader.size * 5)
        val attendeesToLeader = toMuchAttendees / 6
        if (attendeesToLeader > 0) {
            // move x first of attendees to the end of leader
            val toMove = attendees.subList(0, attendeesToLeader)
            leader = leader.plus(toMove)
            attendees = attendees.subList(attendeesToLeader, attendees.size)
        }

        return Pair(leader, attendees)
    }

    private fun ageAtEvent(birthday: String, eventStart: LocalDate): Int {
        return Period.between(birthdayToDate(birthday), eventStart).years
    }
}