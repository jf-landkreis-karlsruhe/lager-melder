package de.kordondev.lagermelder.core.security

import de.kordondev.lagermelder.core.persistence.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SpringSecurityConfig(
    private val userDetailsService: UserDetailsServiceImpl,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val userRepository: UserRepository,
    @Value("\${application.corsOrigins}") private val corsAllowedOrigins: List<String>
) {

    // Secure the endpoins with HTTP Basic authentication
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.authorizeHttpRequests { request ->
            request
                .requestMatchers("/login").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/users/forgotPasswordToken").permitAll()
                .requestMatchers("/users/resetPasswordWithToken").permitAll()
                .requestMatchers("/**").authenticated()
        }
            .sessionManagement { c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .formLogin { formLogin -> formLogin.disable() }
            .authenticationProvider(authenticationProvider())
            .addFilter(JWTAuthenticationFilter(authenticationManager(), userRepository)
            .addFilterBefore(JWTAuthorizationFilter(userRepository), JWTAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder)
        return authenticationProvider
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = corsAllowedOrigins
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        configuration.allowCredentials = true

        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        configuration.maxAge = 1800L
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}