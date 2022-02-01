package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.PCRTestSeriesEntry
import org.springframework.data.repository.CrudRepository

interface PCRTestSeriesRepository : CrudRepository<PCRTestSeriesEntry, Long> {}