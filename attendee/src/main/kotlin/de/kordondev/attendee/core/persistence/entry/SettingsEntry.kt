package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Settings
import java.time.Instant
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "settings")
data class SettingsEntry(
    @Id
    val id: Long = 0,

    @Column(name = "registration_end")
    val registrationEnd: Instant,

    @Column(name = "host_city")
    val hostCity: String,

    @Column(name = "event_start")
    val eventStart: LocalDate,

    @Column(name = "event_end")
    val eventEnd: LocalDate,

    @Column(name = "event_name")
    val eventName: String,

    @Column(name = "event_address")
    val eventAddress: String,

    @Column(name = "organizer")
    val organizer: String,

    @Column(name = "organisationAddress")
    val organisationAddress: String, // Multiline

    @Column(name = "moneyPerYouthLoader")
    val moneyPerYouthLoader: String
) {
    companion object {
        fun of(settings: Settings, id: Long) = SettingsEntry(
            id = id,
            registrationEnd = settings.registrationEnd,
            hostCity = settings.hostCity,
            eventStart = settings.eventStart,
            eventEnd = settings.eventEnd,
            eventName = settings.eventName,
            eventAddress = settings.eventAddress,
            organizer = settings.organizer,
            organisationAddress = settings.organisationAddress,
            moneyPerYouthLoader = settings.moneyPerYouthLoader
        )

        fun to(settings: SettingsEntry) = Settings(
            id = settings.id,
            registrationEnd = settings.registrationEnd,
            hostCity = settings.hostCity,
            eventStart = settings.eventStart,
            eventEnd = settings.eventEnd,
            eventName = settings.eventName,
            eventAddress = settings.eventAddress,
            organizer = settings.organizer,
            organisationAddress = settings.organisationAddress,
            moneyPerYouthLoader = settings.moneyPerYouthLoader
        )
    }
}