package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.NewAttendee
import org.hibernate.Hibernate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "attendees")
data class AttendeeEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "firstName")
    val firstName: String,

    @Column(name = "lastName")
    val lastName: String,

    @Column(name = "birthday")
    val birthday: String,

    @Column(name = "food")
    val food: Food,

    @Column(name = "t_shirt_size")
    val tShirtSize: TShirtSize,

    @Column(name = "additional_information")
    val additionalInformation: String,

    @Column(name = "code", unique = true)
    val code: String,

    @Column(name = "role")
    val role: AttendeeRole,

    @ManyToOne
    @JoinColumn(name = "department")
    val department: DepartmentEntry,
) {
    companion object {
        fun of(attendee: Attendee): AttendeeEntry {
            return AttendeeEntry(
                id = attendee.id,
                firstName = attendee.firstName,
                lastName = attendee.lastName,
                department = DepartmentEntry.of(attendee.department),
                birthday = attendee.birthday,
                food = attendee.food,
                tShirtSize = attendee.tShirtSize,
                additionalInformation = attendee.additionalInformation,
                role = attendee.role,
                code = attendee.code,
            )
        }

        fun of(attendee: NewAttendee, code: String, id: Long = 0) = AttendeeEntry(
            id = id,
            firstName = attendee.firstName,
            lastName = attendee.lastName,
            department = DepartmentEntry.of(attendee.department),
            birthday = attendee.birthday,
            food = attendee.food,
            tShirtSize = attendee.tShirtSize,
            additionalInformation = attendee.additionalInformation,
            role = attendee.role,
            code = code
        )

        fun to(attendeeEntry: AttendeeEntry): Attendee {
            return Attendee(
                id = attendeeEntry.id,
                firstName = attendeeEntry.firstName,
                lastName = attendeeEntry.lastName,
                department = DepartmentEntry.to(attendeeEntry.department),
                birthday = attendeeEntry.birthday,
                food = attendeeEntry.food,
                tShirtSize = attendeeEntry.tShirtSize,
                additionalInformation = attendeeEntry.additionalInformation,
                role = attendeeEntry.role,
                code = attendeeEntry.code
            )
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }

        override fun toString(): String {
            return super.toString()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AttendeeEntry

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(code)
    }
}


