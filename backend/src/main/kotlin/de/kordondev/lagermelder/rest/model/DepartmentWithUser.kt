package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.model.Department
import de.kordondev.lagermelder.core.model.User

data class DepartmentWithUser (
    val departmentId: Long,
    val departmentName: String,
    val leaderName: String,
    val leaderEMail: String,
    val userId: Long,
    val username: String,
    val role: String
) {
    companion object {
        fun of(user: User, department: Department) = DepartmentWithUser (
            departmentId = department.id,
            departmentName = department.name,
            leaderName = department.leaderName,
            leaderEMail = department.leaderEMail,
            userId = user.id,
            username = user.userName,
            role = user.role.toString()
        )
    }
}