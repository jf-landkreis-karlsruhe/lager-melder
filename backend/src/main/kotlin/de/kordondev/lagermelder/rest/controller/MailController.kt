package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.MailService
import de.kordondev.lagermelder.rest.model.RestSendMail
import de.kordondev.lagermelder.rest.model.request.RestSendMailRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


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

