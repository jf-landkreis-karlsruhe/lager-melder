package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.TentsEntity
import de.kordondev.attendee.core.persistence.repository.TentsRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.exception.WrongTimeException
import org.springframework.stereotype.Service

@Service
class TentsService(
    private val tentsRepository: TentsRepository,
    private val authorityService: AuthorityService,
    private val settingsService: SettingsService
) {

    fun getAllTents(): List<TentsEntity> {
        return tentsRepository.findAll()
            .filter { authorityService.hasAuthorityFilter(it.department, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    fun getForDepartment(department: Department): TentsEntity {
        return tentsRepository.findByDepartment(DepartmentEntry.of(department))
            ?: TentsEntity(0, DepartmentEntry.of(department),0,0,0,0,0)
    }

    fun saveForDepartment(tents: TentsEntity): TentsEntity {
        authorityService.hasAuthority(tents, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        checkCanTentsBeEdited()
        return tentsRepository.save(tents)
    }

    private fun checkCanTentsBeEdited() {
        if (!settingsService.canAttendeesBeEdited()) {
            throw WrongTimeException("Registrierungsende wurde überschritten")
        }
    }
}