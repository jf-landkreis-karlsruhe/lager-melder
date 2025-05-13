package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.pdf.AttendeesCommunal
import de.kordondev.lagermelder.core.pdf.AttendeesKarlsruhe
import de.kordondev.lagermelder.core.pdf.StateYouthPlanAttendees
import de.kordondev.lagermelder.core.pdf.StateYouthPlanLeader
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.service.models.Group
import de.kordondev.lagermelder.exception.WrongTimeException
import de.kordondev.lagermelder.rest.model.RestSubsidy
import de.kordondev.lagermelder.rest.model.SubsidyDistribution
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
    val youthPlanAttendeeRoleService: YouthPlanAttendeeRoleService,
    private val stateYouthPlanLeader: StateYouthPlanLeader
) {
    private val logger: Logger = LoggerFactory.getLogger(RegistrationFilesService::class.java)

    fun getAttendeesKarlsruhe(id: Long, group: Group): ByteArray {
        if (!settingsService.canRegistrationFilesDownloaded()) {
            throw WrongTimeException("Dateien können noch nicht heruntergeladen werden.")
        }
        val result = departmentService.getDepartment(id)
            .let { attendeeService.getAttendeesForDepartment(it) }
            .let {
                when (group) {
                    Group.PARTICIPANT -> it.youths + it.youthLeaders
                    Group.CHILD_GROUP -> it.children + it.childLeaders
                }
            }
            .let { attendeesKarlsruhe.createAttendeesKarlsruhePdf(it) }
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getStateYouthPlanYouth(id: Long, group: Group): ByteArray {
        if (!settingsService.canRegistrationFilesDownloaded()) {
            throw WrongTimeException("Dateien können noch nicht heruntergeladen werden.")
        }
        val department = departmentService.getDepartment(id)
        val result = youthPlanAttendeeRoleService.getOptimizedLeaderAndAttendeeIds()
            .filter {
                when (group) {
                    Group.PARTICIPANT -> it.youthPlanRole == AttendeeRole.YOUTH && listOf(
                        AttendeeRole.YOUTH_LEADER,
                        AttendeeRole.YOUTH
                    ).contains(it.attendee.role)

                    Group.CHILD_GROUP -> it.youthPlanRole == AttendeeRole.YOUTH && it.attendee.role == AttendeeRole.CHILD
                }
            }
            .filter { it.departmentId == department.id }
            .map { it.attendee }
            .let { attendees -> attendeeService.getAllAttendeesIn(attendees.map { it.id }) }
            .let { stateYouthPlanAttendees.createStateYouthPlanAttendees(it) }

        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getStateYouthPlanLeader(id: Long, group: Group): ByteArray {
        if (!settingsService.canRegistrationFilesDownloaded()) {
            throw WrongTimeException("Dateien können noch nicht heruntergeladen werden.")
        }
        val department = departmentService.getDepartment(id)
        val result = youthPlanAttendeeRoleService.getOptimizedLeaderAndAttendeeIds()
            .filter {
                when (group) {
                    Group.PARTICIPANT -> it.youthPlanRole == AttendeeRole.YOUTH_LEADER && it.attendee.role == AttendeeRole.YOUTH_LEADER
                    Group.CHILD_GROUP -> it.youthPlanRole == AttendeeRole.YOUTH_LEADER && it.attendee.role == AttendeeRole.CHILD_LEADER
                }
            }
            .filter { it.departmentId == department.id }
            .map { it.attendee }
            .let { attendees -> attendeeService.getAllAttendeesIn(attendees.map { it.id }) }
            .let { stateYouthPlanLeader.createStateYouthPlanLeaderPdf(it) }
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
            .let { attendeesCommunal.createAttendeesCommunalPdf(it, department) }
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun getSubsidy(id: Long): RestSubsidy {
        val department = departmentService.getDepartment(id)
        val youthPlanAttendees = youthPlanAttendeeRoleService.getOptimizedLeaderAndAttendeeIds()
            .filter { it.departmentId == department.id }
        val attendees = attendeeService.getAttendeesForDepartment(department)
        return RestSubsidy(
            id = id,
            participants = SubsidyDistribution(
                stateYouthPlanLeaders = youthPlanAttendees.count { it.youthPlanRole == AttendeeRole.YOUTH_LEADER && it.attendee.role == AttendeeRole.YOUTH_LEADER },
                stateYouthPlanParticipants = youthPlanAttendees.count {
                    it.youthPlanRole == AttendeeRole.YOUTH && listOf(
                        AttendeeRole.YOUTH_LEADER, AttendeeRole.YOUTH
                    ).contains(it.attendee.role)
                },
                karlsruheLeaders = attendees.youthLeaders.count(),
                karlsruheParticipants = attendees.youths.count(),
            ),
            childGroup = SubsidyDistribution(
                stateYouthPlanLeaders = youthPlanAttendees.count { it.youthPlanRole == AttendeeRole.YOUTH_LEADER && it.attendee.role == AttendeeRole.CHILD_LEADER },
                stateYouthPlanParticipants = youthPlanAttendees.count {
                    it.youthPlanRole == AttendeeRole.YOUTH && listOf(
                        AttendeeRole.CHILD_LEADER, AttendeeRole.CHILD
                    ).contains(it.attendee.role)
                },
                karlsruheLeaders = attendees.childLeaders.count(),
                karlsruheParticipants = attendees.children.count(),
            )
        )
    }
}