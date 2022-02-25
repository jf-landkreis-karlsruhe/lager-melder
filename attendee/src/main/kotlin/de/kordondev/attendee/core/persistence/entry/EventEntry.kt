package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Event
import de.kordondev.attendee.core.model.NewEvent
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

    @Column(name = "trashed")
    val trashed: Boolean
) {
    companion object {
        fun of(event: NewEvent, code: String, id: Long = 0): EventEntry {
            return EventEntry(
                id = id,
                name = event.name,
                code = code,
                trashed = false
            )
        }

        fun of(event: Event, code: String): EventEntry {
            return EventEntry(
                id = event.id,
                name = event.name,
                code = code,
                trashed = event.trashed
            )
        }

        fun to(eventEntry: EventEntry): Event {
            return Event(
                id = eventEntry.id,
                name = eventEntry.name,
                code = eventEntry.code,
                trashed = eventEntry.trashed
            )
        }
    }
}