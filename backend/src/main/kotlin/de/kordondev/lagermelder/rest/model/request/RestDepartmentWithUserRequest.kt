package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.UserEntry
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RestDepartmentWithUserRequest(
    @field:Email(message = "username needs to be an email")
    val username: String,
    @field:NotNull(message = "department name cannot be missing")
    @field:NotBlank(message = "department name cannot be blank")
    val departmentName: String,
    @field:NotNull(message = "leaderName cannot be missing")
    @field:NotBlank(message = "leaderName cannot be blank")
    val leaderName: String,
    @field:Email(message = "leaderEMail needs to be an email")
    val leaderEMail: String
) {
    companion object {
        fun toDepartment(departmentWithUser: RestDepartmentWithUserRequest) = DepartmentEntry(
            id = 0,
            name = departmentWithUser.departmentName,
            leaderName = departmentWithUser.leaderName,
            leaderEMail = departmentWithUser.leaderEMail,
            phoneNumber = "",
            shortName = ""
        )

        fun toUser(departmentWithUser: RestDepartmentWithUserRequest, department: DepartmentEntry) = UserEntry(
            userName = departmentWithUser.username,
            passWord = "",
            department = department,
            role = Roles.USER
        )

    }
}
