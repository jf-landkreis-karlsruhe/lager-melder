package de.kordondev.lagermelder.core.persistence.entry.interfaces

import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.AttendeeStatus
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Food

interface Attendee {
    val id: String
    val firstName: String
    val lastName: String
    val food: Food
    val tShirtSize: String
    val additionalInformation: String
    val code: String
    val role: AttendeeRole
    val department: DepartmentEntry
    val status: AttendeeStatus?
}