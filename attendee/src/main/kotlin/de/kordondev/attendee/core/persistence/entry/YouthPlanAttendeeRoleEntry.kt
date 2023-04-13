package de.kordondev.attendee.core.persistence.entry

import org.hibernate.Hibernate
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import javax.persistence.*

@Entity
@Table(name = "youthPlanAttendeeRoles")
data class YouthPlanAttendeeRoleEntry(

    @Id
    @Column(name = "attendee_id")
    val attendeeId: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "attendee_id")
    val attendee: AttendeeEntry,


    @Column(name = "department_id")
    val departmentId: Long,

    @Column(name = "youth_plan_role")
    @Enumerated(EnumType.STRING)
    var youthPlanRole: AttendeeRole,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as YouthPlanAttendeeRoleEntry

        return attendeeId != null && attendeeId == other.attendeeId
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(attendeeId = $attendeeId )"
    }
}
