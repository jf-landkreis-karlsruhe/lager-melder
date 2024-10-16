package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.util.*

data class RestAttendeeRequest(
    @field:NotNull(message = "firstName cannot be missing")
    @field:NotBlank(message = "firstName cannot be blank")
    val firstName: String,
    @field:NotNull(message = "lastName cannot be missing")
    @field:NotBlank(message = "lastName cannot be blank")
    val lastName: String,
    @field:NotNull(message = "departmentId missing")
    val departmentId: Long,
    val birthday: String,
    @field:NotNull(message = "food is missing")
    val food: Food,
    @field:NotNull(message = "tShirtSize is missing")
    val tShirtSize: String,
    val additionalInformation: String,
    @field:NotNull(message = "role is missing")
    val role: AttendeeRole,
    val juleikaNumber: String,
    val juleikaExpireDate: String,
    val partOfDepartmentId: Long,
    val helperDays: Set<String>
) {
    companion object {
        fun to(attendee: RestAttendeeRequest, department: DepartmentEntry, partOfDepartment: DepartmentEntry?, eventDays: Set<EventDayEntity>): Attendee {
            return when (attendee.role) {
                AttendeeRole.YOUTH -> YouthEntry(
                    id = UUID.randomUUID().toString(),
                    firstName = attendee.firstName,
                    lastName = attendee.lastName,
                    birthday = attendee.birthday,
                    food = attendee.food,
                    tShirtSize = attendee.tShirtSize,
                    additionalInformation = attendee.additionalInformation,
                    role = attendee.role,
                    department = department,
                    code = "",
                    status = null
                )

                AttendeeRole.YOUTH_LEADER -> YouthLeaderEntry(
                    id = UUID.randomUUID().toString(),
                    firstName = attendee.firstName,
                    lastName = attendee.lastName,
                    birthday = attendee.birthday,
                    food = attendee.food,
                    tShirtSize = attendee.tShirtSize,
                    additionalInformation = attendee.additionalInformation,
                    role = attendee.role,
                    department = department,
                    code = "",
                    status = null,
                    juleikaNumber = attendee.juleikaNumber,
                    juleikaExpireDate = toDateOrNull(attendee.juleikaExpireDate)
                )

                AttendeeRole.CHILD -> ChildEntry(
                    id = UUID.randomUUID().toString(),
                    firstName = attendee.firstName,
                    lastName = attendee.lastName,
                    birthday = attendee.birthday,
                    food = attendee.food,
                    tShirtSize = attendee.tShirtSize,
                    additionalInformation = attendee.additionalInformation,
                    role = attendee.role,
                    department = department,
                    code = "",
                    status = null
                )

                AttendeeRole.CHILD_LEADER -> ChildLeaderEntry(
                    id = UUID.randomUUID().toString(),
                    firstName = attendee.firstName,
                    lastName = attendee.lastName,
                    birthday = attendee.birthday,
                    food = attendee.food,
                    tShirtSize = attendee.tShirtSize,
                    additionalInformation = attendee.additionalInformation,
                    role = attendee.role,
                    department = department,
                    code = "",
                    status = null,
                    juleikaNumber = attendee.juleikaNumber,
                    juleikaExpireDate = toDateOrNull(attendee.juleikaExpireDate)
                )

                AttendeeRole.Z_KID -> ZKidEntry(
                    id = UUID.randomUUID().toString(),
                    firstName = attendee.firstName,
                    lastName = attendee.lastName,
                    birthday = attendee.birthday,
                    food = attendee.food,
                    tShirtSize = attendee.tShirtSize,
                    additionalInformation = attendee.additionalInformation,
                    role = attendee.role,
                    department = department,
                    code = "",
                    status = null,
                    // at this point we know that partOfDepartment is not null, because we could load it from the database
                    // but all other Attendee types do not have a partOfDepartment
                    partOfDepartment = partOfDepartment!!
                )

                AttendeeRole.HELPER -> HelperEntity(
                    id = UUID.randomUUID().toString(),
                    firstName = attendee.firstName,
                    lastName = attendee.lastName,
                    food = attendee.food,
                    tShirtSize = attendee.tShirtSize,
                    additionalInformation = attendee.additionalInformation,
                    role = attendee.role,
                    department = department,
                    code = "",
                    status = null,
                    helperDays = eventDays.filter { it.id in attendee.helperDays }.toSet()
                )
            }
        }

        private fun toDateOrNull(date: String): LocalDate? {
            return if (date != "") {
                LocalDate.parse(date)
            } else {
                null
            }
        }
    }
}