package de.kordondev.attendee.core.mail

/*
@Configuration
@ComponentScan()
class EMailConfiguration(

) {

    @Bean
    fun getJavaMailSender(): JavaMailSenderImpl {

        val mailConfig: MailConfig
        val mailSender = JavaMailSenderImpl();

        mailSender.host = "smtp.gmail."
        mailSender.port = 123

        mailSender.username = "mymail@address"
        mailSender.password = "password"

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = true
        props["mail.smtp.starttls.enable"] = false
        props["mail.debug"] = true

        return mailSender
    }
}*/