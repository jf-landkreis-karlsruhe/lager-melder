package de.kordondev.attendee.rest.model

data class RestGlobalEventSummary(
    val total: Distribution,
    val departments: List<Distribution>
)

data class Distribution(
    val name: String,
    val checkedInYouth: Int,
    val checkedInYouthLeader: Int,
    val checkedOutYouth: Int,
    val checkedOutYouthLeader: Int
)