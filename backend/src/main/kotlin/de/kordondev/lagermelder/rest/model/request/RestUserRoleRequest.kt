package de.kordondev.lagermelder.rest.model.request

import jakarta.validation.constraints.Pattern


data class RestUserRoleRequest(
    @field:Pattern(
        regexp = "^(USER|LK_KARLSRUHE|SPECIALIZED_FIELD_DIRECTOR|ADMIN)$",
        message = "role needs to be one of USER, LK_KARLSRUHE, ADMIN or SPECIALIZED_FIELD_DIRECTOR"
    )
    val role: String
)