package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AttendeeRepository : CrudRepository<AttendeeEntry, Long> {
    fun findByDepartment(department: DepartmentEntry): List<AttendeeEntry>

    fun findByDepartmentAndFirstNameAndLastName(
        department: DepartmentEntry,
        firstName: String,
        lastName: String
    ): AttendeeEntry?

    fun findByCode(code: String): AttendeeEntry?

    @Query(
        "Select a from AttendeeEntry a LEFT JOIN YouthPlanAttendeeRoleEntry ypa ON a.id = ypa.attendeeId where ypa.attendeeId is NULL",
    )
    fun findAttendeesWithoutYouthPlanRole(): List<AttendeeEntry>
}