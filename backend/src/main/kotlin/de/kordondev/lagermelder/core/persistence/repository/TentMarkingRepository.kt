package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.TentMarkingEntry
import org.springframework.data.repository.CrudRepository

interface TentMarkingRepository : CrudRepository<TentMarkingEntry, String> {

    fun deleteByDepartmentId(departmentId: Long)

}