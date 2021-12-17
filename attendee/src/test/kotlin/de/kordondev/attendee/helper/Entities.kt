package de.kordondev.attendee.helper

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.User
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import de.kordondev.attendee.rest.model.request.RestDepartmentWithUserRequest


class Entities() {
    companion object {
        fun department(): Department {
            return Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        }

        fun attendee(): Attendee {
            return Attendee(
                10L,
                "att",
                "endee",
                "20-09-2005",
                Food.MEAT,
                TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
                "",
                AttendeeRole.YOUTH,
                department(),
                "code"
            )
        }

        fun user(): User {
            return User(id = 1L, role = Roles.USER, department = department(), userName = "user", passWord = "pass")
        }

        fun restDepartmentWithUserRequest(): RestDepartmentWithUserRequest {
            return RestDepartmentWithUserRequest(
                username = "username",
                departmentName = "department",
                leaderName = "leaderName",
                leaderEMail = "leader@department.de"
            )
        }
    }


}