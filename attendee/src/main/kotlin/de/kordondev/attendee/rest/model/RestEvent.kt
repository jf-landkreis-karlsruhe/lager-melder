package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.Event
import de.kordondev.attendee.core.persistence.entry.EventType

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