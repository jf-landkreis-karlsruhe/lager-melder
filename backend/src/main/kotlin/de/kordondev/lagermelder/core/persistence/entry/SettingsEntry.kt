package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.Hibernate
import java.time.Instant
import java.time.LocalDate

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
    val moneyPerYouthLoader: String,

    @Column(name = "start_download_registration_files")
    val startDownloadRegistrationFiles: Instant

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SettingsEntry

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}