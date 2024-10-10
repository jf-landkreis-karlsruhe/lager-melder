package de.kordondev.lagermelder.core.service.models

import de.kordondev.lagermelder.core.persistence.entry.*

data class Attendees(
    val youths: List<YouthEntry>,
    val youthLeaders: List<YouthLeaderEntry>,
    val children: List<ChildEntry>,
    val childLeaders: List<ChildLeaderEntry>,
    val zKids: List<ZKidEntry>
)