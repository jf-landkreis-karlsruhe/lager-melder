package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.NewAttendee
import javax.persistence.*

@Entity
@Table(name="Attendee")
data class AttendeeEntry (

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,

        @Column(name = "firstName")
        val firstName: String,

        @Column(name = "lastName")
        val lastName: String,

        @ManyToOne
        @JoinColumn(name = "department")
        val department: DepartmentEntry
) {
        companion object {
                fun of(attendee: Attendee): AttendeeEntry {
                        return AttendeeEntry(
                                id = attendee.id,
                                firstName = attendee.firstName,
                                lastName = attendee.lastName,
                                department = DepartmentEntry.of(attendee.department)
                        )
                }

                fun of(attendee: NewAttendee) = AttendeeEntry(
                        firstName = attendee.firstName,
                        lastName = attendee.lastName,
                        department = DepartmentEntry.of(attendee.department)
                )

                fun to(attendeeEntry: AttendeeEntry): Attendee {
                        return Attendee(
                                id = attendeeEntry.id,
                                firstName = attendeeEntry.firstName,
                                lastName = attendeeEntry.lastName,
                                department = DepartmentEntry.to(attendeeEntry.department)
                        )
                }

        }
}