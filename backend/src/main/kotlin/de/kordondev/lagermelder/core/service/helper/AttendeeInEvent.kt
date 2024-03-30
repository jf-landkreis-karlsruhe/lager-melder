package de.kordondev.lagermelder.core.service.helper

import java.time.Instant

data class AttendeeInEvent(
    val attendeeFirstName: String,
    val attendeeLastName: String,
    val eventName: String,
    val time: Instant
)
