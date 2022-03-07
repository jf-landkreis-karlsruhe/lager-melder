package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.NOT_DELETABLE_ERROR

class NotDeletableException(message: String?) : RuntimeException(message) {
    val key = NOT_DELETABLE_ERROR
}
