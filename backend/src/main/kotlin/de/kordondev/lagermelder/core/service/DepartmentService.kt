package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.repository.DepartmentFeatureRepository
import de.kordondev.lagermelder.core.persistence.repository.DepartmentRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.exception.ExistingDependencyException
import de.kordondev.lagermelder.exception.NotFoundException
import de.kordondev.lagermelder.rest.model.request.RestDepartmentTentMarkingRequest
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository,
    private val departmentFeatureRepository: DepartmentFeatureRepository,
    private val attendeeService: AttendeeService,
    private val authorityService: AuthorityService,
    private val evacuationGroupService: EvacuationGroupService,
    private val tentMarkingService: TentMarkingService
) {
    fun getDepartments(onlyWithAttendees: Boolean = false): List<DepartmentEntry> {
        val departments = departmentRepository
            .findAll()
            .filter { authorityService.hasAuthorityFilter(it, AuthorityService.LK_KARLSRUHE_ALLOWED) }

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
            ?.let {
                authorityService.hasAuthority(
                    it,
                    AuthorityService.LK_KARLSRUHE_ALLOWED
                )
            }
            ?: throw NotFoundException("Department with id $id not found")
    }

    @Transactional
    fun createDepartment(department: DepartmentEntry): DepartmentEntry {
        authorityService.isSpecializedFieldDirector()
        return departmentRepository.save(department)
    }

    @Transactional
    fun saveDepartment(department: DepartmentEntry): DepartmentEntry {
        authorityService.isSpecializedFieldDirector()
        return saveDepartmentForLKKarlsruhe(department)
    }

    private fun saveDepartmentForLKKarlsruhe(department: DepartmentEntry): DepartmentEntry {
        authorityService.isLkKarlsruhe()

        departmentFeatureRepository.deleteForDepartmentAndNotIn(department.id, department.features.map { it.id })
        return departmentRepository.save(department)
    }

    @Transactional
    fun updateContactDetails(
        department: DepartmentEntry,
        departmentPhoneNumber: String,
        nameKommandant: String,
        phoneNumberKommandant: String
    ): DepartmentEntry {
        return departmentRepository.save(
            department.copy(
                phoneNumber = departmentPhoneNumber,
                nameKommandant = nameKommandant,
                phoneNumberKommandant = phoneNumberKommandant
            )
        )
    }

    @Transactional
    fun updateTentMarkings(
        departmentId: Long,
        tentMarkings: Set<RestDepartmentTentMarkingRequest>,
        evacuationGroupId: String
    ): DepartmentEntry {
        authorityService.isLkKarlsruhe()
        val department = getDepartment(departmentId)
        val evacuationGroup = evacuationGroupService.getEvacuationGroup(evacuationGroupId)
        val updatedTentMarkings =
            tentMarkings.map { RestDepartmentTentMarkingRequest.to(it, departmentId) }.toSet()

        val updatedDepartment = saveDepartmentForLKKarlsruhe(
            department.copy(
                tentMarkings = updatedTentMarkings,
                evacuationGroup = evacuationGroup
            )
        )
        tentMarkingService.deleteTentMarkingsWithoutDepartment()
        return updatedDepartment
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