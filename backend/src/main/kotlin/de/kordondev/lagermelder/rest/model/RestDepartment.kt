package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.DepartmentFeatures

data class RestDepartment(
    val id: Long,
    val name: String,
    val leaderName: String,
    val leaderEMail: String,
    val phoneNumber: String,
    val shortName: String,
    val features: Set<DepartmentFeatures> = emptySet(),
    val headDepartmentName: String,
    val paused: Boolean
) {
    companion object {
        fun of(department: DepartmentEntry) = RestDepartment(
            id = department.id,
            name = department.name,
            leaderName = department.leaderName,
            leaderEMail = department.leaderEMail,
            phoneNumber = department.phoneNumber,
            shortName = department.shortName,
            features = department.features.map { it.feature }.toSet(),
            headDepartmentName = department.headDepartmentName,
            paused = department.paused
        )
    }
}