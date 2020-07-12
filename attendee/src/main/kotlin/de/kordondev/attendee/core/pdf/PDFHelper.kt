package de.kordondev.attendee.core.pdf

import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.springframework.stereotype.Service

@Service
class PDFHelper {
    fun fillField(form: PDAcroForm, fieldName: String, fieldText: String, page: Number): PDField? {
        val field = form.getField(fieldName)
        field?.setValue(fieldText)
        field?.partialName = "$page$fieldName"
        field?.isReadOnly = true
        return field
    }
}