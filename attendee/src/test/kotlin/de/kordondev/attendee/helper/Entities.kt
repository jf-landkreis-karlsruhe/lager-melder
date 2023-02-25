package de.kordondev.attendee.helper

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.User
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import de.kordondev.attendee.rest.model.request.*
import java.time.ZonedDateTime


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
                TShirtSize.S164,
                "",
                AttendeeRole.YOUTH,
                department(),
                "code",
                status = null,
                specialLeave = false
            )
        }

        fun restAttendeeRequest(departmentId: Long = department().id): RestAttendeeRequest {
            return RestAttendeeRequest(
                firstName = "att",
                lastName = "endee",
                departmentId = departmentId,
                birthday = "05-09-2005",
                food = Food.MEAT,
                tShirtSize = TShirtSize.S164.toString(),
                additionalInformation = "n",
                role = AttendeeRole.YOUTH,
                specialLeave = false
            )

        }

        fun restUserRequest(departmentId: Long = department().id): RestUserRequest {
            return RestUserRequest(
                username = "username",
                password = "password",
                departmentId = departmentId,
                role = Roles.USER
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

        fun restDepartmentRequest(): RestDepartmentRequest {
            return RestDepartmentRequest(
                name = "department",
                leaderEMail = "leader@mail.de",
                leaderName = "leader"
            )
        }

        fun event(): RestEventRequest {
            return RestEventRequest("event")
        }

        fun restPCRTestSeriesRequest(
            pcrTestCodes: List<String> = listOf(
                "testcode1",
                "testcode2"
            )
        ): RestPCRTestSeriesRequest {
            return RestPCRTestSeriesRequest(
                name = "test series",
                start = ZonedDateTime.now(),
                end = ZonedDateTime.now().plusDays(1),
                testCodes = pcrTestCodes
            )
        }
    }
}