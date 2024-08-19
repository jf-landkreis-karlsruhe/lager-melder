package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.pdf.AttendeesCommunal
import de.kordondev.lagermelder.core.pdf.AttendeesKarlsruhe
import de.kordondev.lagermelder.core.pdf.StateYouthPlanAttendees
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.exception.WrongTimeException
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
        if (!settingsService.canRegistrationFilesDownloaded()) {
            throw WrongTimeException("Dateien können noch nicht heruntergeladen werden.")
        }
        val result = departmentService.getDepartment(id)
            .let { attendeeService.getAttendeesForDepartment(it) }
            .let { it.youths + it.youthLeaders }
            .let { attendeesKarlsruhe.createAttendeesKarlsruhePdf(it) }
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getStateYouthPlanYouth(id: Long): ByteArray {
        if (!settingsService.canRegistrationFilesDownloaded()) {
            throw WrongTimeException("Dateien können noch nicht heruntergeladen werden.")
        }
        val department = departmentService.getDepartment(id)
        val result = youthPlanAttendeeRoleService.getOptimizedLeaderAndAttendeeIds()
            .filter { it.youthPlanRole == AttendeeRole.YOUTH }
            .filter { it.departmentId == department.id }
            .map { it.attendee }
            .let { stateYouthPlanAttendees.createStateYouthPlanAttendees(it) }

        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getStateYouthPlanLeader(id: Long): ByteArray {
        if (!settingsService.canRegistrationFilesDownloaded()) {
            throw WrongTimeException("Dateien können noch nicht heruntergeladen werden.")
        }
        val department = departmentService.getDepartment(id)
        val result = youthPlanAttendeeRoleService.getOptimizedLeaderAndAttendeeIds()
            .filter { it.youthPlanRole == AttendeeRole.YOUTH_LEADER }
            .filter { it.departmentId == department.id }
            .map { it.attendee }
            .let { stateYouthPlanAttendees.createStateYouthPlanAttendees(it) }
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getAttendeesCommunal(id: Long): ByteArray {
        if (!settingsService.canRegistrationFilesDownloaded()) {
            throw WrongTimeException("Dateien können noch nicht heruntergeladen werden.")
        }
        val department = departmentService.getDepartment(id)
        val result = department
            .let { attendeeService.getAttendeesForDepartment(it) }
            .let { it.youths + it.youthLeaders }
            .let { attendeesCommunal.createAttendeesCommunalPdf(it, department.name) }
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }
}