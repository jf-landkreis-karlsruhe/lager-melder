package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.UNEXPECTED_TYPE

class UnexpectedTypeException(message: String?) : RuntimeException(message) {
    val key = UNEXPECTED_TYPE
}