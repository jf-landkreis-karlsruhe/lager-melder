package de.kordondev.lagermelder.core.service.models

import de.kordondev.lagermelder.core.persistence.entry.ChildEntry
import de.kordondev.lagermelder.core.persistence.entry.ChildLeaderEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry

data class Attendees(
    val youths: List<YouthEntry>,
    val youthLeaders: List<YouthLeaderEntry>,
    val children: List<ChildEntry>,
    val childLeaders: List<ChildLeaderEntry>
)