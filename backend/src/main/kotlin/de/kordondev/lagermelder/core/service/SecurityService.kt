
@Service
class SecurityService(
    private val mailSenderService: MailSenderService,
    private val passwordGenerator: PasswordGenerator,
    private val resetTokenRepository: ResetTokenRepository,
){
    fun createResetPasswordLink(departmentLeaderMail: String, linkAdress: String): User {
      val resetToken = passwordGenerator.generateSecureToken()
      resetTokenRepository.save(ResetTokenEntry.of(resetToken, departmentLeaderMail))

      val resetLink = linkAdress + resetToken

      val mailSend = mailSenderService.sendResetPasswordMail(departmentLeaderMail, resetLink)
      if !mailSend {
        throw MailNotSendException("Mail could not be send")
      }
    }

}
