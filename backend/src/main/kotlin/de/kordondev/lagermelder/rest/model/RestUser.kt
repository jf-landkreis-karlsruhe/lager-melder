package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.UserEntry

data class RestUser(
    val id: Long,
    val username: String,
    val departmentId: Long,
    val role: String
) {
    companion object {
        fun of(user: UserEntry) = RestUser(
            id = user.id,
            username = user.userName,
            departmentId = user.department.id,
            role = user.role
        )
    }
}