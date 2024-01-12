package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.UNIQUE_ERROR

class UniqueException(message: String?) : RuntimeException(message) {
    val key = UNIQUE_ERROR
}