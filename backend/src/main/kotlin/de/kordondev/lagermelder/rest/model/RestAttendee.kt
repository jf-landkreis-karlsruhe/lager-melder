package de.kordondev.lagermelder.rest.model

import com.fasterxml.jackson.annotation.JsonProperty
import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.exception.UnexpectedTypeException

data class RestAttendee(
    val id: String,
    val firstName: String,
    val lastName: String,
    val departmentId: Long,
    val birthday: String? = null,
    val food: Food,
    @get:JsonProperty("tShirtSize")
    val tShirtSize: String,
    val additionalInformation: String,
    val role: AttendeeRole,
    val code: String,
    val status: String,
    val juleikaNumber: String? = null,
    val juleikaExpireDate: String? = null,
    val partOfDepartmentId: Long? = null,
    val helperDays: Set<String>? = null
) {
    companion object {

        fun of(attendee: Attendee): RestAttendee {
            return when (attendee) {
                is YouthEntry -> of(attendee)
                is YouthLeaderEntry -> of(attendee)
                is ChildEntry -> of(attendee)
                is ChildLeaderEntry -> of(attendee)
                is ZKidEntry -> of(attendee)
                is HelperEntity -> of(attendee)
                else -> throw UnexpectedTypeException("attendee is of type ${attendee.javaClass} and can not be be a RestAttendee")
            }
        }

        fun of(attendee: YouthEntry) = RestAttendee(
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

        fun of(attendee: YouthLeaderEntry) = RestAttendee(
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

        fun of(attendee: ChildEntry) = RestAttendee(
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

        fun of(attendee: ChildLeaderEntry) = RestAttendee(
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

        fun of(attendee: ZKidEntry) = RestAttendee(
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
            status = attendee.status.toString(),
            partOfDepartmentId = attendee.partOfDepartment.id
        )

        fun of(attendee: HelperEntity) = RestAttendee(
            id = attendee.id,
            firstName = attendee.firstName,
            lastName = attendee.lastName,
            food = attendee.food,
            tShirtSize = attendee.tShirtSize,
            additionalInformation = attendee.additionalInformation,
            role = attendee.role,
            departmentId = attendee.department.id,
            code = attendee.code,
            status = attendee.status.toString(),
            helperDays = attendee.helperDays.map { it.id }.toSet()
        )
    }
}
