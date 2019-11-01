package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import org.springframework.data.repository.CrudRepository

interface AttendeeRepository : CrudRepository<AttendeeEntry, Long> {
}