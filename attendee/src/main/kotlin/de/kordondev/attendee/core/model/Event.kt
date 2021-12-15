package de.kordondev.attendee.core.model

data class NewEvent(
    val name: String
)

data class Event(
    val id: Long,
    val name: String,
    val code: String
)