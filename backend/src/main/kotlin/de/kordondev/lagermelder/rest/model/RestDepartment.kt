package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry

data class RestDepartment(
        val id: Long,
        val name: String,
        val leaderName: String,
        val leaderEMail: String
) {
    companion object {
        fun of(department: DepartmentEntry) = RestDepartment(
                id = department.id,
                name = department.name,
                leaderName = department.leaderName,
                leaderEMail = department.leaderEMail
        )
    }
}