package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.YouthEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface YouthRepository : CrudRepository<YouthEntry, Long> {
    @Query("SELECT y FROM YouthEntry y WHERE y.role = 'YOUTH' AND y.department.id = :departmentId")
    fun findByDepartment(departmentId: Long): List<YouthEntry>

    @Query("SELECT y FROM YouthEntry y WHERE y.role = 'YOUTH' AND y.department.id = :departmentId AND y.firstName = :firstName AND y.lastName = :lastName")
    fun findByDepartmentAndFirstNameAndLastName(
        departmentId: Long,
        firstName: String,
        lastName: String
    ): YouthEntry?

    @Query("SELECT y FROM YouthEntry y WHERE y.role = 'YOUTH' AND y.code = :code")
    fun findByCode(code: String): YouthEntry?

    @Query("SELECT a.department.id FROM AttendeeEntry a WHERE a.role = 'YOUTH' GROUP BY a.department.id")
    fun findDistinctDepartmentIdsFromAllAttendees(): List<Long>

    @Query("SELECT y FROM YouthEntry y WHERE y.role = 'YOUTH' AND y.tShirtSize = :tShirtSize")
    fun findAllBytShirtSize(tShirtSize: String): List<YouthEntry>
}