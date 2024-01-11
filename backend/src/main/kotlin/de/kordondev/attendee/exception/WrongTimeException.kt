package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.WRONG_TIME_EXCEPTION

class WrongTimeException(message: String?) : RuntimeException(message) {
    val key = WRONG_TIME_EXCEPTION
}