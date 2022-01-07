package de.kordondev.attendee.rest.model.request;

import de.kordondev.attendee.core.model.Settings
import java.time.LocalDateTime
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotNull

data class RestSettingsRequest(
    @NotNull
    @FutureOrPresent
    val registrationEnd: LocalDateTime
) {
    companion object {
        fun to(settings: RestSettingsRequest) = Settings(
            id = 0,
            registrationEnd = settings.registrationEnd
        )
    }
}
