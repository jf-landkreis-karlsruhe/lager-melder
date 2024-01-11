package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.BAD_REQUEST_ERROR

class BadRequestException(message: String?) : RuntimeException(message) {
    val key = BAD_REQUEST_ERROR
}
