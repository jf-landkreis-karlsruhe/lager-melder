package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.EventEntry
import org.springframework.data.repository.CrudRepository

interface EventRepository : CrudRepository<EventEntry, Long> {
    fun findByCodeAndTrashedIsFalse(code: String): EventEntry?
    fun findByIdAndTrashedIsFalse(id: Long): EventEntry?
    fun findAllByTrashedIsFalse(): List<EventEntry>
}