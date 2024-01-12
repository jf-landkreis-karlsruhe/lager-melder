package de.kordondev.lagermelder.core.persistence.entry

import de.kordondev.lagermelder.core.model.Event
import de.kordondev.lagermelder.core.model.NewEvent
import javax.persistence.*

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
) {
    companion object {
        fun of(event: NewEvent, code: String, id: Long = 0, type: EventType = EventType.Location): EventEntry {
            return EventEntry(
                id = id,
                name = event.name,
                code = code,
                trashed = false,
                type = type
            )
        }

        fun of(event: Event, code: String): EventEntry {
            return EventEntry(
                id = event.id,
                name = event.name,
                code = code,
                trashed = event.trashed,
                type = event.type
            )
        }

        fun to(eventEntry: EventEntry): Event {
            return Event(
                id = eventEntry.id,
                name = eventEntry.name,
                code = eventEntry.code,
                trashed = eventEntry.trashed,
                type = eventEntry.type
            )
        }
    }
}