package de.kordondev.attendee.core.model

import de.kordondev.attendee.core.persistence.entry.DepartmentEntry

data class Department(
        val id: Long,
        val name: String,
        val leaderName: String,
        val leaderEMail: String
) {
    companion object {
        fun of(departmentEntry: DepartmentEntry): Department {
            return Department(
                id = departmentEntry.id,
                name = departmentEntry.name,
                leaderName = departmentEntry.leaderName,
                leaderEMail = departmentEntry.leaderEMail
            )
        }
    }
}