package de.kordondev.lagermelder

import java.time.LocalDate
import java.time.Period

class Helper {
    companion object {
        fun birthdayToDate(birthday: String) : LocalDate {
            var dateList = birthday.split("-")
            return LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt())
        }

        fun ageAtEvent(birthday: String, eventStart: LocalDate): Int {
            return Period.between(birthdayToDate(birthday), eventStart).years
        }
    }
}