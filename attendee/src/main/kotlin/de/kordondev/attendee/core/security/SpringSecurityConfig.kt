package de.kordondev.attendee.core.security

import de.kordondev.attendee.core.persistence.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
class SpringSecurityConfig(
        private val userDetailsService: UserDetailsServiceImpl,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val userRepository: UserRepository
) : WebSecurityConfigurerAdapter()  {

    // Secure the endpoins with HTTP Basic authentication
    override fun configure(http: HttpSecurity) {

        http
                //HTTP Basic authentication
                .authorizeRequests()
                .antMatchers("/attendee/**").authenticated()
                .antMatchers("/department/**").authenticated()
                .and()
                .addFilter(JWTAuthenticationFilter(authenticationManager(), userRepository))
                .addFilter(JWTAuthorizationFilter(authenticationManager(), userRepository))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .cors()
                .and()
                .formLogin().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }


}