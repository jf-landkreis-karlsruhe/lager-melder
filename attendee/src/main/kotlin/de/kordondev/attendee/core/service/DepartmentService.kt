package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.repository.DepartmentRepository
import de.kordondev.attendee.exception.NotFoundException
import de.kordondev.attendee.rest.model.RestDepartment
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class DepartmentService (
        private val departmentRepository: DepartmentRepository
) {
    fun getDepartments() : List<Department> {
        return departmentRepository
               .findAll()
                .map { department -> DepartmentEntry.to(department) }
    }

    fun getDepartment(id: Long) : Department {
        return departmentRepository
                .findByIdOrNull(id)
                ?.let { department -> DepartmentEntry.to(department) }
            ?: throw NotFoundException("Attendee with id $id not found")
    }

}