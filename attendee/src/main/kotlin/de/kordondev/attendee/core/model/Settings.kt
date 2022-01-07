package de.kordondev.attendee.core.model

import java.time.LocalDateTime

data class Settings(
    val id: Long,
    val registrationEnd: LocalDateTime
)