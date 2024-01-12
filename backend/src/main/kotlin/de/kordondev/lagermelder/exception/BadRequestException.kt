package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.BAD_REQUEST_ERROR

class BadRequestException(message: String?) : RuntimeException(message) {
    val key = BAD_REQUEST_ERROR
}
