package de.kordondev.lagermelder.rest.model

data class RestSubsidy(
    val id: Long,
    val participants: SubsidyDistribution,
    val childGroup: SubsidyDistribution,
)

data class SubsidyDistribution(
    val stateYouthPlanLeaders: Int,
    val stateYouthPlanParticipants: Int,
    val karlsruheLeaders: Int,
    val karlsruheParticipants: Int
)
