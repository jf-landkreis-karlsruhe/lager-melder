package de.kordondev.attendee.core.mail

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
class SpringMailConfig: ApplicationContextAware, EnvironmentAware {
    private lateinit var applicationContext: ApplicationContext;
    private lateinit var environment: Environment;

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext;
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment;
    }

    @Primary
    @Bean
    fun mailTemplateEngine(): TemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.addTemplateResolver(textTemplateResolver())
        templateEngine.addTemplateResolver(htmlTemplateResolver())
        return templateEngine
    }

    private fun textTemplateResolver(): ITemplateResolver {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.order = 1
        templateResolver.resolvablePatterns = setOf("text/*")
        templateResolver.prefix = "/mail/"
        templateResolver.suffix = ".txt"
        templateResolver.templateMode = TemplateMode.TEXT
        templateResolver.characterEncoding = "UTF-8"
        templateResolver.isCacheable = false
        return templateResolver
    }

    private fun htmlTemplateResolver(): ITemplateResolver {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.order = 2
        templateResolver.resolvablePatterns = setOf("html/*")
        templateResolver.prefix = "/mail/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.characterEncoding = "UTF-8"
        templateResolver.isCacheable = false
        return templateResolver
    }
}