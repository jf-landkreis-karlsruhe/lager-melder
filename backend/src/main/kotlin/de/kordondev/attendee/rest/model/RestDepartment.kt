package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.Department

data class RestDepartment(
        val id: Long,
        val name: String,
        val leaderName: String,
        val leaderEMail: String
) {
    companion object {
        fun of(department: Department) = RestDepartment(
                id = department.id,
                name = department.name,
                leaderName = department.leaderName,
                leaderEMail = department.leaderEMail
        )
    }
}