package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.AttendeeInEventEntry
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AttendeeInEventRepository : CrudRepository<AttendeeInEventEntry, Long> {
    @Modifying
    @Query(
        "DELETE FROM AttendeeInEventEntry a WHERE a.attendeeCode = :attendeeCode"
    )
    fun deleteAllByAttendeeCode(attendeeCode: String)
}
