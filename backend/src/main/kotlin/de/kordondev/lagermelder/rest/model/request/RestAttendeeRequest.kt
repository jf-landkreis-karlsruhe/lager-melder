package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestAttendeeRequest(
    @field:NotNull(message = "firstName cannot be missing")
    @field:NotBlank(message = "firstName cannot be blank")
    val firstName: String,
    @field:NotNull(message = "lastName cannot be missing")
    @field:NotBlank(message = "lastName cannot be blank")
    val lastName: String,
    @field:NotNull(message = "departmentId missing")
    val departmentId: Long,
    @field:NotNull(message = "birthday is missing")
    val birthday: String,
    @field:NotNull(message = "food is missing")
    val food: Food,
    @field:NotNull(message = "tShirtSize is missing")
    val tShirtSize: String,
    val additionalInformation: String,
    @field:NotNull(message = "role is missing")
    val role: AttendeeRole
) {
    companion object {
        fun to(attendee: RestAttendeeRequest, department: DepartmentEntry): AttendeeEntry {
            return AttendeeEntry(
                id = 0,
                firstName = attendee.firstName,
                lastName = attendee.lastName,
                birthday = attendee.birthday,
                food = attendee.food,
                tShirtSize = TShirtSize.fromString(attendee.tShirtSize),
                additionalInformation = attendee.additionalInformation,
                role = attendee.role,
                department = department,
                code = "",
                status = null
            )
        }
    }
}