package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.WRONG_TIME_EXCEPTION

class WrongTimeException(message: String?) : RuntimeException(message) {
    val key = WRONG_TIME_EXCEPTION
}