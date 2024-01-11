package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.SettingsEntry
import org.springframework.data.repository.CrudRepository

interface SettingsRepository : CrudRepository<SettingsEntry, Long> {
}