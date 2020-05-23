package de.kordondev.attendee.core.mail

import de.kordondev.attendee.core.security.AuthorityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*
import javax.mail.SendFailedException

@Service("MailService")
class MailService (
        @Value("\${application.mail.send}") private val sendMail: Boolean,
        @Value("\${spring.mail.from}") private val sendFrom: String,
        private val authorityService: AuthorityService
) {

    @Autowired
    private lateinit var mailSender: JavaMailSender
    @Autowired
    private lateinit var htmlTemplateEngine: TemplateEngine
    private val logger: Logger = LoggerFactory.getLogger(MailService::class.java)
    private val newUserMailTemplate = "new-user"

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        try {
            authorityService.isAdmin()
            val message = SimpleMailMessage()
            message.setFrom(sendFrom)
            message.setTo(to)
            message.setSubject(subject)
            message.setText(text)
            if (sendMail) {
                mailSender.send(message)
                logger.info("Mail send to $to", to);
            } else {
                logger.info("Could have send message: $message", message)
            }

        } catch (exception:SendFailedException) {
            logger.error(exception.message)
        }
    }

    fun sendMailWithInlineImage(to: String) {
        authorityService.isAdmin()
        val cxt = Context()
        cxt.setVariable("name", "Ann")
        cxt.setVariable("subscriptionDate", Date())
        cxt.setVariable("hobbies", listOf("Cinema", "Sports", "Music"))

        val mimeMessage = this.mailSender.createMimeMessage()
        val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
        message.setFrom(sendFrom)
        message.setSubject("New userWith image")
        message.setTo(to)
        val htmlContent = this.htmlTemplateEngine.process(newUserMailTemplate, cxt)
        message.setText(htmlContent, true)

        if (sendMail) {
            this.mailSender.send(mimeMessage)
        } else {
            logger.info("New user mail $mimeMessage", mimeMessage)
        }
    }
}