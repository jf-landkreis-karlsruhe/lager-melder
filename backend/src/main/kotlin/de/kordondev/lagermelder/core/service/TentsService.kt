package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
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
            .filter { authorityService.hasAuthorityFilter(it.department, AuthorityService.LK_KARLSRUHE_ALLOWED) }
    }

    fun getForDepartment(department: DepartmentEntry): TentsEntity {
        return tentsRepository.findByDepartment(department)
            ?.let {
                authorityService.hasAuthority(it, AuthorityService.LK_KARLSRUHE_ALLOWED)
            }
            ?: TentsEntity(0, department, 0, 0, 0, 0, 0)
    }

    fun saveForDepartment(tents: TentsEntity): TentsEntity {
        authorityService.hasAuthority(tents, AuthorityService.LK_KARLSRUHE_ALLOWED)
        checkCanTentsBeEdited()
        return tentsRepository.save(tents)
    }

    fun checkCanTentsBeEdited() {
        if (!settingsService.attendeesCanBeEdited()) {
            throw WrongTimeException("Registrierungsende wurde überschritten")
        }
    }
}