package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.repository.TentMarkingsRepository
import org.springframework.stereotype.Service

@Service
class TentMarkingService(
    private val tentMarkingsRepository: TentMarkingsRepository
) {

    fun deleteTentMarkingsWithoutDepartment() {
        tentMarkingsRepository.deleteWithoutDepartmentId()
    }
}