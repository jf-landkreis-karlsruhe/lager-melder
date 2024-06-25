package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.rest.model.RestTents

data class RestDepartmentRegistrationRequest(
    val tents: RestTents,
    val departmentId: Long,
    val departmentPhoneNumber: String
)
