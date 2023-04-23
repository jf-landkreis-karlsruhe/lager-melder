package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.YouthPlanAttendeeRoleEntry
import org.springframework.data.repository.CrudRepository

interface YouthPlanAttendeeRolesRepository: CrudRepository<YouthPlanAttendeeRoleEntry, Long> {
}