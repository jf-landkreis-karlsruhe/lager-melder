package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.service.models.RoleCount

data class RestRoleCount(
    val helper: Long,
    val youth: Long,
    val youthLeader: Long,
    val children: Long,
    val childLeader: Long,
) {
    companion object {
        fun of(roleCount: RoleCount): RestRoleCount {
            return RestRoleCount(
                helper = roleCount.helper,
                youth = roleCount.youth,
                youthLeader = roleCount.youthLeader,
                children = roleCount.children,
                childLeader = roleCount.childLeader
            )
        }
    }
}