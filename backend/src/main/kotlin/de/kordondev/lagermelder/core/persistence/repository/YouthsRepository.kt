package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.YouthEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface YouthsRepository : CrudRepository<YouthEntry, String> {
    @Query("SELECT y FROM YouthEntry y WHERE y.department.id = :departmentId and y.role = 'YOUTH'")
    fun findByDepartment(departmentId: Long): List<YouthEntry>

    @Query("SELECT y FROM YouthEntry y WHERE y.role = 'YOUTH'")
    override fun findAll(): List<YouthEntry>

    @Query(
        "Select a from YouthEntry a LEFT JOIN YouthPlanAttendeeRoleEntry ypa ON a.id = ypa.attendeeId where ypa.attendeeId is NULL AND a.role = 'YOUTH'",
    )
    fun findAttendeesWithoutYouthPlanRole(): List<YouthEntry>

    @Query("SELECT y FROM YouthEntry y WHERE y.id IN (:ids) AND y.role = 'YOUTH'")
    fun findAllByIds(ids: List<String>): List<YouthEntry>
}