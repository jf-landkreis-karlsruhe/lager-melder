package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "tent_markings")
data class TentMarkingEntry(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "name")
    val name: String,

    @Column(name = "department_id")
    val departmentId: Long
)