package de.kordondev.lagermelder.rest.model

import com.fasterxml.jackson.annotation.JsonProperty
import de.kordondev.lagermelder.core.service.models.Attendees

data class RestAttendees(
    val youths: List<RestYouth>,
    val youthLeaders: List<RestYouthLeader>,
    val children: List<RestChild>,
    val childLeaders: List<RestChildLeader>,
    @get:JsonProperty("zKids")
    val zKids: List<RestZKid>,
    val helpers: List<RestHelper>
) {
    companion object {
        fun of(attendees: Attendees) = RestAttendees(
            youths = attendees.youths.map { RestYouth.of(it) },
            youthLeaders = attendees.youthLeaders.map { RestYouthLeader.of(it) },
            children = attendees.children.map { RestChild.of(it) },
            childLeaders = attendees.childLeaders.map { RestChildLeader.of(it) },
            zKids = attendees.zKids.map { RestZKid.of(it) },
            helpers = attendees.helpers.map { RestHelper.of(it) }
        )
    }
}
