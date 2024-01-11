package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.NOT_DELETABLE_ERROR

class NotDeletableException(message: String?) : RuntimeException(message) {
    val key = NOT_DELETABLE_ERROR
}
