package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.TShirtSizeEntry
import de.kordondev.lagermelder.core.persistence.repository.TShirtSizeRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.service.helper.TShirtSizeValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TShirtSizeService(
    private val tShirtSizeRepository: TShirtSizeRepository,
    private val attendeeService: AttendeeService,
    private val tShirtSizeValidator: TShirtSizeValidator,
    private val authorityService: AuthorityService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun getTShirtSizes(): List<TShirtSizeEntry> {
        return tShirtSizeRepository.findAll().toList()
    }

    fun createTShirtSize(tShirtSize: TShirtSizeEntry): TShirtSizeEntry {
        authorityService.isSpecializedFieldDirector()
        return tShirtSizeRepository.save(tShirtSize)
    }

    fun deleteAndReplaceTShirtSize(size: String, newSize: String) {
        authorityService.isSpecializedFieldDirector()
        tShirtSizeValidator.validate(newSize)

        attendeeService.replaceTShirtSize(size, newSize)
        tShirtSizeRepository.deleteById(size)
    }

}