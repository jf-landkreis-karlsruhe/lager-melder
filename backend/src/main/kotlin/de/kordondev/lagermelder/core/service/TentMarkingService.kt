package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.repository.TentMarkingRepository
import org.springframework.stereotype.Service

@Service
class TentMarkingService(
    private val tentMarkingRepository: TentMarkingRepository
) {

    fun deleteTentMarkingsForDepartment(departmentId: Long) {
        tentMarkingRepository.deleteByDepartmentId(departmentId)
    }
}