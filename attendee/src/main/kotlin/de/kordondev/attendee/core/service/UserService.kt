package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.mail.MailService
import de.kordondev.attendee.core.model.NewUser
import de.kordondev.attendee.core.model.User
import de.kordondev.attendee.core.persistence.entry.UserEntry
import de.kordondev.attendee.core.persistence.repository.UserRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.exception.ResourceAlreadyExistsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserService (
        private val userRepository: UserRepository,
        private val authorityService: AuthorityService,
        private val mailService: MailService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder
    ) {
    fun createUser(user: NewUser) : User {
        authorityService.isAdmin()
        userRepository
                .findOneByUserName(user.userName)
                ?.let { throw ResourceAlreadyExistsException("Username already exists") }
        val userWithEncryptedPassword = user.copy(passWord = bCryptPasswordEncoder.encode(user.passWord))
        return userRepository
                .save(UserEntry.of(userWithEncryptedPassword))
                .let { savedUser -> UserEntry.to(savedUser) }
                .also { sendEmail(user)}
    }

    fun sendEmail(user: NewUser) {
        mailService.sendSimpleMessage(
                to = user.department.leaderEMail,
                subject = "Du hast dich erfolgreich angemeldet",
                text = "Glückwunsch du bist angemeldet mit dem Benutzername ${user.userName} für ${user.department.name}, dein Passwort ist: ${user.passWord}"
        )

    }

}