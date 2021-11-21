package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import org.springframework.data.jpa.repository.JpaRepository

interface AttendeeRepository : JpaRepository<AttendeeEntry, Long> {
    fun findByDepartment(department: DepartmentEntry): List<AttendeeEntry>
}