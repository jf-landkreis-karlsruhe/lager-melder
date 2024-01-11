package de.kordondev.lagermelder.rest.model.request

import javax.validation.constraints.Pattern

data class RestUserRoleRequest(
    @field:Pattern(regexp = "^(USER|SPECIALIZED_FIELD_DIRECTOR)$",message = "role needs to be one of USER or SPECIALIZED_FIELD_DIRECTOR")
    val role: String
)