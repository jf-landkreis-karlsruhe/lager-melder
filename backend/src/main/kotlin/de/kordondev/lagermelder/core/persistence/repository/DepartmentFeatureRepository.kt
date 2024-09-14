package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatureEntry
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface DepartmentFeatureRepository : CrudRepository<DepartmentFeatureEntry, String> {

    @Modifying
    @Query("DELETE FROM DepartmentFeatureEntry d WHERE d.departmentId = :departmentId AND d.id NOT IN :departmentFeatureIds")
    fun deleteForDepartmentAndNotIn(departmentId: Long, departmentFeatureIds: List<String>)
}
