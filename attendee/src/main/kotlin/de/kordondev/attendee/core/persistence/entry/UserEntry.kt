package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.User
import javax.persistence.*

@Entity
@Table(name="User")
data class UserEntry (

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        val role: Roles = Roles.USER,
        val userName: String,
        val passWord: String,

        @ManyToOne
        @JoinColumn(name = "department")
        val department: DepartmentEntry
) {
    companion object {
        fun to(user: UserEntry): User {
            return User(
                    id = user.id,
                    role = user.role,
                    userName = user.userName,
                    passWord = user.passWord,
                    department = DepartmentEntry.to(user.department)
            )
        }
    }
}

enum class Roles {
    USER, ADMIN
}