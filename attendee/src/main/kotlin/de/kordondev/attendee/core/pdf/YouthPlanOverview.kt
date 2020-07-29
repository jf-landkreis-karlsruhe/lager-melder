package de.kordondev.attendee.core.pdf

import de.kordondev.attendee.core.model.Attendee
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

@Service
class YouthPlanOverview (
        val resourceLoader: ResourceLoader,
        val pdfHelper: PDFHelper
) {
    val YEAR = "Landesjugendplan_19"
    val CATEGORY = "Auswahl"
    val CATEGORY_VALUE = "Heimfreizeiten Zeitlager"
    val ORGANISATION = "Organisation"
    val PAGE = "Blatt"
    val TABLE_FIELDS = listOf(
            (1..10).toList().map { it.toString() },
            (106..115).toList().map { it.toString() },
            (116..125).toList().map { it.toString() },
            (126..135).toList().map { it.toString() },
            (136..145).toList().map { it.toString() },
            (146..155).toList().map { it.toString() },
            listOf("11", "12", "18", "19", "20", "21", "22", "23", "24", "25"),
            (26..35).toList().map { it.toString() },
            (36..45).toList().map { it.toString() },
            (46..55).toList().map { it.toString() }
    )
    val TABLE_SUM = listOf("13", "14", "15", "16", "17")

    fun createYouthPlan(attendees: List<Attendee>): PDDocument {
        val resource: Resource = resourceLoader.getResource("classpath:data/paedagogischerBetreuer.pdf")

        val result = PDDocument()
        val fields = mutableListOf<PDField>()

        for (i in 1..3) {
            val pdfDocument = PDDocument.load(resource.inputStream)
            fields.addAll(fillPage(pdfDocument, i))
            result.addPage(pdfDocument.getPage(0))
        }

        val finalForm = PDAcroForm(result)
        result.documentCatalog.acroForm = finalForm
        finalForm.fields = fields
        finalForm.needAppearances = true
        return result
    }

    private fun fillPage(pdfDocument: PDDocument, page: Number): MutableList<PDField> {
        val form1 = pdfDocument.documentCatalog.acroForm;
        val fields = mutableListOf<PDField>()

        pdfHelper.fillField(form1, YEAR, "2020", page)?.let { fields.add(it) }
        pdfHelper.fillField(form1, CATEGORY, CATEGORY_VALUE, page)?.let { fields.add(it) }
        pdfHelper.fillField(form1, ORGANISATION, "FW Landkreis Karlsruhe", page)?.let { fields.add(it) }
        pdfHelper.fillField(form1, PAGE, "$page", page)?.let { fields.add(it) }
        var count = 1
        TABLE_FIELDS.map { column ->
            column.map { cellId ->
                pdfHelper.fillField(form1, "Texteingabe$cellId", "$count", page)?.let { fields.add(it) }
                count++
            }
        }
        TABLE_SUM.map { cellId ->
            pdfHelper.fillField(form1, "Texteingabe$cellId", "s$count", page)?.let { fields.add(it) }
            count++
        }
        return fields
    }

}