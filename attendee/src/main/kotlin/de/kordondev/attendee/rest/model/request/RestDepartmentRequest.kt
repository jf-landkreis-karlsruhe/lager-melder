package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.NewDepartment

data class RestDepartmentRequest(
        val name: String,
        val leaderName: String,
        val leaderEMail: String
) {
    companion object {
        fun to(department: RestDepartmentRequest) = NewDepartment(
                name = department.name,
                leaderName = department.leaderName,
                leaderEMail = department.leaderEMail
        )
    }
}