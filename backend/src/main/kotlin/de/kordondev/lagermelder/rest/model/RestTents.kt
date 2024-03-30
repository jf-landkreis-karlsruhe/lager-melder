package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.TentsEntity

data class RestTents(
    val id: Long,
    val departmentId: Long,
    val sg200: Int,
    val sg20: Int,
    val sg30: Int,
    val sg40: Int,
    val sg50: Int
) {
    companion object {
        fun of(tents: TentsEntity) = RestTents(
            id = tents.id,
            departmentId = tents.department.id,
            sg200 = tents.sg200,
            sg20 = tents.sg20,
            sg30 = tents.sg30,
            sg40 = tents.sg40,
            sg50 = tents.sg50
        )

        fun to(tents: RestTents, department: DepartmentEntry) = TentsEntity(
            id = tents.id,
            department = department,
            sg200 = minZero(tents.sg200),
            sg20 = minZero(tents.sg20),
            sg30 = minZero(tents.sg30),
            sg40 = minZero(tents.sg40),
            sg50 = minZero(tents.sg50)
        )

        private fun minZero(number: Int): Int {
           if (number < 0) {
               return 0
           }
           return number
        }
    }
}