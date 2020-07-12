package de.kordondev.attendee.core.pdf

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
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
            listOf("106", "107", "108", "109", "110", "111", "112", "113", "114", "115"),
            listOf("116", "117", "118", "119", "120", "121", "122", "123", "124", "125"),
            listOf("126", "127", "128", "129", "130", "131", "132", "133", "134", "135"),
            listOf("136", "137", "138", "139", "140", "141", "142", "143", "144", "145"),
            listOf("146", "147", "148", "149", "150", "151", "152", "153", "154", "155"),
            listOf("11", "12", "18", "19", "20", "21", "22", "23", "24", "25"),
            listOf("26", "27", "28", "29", "30", "31", "32", "33", "34", "35"),
            listOf("36", "37", "38", "39", "40", "41", "42", "43", "44", "45"),
            listOf("46", "47", "48", "49", "50", "51", "52", "53", "54", "55")
    )
    val TABLE_SUM = listOf("13", "14", "15", "16", "17")

    fun createYouthPlan(): PDDocument {
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