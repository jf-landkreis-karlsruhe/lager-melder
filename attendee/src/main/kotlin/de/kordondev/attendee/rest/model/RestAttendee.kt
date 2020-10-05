package de.kordondev.attendee.rest.model

import com.fasterxml.jackson.annotation.JsonProperty
import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.TShirtSize

data class RestAttendee(
        val id: Long,
        val firstName: String,
        val lastName: String,
        val departmentId: Long,
        val birthday: String,
        val food: Food,
        @get:JsonProperty("tShirtSize")
        val tShirtSize: TShirtSize,
        val additionalInformation: String,
        val role: AttendeeRole
) {
    companion object {
        fun of(attendee: Attendee) = RestAttendee(
                id = attendee.id,
                firstName = attendee.firstName,
                lastName = attendee.lastName,
                birthday = attendee.birthday,
                food = attendee.food,
                tShirtSize = attendee.tShirtSize,
                additionalInformation = attendee.additionalInformation,
                role = attendee.role,
                departmentId = attendee.department.id
        )
    }
}
