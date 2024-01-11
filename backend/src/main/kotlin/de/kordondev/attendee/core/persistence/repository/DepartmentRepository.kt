package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import org.springframework.data.repository.CrudRepository

interface DepartmentRepository : CrudRepository<DepartmentEntry, Long> {
    fun findOneByName(name: String): DepartmentEntry?
}
