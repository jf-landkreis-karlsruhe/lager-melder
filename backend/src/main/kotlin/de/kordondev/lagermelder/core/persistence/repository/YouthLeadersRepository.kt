package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface YouthLeadersRepository : CrudRepository<YouthLeaderEntry, String> {
    @Query("SELECT y FROM YouthLeaderEntry y WHERE y.department.id = :departmentId AND y.role = 'YOUTH_LEADER'")
    fun findByDepartment(departmentId: Long): List<YouthLeaderEntry>

    @Query("SELECT a.department.id FROM AttendeeEntry a WHERE a.role = 'YOUTH' GROUP BY a.department.id")
    fun findDistinctDepartmentIdsFromAllAttendees(): List<Long>

    @Query("SELECT y FROM YouthLeaderEntry y WHERE y.role = 'YOUTH_LEADER'")
    override fun findAll(): List<YouthLeaderEntry>
}