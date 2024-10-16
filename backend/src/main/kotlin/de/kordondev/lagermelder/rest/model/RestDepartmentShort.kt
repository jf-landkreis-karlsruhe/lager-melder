package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry

data class RestDepartmentShort(
        val id: Long,
        val name: String,
) {
    companion object {
        fun of(department: DepartmentEntry) = RestDepartmentShort(
            id = department.id,
            name = department.name,
        )
    }
}