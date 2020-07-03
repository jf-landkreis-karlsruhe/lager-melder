package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.MailService
import de.kordondev.attendee.rest.model.RestSendMail
import de.kordondev.attendee.rest.model.request.RestSendMailRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
class MailController(
        private val mailService: MailService
) {

    @PostMapping("mail/reminder")
    fun sendReminderMail(@RequestBody(required = true) @Valid sendMailRequest: RestSendMailRequest): RestSendMail {
        return RestSendMail(
                mailService.sendReminderMail(sendTo = sendMailRequest.sendTo)
        )
    }

    @PostMapping("mail/registration-finished")
    fun sendRegistrationFinishedMail(@RequestBody(required = true) @Valid sendMailRequest: RestSendMailRequest): RestSendMail {
        return RestSendMail(
                mailService.sendRegistrationFinishedMail(sendTo = sendMailRequest.sendTo)
        )
    }
}

