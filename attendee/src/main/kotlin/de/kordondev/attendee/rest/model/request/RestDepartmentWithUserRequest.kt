package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewDepartment
import de.kordondev.attendee.core.model.NewUser
import de.kordondev.attendee.core.persistence.entry.Roles
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class RestDepartmentWithUserRequest(
    @field: Size(min = 4, max = 20, message = "username needs to be between 4 and 20 chars long")
    val username: String,
    @field:Size(min = 8, max = 20, message = "password needs to be between 8 and 20 chars long")
    val password: String?,
    @field:NotBlank(message = "departmen name cannot be blank")
    val departmentName: String,
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
        fun toUser(departmentWithUser: RestDepartmentWithUserRequest, department: Department, defaultPassword: String) = NewUser (
            userName = departmentWithUser.username,
            passWord = departmentWithUser.password ?: defaultPassword,
            department = department,
            role = Roles.USER
        )
    }
}
