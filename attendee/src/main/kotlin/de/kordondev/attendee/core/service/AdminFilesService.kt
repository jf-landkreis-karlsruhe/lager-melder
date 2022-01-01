package de.kordondev.attendee.core.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.lowagie.text.*
import com.lowagie.text.pdf.*
import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream


@Service
class AdminFilesService(
    private val resourceLoader: ResourceLoader,
    private val attendeeService: AttendeeService
) {
    private val yDistanceBetweenBatches = 141F
    fun createBatches(): ByteArray {
        val pageStream = ByteArrayOutputStream()
        val documentStream = ByteArrayOutputStream()
        val document = Document(PageSize.A4)
        val pdfCopy = PdfCopy(document, documentStream)
        document.open()

        val department = Department(0, "dep", "leader", "leader@a")
        val attendee = Attendee(
            0, "first", "last", "01-01-1990", Food.VEGAN,
            TShirtSize.M, "", AttendeeRole.YOUTH, department
        )

        val attendees = listOf(
            attendee,
            attendee,
            attendee,
            attendee,
            attendee,
            attendee,
            attendee
        ) // TODO: attendeeService.getAttendees().toList()
        var attendeeIndex = 0
        while (attendeeIndex < attendees.size) {

            val resource = resourceLoader.getResource("classpath:data/batch.pdf")
            val pdfReader = PdfReader(resource.inputStream)
            val stamper = PdfStamper(pdfReader, pageStream)

            val content = stamper.getOverContent(1)
            content.setColorFill(Color.BLACK)
            content.setFontAndSize(
                BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
                16F
            )

            var attendeesOnPage = 0
            while (attendeeIndex < attendees.size && attendeesOnPage < 5) {
                addName(content, attendees[attendeeIndex], attendeesOnPage)
                addDepartment(content, attendees[attendeeIndex], attendeesOnPage)
                addBarCode(content, attendees[attendeeIndex], attendeesOnPage)

                attendeesOnPage++
                attendeeIndex++
            }

            stamper.close()
            pdfReader.close()

            val readInputPDF = PdfReader(pageStream.toByteArray())

            pdfCopy.addPage(pdfCopy.getImportedPage(readInputPDF, 1))
            pdfCopy.freeReader(readInputPDF)
        }

        document.close()
        pdfCopy.close()

        return documentStream.toByteArray()
    }

    fun addName(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        val xValue = 345F
        val yValue = 738F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
        content.showText("${attendee.firstName} ${attendee.lastName}")
        content.endText()
    }

    fun addDepartment(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        val xValue = 335F
        val yValue = 723F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
        content.showText(attendee.department.name)
        content.endText()
    }

    fun addBarCode(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        val barcode128 = Barcode128()
        val code = "att-000$attendeesOnPage" // TODO: use code from attendee
        barcode128.code = code
        barcode128.barHeight = 40F
        barcode128.x = 1.5F
        barcode128.altText = code
        barcode128.baseline = 12F
        barcode128.size = 12F
        val template = barcode128.createTemplateWithBarcode(content, Color.BLACK, Color.BLACK)
        val xValue = 320F
        val yValue = 660F
        content.addTemplate(template, xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
    }

    fun createEventPDF(): ByteArray {
        val out = ByteArrayOutputStream()

        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, out)

        writer.isCloseStream = false

        val font = Font(
            BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
            20F,
            Font.BOLD,
            Color.BLACK
        )

        // TODO: Loop over events
        val headline = Paragraph("Eventname", font) // TODO: Change to event name
        headline.alignment = Element.ALIGN_CENTER
        headline.spacingAfter = 50F


        document.open()
        document.add(headline)
        document.add(Paragraph("Bitte beim Kommen und Gehen ein und ausloggen.")) //TODO: Correct sentence

        val qrCode = Image.getInstance(createEventCode("123456")) // TODO: Use event code
        qrCode.scaleToFit(PageSize.A4.width, PageSize.A4.height)
        val x = (PageSize.A4.width - qrCode.scaledWidth) / 2
        val y = (PageSize.A4.height - qrCode.scaledHeight) / 2
        qrCode.setAbsolutePosition(x, y)
        document.add(qrCode)
        // TODO: Loop over events end

        document.close()
        return out.toByteArray()
    }

    fun createEventCode(eventCode: String): ByteArray {
        val out = ByteArrayOutputStream()
        val matrix: BitMatrix = MultiFormatWriter().encode(eventCode, BarcodeFormat.QR_CODE, 400, 400)
        MatrixToImageWriter.writeToStream(matrix, "png", out)
        return out.toByteArray()
    }

}