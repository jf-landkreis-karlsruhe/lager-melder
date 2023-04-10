package de.kordondev.attendee.core.pdf

import de.kordondev.attendee.Helper
import de.kordondev.attendee.core.model.Attendee
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.springframework.stereotype.Service
import java.time.LocalDate
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

    // TODO: Save afterwards
    fun getOptimizedLeaderAndAttendees(
        allAttendees: List<Attendee>,
        eventStart: LocalDate
    ): Pair<List<Attendee>, List<Attendee>> {
        // birthday: "2020-03-30" "yyyy-MM-dd"
        /* val fixedAttendees = allAttendees.groupBy { it.youthPlanRole }
         val fixedLeader = fixedAttendees[AttendeeRole.YOUTH_LEADER] ?: listOf()
         val fixedYouth = fixedAttendees[AttendeeRole.YOUTH] ?: listOf()

         val undistributedAttendees = fixedAttendees[null]
         if (undistributedAttendees != null) {
             val (youth, leader) = distributeNewAttendees(undistributedAttendees,eventStart, allAttendees.size, fixedAttendees[AttendeeRole.YOUTH_LEADER]?.size)
             return Pair(fixedYouth + youth, fixedLeader + leader)
         }

         return Pair(fixedYouth, fixedLeader)*/
        return Pair(allAttendees, listOf())
    }

}