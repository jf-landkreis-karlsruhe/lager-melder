package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.ZKidEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ZKidsRepository : CrudRepository<ZKidEntry, String> {
    @Query("SELECT z FROM ZKidEntry z WHERE z.department.id = :departmentId AND z.role = 'Z_KID'")
    fun findByDepartment(departmentId: Long): List<ZKidEntry>


    @Query("SELECT z FROM ZKidEntry z WHERE z.partOfDepartment.id = :partOfDepartmentId AND z.role = 'Z_KID'")
    fun findByPartOfDepartment(partOfDepartmentId: Long): List<ZKidEntry>

    @Query("SELECT y FROM ZKidEntry y WHERE y.role = 'Z_KID'")
    override fun findAll(): List<ZKidEntry>
}