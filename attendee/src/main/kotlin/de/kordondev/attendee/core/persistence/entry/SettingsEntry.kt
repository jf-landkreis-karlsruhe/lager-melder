package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Settings
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "settings")
data class SettingsEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "registration_end")
    val registrationEnd: LocalDateTime
) {
    companion object {
        fun of(settings: Settings, id: Long) = SettingsEntry(
            id = id,
            registrationEnd = settings.registrationEnd
        )

        fun to(settings: SettingsEntry) = Settings(
            id = settings.id,
            registrationEnd = settings.registrationEnd
        )
    }
}