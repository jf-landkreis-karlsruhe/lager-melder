package de.kordondev.attendee.core.mail

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import javax.mail.SendFailedException

@Configuration
@Service("EmailService")
class EmailServiceImpl (
        @Value("\${application.mail.send}") private val sendMail: Boolean,
        @Value("\${spring.mail.from}") private val sendFrom: String
) {

    @Autowired
    lateinit var emailSender: JavaMailSender
    val logger: Logger = LoggerFactory.getLogger(EmailServiceImpl::class.java)

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        try {
            val message = SimpleMailMessage()
            message.setFrom(sendFrom)
            message.setTo(to)
            message.setSubject(subject)
            message.setText(text)
            if (sendMail) {
                emailSender.send(message)
                logger.info("Mail send to $to", to);
            } else {
                logger.info("Could have send message: $message", message)
            }

        } catch (exception:SendFailedException) {
            logger.error(exception.message)
        }

    }
}