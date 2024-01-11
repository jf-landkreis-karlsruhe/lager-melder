package de.kordondev.lagermelder.core.model

import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.AttendeeStatus
import de.kordondev.lagermelder.core.persistence.entry.Food
import de.kordondev.lagermelder.core.persistence.entry.TShirtSize

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
    var status: AttendeeStatus?
)

