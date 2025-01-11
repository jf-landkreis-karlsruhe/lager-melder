package de.kordondev.lagermelder.rest.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.servlet.http.HttpServletRequest

const val HEADER_STRING = "Authorization"
const val TOKEN_PREFIX = "Bearer "

class RestJWT(private var token: String) {
    @get:JsonProperty("Authorization")
    val authorization: String = "$TOKEN_PREFIX$token"

    companion object {
        fun getToken(request: HttpServletRequest): String? {
            val header = request.getHeader(HEADER_STRING)
            if (header != null && header.startsWith(TOKEN_PREFIX)) {
                return header.replace(TOKEN_PREFIX, "")
            }
            return null
        }
    }

}