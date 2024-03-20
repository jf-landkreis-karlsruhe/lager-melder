package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.model.NewDepartment
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.UserEntry
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class RestDepartmentWithUserRequest(
    @field: Size(min = 4, max = 40, message = "username needs to be between 4 and 40 chars long")
    val username: String,
    @field:NotNull(message = "departmen name cannot be missing")
    @field:NotBlank(message = "departmen name cannot be blank")
    val departmentName: String,
    @field:NotNull(message = "leaderName cannot be missing")
    @field:NotBlank(message = "leaderName cannot be blank")
    val leaderName: String,
    @field:Email(message = "leaderEMail needs to be an email")
    val leaderEMail: String
) {
    companion object {
        fun toDepartment(departmentWithUser: RestDepartmentWithUserRequest) = NewDepartment(
            name = departmentWithUser.departmentName,
            leaderName = departmentWithUser.leaderName,
            leaderEMail = departmentWithUser.leaderEMail
        )

        fun toUser(departmentWithUser: RestDepartmentWithUserRequest, department: DepartmentEntry) = UserEntry(
            userName = departmentWithUser.username,
            passWord = "",
            department = department,
            role = Roles.USER
        )

    }
}
