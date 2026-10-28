package de.kordondev.lagermelder.core.service.helper

import com.lowagie.text.Document
import com.lowagie.text.pdf.Barcode128
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.PdfWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream

data class BatchData(
    val name: String,
    val department: String,
    val code: String,
)

@Service
class BatchPdfCreator {
    private val yDistanceBetweenBatches = 141F
    private val logger: Logger = LoggerFactory.getLogger(BatchPdfCreator::class.java)

    /**
     * The template page is imported once and only referenced on every page. Copying it per page would embed its logos
     * and fonts again on every page and make the PDF grow by the size of the template per page.
     */
    fun create(
        batches: List<BatchData>,
        template: ByteArray,
    ): ByteArray {
        val documentStream = ByteArrayOutputStream()
        val templateReader = PdfReader(template)
        val document = Document(templateReader.getPageSizeWithRotation(1))
        val writer = PdfWriter.getInstance(document, documentStream)
        document.open()
        val templatePage = writer.getImportedPage(templateReader, 1)
        val font = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)

        val pages = batches.chunked(BATCHES_PER_PAGE)
        logger.info("Creating batches for ${batches.size} on ${pages.size} pages")
        pages.forEach { batchesOfPage ->
            writer.directContentUnder.addTemplate(templatePage, 0F, 0F)
            val content = writer.directContent
            content.setColorFill(Color.BLACK)
            content.setFontAndSize(font, FONT_SIZE)
            batchesOfPage.forEachIndexed { batchesOnPage, batch ->
                addName(content, batch, batchesOnPage)
                addDepartment(content, batch, batchesOnPage)
                addBarCode(content, batch, batchesOnPage)
            }
            document.newPage()
        }

        document.close()
        templateReader.close()

        return documentStream.toByteArray()
    }

    private fun addName(
        content: PdfContentByte,
        batch: BatchData,
        batchesOnPage: Int,
    ) {
        content.beginText()
        val xValue = 345F
        val yValue = 738F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * batchesOnPage)
        content.showText(batch.name)
        content.endText()
    }

    private fun addDepartment(
        content: PdfContentByte,
        batch: BatchData,
        batchesOnPage: Int,
    ) {
        content.beginText()
        val xValue = 335F
        val yValue = 723F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * batchesOnPage)
        content.showText(batch.department)
        content.endText()
    }

    private fun addBarCode(
        content: PdfContentByte,
        batch: BatchData,
        batchesOnPage: Int,
    ) {
        val barcode = Barcode128()
        barcode.code = batch.code
        barcode.barHeight = BARCODE_HEIGHT
        barcode.x = BARCODE_BAR_WIDTH
        barcode.altText = batch.code
        barcode.baseline = BARCODE_TEXT_SIZE
        barcode.size = BARCODE_TEXT_SIZE
        val template = barcode.createTemplateWithBarcode(content, Color.BLACK, Color.BLACK)
        val xValue = 320F
        val yValue = 660F
        content.addTemplate(template, xValue, yValue - yDistanceBetweenBatches * batchesOnPage)
    }

    companion object {
        private const val BATCHES_PER_PAGE = 5
        private const val FONT_SIZE = 16F
        private const val BARCODE_HEIGHT = 40F
        private const val BARCODE_BAR_WIDTH = 1.4F
        private const val BARCODE_TEXT_SIZE = 12F
    }
}
