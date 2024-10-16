package de.kordondev.lagermelder.rest.model

import com.fasterxml.jackson.annotation.JsonProperty
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.Food
import de.kordondev.lagermelder.core.persistence.entry.HelperEntity

data class RestHelper(
    val id: String,
    val firstName: String,
    val lastName: String,
    val departmentId: Long,
    val food: Food,
    @get:JsonProperty("tShirtSize")
    val tShirtSize: String,
    val additionalInformation: String,
    val role: AttendeeRole,
    val code: String,
    val status: String?,
    val helperDays: Set<String>
) {
    companion object {
        fun of(attendee: HelperEntity) = RestHelper(
            id = attendee.id,
            firstName = attendee.firstName,
            lastName = attendee.lastName,
            food = attendee.food,
            tShirtSize = attendee.tShirtSize,
            additionalInformation = attendee.additionalInformation,
            role = attendee.role,
            departmentId = attendee.department.id,
            code = attendee.code,
            status = attendee.status?.toString(),
            helperDays = attendee.helperDays.map { it.id }.toSet()
        )
    }
}
