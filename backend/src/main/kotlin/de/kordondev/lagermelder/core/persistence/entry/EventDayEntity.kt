package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "event_days")
data class EventDayEntity(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "name")
    val name: String,

    @Column(name = "day_of_event")
    val dayOfEvent: Int
)
