package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.TentMarkingEntry
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class RestDepartmentTentMarkingRequest(
    val id: String?,
    @field:NotNull(message = "name cannot be missing")
    @field:NotBlank(message = "name cannot be blank")
    val name: String,
) {
    companion object {
        fun to(tentMarking: RestDepartmentTentMarkingRequest, departmentId: Long) = TentMarkingEntry(
            id = tentMarking.id ?: UUID.randomUUID().toString(),
            name = tentMarking.name,
            departmentId = departmentId,
        )
    }
}