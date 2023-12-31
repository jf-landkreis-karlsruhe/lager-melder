package de.kordondev.lagermelder.core.mail

import de.kordondev.lagermelder.core.pdf.PDFHelper.Companion.germanDate
import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import de.kordondev.lagermelder.core.security.AuthorityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import javax.mail.SendFailedException


@Service("MailSenderService")
class MailSenderService(
    @Value("\${application.mail.send}") private val sendMail: Boolean,
    @Value("\${spring.mail.from}") private val sendFrom: String,
    private val authorityService: AuthorityService,
    private val mailSender: JavaMailSender,
    private val htmlTemplateEngine: TemplateEngine
) {
    private val logger: Logger = LoggerFactory.getLogger(MailSenderService::class.java)
    private val newUserMailTemplate = "new-user"
    private val reminderMailTemplate = "reminder"
    private val registrationFinishedTemplate = "registration-finished"

    fun sendRegistrationMail(
        to: String,
        leaderName: String,
        username: String,
        password: String,
        settings: SettingsEntry
    ) {
        try {
            authorityService.isSpecializedFieldDirector()
            val headerLogoName = "kreiszeltlager-logo.jpg"
            val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")

            val cxt = Context()
            val registrationDeadlineDate = LocalDate.ofInstant(settings.registrationEnd, ZoneId.of("Europe/Berlin"))
            cxt.setVariable("leaderName", leaderName)
            cxt.setVariable("hostCity", settings.hostCity)
            cxt.setVariable("registrationDeadline", registrationDeadlineDate.format(germanDate))
            cxt.setVariable("username", username)
            cxt.setVariable("password", password)
            cxt.setVariable("headerLogo", headerLogoName);

            val mimeMessage = this.mailSender.createMimeMessage()
            val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
            message.setFrom(sendFrom)
            message.setSubject("Onlineanmeldung Kreiszeltlager in ${settings.hostCity} eröffnet")
            message.setTo(to)
            val htmlContent = this.htmlTemplateEngine.process(newUserMailTemplate, cxt)
            message.setText(htmlContent, true)
            message.addInline(headerLogoName, headerLogo);

            logger.info("RegistrationMail send to $to")
            if (sendMail) {
                this.mailSender.send(mimeMessage)
            }
        } catch (exception: SendFailedException) {
            logger.error(exception.message)
        }
    }

    fun sendReminderMail(to: String, leaderName: String, settings: SettingsEntry): Boolean {
        try {
            authorityService.isAdmin()
            val headerLogoName = "kreiszeltlager-logo.jpg"
            val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")
            val registrationDeadlineDate = LocalDate.ofInstant(settings.registrationEnd, ZoneId.of("Europe/Berlin"))
            val daysLeft =
                Duration.between(registrationDeadlineDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays()

            val cxt = Context()
            cxt.setVariable("leaderName", leaderName)
            cxt.setVariable("hostCity", settings.hostCity)
            cxt.setVariable("registrationDeadline", registrationDeadlineDate.format(germanDate))
            cxt.setVariable("daysLeft", daysLeft)
            cxt.setVariable("headerLogo", headerLogoName);

            val mimeMessage = this.mailSender.createMimeMessage()
            val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
            message.setFrom(sendFrom)
            message.setSubject("Onlineanmeldung Kreiszeltlager in ${settings.hostCity} noch $daysLeft Tage offen")
            message.setTo(to)
            val htmlContent = this.htmlTemplateEngine.process(reminderMailTemplate, cxt)
            message.setText(htmlContent, true)
            message.addInline(headerLogoName, headerLogo);

            logger.info("Reminder mail send to $to")
            if (sendMail) {
                this.mailSender.send(mimeMessage)
            }
            return true
        } catch (exception: SendFailedException) {
            logger.error(exception.message)
            return false
        }
    }

    fun sendRegistrationFinishedMail(to: String, leaderName: String, settings: SettingsEntry): Boolean {
        try {
            authorityService.isAdmin()
            val headerLogoName = "kreiszeltlager-logo.jpg"
            val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")

            val cxt = Context()
            cxt.setVariable("leaderName", leaderName)
            cxt.setVariable("hostCity", settings.hostCity)
            cxt.setVariable("headerLogo", headerLogoName);

            val mimeMessage = this.mailSender.createMimeMessage()
            val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
            message.setFrom(sendFrom)
            message.setSubject("Onlineanmeldung Kreiszeltlager in ${settings.hostCity} abgeschlossen")
            message.setTo(to)
            val htmlContent = this.htmlTemplateEngine.process(registrationFinishedTemplate, cxt)
            message.setText(htmlContent, true)
            message.addInline(headerLogoName, headerLogo);

            logger.info("Registration finished mail send to $to")
            if (sendMail) {
                this.mailSender.send(mimeMessage)
            }
            return true
        } catch (exception: SendFailedException) {
            logger.error(exception.message)
            return false
        }
    }
    
    fun sendForgotPasswordMail(to: String, changePasswordLink: String): Boolean {
      try {
          val headerLogoName = "kreiszeltlager-logo.jpg"
          val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")

          val cxt = Context()
          cxt.setVariable("headerLogo", headerLogoName);
          cxt.setVariable("changePasswordLink", changePasswordLink)
          val mimeMessage = this.mailSender.createMimeMessage()
          val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
          message.setFrom(sendFrom)
          message.setTo(to)
          val htmlContent = this.htmlTemplateEngine.process("forgot-password", cxt)
          message.setText(htmlContent, true)
          message.addInline(headerLogoName, headerLogo);
          logger.info("Forgot password mail send to $to")
          if (sendMail) {
              this.mailSender.send(mimeMessage)
          }
          return true
      } catch (exception: SendFailedException) {
          logger.error(exception.message)
          return false
      }
    }
}
