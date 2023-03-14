package de.kordondev.attendee.core.pdf

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
        var dateList = birthday.split("-")
        return LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt()).format(formatter)
    }

    fun birthdayToDate(birthday: String) : LocalDate {
        var dateList = birthday.split("-")
        return LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt())
    }

    fun ageAtEvent(birthday: String, eventStart: LocalDate): Int {
        return Period.between(birthdayToDate(birthday), eventStart).years
    }
}