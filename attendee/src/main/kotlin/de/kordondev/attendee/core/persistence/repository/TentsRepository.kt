package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.entry.TentsEntity
import org.springframework.data.repository.CrudRepository

interface TentsRepository : CrudRepository<TentsEntity, Long> {
    fun findByDepartment(department: DepartmentEntry): TentsEntity?
}