package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.*

@Entity
@Table(name = "department_features")
data class DepartmentFeatureEntry(

    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "department_id")
    val departmentId: Long,

    @Id
    @Column(name = "feature")
    @Enumerated(EnumType.STRING)
    val feature: DepartmentFeatures,
)
