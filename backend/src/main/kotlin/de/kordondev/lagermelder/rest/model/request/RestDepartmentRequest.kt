package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestDepartmentRequest(
    @field:NotNull(message = "name cannot be missing")
    @field:NotBlank(message = "name cannot be blank")
    val name: String,
    @field:NotNull(message = "leaderName cannot be missing")
    @field:NotBlank(message = "leaderName cannot be blank")
    val leaderName: String,
    @field:Email(message = "leaderEMail needs to be an email")
    val leaderEMail: String
) {
    companion object {
        fun to(department: RestDepartmentRequest) = DepartmentEntry(
            id = 0,
            name = department.name,
            leaderName = department.leaderName,
            leaderEMail = department.leaderEMail
        )

        fun to(department: RestDepartmentRequest, id: Long) = DepartmentEntry(
            id = id,
            name = department.name,
            leaderName = department.leaderName,
            leaderEMail = department.leaderEMail
        )
    }
}