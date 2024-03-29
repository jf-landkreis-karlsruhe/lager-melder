package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.EventEntry
import de.kordondev.lagermelder.core.persistence.entry.EventType
import org.springframework.data.repository.CrudRepository

interface EventRepository : CrudRepository<EventEntry, Long> {
    fun findByCodeAndTrashedIsFalse(code: String): EventEntry?
    fun findByIdAndTrashedIsFalse(id: Long): EventEntry?
    fun findAllByTrashedIsFalse(): List<EventEntry>

    fun findByTypeAndTrashedIsFalse(type: EventType): EventEntry?
}