package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.TentMarkingEntry

data class RestTentMarking(
    val id: String,
    val name: String,
) {
    companion object {
        fun of(tentMarking: TentMarkingEntry) = RestTentMarking(
            id = tentMarking.id,
            name = tentMarking.name,
        )
    }
}