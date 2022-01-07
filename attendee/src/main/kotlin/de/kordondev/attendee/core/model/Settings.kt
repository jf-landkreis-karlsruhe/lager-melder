package de.kordondev.attendee.core.model

import java.time.Instant

data class Settings(
    val id: Long,
    val registrationEnd: Instant
)