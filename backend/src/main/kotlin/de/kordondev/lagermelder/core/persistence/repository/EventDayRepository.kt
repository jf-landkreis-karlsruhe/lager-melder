package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.EventDayEntity
import org.springframework.data.repository.CrudRepository

interface EventDayRepository : CrudRepository<EventDayEntity, String> {
}