package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.NewUser
import de.kordondev.attendee.core.model.User
import javax.persistence.*

@Entity
@Table(name = "users")
data class UserEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "role")
    val role: String = Roles.USER,

    @Column(name = "username")
    val userName: String,

    @Column(name = "password")
    val passWord: String,

    @ManyToOne
    @JoinColumn(name = "department_id")
    val department: DepartmentEntry
) {
    companion object {
        fun to(user: UserEntry): User {
            return User(
                id = user.id,
                role = Roles.filterToExistingRoles(user.role),
                userName = user.userName,
                passWord = user.passWord,
                department = DepartmentEntry.to(user.department)
            )
        }

        fun of(user: NewUser): UserEntry {
            return UserEntry(
                role = user.role,
                userName = user.userName,
                passWord = user.passWord,
                department = DepartmentEntry.of(user.department)
            )
        }

        fun of(user: User): UserEntry {
            return UserEntry(
                id = user.id,
                role = user.role,
                userName = user.userName,
                passWord = user.passWord,
                department = DepartmentEntry.of(user.department)
            )
        }
    }
}

class Roles {
    companion object {
        const val USER = "USER"
        const val ADMIN = "ADMIN"
        const val SPECIALIZED_FIELD_DIRECTOR = "SPECIALIZED_FIELD_DIRECTOR"
        val existingRoles = listOf(ADMIN, SPECIALIZED_FIELD_DIRECTOR, USER)

        fun filterToExistingRoles(role: String): String {
            if (existingRoles.contains(role)) {
                return role;
            }
            return USER
        }
    }

}