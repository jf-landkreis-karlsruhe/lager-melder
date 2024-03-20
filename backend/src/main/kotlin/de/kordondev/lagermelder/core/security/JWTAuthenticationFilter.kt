package de.kordondev.lagermelder.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.kordondev.lagermelder.core.persistence.repository.UserRepository
import de.kordondev.lagermelder.core.security.SecurityConstants.EXPIRATION_TIME
import de.kordondev.lagermelder.core.security.SecurityConstants.HEADER_STRING
import de.kordondev.lagermelder.core.security.SecurityConstants.SECRET
import de.kordondev.lagermelder.core.security.SecurityConstants.TOKEN_PREFIX
import de.kordondev.lagermelder.rest.model.RestLoginUser
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(
    private val auth: AuthenticationManager,
    private val userRepository: UserRepository
) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val user = jacksonObjectMapper().readValue<RestLoginUser>(request.inputStream)
            auth.authenticate(
                UsernamePasswordAuthenticationToken(
                    user.username,
                    user.password,
                    listOf()
                )
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return super.attemptAuthentication(request, response)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        userRepository.findOneByUserName((authResult.principal as User).username)
            ?.let { user ->
                JWT.create()
                    .withSubject(user.userName)
                    .withClaim("departmentId", user.department.id)
                    .withClaim("role", user.role)
                    .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(Algorithm.HMAC512(SECRET))
            }
            ?.let { token ->
                response.contentType = "application/json"
                response.characterEncoding = "UTF-8"
                val jsonBody = "{\"$HEADER_STRING\":\"$TOKEN_PREFIX$token\"}"
                response.writer.write(jsonBody)
                response.writer.flush()
                response.writer.close()
            }
    }
}
