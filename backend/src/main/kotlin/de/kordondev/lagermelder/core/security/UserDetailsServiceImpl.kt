package de.kordondev.lagermelder.core.security

import de.kordondev.lagermelder.core.persistence.repository.UserRepository
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
            ?.let { user -> User(user.userName, user.passWord, listOf()) }
            ?: throw UsernameNotFoundException(userName)
    }

}