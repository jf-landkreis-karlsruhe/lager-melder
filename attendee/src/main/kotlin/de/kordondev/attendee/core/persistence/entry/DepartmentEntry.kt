package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Department
import javax.persistence.*

@Entity
@Table(name = "Department")
data class DepartmentEntry (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "name")
        val name: String,

        @Column(name = "leaderName")
        val leaderName: String,

        @Column(name = "leaderEMail")
        val leaderEMail: String,

        @OneToMany(mappedBy = "department")
        val attendees: List<AttendeeEntry> = listOf()
) {
        companion object {
                fun of(department: Department): DepartmentEntry {
                        return DepartmentEntry(
                                id = department.id,
                                name = department.name,
                                leaderName = department.leaderName,
                                leaderEMail = department.leaderEMail
                        )
                }

                fun to(departmentEntry: DepartmentEntry) = Department(
                        id = departmentEntry.id,
                        name = departmentEntry.name,
                        leaderName = departmentEntry.leaderName,
                        leaderEMail = departmentEntry.leaderEMail
                )
        }
}