package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.ChildEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ChildRepository : CrudRepository<ChildEntry, String> {
    @Query("SELECT y FROM ChildEntry y WHERE y.department.id = :departmentId and y.role = 'CHILD'")
    fun findByDepartment(departmentId: Long): List<ChildEntry>

    @Query("SELECT y FROM ChildEntry y WHERE y.role = 'CHILD'")
    override fun findAll(): List<ChildEntry>
}