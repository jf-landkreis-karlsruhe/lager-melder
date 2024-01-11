package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.model.Department
import de.kordondev.lagermelder.core.model.NewDepartment
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.repository.DepartmentRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.exception.ExistingDependencyException
import de.kordondev.lagermelder.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository,
    private val attendeeService: AttendeeService,
    private val authorityService: AuthorityService
) {
    fun getDepartments(): List<Department> {
        return departmentRepository
            .findAll()
            .map { DepartmentEntry.to(it) }
            .filter { authorityService.hasAuthorityFilter(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    fun getDepartment(id: Long): Department {
        return departmentRepository
            .findByIdOrNull(id)
            ?.let { DepartmentEntry.to(it) }
            ?.let { authorityService.hasAuthority(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
            ?: throw NotFoundException("Attendee with id $id not found")
    }

    fun createDepartment(department: NewDepartment): Department {
        authorityService.isSpecializedFieldDirector()
        return departmentRepository
            .save(DepartmentEntry.of(department))
            .let { DepartmentEntry.to(it) }
    }

    fun saveDepartment(id: Long, department: NewDepartment): Department {
        authorityService.isSpecializedFieldDirector()
        return departmentRepository
            .save(DepartmentEntry.of(department, id))
            .let { DepartmentEntry.to(it) }
    }

    fun deleteDepartment(id: Long) {
        authorityService.isSpecializedFieldDirector()
        val department = this.getDepartment(id)
        if (attendeeService.getAttendeesForDepartment(department).isNotEmpty()) {
            throw ExistingDependencyException("Attendees for department existing. Delete them first.")
        }
        departmentRepository.delete(DepartmentEntry.of(department))
    }
}