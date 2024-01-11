package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.YouthPlanAttendeeRoleService
import de.kordondev.lagermelder.rest.model.YouthPlanDistribution
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class YouthPlanAttendeeRoleController(
    private val youthPlanAttendeeRoleService: YouthPlanAttendeeRoleService
) {
    @GetMapping("/youth-plan-attendees/distribution")
    fun getYouthPlanDistribution(): YouthPlanDistribution {
        return youthPlanAttendeeRoleService.getAttendeeDistribution()
    }
}