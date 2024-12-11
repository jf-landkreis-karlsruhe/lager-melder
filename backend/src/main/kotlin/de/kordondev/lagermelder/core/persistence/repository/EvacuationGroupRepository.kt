package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.EvacuationGroupEntry
import org.springframework.data.repository.CrudRepository

interface EvacuationGroupRepository : CrudRepository<EvacuationGroupEntry, String> {
}

