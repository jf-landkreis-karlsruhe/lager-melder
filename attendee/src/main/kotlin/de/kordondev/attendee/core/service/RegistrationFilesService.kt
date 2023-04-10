package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.pdf.AttendeesCommunal
import de.kordondev.attendee.core.pdf.AttendeesKarlsruhe
import de.kordondev.attendee.core.pdf.StateYouthPlanAttendees
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
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
    val attendeesCommunal: AttendeesCommunal,
    val departmentService: DepartmentService,
    val settingsService: SettingsService,
    val youthPlanAttendeeRoleService: YouthPlanAttendeeRoleService
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

    fun getStateYouthPlanYouth(id: Long): ByteArray {
        val department = departmentService.getDepartment(id)
        val result = youthPlanAttendeeRoleService.getOptimizedLeaderAndAttendeeIds()
            .filter { it.youthPlanRole == AttendeeRole.YOUTH }
            .filter { it.departmentId == department.id }
            .map { AttendeeEntry.to(it.attendee) }
            .let { stateYouthPlanAttendees.createStateYouthPlanAttendees(it) }

        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getStateYouthPlanLeader(id: Long): ByteArray {
        val department = departmentService.getDepartment(id)
        val result = youthPlanAttendeeRoleService.getOptimizedLeaderAndAttendeeIds()
            .filter { it.youthPlanRole == AttendeeRole.YOUTH_LEADER }
            .filter { it.departmentId == department.id }
            .map { AttendeeEntry.to(it.attendee) }
            .let { stateYouthPlanAttendees.createStateYouthPlanAttendees(it) }
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