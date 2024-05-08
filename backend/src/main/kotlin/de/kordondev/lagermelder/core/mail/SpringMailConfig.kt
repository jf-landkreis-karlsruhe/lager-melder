package de.kordondev.lagermelder.core.mail

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
class SpringMailConfig: ApplicationContextAware, EnvironmentAware {
    private lateinit var applicationContext: ApplicationContext
    private lateinit var environment: Environment

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    @Primary
    @Bean
    fun mailTemplateEngine(): TemplateEngine {
        val templateEngine = SpringTemplateEngine()
        // templateEngine.addDialect(Java8TimeDialect()) FixMe: is that still needed?
        templateEngine.addTemplateResolver(htmlTemplateResolver())
        return templateEngine
    }

    private fun htmlTemplateResolver(): ITemplateResolver {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.order = 2
        templateResolver.prefix = "mail/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.characterEncoding = "UTF-8"
        templateResolver.isCacheable = false
        return templateResolver
    }
}