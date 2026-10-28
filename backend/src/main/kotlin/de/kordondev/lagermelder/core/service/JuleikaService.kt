package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.juleika.JuleikaValidationResult
import de.kordondev.lagermelder.core.juleika.JuleikaValidationService
import de.kordondev.lagermelder.core.persistence.entry.ChildLeaderEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.security.AuthorityService
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class JuleikaService(
    private val authorityService: AuthorityService,
    private val juleikaValidationService: JuleikaValidationService,
) {
    fun validate(
        cardNumber: String,
        lastName: String,
    ): JuleikaValidationResult {
        if (!authorityService.hasRole(AuthorityService.USER_ALLOWED)) {
            throw AccessDeniedException("You are not allowed to validate a Juleika")
        }
        return juleikaValidationService.validate(cardNumber, lastName)
    }

    /**
     * Replaces the Juleika expire date sent by the client with the one the Juleika API confirms.
     * If number and last name are unchanged compared to the stored attendee, the stored date is kept without a new check.
     */
    fun withCheckedExpireDate(
        attendee: Attendee,
        stored: Attendee? = null,
    ): Attendee =
        when (attendee) {
            is YouthLeaderEntry -> attendee.copy(juleikaExpireDate = checkedExpireDate(attendee, stored))
            is ChildLeaderEntry -> attendee.copy(juleikaExpireDate = checkedExpireDate(attendee, stored))
            else -> attendee
        }

    private fun checkedExpireDate(
        attendee: Attendee,
        stored: Attendee?,
    ): LocalDate? {
        val juleika = juleikaOf(attendee) ?: return null
        val storedJuleika = stored?.let { juleikaOf(it) }
        return if (storedJuleika?.number == juleika.number && storedJuleika.lastName == juleika.lastName) {
            storedJuleika.expireDate
        } else {
            juleikaValidationService
                .validate(juleika.number, juleika.lastName)
                .takeIf { it.valid }
                ?.expireDate
        }
    }

    private fun juleikaOf(attendee: Attendee): Juleika? =
        when (attendee) {
            is YouthLeaderEntry -> Juleika(attendee.juleikaNumber, attendee.lastName, attendee.juleikaExpireDate)
            is ChildLeaderEntry -> Juleika(attendee.juleikaNumber, attendee.lastName, attendee.juleikaExpireDate)
            else -> null
        }

    private data class Juleika(
        val number: String,
        val lastName: String,
        val expireDate: LocalDate?,
    )
}
