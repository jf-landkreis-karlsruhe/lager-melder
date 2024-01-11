package de.kordondev.lagermelder.core.security

object SecurityConstants {
    const val SECRET = "SecretKeyToGenJWTs"
    const val EXPIRATION_TIME = 10_800_000L
    const val HEADER_STRING = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    const val USER_ID_PREFIX = "USER_ID-"
    const val DEPARTMENT_ID_PREFIX = "DEPARTMENT_ID-"
    const val ROLE_PREFIX = "ROLE-"
}
