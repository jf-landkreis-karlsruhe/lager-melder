package de.kordondev.lagermelder.core.persistence.entry

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "attendee_in_event")
data class AttendeeInEventEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "attendeeCode")
    val attendeeCode: String,

    @Column(name = "eventCode")
    val eventCode: String,

    @Column(name = "time")
    val time: Instant
)
