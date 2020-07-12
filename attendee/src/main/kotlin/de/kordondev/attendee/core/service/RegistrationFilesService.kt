package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.pdf.AttendeesBW
import de.kordondev.attendee.core.pdf.YouthPlanOverview
import org.apache.commons.io.IOUtils
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


@Service
class RegistrationFilesService(
        val youthPlanOverview: YouthPlanOverview,
        val attendeesBW: AttendeesBW,
        val attendeeService: AttendeeService,
        val departmentService: DepartmentService
) {
    private val logger: Logger = LoggerFactory.getLogger(RegistrationFilesService::class.java)

    fun getYouthPlan(id: Long): ByteArray {
        val result = youthPlanOverview.createYouthPlan()
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getAttendeesBW(id: Long):ByteArray {
        val dep =departmentService.getDepartment(id);
        val att  = attendeeService.getAttendeesForDepartment(dep)
        val result = departmentService.getDepartment(id)
                .let { attendeeService.getAttendeesForDepartment(it) }
                .let { attendeesBW.createAttendeesBWPdf(it) }

        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }


}