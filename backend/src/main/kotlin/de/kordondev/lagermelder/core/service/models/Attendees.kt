package de.kordondev.lagermelder.core.service.models

import de.kordondev.lagermelder.core.persistence.entry.YouthEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry

data class Attendees(
    val youths: List<YouthEntry>,
    val youthLeaders: List<YouthLeaderEntry>
)