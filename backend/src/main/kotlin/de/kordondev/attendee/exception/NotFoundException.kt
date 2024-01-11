package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.NOT_FOUND_ERROR

class NotFoundException(message: String?) : RuntimeException(message) {
    val key = NOT_FOUND_ERROR
}