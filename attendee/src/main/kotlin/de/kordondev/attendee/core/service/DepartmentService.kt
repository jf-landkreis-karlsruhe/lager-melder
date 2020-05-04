package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewDepartment
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.repository.DepartmentRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.exception.ExistingDependencyException
import de.kordondev.attendee.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class DepartmentService (
        private val departmentRepository: DepartmentRepository,
        private val attendeeService: AttendeeService,
        private val authorityService: AuthorityService
) {
    fun getDepartments() : List<Department> {
        return departmentRepository
               .findAll()
                .map { department -> DepartmentEntry.to(department) }
                .filter { authorityService.hasAuthorityFilter(it) }
    }

    fun getDepartment(id: Long) : Department {
        authorityService.isAdmin()
        return departmentRepository
                .findByIdOrNull(id)
                ?.let { department -> DepartmentEntry.to(department) }
                ?.let { authorityService.hasAuthority(it) }
            ?: throw NotFoundException("Attendee with id $id not found")
    }

    fun createDepartment(department: NewDepartment) : Department {
        authorityService.isAdmin()
        return departmentRepository
                .save(DepartmentEntry.of(department))
                .let { savedDepartment -> DepartmentEntry.to(savedDepartment) }
    }

    fun saveDepartment(id: Long, department: NewDepartment): Department {
        authorityService.isAdmin()
        return departmentRepository
                .save( DepartmentEntry.of(department, id))
                .let { savedDepartment -> DepartmentEntry.to(savedDepartment) }
    }

    fun deleteDepartment(id: Long) {
        authorityService.isAdmin()
        val department = this.getDepartment(id)
        if (attendeeService.getAttendeesForDepartment(department).toList().isNotEmpty()) {
            throw ExistingDependencyException("Attendees for department existing. Delete them first.")
        }
        departmentRepository.delete(DepartmentEntry.of(department))
    }
}