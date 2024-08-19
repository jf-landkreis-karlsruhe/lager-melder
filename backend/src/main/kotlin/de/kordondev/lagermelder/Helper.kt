package de.kordondev.lagermelder

import de.kordondev.lagermelder.core.persistence.entry.YouthEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import java.time.LocalDate
import java.time.Period

class Helper {
    companion object {
        fun birthdayToDate(birthday: String) : LocalDate {
            var dateList = birthday.split("-")
            return LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt())
        }

        fun ageAtEvent(attendee: Attendee, eventStart: LocalDate): Int {
            return when (attendee) {
                is YouthEntry -> ageAtEvent(attendee.birthday, eventStart)
                is YouthLeaderEntry -> ageAtEvent(attendee.birthday, eventStart)
                else -> 0
            }
        }

        // TODO: make private
        fun ageAtEvent(birthday: String, eventStart: LocalDate): Int {
            return Period.between(birthdayToDate(birthday), eventStart).years
        }
    }
}