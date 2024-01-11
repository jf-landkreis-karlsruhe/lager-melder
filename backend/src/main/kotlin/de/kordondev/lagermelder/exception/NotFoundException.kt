package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.NOT_FOUND_ERROR

class NotFoundException(message: String?) : RuntimeException(message) {
    val key = NOT_FOUND_ERROR
}