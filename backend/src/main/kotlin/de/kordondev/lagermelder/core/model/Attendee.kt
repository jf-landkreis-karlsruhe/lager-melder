package de.kordondev.lagermelder.core.model

import de.kordondev.lagermelder.core.persistence.entry.*

data class NewAttendee(
    val firstName: String,
    val lastName: String,
    val birthday: String,
    val food: Food,
    val tShirtSize: TShirtSize,
    val additionalInformation: String,
    val role: AttendeeRole,
    val department: DepartmentEntry
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
    val department: DepartmentEntry,
    val code: String,
    var status: AttendeeStatus?
)

