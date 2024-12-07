package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.TentMarkingEntry
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface TentMarkingsRepository : CrudRepository<TentMarkingEntry, Long> {

    @Modifying
    @Query("DELETE TentMarkingEntry t WHERE t.departmentId is NULL")
    fun deleteWithoutDepartmentId()
}