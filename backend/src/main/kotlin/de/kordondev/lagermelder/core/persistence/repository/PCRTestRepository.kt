package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.PCRTestEntry
import org.springframework.data.repository.CrudRepository

interface ResetTokenRepository : CrudRepository<ResetTokenEntry, String> {
    fun findByToken(token: String): ResetTokenRepository?
}
