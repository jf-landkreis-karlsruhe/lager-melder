package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface AttendeeRepository : CrudRepository<AttendeeEntry, Long> {
    fun findByDepartment(department: DepartmentEntry): List<AttendeeEntry>

    fun findByDepartmentAndFirstNameAndLastName(
        department: DepartmentEntry,
        firstName: String,
        lastName: String
    ): AttendeeEntry?

    fun findByCode(code: String): AttendeeEntry?

    // TODO: not sure this works
    @Modifying
    @Query("update AttendeeEntry a set a.youthPlanRole = :youthPlanRole where a.id in :ids")
    fun updateYouthPlanRolesIn(@Param("youthPlanRole") youthPlanRole: String, @Param("ids") ids: List<Long>)
}