package de.kordondev.lagermelder.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import de.kordondev.lagermelder.core.persistence.repository.UserRepository
import de.kordondev.lagermelder.core.security.SecurityConstants.DEPARTMENT_ID_PREFIX
import de.kordondev.lagermelder.core.security.SecurityConstants.ROLE_PREFIX
import de.kordondev.lagermelder.core.security.SecurityConstants.SECRET
import de.kordondev.lagermelder.core.security.SecurityConstants.USER_ID_PREFIX
import de.kordondev.lagermelder.rest.model.RestJWT
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JWTAuthorizationFilter(
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        val token = RestJWT.getToken(request)

        if (token == null) {
            chain.doFilter(request, response)
            return
        }

        val authentication = getAuthentication(request)

        if (authentication != null) {
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = RestJWT.getToken(request)
        if (token != null) {
            val username = verifiedSubject(token)
            if (username != null) {
                return userRepository
                    .findOneByUserName(username)
                    ?.let { user ->
                        UsernamePasswordAuthenticationToken(
                            user.userName,
                            null,
                            listOf(
                                SimpleGrantedAuthority(USER_ID_PREFIX + user.id.toString()),
                                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + user.department.id.toString()),
                                SimpleGrantedAuthority(ROLE_PREFIX + user.role),
                            ),
                        )
                    }
            }
        }
        return null
    }

    private fun verifiedSubject(token: String): String? =
        try {
            JWT
                .require(Algorithm.HMAC512(SECRET))
                .build()
                .verify(token)
                .subject
        } catch (e: JWTVerificationException) {
            // invalid or expired token: continue unauthenticated, protected endpoints answer with 403
            logger.debug("Rejected JWT: ${e.message}", e)
            null
        }
}
