package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.NewEvent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestEventRequest(
    @field:NotNull(message = "name cannot be missing")
    @field:NotBlank(message = "name cannot be blank")
    val name: String
) {
    companion object {
        fun to(event: RestEventRequest): NewEvent {
            return NewEvent(name = event.name)
        }
    }
}