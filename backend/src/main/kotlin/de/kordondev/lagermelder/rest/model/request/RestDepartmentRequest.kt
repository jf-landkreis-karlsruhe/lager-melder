package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatureEntry
import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class RestDepartmentRequest(
    @field:NotNull(message = "name cannot be missing")
    @field:NotBlank(message = "name cannot be blank")
    val name: String,
    @field:NotNull(message = "leaderName cannot be missing")
    @field:NotBlank(message = "leaderName cannot be blank")
    val leaderName: String,
    @field:Email(message = "leaderEMail needs to be an email")
    val leaderEMail: String,
    val phoneNumber: String,
    val shortName: String,
    val features: Set<DepartmentFeatures>,
    val headDepartmentName: String,
) {
    companion object {
        fun to(department: RestDepartmentRequest) = DepartmentEntry(
            id = 0,
            name = department.name,
            leaderName = department.leaderName,
            leaderEMail = department.leaderEMail,
            phoneNumber = department.phoneNumber,
            shortName = department.shortName,
            headDepartmentName = department.headDepartmentName
        )

        fun to(department: RestDepartmentRequest, id: Long, features: Set<DepartmentFeatureEntry>) = DepartmentEntry(
            id = id,
            name = department.name,
            leaderName = department.leaderName,
            leaderEMail = department.leaderEMail,
            phoneNumber = department.phoneNumber,
            shortName = department.shortName,
            features = department.features.map {
                features.firstOrNull { feature -> feature.feature == it }
                    ?: DepartmentFeatureEntry(
                        id = UUID.randomUUID().toString(), departmentId = id, feature = it
                    )
            }.toSet(),
            headDepartmentName = department.headDepartmentName
        )
    }
}