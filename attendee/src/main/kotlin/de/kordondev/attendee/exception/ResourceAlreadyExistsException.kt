package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.RESOURCE_ALREADY_EXISTS_ERROR

class ResourceAlreadyExistsException(message: String?) : RuntimeException(message) {
    val key = RESOURCE_ALREADY_EXISTS_ERROR
}