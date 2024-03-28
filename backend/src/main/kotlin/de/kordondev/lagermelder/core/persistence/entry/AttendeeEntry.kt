package de.kordondev.lagermelder.core.persistence.entry

import de.kordondev.lagermelder.core.model.Attendee
import de.kordondev.lagermelder.core.model.NewAttendee
import org.hibernate.Hibernate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "attendees")
data class AttendeeEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "first_name")
    val firstName: String,

    @Column(name = "last_name")
    val lastName: String,

    @Column(name = "birthday")
    val birthday: String,

    @Column(name = "food")
    @Enumerated(EnumType.STRING)
    val food: Food,

    @Column(name = "t_shirt_size")
    @Enumerated(EnumType.STRING)
    val tShirtSize: TShirtSize,

    @Column(name = "additional_information")
    val additionalInformation: String,

    @Column(name = "code", unique = true)
    val code: String,

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val role: AttendeeRole,

    @ManyToOne
    @JoinColumn(name = "department_id")
    val department: DepartmentEntry,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    val status: AttendeeStatus?

) {
    companion object {
        fun of(attendee: Attendee, status: AttendeeStatus?): AttendeeEntry {
            return AttendeeEntry(
                id = attendee.id,
                firstName = attendee.firstName,
                lastName = attendee.lastName,
                department = attendee.department,
                birthday = attendee.birthday,
                food = attendee.food,
                tShirtSize = attendee.tShirtSize,
                additionalInformation = attendee.additionalInformation,
                role = attendee.role,
                code = attendee.code,
                status = status,
            )
        }

        fun of(attendee: NewAttendee, code: String, id: Long = 0) = AttendeeEntry(
            id = id,
            firstName = attendee.firstName,
            lastName = attendee.lastName,
            department = attendee.department,
            birthday = attendee.birthday,
            food = attendee.food,
            tShirtSize = attendee.tShirtSize,
            additionalInformation = attendee.additionalInformation,
            role = attendee.role,
            code = code,
            status = null
        )

        fun to(attendeeEntry: AttendeeEntry): Attendee {
            return Attendee(
                id = attendeeEntry.id,
                firstName = attendeeEntry.firstName,
                lastName = attendeeEntry.lastName,
                department = attendeeEntry.department,
                birthday = attendeeEntry.birthday,
                food = attendeeEntry.food,
                tShirtSize = attendeeEntry.tShirtSize,
                additionalInformation = attendeeEntry.additionalInformation,
                role = attendeeEntry.role,
                code = attendeeEntry.code,
                status = attendeeEntry.status
            )
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AttendeeEntry

        return id != null && id == other.id
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun hashCode(): Int {
        return Objects.hash(code)
    }
}
