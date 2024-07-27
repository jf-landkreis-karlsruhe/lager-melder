package de.kordondev.lagermelder.rest.model

import com.fasterxml.jackson.annotation.JsonProperty
import de.kordondev.lagermelder.core.persistence.entry.AttendeeEntry
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.Food

data class RestAttendee(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val departmentId: Long,
    val birthday: String,
    val food: Food,
    @get:JsonProperty("tShirtSize")
    val tShirtSize: String,
    val additionalInformation: String,
    val role: AttendeeRole,
    val code: String,
    val status: String
) {
    companion object {
        fun of(attendee: AttendeeEntry) = RestAttendee(
            id = attendee.id,
            firstName = attendee.firstName,
            lastName = attendee.lastName,
            birthday = attendee.birthday,
            food = attendee.food,
            tShirtSize = attendee.tShirtSize,
            additionalInformation = attendee.additionalInformation,
            role = attendee.role,
            departmentId = attendee.department.id,
            code = attendee.code,
            status = attendee.status.toString()
        )
    }
}
