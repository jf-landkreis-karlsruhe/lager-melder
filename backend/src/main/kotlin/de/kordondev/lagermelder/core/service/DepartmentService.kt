package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.repository.DepartmentFeatureRepository
import de.kordondev.lagermelder.core.persistence.repository.DepartmentRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.exception.ExistingDependencyException
import de.kordondev.lagermelder.exception.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository,
    private val departmentFeatureRepository: DepartmentFeatureRepository,
    private val attendeeService: AttendeeService,
    private val authorityService: AuthorityService
) {
    fun getDepartments(onlyWithAttendees: Boolean = false): List<DepartmentEntry> {
        val departments = departmentRepository
            .findAll()
            .filter { authorityService.hasAuthorityFilter(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }

        if (onlyWithAttendees) {
            val departmentsWithAttendees = attendeeService.getDepartmentIdsForAllAttendees()
            return departments
                .filter { departmentsWithAttendees.contains(it.id) }
        }

        return departments
    }

    fun getDepartmentsForSelecting(): List<DepartmentEntry> {
        return departmentRepository.findAll().toList()
    }

    fun getDepartment(id: Long): DepartmentEntry {
        return departmentRepository
            .findByIdOrNull(id)
            ?.let { authorityService.hasAuthority(it, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
            ?: throw NotFoundException("Attendee with id $id not found")
    }

    @Transactional
    fun createDepartment(department: DepartmentEntry): DepartmentEntry {
        authorityService.isSpecializedFieldDirector()
        return departmentRepository.save(department)
    }

    @Transactional
    fun saveDepartment(department: DepartmentEntry): DepartmentEntry {
        authorityService.isSpecializedFieldDirector()

        departmentFeatureRepository.deleteForDepartmentAndNotIn(department.id, department.features.map { it.id })
        return departmentRepository.save(department)
    }

    fun deleteDepartment(id: Long) {
        authorityService.isSpecializedFieldDirector()
        val department = this.getDepartment(id)
        val attendeesInDepartment = attendeeService.getAttendeesForDepartment(department)
        if (attendeesInDepartment.youths.isNotEmpty() || attendeesInDepartment.youthLeaders.isNotEmpty()) {
            throw ExistingDependencyException("Attendees for department existing. Delete them first.")
        }
        departmentRepository.delete(department)
    }
}