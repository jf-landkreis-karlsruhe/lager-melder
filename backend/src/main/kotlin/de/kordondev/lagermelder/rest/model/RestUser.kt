package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.model.User

data class RestUser(
    val id: Long,
    val username: String,
    val departmentId: Long,
    val role: String
) {
    companion object {
        fun of(user: User) = RestUser(
            id = user.id,
            username = user.userName,
            departmentId = user.department.id,
            role = user.role
        )
    }
}