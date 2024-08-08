package de.kordondev.lagermelder.core.service.helper

import de.kordondev.lagermelder.core.persistence.repository.TShirtSizeRepository
import de.kordondev.lagermelder.exception.BadRequestException
import org.springframework.stereotype.Service

@Service
class TShirtSizeValidator(
    private val tShirtSizeRepository: TShirtSizeRepository
) {
    fun validate(tShirtSize: String) {
        val validTShirtSizes = tShirtSizeRepository.findAll()
        if (validTShirtSizes.map { it.size }.contains(tShirtSize)) {
            return
        }
        throw BadRequestException("T-Shirt Größe $tShirtSize existiert nicht")
    }

}