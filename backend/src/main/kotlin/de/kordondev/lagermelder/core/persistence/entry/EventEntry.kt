package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.*

@Entity
@Table(name = "events")
data class EventEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name")
    val name: String,

    @Column(name = "code")
    val code: String,

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    val type: EventType,

    @Column(name = "trashed")
    val trashed: Boolean
)