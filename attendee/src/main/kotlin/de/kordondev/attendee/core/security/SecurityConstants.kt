package de.kordondev.attendee.core.security

object SecurityConstants {
    const val SECRET = "SecretKeyToGenJWTs"
    const val EXPIRATION_TIME = 864_000_000L;
    const val HEADER_STRING = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
}