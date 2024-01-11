package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.TentsEntity
import org.springframework.data.repository.CrudRepository

interface TentsRepository : CrudRepository<TentsEntity, Long> {
    fun findByDepartment(department: DepartmentEntry): TentsEntity?
}