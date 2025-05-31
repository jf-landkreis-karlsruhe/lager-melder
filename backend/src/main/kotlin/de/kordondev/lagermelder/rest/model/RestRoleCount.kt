package de.kordondev.lagermelder.rest.model

data class RestRoleCount(
    val helpers: Int,
    val youths: Int,
    val youthLeaders: Int,
    val children: Int,
    val childLeaders: Int,
) {
    companion object {
        fun of(distribution: Distribution): RestRoleCount {
            return RestRoleCount(
                helpers = distribution.helpers,
                youths = distribution.youths,
                youthLeaders = distribution.youthLeaders,
                children = distribution.children,
                childLeaders = distribution.childLeaders
            )
        }
    }
}