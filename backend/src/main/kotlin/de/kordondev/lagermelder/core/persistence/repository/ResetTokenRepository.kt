package de.kordondev.lagermelder.core.persistence.repository

import ResetTokenEntry
import org.springframework.data.repository.CrudRepository

interface ResetTokenRepository : CrudRepository<ResetTokenEntry, Long> {
    fun findByToken(token: String): ResetTokenEntry?
}