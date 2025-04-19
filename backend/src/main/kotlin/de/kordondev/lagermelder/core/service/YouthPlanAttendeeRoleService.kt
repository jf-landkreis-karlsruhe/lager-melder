package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.YouthPlanAttendeeRoleEntry
import de.kordondev.lagermelder.core.persistence.repository.YouthPlanAttendeeRolesRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.service.helper.AttendeeRoleHelper
import de.kordondev.lagermelder.exception.WrongTimeException
import de.kordondev.lagermelder.rest.model.YouthPlanDistribution
import org.springframework.stereotype.Service

@Service
class YouthPlanAttendeeRoleService(
    private val youthPlanAttendeeRolesRepository: YouthPlanAttendeeRolesRepository,
    private val attendeeService: AttendeeService,
    private val settingsService: SettingsService,
    private val attendeeRoleHelper: AttendeeRoleHelper,
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
        if (!settingsService.canRegistrationFilesDownloaded()) {
            throw WrongTimeException("Erst wenn die Registrierungsunterlagen heruntergeladen werden können, kann die Verteilung der Teilnehmerrollen erfolgen.")
        }
        val distribution = getOptimizedLeaderAndAttendeeIds().groupBy { it.youthPlanRole }
        return YouthPlanDistribution(
            distribution[AttendeeRole.YOUTH_LEADER]?.size,
            distribution[AttendeeRole.YOUTH]?.size
        )
    }

    fun getOptimizedLeaderAndAttendeeIds(): List<YouthPlanAttendeeRoleEntry> {
        val undistributedAttendees = attendeeService.getAttendeesWithoutYouthPlanRole()
        val distributedAttendees = getAll()
        val newDistributed = attendeeRoleHelper.getOptimizedLeaderAndAttendeeIds(
            distributedAttendees,
            undistributedAttendees,
            settingsService.getSettings().eventStart,
        )
        saveAll(newDistributed)
        return distributedAttendees + newDistributed
    }

}
