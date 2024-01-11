package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.RESOURCE_ALREADY_EXISTS_ERROR

class ResourceAlreadyExistsException(message: String?) : RuntimeException(message) {
    val key = RESOURCE_ALREADY_EXISTS_ERROR
}