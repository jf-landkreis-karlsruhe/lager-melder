package de.kordondev.lagermelder.core.persistence.entry

import de.kordondev.lagermelder.core.model.NewUser
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
        fun of(user: NewUser): UserEntry {
            return UserEntry(
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
        private val existingRoles = listOf(ADMIN, SPECIALIZED_FIELD_DIRECTOR, USER)

        fun filterToExistingRoles(role: String): String {
            if (existingRoles.contains(role)) {
                return role
            }
            return USER
        }
    }

}