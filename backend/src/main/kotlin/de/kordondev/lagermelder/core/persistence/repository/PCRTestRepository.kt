package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.PCRTestEntry
import org.springframework.data.repository.CrudRepository

interface PCRTestRepository : CrudRepository<PCRTestEntry, Long> {
    fun findAllByPcrTestSeriesId(pcrTestSeriesId: Long): Set<PCRTestEntry>
    fun findByCodeAndTrashedIsFalse(code: String): PCRTestEntry?
    fun findByCodeAndTrashedIsTrue(code: String): PCRTestEntry?
    fun findAllByCodeInAndTrashedIsFalse(code: List<String>): Set<PCRTestEntry>
    fun findByTestedAttendeesIdAndPcrTestSeriesId(attendeeId: Long, pcrTestSeriesId: Long): PCRTestEntry?
}