package de.kordondev.lagermelder

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.exception.UnexpectedTypeException
import java.time.LocalDate
import java.time.Period

class Helper {
    companion object {
        fun birthdayToDate(birthday: String) : LocalDate {
            val dateList = birthday.split("-")
            return LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt())
        }

        fun ageAtEvent(attendee: Attendee, eventStart: LocalDate): Int {
            return ageAtEvent(getBirthday(attendee), eventStart)
        }

        fun ageAtEvent(attendee: Attendee, eventStart: LocalDate, fallback: Int): Int {
            return try {
                ageAtEvent(getBirthday(attendee), eventStart)
            } catch (e: UnexpectedTypeException) {
                fallback
            }
        }

        fun getBirthday(attendee: Attendee): String {
            return when (attendee) {
                is YouthEntry -> attendee.birthday
                is YouthLeaderEntry -> attendee.birthday
                is ChildEntry -> attendee.birthday
                is ChildLeaderEntry -> attendee.birthday
                is ZKidEntry -> attendee.birthday
                else -> throw UnexpectedTypeException("Attendee ${attendee.id} has no age")
            }
        }

        private fun ageAtEvent(birthday: String, eventStart: LocalDate): Int {
            return Period.between(birthdayToDate(birthday), eventStart).years
        }
    }
}