package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.EvacuationGroupEntry
import de.kordondev.lagermelder.core.persistence.repository.EvacuationGroupRepository
import de.kordondev.lagermelder.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EvacuationGroupService(
    private val evacuationGroupRepository: EvacuationGroupRepository
) {
    fun getEvacuationGroup(id: String): EvacuationGroupEntry {
        return evacuationGroupRepository
            .findByIdOrNull(id)
            ?: throw NotFoundException("Evacuation group with id $id not found")
    }

    fun getEvacuationGroups(): List<EvacuationGroupEntry> {
        return evacuationGroupRepository.findAll().toList()
    }

}