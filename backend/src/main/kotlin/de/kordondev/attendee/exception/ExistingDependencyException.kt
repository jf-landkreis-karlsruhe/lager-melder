package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.EXISTING_DEPENDENCY_ERROR

class ExistingDependencyException(message: String?) : RuntimeException(message) {
    val key = EXISTING_DEPENDENCY_ERROR
}
