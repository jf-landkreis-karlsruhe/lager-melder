package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.ChildLeaderEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ChildLeaderRepository : CrudRepository<ChildLeaderEntry, String> {
    @Query("SELECT y FROM ChildLeaderEntry y WHERE y.department.id = :departmentId and y.role = 'CHILD_LEADER'")
    fun findByDepartment(departmentId: Long): List<ChildLeaderEntry>

    @Query("SELECT y FROM ChildLeaderEntry y WHERE y.role = 'CHILD_LEADER'")
    override fun findAll(): List<ChildLeaderEntry>
}