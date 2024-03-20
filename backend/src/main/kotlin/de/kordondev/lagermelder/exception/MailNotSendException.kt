package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.MAIL_NOT_SEND_ERROR

class MailNotSendException(message: String?) : RuntimeException(message) {
    val key = MAIL_NOT_SEND_ERROR
}
