package de.kordondev.lagermelder.core.pdf

import de.kordondev.lagermelder.Helper
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.springframework.stereotype.Service
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
            field.partialName = "$page$fieldName"
            field.isReadOnly = true
        }
        return field
    }

    fun getAndFormatBirthday(attendee: Attendee, formatter: DateTimeFormatter): String {
        return formatBirthday(Helper.getBirthday(attendee), formatter)
    }

    fun formatBirthday(birthday: String, formatter: DateTimeFormatter): String {
        return Helper.birthdayToDate(birthday).format(formatter)
    }

    fun writeDocumentTitle(pdfDocument: PDDocument, text: String, x: Float, y: Float) {
        for (page in 0..pdfDocument.numberOfPages - 1) {
            val page = pdfDocument.getPage(page)
            val contentStream = PDPageContentStream(
                pdfDocument,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
            )

            contentStream.beginText()
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12F)
            contentStream.newLineAtOffset(x, y)
            contentStream.showText(text)
            contentStream.endText()
            contentStream.close()
        }
    }

}