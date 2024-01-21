import de.kordondev.lagermelder.core.mail.MailSenderService
import de.kordondev.lagermelder.core.persistence.repository.ResetTokenRepository
import de.kordondev.lagermelder.core.security.PasswordGenerator
import org.springframework.stereotype.Service

@Service
class SecurityService(
    private val mailSenderService: MailSenderService,
    private val resetTokenRepository: ResetTokenRepository,
){
    fun createResetPasswordLink(departmentLeaderMail: String, linkAddress: String): User {
      val resetToken = PasswordGenerator.generateSecureToken()
      resetTokenRepository.save(ResetTokenEntry.of(resetToken, departmentLeaderMail))

      val resetLink = linkAddress + resetToken

      val mailSend = mailSenderService.sendResetPasswordMail(departmentLeaderMail, resetLink)
      if !mailSend {
        throw MailNotSendException("Mail could not be send")
      }
    }

}
