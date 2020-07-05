package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.NewDepartment
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class RestDepartmentRequest(
        @field:NotBlank(message = "name cannot be blank")
        val name: String,
        @field:NotBlank(message = "leaderName cannot be blank")
        val leaderName: String,
        @field:Email(message = "leaderEMail needs to be an email")
        val leaderEMail: String
) {
    companion object {
        fun to(department: RestDepartmentRequest) = NewDepartment(
                name = department.name,
                leaderName = department.leaderName,
                leaderEMail = department.leaderEMail
        )
    }
}