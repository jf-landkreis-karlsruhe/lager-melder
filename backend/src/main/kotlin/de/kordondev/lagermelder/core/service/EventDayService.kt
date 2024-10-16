package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.EventDayEntity
import de.kordondev.lagermelder.core.persistence.repository.EventDayRepository
import org.springframework.stereotype.Service


@Service
class EventDayService(
    val eventDayRepository: EventDayRepository
) {
    fun getEventDays(): Iterable<EventDayEntity> = eventDayRepository.findAll()
}