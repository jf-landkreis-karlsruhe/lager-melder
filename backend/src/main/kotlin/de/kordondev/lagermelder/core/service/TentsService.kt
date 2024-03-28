package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.TentsEntity
import de.kordondev.lagermelder.core.persistence.repository.TentsRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.exception.WrongTimeException
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

    fun getForDepartment(department: DepartmentEntry): TentsEntity {
        return tentsRepository.findByDepartment(department)
            ?: TentsEntity(0, department, 0, 0, 0, 0, 0)
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