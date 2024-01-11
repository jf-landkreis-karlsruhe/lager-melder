package de.kordondev.attendee.rest.model

import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.User

data class RestDepartmentWithUser(
    val userId: Long,
    val username: String,
    val role: String,
    val departmentId: Long,
    val departmentName: String,
    val leaderName: String,
    val leaderEMail: String
) {
    companion object {
        fun from(department: Department, user: User) = RestDepartmentWithUser(
            userId = user.id,
            username = user.userName,
            role = user.role.toString(),
            departmentId = department.id,
            departmentName = department.name,
            leaderName = department.leaderName,
            leaderEMail = department.leaderEMail
        )
    }
}