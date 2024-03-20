package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.model.Department
import de.kordondev.lagermelder.core.persistence.entry.UserEntry

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
        fun from(department: Department, user: UserEntry) = RestDepartmentWithUser(
            userId = user.id,
            username = user.userName,
            role = user.role,
            departmentId = department.id,
            departmentName = department.name,
            leaderName = department.leaderName,
            leaderEMail = department.leaderEMail
        )
    }
}