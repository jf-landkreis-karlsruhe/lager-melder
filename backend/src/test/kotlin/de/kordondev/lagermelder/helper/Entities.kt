package de.kordondev.lagermelder.helper

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.rest.model.request.*


class Entities() {
    enum class TShirtSizeMock(val size: String) {
        S164("164"),
        M("M"),
        L("L"),
    }
    companion object {
        fun department(): DepartmentEntry {
            return DepartmentEntry(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com", "", "")
        }

        fun departmentEntry(): DepartmentEntry {
            return DepartmentEntry(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com", "", "")
        }

        fun attendee(): AttendeeEntry {
            return AttendeeEntry(
                10L,
                "att",
                "endee",
                "20-09-2005",
                Food.MEAT,
                TShirtSizeMock.S164.size,
                "",
                "code",
                AttendeeRole.YOUTH,
                department(),
                status = null,
            )
        }

        fun restAttendeeRequest(departmentId: Long = department().id): RestAttendeeRequest {
            return RestAttendeeRequest(
                firstName = "att",
                lastName = "endee",
                departmentId = departmentId,
                birthday = "05-09-2005",
                food = Food.MEAT,
                tShirtSize = TShirtSizeMock.S164.size,
                additionalInformation = "n",
                role = AttendeeRole.YOUTH,
            )

        }

        fun restUserRequest(departmentId: Long = department().id): RestUserRequest {
            return RestUserRequest(
                username = "username@email.de",
                password = "password",
                departmentId = departmentId,
                role = Roles.USER
            )
        }

        fun user(): UserEntry {
            return UserEntry(
                id = 1L,
                role = Roles.USER,
                department = departmentEntry(),
                userName = "user@email.de",
                passWord = "pass"
            )
        }

        fun restDepartmentWithUserRequest(): RestDepartmentWithUserRequest {
            return RestDepartmentWithUserRequest(
                username = "username@email.de",
                departmentName = "department",
                leaderName = "leaderName",
                leaderEMail = "leader@department.de"
            )
        }

        fun restDepartmentRequest(): RestDepartmentRequest {
            return RestDepartmentRequest(
                name = "department",
                leaderEMail = "leader@mail.de",
                leaderName = "leader",
                phoneNumber = "",
                shortName = ""
            )
        }

        fun event(): RestEventRequest {
            return RestEventRequest("event")
        }

    }
}