package de.kordondev.lagermelder.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.kordondev.lagermelder.core.persistence.entry.UserEntry
import de.kordondev.lagermelder.core.persistence.repository.UserRepository
import de.kordondev.lagermelder.core.security.SecurityConstants.EXPIRATION_TIME
import de.kordondev.lagermelder.core.security.SecurityConstants.SECRET
import de.kordondev.lagermelder.rest.model.RestJWT
import de.kordondev.lagermelder.rest.model.RestLoginUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
class CreateJWTAuthentication(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager
) : UsernamePasswordAuthenticationFilter(authenticationManager) {


    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val user = jacksonObjectMapper().readValue<RestLoginUser>(request.inputStream)
            val authenticationToken = UsernamePasswordAuthenticationToken(
                user.username,
                user.password,
                listOf()
            )
            this.authenticationManager.authenticate(authenticationToken)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        userRepository.findOneByUserName((authResult.principal as User).username)
            ?.let { user -> createJWT(response, user) }
            ?.let { jwt ->
                response.contentType = "application/json"
                response.characterEncoding = "UTF-8"
                val jsonString = jacksonObjectMapper().writeValueAsString(jwt)
                response.writer.write(jsonString)
                response.writer.flush()
                response.writer.close()
            }
    }

    fun createJWT(response: HttpServletResponse, user: UserEntry): RestJWT {
        val token = JWT.create()
            .withSubject(user.userName)
            .withClaim("departmentId", user.department.id)
            .withClaim("role", user.role)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(SECRET))
        return RestJWT(token)
    }
}
