package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.pdf.*
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


@Service
class RegistrationFilesService(
    val stateYouthPlanAttendees: StateYouthPlanAttendees,
    val attendeesKarlsruhe: AttendeesKarlsruhe,
    val attendeeService: AttendeeService,
    val stateYouthPlanLeader: StateYouthPlanLeader,
    val attendeesCommunal: AttendeesCommunal,
    val departmentService: DepartmentService
) {
    private val logger: Logger = LoggerFactory.getLogger(RegistrationFilesService::class.java)

    fun getAttendeesKarlsruhe(id: Long): ByteArray {
        val result = departmentService.getDepartment(id)
                .let { attendeeService.getAttendeesForDepartment(it) }
                .let { attendeesKarlsruhe.createAttendeesKarlsruhePdf(it) }
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getStateYouthPlanAttendees(id: Long):ByteArray {
        val result = departmentService.getDepartment(id)
            .let { attendeeService.getAttendeesForDepartment(it) }
            .let { stateYouthPlanAttendees.createStateYouthPlanAttendees(it) }

        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getStateYouthPlanLeader(id: Long): ByteArray {
        val result = departmentService.getDepartment(id)
                .let { attendeeService.getAttendeesForDepartment(it) }
                .let { stateYouthPlanLeader.createStateYouthPlanLeaderPdf(it) }
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getAttendeesCommunal(id: Long): ByteArray {
        val department = departmentService.getDepartment(id)
        val result = department
                .let { attendeeService.getAttendeesForDepartment(it) }
                .let { attendeesCommunal.createAttendeesCommunalPdf(it, department.name) }
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

}