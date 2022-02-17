package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.UNIQUE_ERROR

class UniqueException(message: String?) : RuntimeException(message) {
    val key = UNIQUE_ERROR
}