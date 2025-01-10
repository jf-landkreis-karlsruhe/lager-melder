package de.kordondev.lagermelder.core.security

object SecurityConstants {
    const val SECRET = "SecretKeyToGenJWTs"
    const val EXPIRATION_TIME = 10_800_000L // ms = 3 hours
    const val USER_ID_PREFIX = "USER_ID-"
    const val DEPARTMENT_ID_PREFIX = "DEPARTMENT_ID-"
    const val ROLE_PREFIX = "ROLE-"
}
