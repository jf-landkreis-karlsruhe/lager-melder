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
import de.kordondev.lagermelder.core.persistence.entry.TentsEntity
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.service.helper.AttendeeRoleHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.math.round


@Service
class PlanningFilesService(
    private val resourceLoader: ResourceLoader,
    private val attendeeService: AttendeeService,
    private val eventService: EventService,
    private val departmentService: DepartmentService,
    private val authorityService: AuthorityService,
    private val settingsService: SettingsService,
    private val tShirtSizeService: TShirtSizeService,
    private val eventDayService: EventDayService,
    private val attendeeRoleHelper: AttendeeRoleHelper,
    private val tentsService: TentsService
) {
    private val yDistanceBetweenBatches = 141F
    private val logger: Logger = LoggerFactory.getLogger(PlanningFilesService::class.java)
    private val headlineFont = Font(Font.TIMES_ROMAN, 20F, Font.NORMAL, Color.BLACK)
    private val markingFont = Font(Font.TIMES_ROMAN, 90F, Font.NORMAL, Color.BLACK)
    private val markingFontSmall = Font(Font.TIMES_ROMAN, 60F, Font.NORMAL, Color.BLACK)

    fun createBatchesOrderedByDepartment(): ByteArray {
        authorityService.isLkKarlsruhe()
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
        authorityService.isLkKarlsruhe()
        val attendeesFromDB = attendeeService.getAttendees()
        val attendees =
            (attendeesFromDB.youths + attendeesFromDB.youthLeaders + attendeesFromDB.children + attendeesFromDB.childLeaders + attendeesFromDB.zKids)
                .sortedByDescending { it.createdAt }
        return createBatches(attendees)
    }

    private fun createBatches(attendees: kotlin.collections.List<Attendee>): ByteArray {
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
        authorityService.isLkKarlsruhe()
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
        authorityService.isLkKarlsruhe()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out)

        val globalDepartments =
            DepartmentEntry(0, "Zeltlager gesamt", "", "", "", "", emptySet(), "", false, emptySet(), null)
        val allAttendees = attendeeService.getAttendees()
        val totalTShirtCount =
            countTShirtPerSize(allAttendees.youths + allAttendees.youthLeaders + allAttendees.zKids + allAttendees.helpers)
        val eventStart = settingsService.getSettings().eventStart
        val totalBraceletCount = countBracelet(
            allAttendees.youths + allAttendees.youthLeaders + allAttendees.children + allAttendees.childLeaders + allAttendees.zKids,
            eventStart
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
                val braceletCount = countBracelet(attendees.children + attendees.childLeaders, eventStart)
                addTShirtsAndBraceletForDepartment(
                    "${department.name} Kindergruppe",
                    listOf<String>().toMutableList(),
                    mapOf<String, Int>().toMutableMap(),
                    braceletCount,
                    document,
                    true
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
        document: Document,
        skipTShirt: Boolean = false
    ) {
        val headlineFont = Font(Font.TIMES_ROMAN, 20F, Font.NORMAL, Color.BLACK)
        document.add(Paragraph("Abteilung: $departmentName", headlineFont))

        if (!skipTShirt) {
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
        }

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
        authorityService.isLkKarlsruhe()
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
        authorityService.isLkKarlsruhe()
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
        authorityService.isLkKarlsruhe()
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
                addTShirtsAndBraceletsPerPersonToDocument(
                    document,
                    department.name,
                    attendees.youths + attendees.youthLeaders + attendees.zKids,
                    eventStart
                )
            }

            if (attendees.helpers.isNotEmpty()) {
                addTShirtsAndBraceletsPerPersonToDocument(
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
        authorityService.isLkKarlsruhe()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out)

        val dbAttendees = attendeeService.getAllAttendees()
        val departmentWithAttendees = (dbAttendees.youths + dbAttendees.youthLeaders + dbAttendees.children + dbAttendees.childLeaders + dbAttendees.zKids + dbAttendees.helpers)
            .groupBy { attendeeService.getPartOfDepartmentOrDepartment(it) }

        departmentWithAttendees.keys.sortedBy { it.headDepartmentName + it.name }.map { department ->
            document.add(Paragraph("${department.headDepartmentName} ${department.name}", headlineFont))
            document.add(Paragraph("Jugendwart: ${department.leaderName}, EMail: ${department.leaderEMail}, Telefon während Kreiszeltlager: ${department.phoneNumber}"))
            document.add(Paragraph("Kommandant: ${department.nameKommandant}, Telefon: ${department.phoneNumberKommandant}"))
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
                table.addCell(attendeeByType[AttendeeRole.YOUTH]?.size?.toString() ?: "0")
                table.addCell(attendeeByType[AttendeeRole.YOUTH_LEADER]?.size?.toString() ?: "0")
                table.addCell(attendeeByType[AttendeeRole.CHILD]?.size?.toString() ?: "0")
                table.addCell(attendeeByType[AttendeeRole.CHILD_LEADER]?.size?.toString() ?: "0")
            }
            document.add(table)
        }

        document.close()
        return out.toByteArray()
    }

    private fun addTShirtsAndBraceletsPerPersonToDocument(
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
        authorityService.isLkKarlsruhe()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out, PageSize.A4.rotate())

        val departments = departmentService.getDepartments(true)
        var hasTentMarkings = false

        departments.sortedBy { it.headDepartmentName + it.name }.map { department ->
            department.tentMarkings.forEach { tentMarking ->
                hasTentMarkings = true
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

        if (!hasTentMarkings) {
            document.add(Paragraph("Es sind keine Zeltmarkierungen vorhanden."))
        }

        document.close()
        return out.toByteArray()
    }

    fun createMissingJuleika(): ByteArray {
        authorityService.isLkKarlsruhe()
        val out = ByteArrayOutputStream()
        val document = prepareDocument(out, PageSize.A4)
        var missingJuleikas = false

        val departments = departmentService.getDepartments(true)
        val attendeesPerDepartment = attendeeService.getAttendeesPerDepartments()
        val eventStart = settingsService.getSettings().eventStart

        departments
            .sortedBy { it.headDepartmentName + it.name }
            .map { department ->
                val attendeesPerRole = attendeesPerDepartment[department.id]?.groupBy { it.role }
                val youthLeader =
                    attendeeService.getYouthLeaderIn(attendeesPerRole?.get(AttendeeRole.YOUTH_LEADER) ?: emptyList())
                val (validLeader, invalidLeader) = youthLeader
                    .partition { attendeeRoleHelper.leaderWithValidJuleika(it, eventStart) }
                val numberOfYouth = attendeesPerRole?.get(AttendeeRole.YOUTH)?.size ?: 0

                if (validLeader.size >= attendeeRoleHelper.leaderFor(numberOfYouth)) {
                    return@map
                }
                missingJuleikas = true
                document.add(Paragraph("${department.headDepartmentName} ${department.name}", headlineFont))
                document.add(Paragraph("Jugendwart: ${department.leaderName}, EMail: ${department.leaderEMail}, Telefon: ${department.phoneNumber}"))

                document.add(Paragraph("Angemeldete Teilnehmer: ${numberOfYouth}"))
                document.add(Paragraph("Anzahl Jugendleiter mit gültiger Juleika: ${validLeader.size}"))
                document.add(
                    Paragraph(
                        "Mindestanzahl geforderter Jugendleiter mit gültiger Juleika: ${
                            attendeeRoleHelper.leaderFor(numberOfYouth)
                        }"
                    )
                )
                document.add(Paragraph("Anzahl Jugendleiter ohne gültige Juleika: ${invalidLeader.size}"))

                val list = List()
                list.setListSymbol("\u2022")
                for (att in invalidLeader) {
                    list.add(
                        " ${att.firstName} ${att.lastName} Juleikanummer: ${att.juleikaNumber} Gültig bis: ${
                            att.juleikaExpireDate?.format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            ) ?: "-"
                        }"
                    )
                }
                document.add(list)
                document.newPage()
            }

        if (!missingJuleikas) {
            document.add(Paragraph("Es sind keine fehlenden Juleika vorhanden."))
        }

        document.close()
        return out.toByteArray()
    }

    public fun createTentsAndDutiesCSV(): String {
        authorityService.isLkKarlsruhe()
        val numberOfDuties = settingsService.getSettings().numberOfDuties
        val departments = departmentService.getDepartments(true)
        return getTentAndDutiesCsv(departments, numberOfDuties)

    }

    private fun getTentAndDutiesCsv(
        departments: kotlin.collections.List<DepartmentEntry>,
        numberOfDuties: Int
    ): String {
        val attendeesPerDepartment =
            attendeeService.getAttendeesPerDepartments()
                .filterNot { it.value.first().department.isOrganizer() }
                .mapValues { it.value.size }
        val tentsByDepartmentId = tentsService.getAllTents()
        var dutyNumber = 1;
        val sb = StringBuilder()

        // Calculate total number of attendees
        val totalAttendees = attendeesPerDepartment.values.sum()

        // Calculate duties based on department sizes
        val dutiesPerDepartment = calculateNumberOfDuties(attendeesPerDepartment, totalAttendees, numberOfDuties)

        // Add header row
        sb.append("Jugendfeuer;Teilnehmer;SG 200;SG 20;SG 30;SG 40;SG 50;Zelte gesamt;Anzahl Lagerdienste;Lagerdienste\n")


        // Add department rows
        for (department in departments.sortedBy { it.headDepartmentName + it.name }) {
            val totalDepartmentAttendees = attendeesPerDepartment[department.id] ?: 0
            val tents = tentsByDepartmentId.find { it.department.id == department.id } ?: TentsEntity(
                0,
                department,
                0,
                0,
                0,
                0,
                0
            )

            sb.append("${department.name};$totalDepartmentAttendees")


            // Add tent counts by type
            sb.append(";${tents.sg200}")
            sb.append(";${tents.sg20}")
            sb.append(";${tents.sg30}")
            sb.append(";${tents.sg40}")
            sb.append(";${tents.sg50}")

            // Add total tents
            sb.append(";${tents.sg20 + tents.sg30 + tents.sg40 + tents.sg50 + tents.sg200}")

            // Get number of duties for this department
            val numberOfDuties = dutiesPerDepartment[department.id] ?: 0
            sb.append(";$numberOfDuties")
            val duties = (dutyNumber..dutyNumber + numberOfDuties - 1).joinToString(", ")
            dutyNumber += numberOfDuties
            sb.append(";$duties")

            sb.append("\n")
        }

        val summedUpTents = sumUpTents(tentsByDepartmentId)
        sb.append("Gesamt; $totalAttendees")
        // Add tent counts by type
        sb.append(";${summedUpTents.sg200}")
        sb.append(";${summedUpTents.sg20}")
        sb.append(";${summedUpTents.sg30}")
        sb.append(";${summedUpTents.sg40}")
        sb.append(";${summedUpTents.sg50}")

        // Add total tents and number of shifts
        sb.append(";${summedUpTents.sg20 + summedUpTents.sg30 + summedUpTents.sg40 + summedUpTents.sg50 + summedUpTents.sg200}")

        return sb.toString()
    }

    private fun calculateNumberOfDuties(
        attendeesPerDepartment: Map<Long, Int>,
        totalAttendees: Int,
        numberOfShifts: Int
    ): Map<Long, Int> {
        val duties = mutableMapOf<Long, Int>()
        var allocatedShifts = 0

        // First distribute the shifts proportionally
        for ((departmentId, attendeeCount) in attendeesPerDepartment) {
            if (totalAttendees > 0) {
                val proportionalShifts =
                    min(1, round(attendeeCount.toDouble() / totalAttendees * numberOfShifts).toInt())
                duties[departmentId] = proportionalShifts
                allocatedShifts += proportionalShifts
            }
        }

        val departmentsBySize = attendeesPerDepartment.entries
            .sortedByDescending { it.value }
            .map { it.key }

        var departmentCounter = 0

        // Distribute remaining shifts to largest departments first
        while (allocatedShifts < numberOfShifts) {
            val departmentId = departmentsBySize[departmentCounter]
            duties[departmentId] = (duties[departmentId] ?: 0) + 1
            allocatedShifts++
            departmentCounter = (departmentCounter + 1) % departmentsBySize.size
        }

        // Handle case where we might have allocated too many shifts due to rounding
        while (allocatedShifts > numberOfShifts) {
            val departmentId = departmentsBySize[departmentCounter]
            if (duties[departmentId]!! > 0) {
                duties[departmentId] = (duties[departmentId] ?: 0) - 1
                allocatedShifts--
            }
            departmentCounter = (departmentCounter + 1) % departmentsBySize.size
        }

        return duties
    }

    private fun sumUpTents(tentsByDepartmentId: kotlin.collections.List<TentsEntity>): TentsEntity {
        return tentsByDepartmentId.stream()
            .reduce(
                TentsEntity(
                    0,
                    DepartmentEntry(0, "Gesamt", "", "", "", "", emptySet(), "", false, emptySet(), null),
                    0,
                    0,
                    0,
                    0,
                    0
                )
            ) { acc, tents ->
                TentsEntity(
                    0,
                    acc.department,
                    acc.sg200 + tents.sg200,
                    acc.sg20 + tents.sg20,
                    acc.sg30 + tents.sg30,
                    acc.sg40 + tents.sg40,
                    acc.sg50 + tents.sg50
                )
            }
    }
}
