package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.YouthPlanAttendeeRoleEntry
import org.springframework.data.repository.CrudRepository

interface YouthPlanAttendeeRolesRepository: CrudRepository<YouthPlanAttendeeRoleEntry, Long> {
}