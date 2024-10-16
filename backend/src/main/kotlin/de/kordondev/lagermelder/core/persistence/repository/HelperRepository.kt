package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.HelperEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface HelperRepository : CrudRepository<HelperEntity, String> {
    @Query("SELECT h FROM HelperEntity h WHERE h.department.id = :departmentId AND h.role = 'HELPER'")
    fun findByDepartment(departmentId: Long): List<HelperEntity>

    @Query("SELECT h FROM HelperEntity h WHERE h.role = 'HELPER'")
    override fun findAll(): List<HelperEntity>
}