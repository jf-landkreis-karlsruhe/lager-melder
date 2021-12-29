package de.kordondev.attendee.rest.controller

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import de.kordondev.attendee.core.service.RegistrationFilesService
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.servlet.http.HttpServletResponse


@RestController
class RegistrationFilesController(
    private val registrationFilesService: RegistrationFilesService,
    val resourceLoader: ResourceLoader,
) {

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/youthPlan/{id}"], produces = ["application/pdf"])
    fun getYouthPlan(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=paedagogischeBetreuer.pdf")
        return registrationFilesService.getYouthLeader(id);
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/attendeesKarlsruhe/{id}"], produces = ["application/pdf"])
    fun getAttendeesKarlsruhe(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=teilnehmenlisteKarlsruhe.pdf")
        return registrationFilesService.getAttendeesKarlsruhe(id);
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/attendeesBW/{id}"], produces = ["application/pdf"])
    fun getAttendeesBW(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=teilnehmenlisteBadenWürttemberg.pdf")
        return registrationFilesService.getAttendeesBW(id);
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/attendeesCommunal/{id}"], produces = ["application/pdf"])
    fun getAttendeesCommunal(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=teilnehmerKommandant.pdf")
        return registrationFilesService.getAttendeesCommunal(id);
    }

    @ResponseBody
    @Throws(IOException::class)
    @GetMapping(value = ["registrationFiles/events/{id}"], produces = ["application/pdf"])
    fun getEventsPDF(@PathVariable(value = "id") id: Long, response: HttpServletResponse): ByteArray? {
        // FixMe: useEventName
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=event-${"event.name"}.pdf")
        return stamp()
    }

    fun copy(): ByteArray {
        val out = ByteArrayOutputStream()
        val resource = resourceLoader.getResource("classpath:data/batch.pdf")

        val document = Document(PageSize.A4)

        val pdfCopy = PdfCopy(document, out)

        val readInputPDF = PdfReader(resource.inputStream);
        document.open()

        pdfCopy.addPage(pdfCopy.getImportedPage(readInputPDF, 1))
        pdfCopy.freeReader(readInputPDF)


        pdfCopy.add(Paragraph("Eventname"));

        val barcode128 = Barcode128()
        val code = "att-0001"
        barcode128.code = code
        barcode128.barHeight = 25F
        barcode128.altText = code

        val content = pdfCopy.directContent
        val template = barcode128.createTemplateWithBarcode(content, Color.BLACK, Color.BLACK)
        content.addTemplate(template, 10F, 580F)

        content.moveTo(20F, 550F)
        content.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED), 12F)
        content.showText("WAS?")

        document.close()
        pdfCopy.close()
        return out.toByteArray();

    }


    fun stamp(): ByteArray {
        val out = ByteArrayOutputStream()
        val outerOut = ByteArrayOutputStream()
        val document = Document(PageSize.A4)
        val pdfCopy = PdfCopy(document, outerOut)
        document.open()

        for (i in 0..1) {

            val resource = resourceLoader.getResource("classpath:data/batch.pdf")
            val pdfReader = PdfReader(resource.inputStream)
            val stamper = PdfStamper(pdfReader, out)

            val fontNormal = Font(1, 16F, Font.NORMAL, Color.BLACK)
            val cb = stamper.getOverContent(1);
            val ct = ColumnText(cb);
            ct.setSimpleColumn(340f, 600f, 700f, 500f);
            val pz = Paragraph(Phrase(0F, "Text in seehr lnag", fontNormal))
            ct.addElement(pz);
            ct.go();
            val ct2 = ColumnText(cb);
            ct2.setSimpleColumn(340f, 570f, 700f, 500f);
            val pz2 = Paragraph(Phrase(0F, "Text in seehr lang", fontNormal))
            ct2.addElement(pz2);
            ct2.go()


            val barcode128 = Barcode128()
            val code = "att-000${i}"
            barcode128.code = code
            barcode128.barHeight = 25F
            barcode128.altText = code
            val template = barcode128.createTemplateWithBarcode(cb, Color.BLACK, Color.BLACK)
            cb.addTemplate(template, 340F, 580F)

            cb.setColorFill(Color.BLACK);
            cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED), 12F)

            cb.beginText();
            cb.setTextMatrix(300F, 500F);
            cb.showText("Text at position 100,400.");
            cb.endText();

            stamper.close()
            pdfReader.close()


            val readInputPDF = PdfReader(out.toByteArray());
            pdfCopy.newPage()
            pdfCopy.isPageEmpty = false

            pdfCopy.addPage(pdfCopy.getImportedPage(readInputPDF, 1))
            pdfCopy.freeReader(readInputPDF)
            pdfCopy.newPage()
            pdfCopy.add(Paragraph("The previous page was a blank page!"));
        }

        document.close()
        pdfCopy.close()

        return outerOut.toByteArray();
    }

    fun newDocument(): ByteArray {
        val out = ByteArrayOutputStream()

        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, out)

        writer.isCloseStream = false
        writer.setMargins(36F, 36F, 36F, 36F)

        document.open()
        document.add(Paragraph("Eventname"));

        val barcode128 = Barcode128()
        val code = "att-0001"
        barcode128.code = code
        barcode128.barHeight = 25F
        barcode128.altText = code

        val content = writer.directContent
        val template = barcode128.createTemplateWithBarcode(content, Color.BLACK, Color.BLACK)
        content.addTemplate(template, 10F, 580F)

        content.moveTo(20F, 550F)
        content.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED), 12F)
        content.showText("WAS?")

        document.close()

        return out.toByteArray();
    }
}