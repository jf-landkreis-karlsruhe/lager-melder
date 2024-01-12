package de.kordondev.lagermelder.core.model

import de.kordondev.lagermelder.core.persistence.entry.EventType

data class NewEvent(
    val name: String
)

data class Event(
    val id: Long,
    val name: String,
    val code: String,
    val type: EventType,
    val trashed: Boolean
)