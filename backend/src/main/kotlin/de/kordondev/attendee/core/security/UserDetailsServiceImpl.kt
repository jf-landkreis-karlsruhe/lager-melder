package de.kordondev.attendee.core.security

import de.kordondev.attendee.core.persistence.entry.UserEntry
import de.kordondev.attendee.core.persistence.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails {
        return userRepository.findOneByUserName(userName)
            ?.let { UserEntry.to(it) }
            ?.let { user -> User(user.userName, user.passWord, listOf()) }
            ?: throw UsernameNotFoundException(userName)
    }

}