package de.kordondev.attendee.core.persistence

import de.kordondev.attendee.core.persistence.entry.EventEntry
import org.springframework.data.repository.CrudRepository

interface EventRepository : CrudRepository<EventEntry, Long> {
    fun findByCodeOrNull(code: String): EventEntry?
}