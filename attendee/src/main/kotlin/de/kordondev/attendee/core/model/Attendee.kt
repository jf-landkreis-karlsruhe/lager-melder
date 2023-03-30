package de.kordondev.attendee.core.model

import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.AttendeeStatus
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.TShirtSize

data class NewAttendee(
    val firstName: String,
    val lastName: String,
    val birthday: String,
    val food: Food,
    val tShirtSize: TShirtSize,
    val additionalInformation: String,
    val role: AttendeeRole,
    val department: Department
)

data class Attendee(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val birthday: String,
    val food: Food,
    val tShirtSize: TShirtSize,
    val additionalInformation: String,
    val role: AttendeeRole,
    val department: Department,
    val code: String,
    var youthPlanRole: AttendeeRole?,
    var status: AttendeeStatus?
)

