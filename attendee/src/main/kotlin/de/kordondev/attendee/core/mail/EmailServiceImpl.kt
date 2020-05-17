package de.kordondev.attendee.core.mail

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import javax.mail.SendFailedException

@Service("EmailService")
class EmailServiceImpl {

    @Autowired
    lateinit var emailSender: JavaMailSender
    val logger: Logger = LoggerFactory.getLogger(EmailServiceImpl::class.java)

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        try {
            val message = SimpleMailMessage()
            message.setTo(to)
            message.setSubject(subject)
            message.setText(text)
            emailSender.send(message)
            logger.info("Mail send")
        } catch (exception:SendFailedException) {
            logger.error(exception.message)
        }

    }
}