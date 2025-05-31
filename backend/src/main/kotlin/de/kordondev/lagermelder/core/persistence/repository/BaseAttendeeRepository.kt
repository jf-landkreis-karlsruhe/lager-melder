package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.BaseAttendeeEntry
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface BaseAttendeeRepository : CrudRepository<BaseAttendeeEntry, String> {
    fun findByDepartmentAndFirstNameAndLastName(
        department: DepartmentEntry,
        firstName: String,
        lastName: String
    ): BaseAttendeeEntry?

    fun findByCode(code: String): BaseAttendeeEntry?

    @Query("SELECT a.department.id FROM BaseAttendeeEntry a GROUP BY a.department.id")
    fun findDistinctDepartmentIdsFromAllAttendees(): List<Long>

    fun findAllBytShirtSize(tShirtSize: String): List<BaseAttendeeEntry>

    @Query("SELECT a FROM BaseAttendeeEntry a WHERE a.role IN :roles")
    fun findByRoleIn(roles: List<String>): List<BaseAttendeeEntry>

    @Query(
        """
            SELECT
             CASE
                    WHEN b.role = 'Z_KID'
                        THEN 'YOUTH'
                     WHEN b.department.headDepartmentName = 'LK Karlsruhe'
                        THEN 'HELPER'
                    ELSE b.role
                END AS executedRole,
                COUNT(*) AS count
            FROM BaseAttendeeEntry b
            WHERE b.status = 'ENTERED'
            AND b.department.paused = false
            GROUP BY executedRole
    """
    )
    fun countByExecutedRole(): List<RoleCount>

    interface RoleCount {
        fun getExecutedRole(): String
        fun getCount(): Long
    }
}