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

    private val oldFirst = compareBy<Attendee> {it.birthday}
    private val oldFirstThenFirstname = oldFirst.thenByDescending {it.firstName}
    fun getOptimizedLeaderAndAttendees(allAttendees: List<Attendee>, eventStart: LocalDate): Pair<List<Attendee>, List<Attendee>> {
        // birthday: "2020-03-30" "yyyy-MM-dd"
        var (youth, leader) = allAttendees
            .sortedWith(oldFirstThenFirstname)
            .filter { ageAtEvent(it.birthday, eventStart) >= 6 }
            .partition { ageAtEvent(it.birthday, eventStart) <= 26 }

        val correctDistributedAttendees = leader.size + leader.size * 5
        val toMuchYouths = allAttendees.size - correctDistributedAttendees
        val possibleLeaderCount = toMuchYouths / 6
        if (possibleLeaderCount > 0) {
            // move x first of attendees, who are at least 18, to the end of leader
            val newLeader = youth
                .subList(0, possibleLeaderCount)
                .filter { ageAtEvent(it.birthday, eventStart) >= 18 }
            leader = leader.plus(newLeader)
            youth = youth.subList(newLeader.size, youth.size)
        }

        return Pair(youth, leader)
    }

    fun birthdayToDate(birthday: String) : LocalDate {
        var dateList = birthday.split("-")
        return LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt())
    }

    fun ageAtEvent(birthday: String, eventStart: LocalDate): Int {
        return Period.between(birthdayToDate(birthday), eventStart).years
    }
}