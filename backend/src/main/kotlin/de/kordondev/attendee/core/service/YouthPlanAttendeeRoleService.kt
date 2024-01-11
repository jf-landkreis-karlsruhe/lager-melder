package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.YouthPlanAttendeeRoleEntry
import de.kordondev.attendee.core.persistence.repository.YouthPlanAttendeeRolesRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.core.service.helper.YouthPlanAttendeeRoleHelper
import de.kordondev.attendee.rest.model.YouthPlanDistribution
import org.springframework.stereotype.Service

@Service
class YouthPlanAttendeeRoleService(
    private val youthPlanAttendeeRolesRepository: YouthPlanAttendeeRolesRepository,
    private val attendeeService: AttendeeService,
    private val settingsService: SettingsService,
    private val youthPlanAttendeeRoleHelper: YouthPlanAttendeeRoleHelper,
    private val authorityService: AuthorityService
) {

    fun saveAll(attendeeRoles: List<YouthPlanAttendeeRoleEntry>): List<YouthPlanAttendeeRoleEntry> {
        return youthPlanAttendeeRolesRepository.saveAll(attendeeRoles).toList()
    }

    fun getAll(): List<YouthPlanAttendeeRoleEntry> {
        return youthPlanAttendeeRolesRepository.findAll().toList()
    }

    fun getAttendeeDistribution(): YouthPlanDistribution {
        authorityService.isSpecializedFieldDirector()
        val distribution = getOptimizedLeaderAndAttendeeIds().groupBy { it.youthPlanRole }
        return YouthPlanDistribution(
            distribution[AttendeeRole.YOUTH_LEADER]?.size,
            distribution[AttendeeRole.YOUTH]?.size
        )
    }

    fun getOptimizedLeaderAndAttendeeIds(): List<YouthPlanAttendeeRoleEntry> {
        val undistributedAttendees = attendeeService.getAttendeesWithoutYouthPlanRole()
        val distributedAttendees = getAll()
        val newDistributed = youthPlanAttendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            distributedAttendees,
            undistributedAttendees,
            settingsService.getSettings().eventStart
        )
        saveAll(newDistributed)
        return distributedAttendees + newDistributed
    }

}
