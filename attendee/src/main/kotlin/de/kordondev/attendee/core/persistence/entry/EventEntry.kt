package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Event
import de.kordondev.attendee.core.model.NewEvent
import javax.persistence.*

@Entity
@Table
data class EventEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name")
    val name: String,

    @Column(name = "code")
    val code: String
) {
    companion object {
        fun of(event: NewEvent, code: String, id: Long = 0): EventEntry {
            return EventEntry(
                id = id,
                name = event.name,
                code = code
            )
        }

        fun of(event: Event, code: String): EventEntry {
            return EventEntry(
                id = event.id,
                name = event.name,
                code = code
            )
        }

        fun to(eventEntry: EventEntry): Event {
            return Event(
                id = eventEntry.id,
                name = eventEntry.name,
                code = eventEntry.code
            )
        }
    }
}