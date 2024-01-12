package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.AttendeeInEventEntry
import org.springframework.data.repository.CrudRepository

interface AttendeeInEventRepository : CrudRepository<AttendeeInEventEntry, Long> {
}
