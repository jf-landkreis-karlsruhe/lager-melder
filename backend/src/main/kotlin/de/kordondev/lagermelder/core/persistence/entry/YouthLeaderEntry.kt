package de.kordondev.lagermelder.core.persistence.entry

import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import jakarta.persistence.*
import org.hibernate.Hibernate
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "base_attendees")
@SecondaryTable(name = "youth_leaders", pkJoinColumns = [PrimaryKeyJoinColumn(name = "base_attendee_id")])
data class YouthLeaderEntry(

    @Id
    override val id: String = UUID.randomUUID().toString(),

    @Column(name = "first_name")
    override val firstName: String,

    @Column(name = "last_name")
    override val lastName: String,

    @Column(name = "birthday", table = "youth_leaders")
    val birthday: String,

    @Column(name = "food")
    @Enumerated(EnumType.STRING)
    override val food: Food,

    @Column(name = "t_shirt_size")
    override val tShirtSize: String,

    @Column(name = "additional_information")
    override val additionalInformation: String,

    @Column(name = "code", unique = true)
    override val code: String,

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    override val role: AttendeeRole,

    @ManyToOne
    @JoinColumn(name = "department_id")
    override val department: DepartmentEntry,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    override val status: AttendeeStatus?,

    @Column(name = "created_at")
    override val createdAt: Instant = Instant.now(),

    @Column(name = "juleika_number", table = "youth_leaders")
    val juleikaNumber: String,

    @Column(name = "juleika_expire_date", table = "youth_leaders")
    val juleikaExpireDate: LocalDate?,

    ) : Attendee {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as YouthLeaderEntry

        return id != null && id == other.id
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun hashCode(): Int {
        return Objects.hash(code)
    }
}