package de.kordondev.lagermelder.exception

import de.kordondev.lagermelder.exception.ErrorConstants.EXISTING_DEPENDENCY_ERROR

class ExistingDependencyException(message: String?) : RuntimeException(message) {
    val key = EXISTING_DEPENDENCY_ERROR
}
