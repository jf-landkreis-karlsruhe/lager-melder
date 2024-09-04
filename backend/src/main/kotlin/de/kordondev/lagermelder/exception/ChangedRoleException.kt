package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.CHANGED_ROLE

class ChangedRoleException(message: String?) : RuntimeException(message) {
    val key = CHANGED_ROLE
}