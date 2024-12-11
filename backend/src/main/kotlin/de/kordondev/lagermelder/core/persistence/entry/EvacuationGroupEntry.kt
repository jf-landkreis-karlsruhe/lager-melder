package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "evacuation_groups")
data class EvacuationGroupEntry (
        @Id
        val id: String = UUID.randomUUID().toString(),

        @Column(name = "name")
        val name: String,

        @Column(name = "color")
        val color: String
)