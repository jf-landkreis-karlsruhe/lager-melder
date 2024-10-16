package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.EventDayEntity

data class RestEventDay(
    val id: String,
    val name: String,
    val dayOfEvent: Int
) {
    companion object {
        fun of(eventDay: EventDayEntity) = RestEventDay(
            id = eventDay.id,
            name = eventDay.name,
            dayOfEvent = eventDay.dayOfEvent
        )
    }
}
