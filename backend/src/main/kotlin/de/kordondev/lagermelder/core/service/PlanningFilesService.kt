package de.kordondev.lagermelder.core.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.lowagie.text.*
import com.lowagie.text.List
import com.lowagie.text.pdf.*
import de.kordondev.lagermelder.Helper
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Food
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.security.AuthorityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.time.LocalDate


@Service
class PlanningFilesService(
    private val resourceLoader: ResourceLoader,
    private val attendeeService: AttendeeService,
    private val eventService: EventService,
    private val departmentService: DepartmentService,
    private val authorityService: AuthorityService,
    private val settingsService: SettingsService,
    private val tShirtSizeService: TShirtSizeService,
    private val eventDayService: EventDayService
) {
    private val yDistanceBetweenBatches = 141F
    private val logger: Logger = LoggerFactory.getLogger(PlanningFilesService::class.java)
    private val headlineFont = Font(Font.TIMES_ROMAN, 20F, Font.NORMAL, Color.BLACK)
    private val markingFont = Font(Font.TIMES_ROMAN, 90F, Font.NORMAL, Color.BLACK)
    private val markingFontSmall = Font(Font.TIMES_ROMAN, 60F, Font.NORMAL, Color.BLACK)

    fun createBatchesOrderedByDepartment(): ByteArray {
        val attendeesFromDB = attendeeService.getAttendees()
        val attendees =
            (attendeesFromDB.youths + attendeesFromDB.youthLeaders + attendeesFromDB.children + attendeesFromDB.childLeaders + attendeesFromDB.zKids)
                .sortedBy {
                    attendeeService.getPartOfDepartmentOrDepartment(it).headDepartmentName + attendeeService.getPartOfDepartmentOrDepartment(
                        it
                    ).name
                }
        return createBatches(attendees)
    }

    fun createBatchesOrderedByCreationDate(): ByteArray {
        val attendeesFromDB = attendeeService.getAttendees()
        val attendees =
            (attendeesFromDB.youths + attendeesFromDB.youthLeaders + attendeesFromDB.children + attendeesFromDB.childLeaders + attendeesFromDB.zKids)
                .sortedByDescending { it.createdAt }
        return createBatches(attendees)
    }

    fun createBatches(attendees: kotlin.collections.List<Attendee>): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val documentStream = ByteArrayOutputStream()
        val document = Document(PageSize.A4)
        val pdfCopy = PdfCopy(document, documentStream)
        document.open()

        logger.info("Creating batches for ${attendees.size} on ${1 + (attendees.size / 5)} pages")
        var attendeeIndex = 0
        while (attendeeIndex < attendees.size) {

            val resource = resourceLoader.getResource("classpath:data/batch.pdf")
            val pdfReader = PdfReader(resource.inputStream)
            val pageStream = ByteArrayOutputStream()
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

    private fun addName(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        val xValue = 345F
        val yValue = 738F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
        content.showText("${attendee.firstName} ${attendee.lastName}")
        content.endText()
    }

    private fun addDepartment(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        content.beginText()
        val xValue = 335F
        val yValue = 723F
        content.setTextMatrix(xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
        content.showText(getPartOfDepartmentOrDepartmentName(attendee, true))
        content.endText()
    }

    private fun addBarCode(content: PdfContentByte, attendee: Attendee, attendeesOnPage: Int) {
        val barcode = Barcode128()
        barcode.code = attendee.code
        barcode.barHeight = 40F
        barcode.x = 1.4F
        barcode.altText = attendee.code
        barcode.baseline = 12F
        barcode.size = 12F
        val template = barcode.createTemplateWithBarcode(content, Color.BLACK, Color.BLACK)
        val xValue = 320F
        val yValue = 660F
        content.addTemplate(template, xValue, yValue - yDistanceBetweenBatches * attendeesOnPage)
    }

    fun createEventPDF(frontendBaseUrl: String): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()

        val document = prepareDocument(out)

        val font = Font(
            BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
            20F,
            Font.BOLD,
            Color.BLACK
        )

        val events = eventService.getEvents()

        for (event in events) {
            val headline = Paragraph(event.name, font)
            headline.alignment = Element.ALIGN_CENTER
            headline.spacingAfter = 50F

            document.open()
            document.add(headline)
            document.add(Paragraph("Bitte beim Kommen und Gehen scannen."))


            val qrCode = Image.getInstance(createEventCode(createEventUrl(frontendBaseUrl, event.code)))
            qrCode.scaleToFit(PageSize.A4.width, PageSize.A4.height)
            val x = (PageSize.A4.width - qrCode.scaledWidth) / 2
            val y = (PageSize.A4.height - qrCode.scaledHeight) / 2
            qrCode.setAbsolutePosition(x, y)
            document.add(qrCode)
            document.newPage()
        }

        document.close()
        return out.toByteArray()
    }

    private fun createEventCode(eventCode: String): ByteArray {
        val out = ByteArrayOutputStream()
        val matrix: BitMatrix = MultiFormatWriter().encode(eventCode, BarcodeFormat.QR_CODE, 400, 400)
        MatrixToImageWriter.writeToStream(matrix, "png", out)
        return out.toByteArray()
    }

    private fun createEventUrl(frontendBaseUrl: String, eventCode: String): String {
        if (frontendBaseUrl.endsWith("/", true)) {
            return "$frontendBaseUrl$eventCode"
        }
        return "$frontendBaseUrl/$eventCode"
    }

    fun createTShirtPDF(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out)

        val globalDepartments =
            DepartmentEntry(0, "Zeltlager gesamt", "", "", "", "", emptySet(), "", false, emptySet(), null)
        val allAttendees = attendeeService.getAttendees()
        val totalTShirtCount =
            countTShirtPerSize(allAttendees.youths + allAttendees.youthLeaders + allAttendees.zKids + allAttendees.helpers)
        val eventStart = settingsService.getSettings().eventStart
        val totalBraceletCount = countBracelet(
            allAttendees.youths + allAttendees.youthLeaders + allAttendees.zKids, eventStart
        )
        val tShirtSizes = tShirtSizeService.getTShirtSizes().map { it.size }.toMutableList()

        addTShirtsAndBraceletForDepartment(
            globalDepartments.name,
            tShirtSizes,
            totalTShirtCount,
            totalBraceletCount,
            document
        )

        val departments = departmentService.getDepartments().sortedBy { it.headDepartmentName + it.name }
        for (department in departments) {
            val attendees = attendeeService.getAttendeesForDepartmentWithZKidsBeingPartOf(department.id)
            if (attendees.youths.isNotEmpty() || attendees.youthLeaders.isNotEmpty() || attendees.zKids.isNotEmpty()) {
                val tShirtCount = countTShirtPerSize(attendees.youths + attendees.youthLeaders + attendees.zKids)
                val braceletCount = countBracelet(attendees.youths + attendees.youthLeaders + attendees.zKids, eventStart)
                addTShirtsAndBraceletForDepartment(department.name, tShirtSizes, tShirtCount, braceletCount, document)
            }
            if (attendees.children.isNotEmpty() || attendees.childLeaders.isNotEmpty()) {
                val tShirtCount = countTShirtPerSize(attendees.children + attendees.childLeaders)
                val braceletCount = countBracelet(attendees.children + attendees.childLeaders, eventStart)
                addTShirtsAndBraceletForDepartment(
                    "${department.name} Kindergruppe",
                    tShirtSizes,
                    tShirtCount,
                    braceletCount,
                    document
                )
            }
            if (attendees.helpers.isNotEmpty()) {
                val tShirtCount = countTShirtPerSize(attendees.helpers)
                val braceletCount = countBracelet(attendees.helpers, eventStart)
                addTShirtsAndBraceletForDepartment(
                    "${department.name} Helfer",
                    tShirtSizes,
                    tShirtCount,
                    braceletCount,
                    document
                )
            }
        }
        document.close()
        return out.toByteArray()
    }

    private fun addTShirtsAndBraceletForDepartment(
        departmentName: String,
        tShirtSizes: MutableList<String>,
        tShirtCount: MutableMap<String, Int>,
        braceletCount: MutableMap<Color, Int>,
        document: Document
    ) {
        val headlineFont = Font(Font.TIMES_ROMAN, 20F, Font.NORMAL, Color.BLACK)
        document.add(Paragraph("Abteilung: $departmentName", headlineFont))
        document.add(Paragraph("Kreiszeltlager - T-Shirt", headlineFont))

        val table = Table(2)
        table.borderWidth = 1F
        table.borderColor = Color(0, 0, 0)
        table.padding = 5F
        table.spacing = 0F
        table.addCell("Größe:")
        table.addCell("Anzahl:")
        table.endHeaders()
        for (size in tShirtSizes) {
            table.addCell(size)
            table.addCell("${tShirtCount[size] ?: 0}")
        }
        document.add(table)

        document.add(Paragraph("Kreiszeltlager - Armband", headlineFont))
        val braceletTable = Table(2)
        braceletTable.borderWidth = 1F
        braceletTable.borderColor = Color(0, 0, 0)
        braceletTable.padding = 5F
        braceletTable.spacing = 0F
        braceletTable.addCell("Farbe:")
        braceletTable.addCell("Anzahl:")
        braceletTable.endHeaders()

        for (color in listOf(Color.GREEN, Color.YELLOW, Color.RED)) {
            braceletTable.addCell(colorToString(color))
            braceletTable.addCell("${braceletCount[color] ?: 0}")
        }
        document.add(braceletTable)

        document.newPage()
    }

    private fun countTShirtPerSize(attendees: kotlin.collections.List<Attendee>): MutableMap<String, Int> {
        val tShirtCount = mutableMapOf<String, Int>()
        for (attendee in (attendees)) {
            val currentCount = tShirtCount[attendee.tShirtSize] ?: 0
            tShirtCount[attendee.tShirtSize] = (currentCount + 1)
        }
        return tShirtCount
    }

    private fun countBracelet(
        attendees: kotlin.collections.List<Attendee>,
        eventStart: LocalDate
    ): MutableMap<Color, Int> {
        val braceletCount = mutableMapOf<Color, Int>()
        for (attendee in attendees) {
            val color = colorForAgeGroup(attendee, eventStart)
            val currentCount = braceletCount[color] ?: 0
            braceletCount[color] = (currentCount + 1)
        }
        return braceletCount
    }

    private fun colorForAgeGroup(attendee: Attendee, eventStart: LocalDate): Color {
        val age = Helper.ageAtEvent(attendee, eventStart, 18)
        if (age < 16) {
            return Color.RED
        }
        if (age < 18) {
            return Color.YELLOW
        }
        return Color.GREEN
    }

    private fun colorToString(color: Color): String {
        return when (color) {
            Color.GREEN -> "Grün"
            Color.YELLOW -> "Gelb"
            Color.RED -> "Rot"
            else -> color.toString()
        }
    }


    fun createFoodPDF(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out)

        val attendees = attendeeService.getAttendees()

        val foodAttendees = foodFromAttendees(attendees.youths + attendees.youthLeaders + attendees.zKids)
        addFoodToDocument(document, foodAttendees, "Essen - Teilnehmer")
        document.newPage()

        val foodChildren = foodFromAttendees(attendees.children + attendees.childLeaders)
        addFoodToDocument(document, foodChildren, "Essen - Kindergruppe")

        val eventDays = eventDayService.getEventDays().sortedBy { it.dayOfEvent }
        for (eventDay in eventDays) {
            document.newPage()
            val helperForDay = attendees.helpers.filter { helper -> helper.helperDays.map { it.id }.contains(eventDay.id) }
            val foodEventDay = foodFromAttendees(helperForDay)
            addFoodToDocument(document, foodEventDay, "Helferessen - ${eventDay.name}")
        }


        document.close()
        return out.toByteArray()
    }

    private fun foodFromAttendees(attendees: kotlin.collections.List<Attendee>): Map<Food, MutableList<Attendee>> {
        val foodAttendees = mutableMapOf<Food, MutableList<Attendee>>()
        for (food in Food.entries) {
            foodAttendees[food] = mutableListOf()
        }

        for (attendee in attendees) {
            foodAttendees[attendee.food]?.add(attendee)
        }
        return foodAttendees
    }

    private fun addFoodToDocument(
        document: Document,
        foodAttendees: Map<Food, MutableList<Attendee>>,
        headline: String
    ) {
        document.add(Paragraph(headline, headlineFont))
        for (food in Food.entries) {
            document.add(Paragraph("$food (${foodAttendees[food]!!.size})", headlineFont))
            val list = List()
            list.setListSymbol("\u2022")
            for (att in foodAttendees[food]!!) {
                if (att.additionalInformation.isNotEmpty()) {
                    list.add(" ${att.firstName} ${att.lastName} aus ${att.department.name}. Kommentar: ${att.additionalInformation}. (Jugendwart: ${att.department.leaderEMail}, EMail: ${att.department.leaderEMail})")
                } else if (food != Food.MEAT) {
                    list.add(" ${att.firstName} ${att.lastName} aus ${att.department.name}")
                }
            }
            document.add(list)
        }
    }

    fun createAdditionalInformationPDF(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out)
        val attendees = attendeeService.getAttendees()

        document.add(Paragraph("Kreiszeltlager - Kommentare", headlineFont))
        val departmentAttendees = attendeesWithAdditionalInformation(attendees.youths + attendees.youthLeaders + attendees.zKids)
        addCommentsToDocument(document, departmentAttendees)
        document.newPage()

        val departmentChildren = attendeesWithAdditionalInformation(attendees.children + attendees.childLeaders)
        addCommentsToDocument(document, departmentChildren)
        document.newPage()

        val departmentHelper = attendeesWithAdditionalInformation(attendees.helpers)
        addCommentsToDocument(document, departmentHelper)

        document.close()
        return out.toByteArray()
    }

    private fun attendeesWithAdditionalInformation(attendees: kotlin.collections.List<Attendee>): Map<DepartmentEntry, MutableList<Attendee>> {
        val departmentAttendees = mutableMapOf<DepartmentEntry, MutableList<Attendee>>()
        for (attendee in attendees) {
            if (attendee.additionalInformation.isEmpty()) {
                continue
            }
            val department = attendeeService.getPartOfDepartmentOrDepartment(attendee)
            if (departmentAttendees[department] == null) {
                departmentAttendees[department] = mutableListOf()
            }
            departmentAttendees[department]?.add(attendee)
        }
        return departmentAttendees
    }

    private fun addCommentsToDocument(
        document: Document,
        departmentAttendees: Map<DepartmentEntry, MutableList<Attendee>>,
    ) {
        val departments = departmentAttendees.keys.toList().sortedBy { it.headDepartmentName + it.name }
        for (department in departments) {
            document.add(Paragraph(department.name, headlineFont))
            document.add(Paragraph("Jugendwart: ${department.leaderName}, EMail: ${department.leaderEMail}"))
            val list = List()
            list.setListSymbol("\u2022")
            for (att in departmentAttendees[department]!!) {
                list.add(" ${att.firstName} ${att.lastName}: ${att.additionalInformation}")
            }
            document.add(list)
        }
    }

    fun createOverviewForEachDepartment(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val eventStart = settingsService.getSettings().eventStart
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out)

        val departments = departmentService.getDepartments().sortedBy { it.headDepartmentName + it.name }
        for (department in departments) {
            val attendees = attendeeService.getAttendeesForDepartmentWithZKidsBeingPartOf(department.id)
            if (attendees.youths.isEmpty() && attendees.youthLeaders.isEmpty() && attendees.zKids.isEmpty() && attendees.helpers.isEmpty()) {
                continue
            }
            if (attendees.youths.isNotEmpty() || attendees.youthLeaders.isNotEmpty() || attendees.zKids.isNotEmpty()) {
                addDepartmentTableToDocument(
                    document,
                    department.name,
                    attendees.youths + attendees.youthLeaders + attendees.zKids,
                    eventStart
                )
            }

            if (attendees.helpers.isNotEmpty()) {
                addDepartmentTableToDocument(
                    document,
                    "${department.name} Helfer",
                    attendees.helpers,
                    eventStart
                )
            }

            document.newPage()
        }
        document.close()
        return out.toByteArray()
    }

    fun createContactList(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out)

        val dbAttendees = attendeeService.getAllAttendees()
        val departmentWithAttendees = (dbAttendees.youths + dbAttendees.youthLeaders + dbAttendees.children + dbAttendees.childLeaders + dbAttendees.zKids + dbAttendees.helpers)
            .groupBy { attendeeService.getPartOfDepartmentOrDepartment(it) }

        departmentWithAttendees.keys.sortedBy { it.headDepartmentName + it.name }.map { department ->
            document.add(Paragraph("${department.headDepartmentName} ${department.name}", headlineFont))
            document.add(Paragraph("Jugendwart: ${department.leaderName}, EMail: ${department.leaderEMail}, Telefon: ${department.phoneNumber}"))
            val table = Table(4)
            table.borderWidth = 1F
            table.borderColor = Color(0, 0, 0)
            table.padding = 2F
            table.spacing = 0F
            table.addCell("Jugendliche")
            table.addCell("Jugendleiter")
            table.addCell("Kinder")
            table.addCell("Kinderleiter")
            table.endHeaders()
            val attendeeByType = departmentWithAttendees[department]?.groupBy { it.role }
            if (attendeeByType != null) {
                table.addCell((attendeeByType[AttendeeRole.YOUTH]?.plus(attendeeByType[AttendeeRole.Z_KID]))?.size?.toString() ?: "0")
                table.addCell(attendeeByType[AttendeeRole.YOUTH_LEADER]?.size?.toString() ?: "0")
                table.addCell(attendeeByType[AttendeeRole.CHILD]?.size?.toString() ?: "0")
                table.addCell(attendeeByType[AttendeeRole.CHILD_LEADER]?.size?.toString() ?: "0")
            }
            document.add(table)
        }

        document.close()
        return out.toByteArray()
    }

    private fun addDepartmentTableToDocument(
        document: Document,
        departmentName: String,
        attendees: kotlin.collections.List<Attendee>,
        eventStart: LocalDate
    ) {
        document.add(Paragraph(departmentName, headlineFont))

        val table = Table(3)
        table.borderWidth = 1F
        table.borderColor = Color(0, 0, 0)
        table.padding = 3F
        table.spacing = 0F
        table.addCell("Name:")
        table.addCell("T-Shirt")
        table.addCell("Armband")
        table.endHeaders()
        for (attendee in attendees) {
            table.addCell("${attendee.firstName} ${attendee.lastName}")
            table.addCell(attendee.tShirtSize)
            table.addCell(colorToString(colorForAgeGroup(attendee, eventStart)))
        }
        document.add(table)
    }

    private fun prepareDocument(documentStream: ByteArrayOutputStream, pageSize: Rectangle = PageSize.A4): Document {
        val document = Document(pageSize)
        val writer = PdfWriter.getInstance(document, documentStream)

        writer.isCloseStream = false
        document.open()

        return document
    }

    private fun getPartOfDepartmentOrDepartmentName(attendee: Attendee, withShortName: Boolean = false): String {
        val department = attendeeService.getPartOfDepartmentOrDepartment(attendee)
        if (withShortName && department.shortName.isNotEmpty()) {
            return department.shortName
        }
        return department.name
    }

    fun createTentMarkings(): ByteArray {
        authorityService.isSpecializedFieldDirector()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out, PageSize.A4.rotate())

        val departments = departmentService.getDepartments(true)

        departments.sortedBy { it.headDepartmentName + it.name }.map { department ->
            department.tentMarkings.forEach { tentMarking ->
                val tentName = Paragraph("Zelt: ${tentMarking.name}", markingFont)
                tentName.alignment = Element.ALIGN_CENTER
                val departmentName = Paragraph("${department.headDepartmentName} ${department.name}", markingFontSmall)
                departmentName.alignment = Element.ALIGN_CENTER

                val evacuationGroupName = Paragraph("     ${department.evacuationGroup?.name}     ", markingFont)
                val chunk = Chunk(evacuationGroupName.getContent());
                chunk.setBackground(Color.decode(department.evacuationGroup?.color))
                evacuationGroupName.clear();
                evacuationGroupName.add(chunk);
                evacuationGroupName.alignment = Element.ALIGN_CENTER

                document.add(tentName)
                document.add(departmentName)
                document.add(evacuationGroupName)

                document.newPage()
            }
        }

        document.close()
        return out.toByteArray()
    }

}