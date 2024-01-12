package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.model.Event
import de.kordondev.lagermelder.core.persistence.entry.EventType

data class RestEvent(
    val id: Long,
    val name: String,
    val code: String,
    val type: EventType
) {
    companion object {
        fun of(event: Event): RestEvent {
            return RestEvent(
                id = event.id,
                name = event.name,
                code = event.code,
                type = event.type
            )
        }
    }
}