package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.mail.MailSenderService
import de.kordondev.lagermelder.core.persistence.entry.ResetTokenEntry
import de.kordondev.lagermelder.core.persistence.repository.ResetTokenRepository
import de.kordondev.lagermelder.core.security.PasswordGenerator
import de.kordondev.lagermelder.exception.MailNotSendException
import org.springframework.stereotype.Service

@Service
class SecurityService(
    private val mailSenderService: MailSenderService,
    private val userService: UserService,
    private val resetTokenRepository: ResetTokenRepository
) {
    fun sendResetPasswordLink(username: String, linkAddress: String) {
        val user = userService.getUserByUsername(username)
        val resetToken = PasswordGenerator.generateSecureToken()
        resetTokenRepository.save(ResetTokenEntry.of(resetToken, user.id))

        val resetLink = linkAddress + resetToken

        val mailSend = mailSenderService.sendForgotPasswordMail(username, resetLink)
        if (!mailSend) {
            throw MailNotSendException("Mail could not be send")
        }
    }

}
