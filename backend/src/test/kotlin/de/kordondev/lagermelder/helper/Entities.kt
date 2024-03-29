package de.kordondev.lagermelder.helper

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.rest.model.request.*
import java.time.ZonedDateTime


class Entities() {
    companion object {
        fun department(): DepartmentEntry {
            return DepartmentEntry(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        }

        fun departmentEntry(): DepartmentEntry {
            return DepartmentEntry(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        }

        fun attendee(): AttendeeEntry {
            return AttendeeEntry(
                10L,
                "att",
                "endee",
                "20-09-2005",
                Food.MEAT,
                TShirtSize.S164,
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
                tShirtSize = TShirtSize.S164.toString(),
                additionalInformation = "n",
                role = AttendeeRole.YOUTH,
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

        fun user(): UserEntry {
            return UserEntry(
                id = 1L,
                role = Roles.USER,
                department = departmentEntry(),
                userName = "user",
                passWord = "pass"
            )
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