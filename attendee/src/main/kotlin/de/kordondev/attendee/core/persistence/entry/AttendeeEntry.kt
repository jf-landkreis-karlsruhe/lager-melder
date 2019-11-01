package de.kordondev.attendee.core.persistence.entry

import javax.persistence.*

@Entity
@Table(name="Attendee")
data class AttendeeEntry (

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "firstName")
        val firstName: String,

        @Column(name = "lastName")
        val lastName: String
)