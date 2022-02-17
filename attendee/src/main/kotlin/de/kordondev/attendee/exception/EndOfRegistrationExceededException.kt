package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.END_OF_REGISTRATION_ERROR

class EndOfRegistrationExceededException(message: String?) : RuntimeException(message) {
    val key = END_OF_REGISTRATION_ERROR
}