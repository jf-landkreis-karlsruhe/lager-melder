package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.Event

data class RestEvent(
    val id: Long,
    val name: String,
    val code: String
) {
    companion object {
        fun of(event: Event): RestEvent {
            return RestEvent(
                id = event.id,
                name = event.name,
                code = event.code
            )
        }
    }
}