package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.AttendeeInEventEntry
import org.springframework.data.repository.CrudRepository

interface AttendeeInEventRepository : CrudRepository<AttendeeInEventEntry, Long> {
}
