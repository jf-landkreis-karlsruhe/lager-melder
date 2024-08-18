package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.YouthEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface YouthsRepository : CrudRepository<YouthEntry, String> {
    @Query("SELECT y FROM YouthEntry y WHERE y.department.id = :departmentId")
    fun findByDepartment(departmentId: Long): List<YouthEntry>

    @Query("SELECT a.department.id FROM AttendeeEntry a WHERE a.role = 'YOUTH' GROUP BY a.department.id")
    fun findDistinctDepartmentIdsFromAllAttendees(): List<Long>
}