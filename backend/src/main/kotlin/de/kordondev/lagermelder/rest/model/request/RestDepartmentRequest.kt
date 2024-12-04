package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.*
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
    val paused: Boolean,
) {
    companion object {
        fun to(
            department: RestDepartmentRequest,
            id: Long,
            features: Set<DepartmentFeatureEntry>,
            evacuationGroup: EvacuationGroupEntry?,
            tentMarkings: Set<TentMarkingEntry>
        ) = DepartmentEntry(
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
            headDepartmentName = department.headDepartmentName,
            paused = department.paused,
            evacuationGroup = evacuationGroup,
            tentMarkings = tentMarkings
        )
    }
}