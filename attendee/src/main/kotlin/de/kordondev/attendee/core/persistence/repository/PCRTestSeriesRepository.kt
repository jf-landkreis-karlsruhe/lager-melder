package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.PCRTestSeriesEntry
import org.springframework.data.repository.CrudRepository

interface PCRTestSeriesRepository : CrudRepository<PCRTestSeriesEntry, Long> {
    fun findAllByTrashedIsFalse(): Set<PCRTestSeriesEntry>
    fun findByIdAndTrashedIsFalse(id: Long): PCRTestSeriesEntry?
    fun findByIdAndTrashedIs(id: Long, trashed: Boolean): PCRTestSeriesEntry?
    fun findByNameAndTrashedIsTrue(name: String): PCRTestSeriesEntry?
}