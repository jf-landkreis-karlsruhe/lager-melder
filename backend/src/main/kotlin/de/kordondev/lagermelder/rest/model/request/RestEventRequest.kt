package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.EventEntry
import de.kordondev.lagermelder.core.persistence.entry.EventType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestEventRequest(
    @field:NotNull(message = "name cannot be missing")
    @field:NotBlank(message = "name cannot be blank")
    val name: String
) {
    companion object {
        fun to(event: RestEventRequest): EventEntry {
            return EventEntry(
                id = 0,
                name = event.name,
                code = "",
                type = EventType.Location,
                trashed = false
            )
        }
    }
}