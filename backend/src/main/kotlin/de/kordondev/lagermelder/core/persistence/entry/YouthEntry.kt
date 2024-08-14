package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.util.*

@Entity
@Table(name = "base_attendees")
@SecondaryTable(name = "youths", pkJoinColumns = [PrimaryKeyJoinColumn(name = "base_attendee_id")])
data class YouthEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "first_name")
    val firstName: String,

    @Column(name = "last_name")
    val lastName: String,

    @Column(name = "birthday", table = "youths")
    val birthday: String,

    @Column(name = "food")
    @Enumerated(EnumType.STRING)
    val food: Food,

    @Column(name = "t_shirt_size")
    val tShirtSize: String,

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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as YouthEntry

        return id != null && id == other.id
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun hashCode(): Int {
        return Objects.hash(code)
    }
}
