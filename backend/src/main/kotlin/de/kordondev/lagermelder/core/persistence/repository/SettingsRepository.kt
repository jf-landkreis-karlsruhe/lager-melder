package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import org.springframework.data.repository.CrudRepository

interface SettingsRepository : CrudRepository<SettingsEntry, Long> {
}