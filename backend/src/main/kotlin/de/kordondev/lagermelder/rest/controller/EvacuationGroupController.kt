package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.EvacuationGroupService
import de.kordondev.lagermelder.rest.model.RestEvacuationGroup
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EvacuationGroupController(
    private val evacuationGroupService: EvacuationGroupService
) {

    @GetMapping("/evacuation-groups")
    fun getEvacuationGroups(): List<RestEvacuationGroup> {
        return evacuationGroupService.getEvacuationGroups()
            .map { RestEvacuationGroup.of(it) }
    }
}