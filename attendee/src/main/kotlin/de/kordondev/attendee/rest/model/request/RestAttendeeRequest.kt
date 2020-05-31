package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.TShirtSize

data class RestAttendeeRequest(
        val firstName: String,
        val lastName: String,
        val departmentId: Long,
        val birthday: String,
        val food: Food,
        val tShirtSize: TShirtSize,
        val additionalInformation: String,
        val role: AttendeeRole
) {
    companion object {
        fun to(attendee: RestAttendeeRequest, department: Department): NewAttendee {
            return NewAttendee(
                    firstName = attendee.firstName,
                    lastName = attendee.lastName,
                    birthday = attendee.birthday,
                    food = attendee.food,
                    tShirtSize = attendee.tShirtSize,
                    additionalInformation = attendee.additionalInformation,
                    role = attendee.role,
                    department = department
            )
        }
    }
}