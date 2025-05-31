package de.kordondev.lagermelder.rest.model

data class RestGlobalEventSummary(
    val total: Distribution,
    val departments: List<Distribution>
)

data class Distribution(
    val name: String,
    val youths: Int,
    val youthLeaders: Int,
    val children: Int,
    val childLeaders: Int,
    val helpers: Int,
)