package de.kordondev.attendee.core.service

import com.lowagie.text.Document
import com.lowagie.text.PageSize
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
class AdminPDFService(
    private val resourceLoader: ResourceLoader,
    private val attendeeService: AttendeeService
) {
    fun createBatches(): ByteArray {

        val out = ByteArrayOutputStream()
        val outerOut = ByteArrayOutputStream()
        val document = Document(PageSize.A4)
        val pdfCopy = PdfCopy(document, outerOut)
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
            val stamper = PdfStamper(pdfReader, out)

            val content = stamper.getOverContent(1);
            content.setColorFill(Color.BLACK);
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

            val readInputPDF = PdfReader(out.toByteArray());

            pdfCopy.addPage(pdfCopy.getImportedPage(readInputPDF, 1))
            pdfCopy.freeReader(readInputPDF)
        }

        document.close()
        pdfCopy.close()

        return outerOut.toByteArray();
    }

    fun addName(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText();
        content.setTextMatrix(345F, 738F - 141F * attendeesOnPage); //TODO: Fix calculation
        content.showText("${attendeesOnPage} ${attendee.firstName} ${attendee.lastName}");
        content.endText();
    }

    fun addDepartment(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        content.setTextMatrix(335F, 723F - 141F * attendeesOnPage) //TODO: Fix calculation
        content.showText("$attendeesOnPage ${attendee.department.name}")
        content.endText()
    }

    fun addBarCode(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        val barcode128 = Barcode128()
        val code = "att-000$attendeesOnPage" // TODO: useAttendeeCode
        barcode128.code = code
        barcode128.barHeight = 40F
        barcode128.x = 1.5F
        barcode128.altText = code
        barcode128.baseline = 12F
        barcode128.size = 12F
        val template = barcode128.createTemplateWithBarcode(content, Color.BLACK, Color.BLACK)
        content.addTemplate(template, 320F, 660F - 141F * attendeesOnPage)//TODO: Fix calculation

    }

}