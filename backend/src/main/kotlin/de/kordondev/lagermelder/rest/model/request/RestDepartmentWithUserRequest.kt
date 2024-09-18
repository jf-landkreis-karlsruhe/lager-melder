package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

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
    val leaderEMail: String,
    val features: Set<DepartmentFeatures>
) {
    companion object {
        fun toDepartment(departmentWithUser: RestDepartmentWithUserRequest, departmentId: Long) = DepartmentEntry(
            id = departmentId,
            name = departmentWithUser.departmentName,
            leaderName = departmentWithUser.leaderName,
            leaderEMail = departmentWithUser.leaderEMail,
            phoneNumber = "",
            shortName = "",
            features = departmentWithUser.features.map { DepartmentFeatureEntry(UUID.randomUUID().toString(), departmentId, it) }.toSet()
        )

        fun toUser(departmentWithUser: RestDepartmentWithUserRequest, department: DepartmentEntry) = UserEntry(
            userName = departmentWithUser.username,
            passWord = "",
            department = department,
            role = Roles.USER
        )

    }
}
