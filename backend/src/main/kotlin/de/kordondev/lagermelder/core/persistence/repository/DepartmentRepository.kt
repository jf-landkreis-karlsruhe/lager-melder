package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import org.springframework.data.repository.CrudRepository

interface DepartmentRepository : CrudRepository<DepartmentEntry, Long> {
    fun findOneByName(name: String): DepartmentEntry?
}
