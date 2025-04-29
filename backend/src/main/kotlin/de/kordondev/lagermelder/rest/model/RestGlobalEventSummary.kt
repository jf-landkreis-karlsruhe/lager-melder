package de.kordondev.lagermelder.rest.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RestGlobalEventSummary(
    val total: Distribution,
    val departments: List<Distribution>
)

data class Distribution(
    val name: String,
    val youths: Int,
    val youthLeaders: Int,
    @get:JsonProperty("zKids")
    val zKids: Int,
    val children: Int,
    val childLeaders: Int,
)