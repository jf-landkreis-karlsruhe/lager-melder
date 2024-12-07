package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.EvacuationGroupEntry

data class RestEvacuationGroup(
    val id: String,
    val name: String,
    val color: String
) {
    companion object {
        fun of(evacuationGroup: EvacuationGroupEntry) = RestEvacuationGroup(
            id = evacuationGroup.id,
            name = evacuationGroup.name,
            color = evacuationGroup.color
        )
    }
}