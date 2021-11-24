package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import org.springframework.data.repository.CrudRepository

interface AttendeeRepository : CrudRepository<AttendeeEntry, Long> {
    fun findByDepartment(department: DepartmentEntry): List<AttendeeEntry>
}