package de.kordondev.lagermelder.core.service.helper

import com.lowagie.text.pdf.PRStream
import com.lowagie.text.pdf.PdfName
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BatchPdfCreatorTest {
    private val template = javaClass.getResourceAsStream("/data/batch.pdf")!!.use { it.readBytes() }
    private val batchPdfCreator = BatchPdfCreator()

    private fun batches(count: Int) = (1..count).map { BatchData("Vorname$it Nachname$it", "Feuerwehr$it", "CODE%04d".format(it)) }

    @ParameterizedTest
    @CsvSource("1, 1", "5, 1", "6, 2", "100, 20")
    fun putsFiveBatchesOnEachPage(
        batchCount: Int,
        pageCount: Int,
    ) {
        val pdf = PdfReader(batchPdfCreator.create(batches(batchCount), template))

        assertThat(pdf.numberOfPages).isEqualTo(pageCount)
    }

    @Test
    fun writesTheTemplateOnlyOnce() {
        val pdf = batchPdfCreator.create(batches(100), template)

        // the template contains two logos, every page only references them
        assertThat(countImages(PdfReader(pdf))).isEqualTo(countImages(PdfReader(template)))
        assertThat(pdf.size).isLessThan(2 * template.size)
    }

    @Test
    fun writesNameAndDepartmentOnTheRightPage() {
        val pdf = PdfReader(batchPdfCreator.create(batches(6), template))
        val extractor = PdfTextExtractor(pdf)

        assertThat(extractor.getTextFromPage(1)).contains("Vorname1 Nachname1", "Feuerwehr1", "Vorname5 Nachname5")
        assertThat(extractor.getTextFromPage(1)).doesNotContain("Vorname6 Nachname6")
        assertThat(extractor.getTextFromPage(2)).contains("Vorname6 Nachname6", "Feuerwehr6")
    }

    private fun countImages(pdf: PdfReader) =
        (1 until pdf.xrefSize)
            .map { pdf.getPdfObject(it) }
            .count { it is PRStream && it.get(PdfName.SUBTYPE) == PdfName.IMAGE }
}
