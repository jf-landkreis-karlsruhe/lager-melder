package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.User

data class RestUserRequest(
        val username: String,
        val password: String?,
        val departmentId: Long,
        val role: String
)