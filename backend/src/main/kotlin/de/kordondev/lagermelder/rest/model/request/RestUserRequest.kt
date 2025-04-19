package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.UserEntry
import de.kordondev.lagermelder.exception.BadRequestException
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RestUserRequest(
    @field:Email(message = "username needs to be an email")
    val username: String,
    @field:Size(min = 8, max = 20, message = "password needs to be between 8 and 20 chars long")
    val password: String?,
    @field:NotNull(message = "departmentId needs to be defined")
    val departmentId: Long,
    @field:Pattern(
        regexp = "^(USER|LK_KARLSRUHE|SPECIALIZED_FIELD_DIRECTOR|ADMIN)$",
        message = "role needs to be one of USER, LK_KARLSRUHE, ADMIN or SPECIALIZED_FIELD_DIRECTOR"
    )
    val role: String
) {
    companion object {
        fun to(user: RestUserRequest, id: Long, department: DepartmentEntry): UserEntry {
            if (user.password == null) {
                throw BadRequestException("password missing")
            }
            return UserEntry(
                id = id,
                userName = user.username,
                passWord = user.password,
                department = department,
                role = user.role
            )
        }
    }
}

