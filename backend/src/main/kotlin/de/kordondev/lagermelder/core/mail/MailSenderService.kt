package de.kordondev.lagermelder.core.mail

import de.kordondev.lagermelder.core.pdf.PDFHelper.Companion.germanDate
import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import de.kordondev.lagermelder.core.security.AuthorityService
import jakarta.mail.internet.MimeMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId


@Service("MailSenderService")
class MailSenderService(
    @Value("\${application.mail.send}") private val shouldSendMail: Boolean,
    @Value("\${spring.mail.from}") private val sendFrom: String,
    private val authorityService: AuthorityService,
    private val mailSender: JavaMailSender,
    private val htmlTemplateEngine: TemplateEngine
) {
    private val logger: Logger = LoggerFactory.getLogger(MailSenderService::class.java)
    private val newUserMailTemplate = "new-user"
    private val reminderMailTemplate = "reminder"
    private val registrationFinishedTemplate = "registration-finished"
    private val headerLogo = "kreiszeltlager-logo.jpg"

    fun sendRegistrationMail(
        to: String,
        leaderName: String,
        username: String,
        password: String,
        settings: SettingsEntry
    ) {
        try {
            authorityService.isSpecializedFieldDirector()
            val headerLogoName = headerLogo
            val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")

            val cxt = Context()
            val registrationDeadlineDate = LocalDate.ofInstant(settings.registrationEnd, ZoneId.of("Europe/Berlin"))
            cxt.setVariable("leaderName", leaderName)
            cxt.setVariable("hostCity", settings.hostCity)
            cxt.setVariable("registrationDeadline", registrationDeadlineDate.format(germanDate))
            cxt.setVariable("username", username)
            cxt.setVariable("password", password)
            cxt.setVariable("headerLogo", headerLogoName)
            val htmlContent = this.htmlTemplateEngine.process(newUserMailTemplate, cxt)

            val mimeMessage = this.mailSender.createMimeMessage()
            val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
            message.setFrom(sendFrom)
            message.setSubject("Onlineanmeldung Kreiszeltlager in ${settings.hostCity} eröffnet")
            message.setTo(to)
            message.setText(htmlContent, true)
            message.addInline(headerLogoName, headerLogo)

            logger.info("RegistrationMail send to $to")
            sendMail(mimeMessage, htmlContent)
        } catch (exception: MailException) {
            logger.error(exception.message)
        }
    }

    fun sendReminderMail(to: String, leaderName: String, settings: SettingsEntry): Boolean {
        try {
            authorityService.isAdmin()
            val headerLogoName = headerLogo
            val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")
            val registrationDeadlineDate = LocalDate.ofInstant(settings.registrationEnd, ZoneId.of("Europe/Berlin"))
            val daysLeft = daysUntilEnd(LocalDate.now(), registrationDeadlineDate)

            val cxt = Context()
            cxt.setVariable("leaderName", leaderName)
            cxt.setVariable("hostCity", settings.hostCity)
            cxt.setVariable("registrationDeadline", registrationDeadlineDate.format(germanDate))
            cxt.setVariable("daysLeft", daysLeft)
            cxt.setVariable("headerLogo", headerLogoName)

            val mimeMessage = this.mailSender.createMimeMessage()
            val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
            message.setFrom(sendFrom)
            message.setSubject("Onlineanmeldung Kreiszeltlager in ${settings.hostCity} noch $daysLeft Tage offen")
            message.setTo(to)
            val htmlContent = this.htmlTemplateEngine.process(reminderMailTemplate, cxt)
            message.setText(htmlContent, true)
            message.addInline(headerLogoName, headerLogo)

            logger.info("Reminder mail send to $to")
            sendMail(mimeMessage, htmlContent)
            return true
        } catch (exception: MailException) {
            logger.error(exception.message)
            return false
        }
    }

    fun sendRegistrationFinishedMail(to: String, leaderName: String, settings: SettingsEntry): Boolean {
        try {
            authorityService.isAdmin()
            val headerLogoName = headerLogo
            val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")
            val startDownloadRegistrationFiles =
                LocalDate.ofInstant(settings.startDownloadRegistrationFiles, ZoneId.of("Europe/Berlin"))

            val cxt = Context()
            cxt.setVariable("leaderName", leaderName)
            cxt.setVariable("hostCity", settings.hostCity)
            cxt.setVariable("headerLogo", headerLogoName)
            cxt.setVariable("startDownloadRegistrationFiles", startDownloadRegistrationFiles.format(germanDate))

            val mimeMessage = this.mailSender.createMimeMessage()
            val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
            message.setFrom(sendFrom)
            message.setSubject("Onlineanmeldung Kreiszeltlager in ${settings.hostCity} abgeschlossen")
            message.setTo(to)
            val htmlContent = this.htmlTemplateEngine.process(registrationFinishedTemplate, cxt)
            message.setText(htmlContent, true)
            message.addInline(headerLogoName, headerLogo)

            logger.info("Registration finished mail send to $to")
            sendMail(mimeMessage, htmlContent)
            return true
        } catch (exception: MailException) {
            logger.error(exception.message)
            return false
        }
    }
    
    fun sendForgotPasswordMail(to: String, changePasswordLink: String): Boolean {
      try {
          val headerLogoName = headerLogo
          val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")

          val cxt = Context()
          cxt.setVariable("headerLogo", headerLogoName)
          cxt.setVariable("changePasswordLink", changePasswordLink)
          val mimeMessage = this.mailSender.createMimeMessage()
          val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
          message.setFrom(sendFrom)
          message.setTo(to)
          val htmlContent = this.htmlTemplateEngine.process("forgot-password", cxt)
          message.setText(htmlContent, true)
          message.addInline(headerLogoName, headerLogo)
          logger.info("Forgot password mail send to $to")
          sendMail(mimeMessage, htmlContent)
          return true
      } catch (exception: MailException) {
          logger.error(exception.message)
          return false
      }
    }

    private fun sendMail(mimeMessage: MimeMessage, content: String) {
        if (shouldSendMail) {
            this.mailSender.send(mimeMessage)
        } else {
            logger.info("Mail send ${mimeMessageToString(mimeMessage, content)}")
        }
    }

    private fun mimeMessageToString(mimeMessage: MimeMessage, content: String): String {
        return """
            From: ${mimeMessage.from.map { it.toString() }}
            To: ${mimeMessage.allRecipients.map { it.toString() }}
            Subject: ${mimeMessage.subject}
            Message: $content
        """.trimIndent()
    }

    private fun daysUntilEnd(today: LocalDate, end: LocalDate): Long {
        if (today.isAfter(end)) {
            return 0
        }
        return Duration.between(today.atStartOfDay(), end.atStartOfDay()).toDays()
    }

}
