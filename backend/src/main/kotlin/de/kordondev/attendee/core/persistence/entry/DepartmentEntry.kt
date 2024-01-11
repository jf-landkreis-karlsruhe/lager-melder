package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewDepartment
import javax.persistence.*

@Entity
@Table(name = "departments")
data class DepartmentEntry (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "name")
        val name: String,

        @Column(name = "leader_name")
        val leaderName: String,

        @Column(name = "leader_email")
        val leaderEMail: String
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

                fun of(department: NewDepartment, id: Long = 0): DepartmentEntry {
                        return DepartmentEntry(
                                id = id,
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