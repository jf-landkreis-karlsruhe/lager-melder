package de.kordondev.lagermelder.helper

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.rest.model.request.*
import java.time.LocalDate
import java.util.*

class Entities {
    enum class TShirtSizeMock(
        val size: String,
    ) {
        S("S"),
        M("M"),
        L("L"),
    }

    companion object {
        fun department(): DepartmentEntry =
            DepartmentEntry(
                id = 1L,
                name = "Dep",
                leaderName = "depLeader",
                leaderEMail = "l@dep.com",
                "",
                "",
                emptySet(),
                "",
                paused = false,
                emptySet(),
                null,
            )

        fun departmentEntry(): DepartmentEntry =
            DepartmentEntry(
                id = 1L,
                name = "Dep",
                leaderName = "depLeader",
                leaderEMail = "l@dep.com",
                "",
                "",
                emptySet(),
                "",
                paused = false,
                emptySet(),
                null,
            )

        fun attendee(): Attendee =
            YouthEntry(
                UUID.randomUUID().toString(),
                "att",
                "endee",
                "20-09-2005",
                Food.MEAT,
                TShirtSizeMock.S.size,
                "",
                "code",
                AttendeeRole.YOUTH,
                department(),
                status = null,
            )

        fun restAttendeeRequest(departmentId: Long = department().id): RestAttendeeRequest =
            RestAttendeeRequest(
                firstName = "att",
                lastName = "endee",
                departmentId = departmentId,
                birthday = "05-09-2005",
                food = Food.MEAT,
                tShirtSize = TShirtSizeMock.S.size,
                additionalInformation = "n",
                role = AttendeeRole.YOUTH,
                juleikaNumber = "12345678",
                juleikaExpireDate = LocalDate.of(2099, 5, 5).toString(),
                partOfDepartmentId = departmentId,
                helperDays = emptySet(),
            )

        fun restUserRequest(departmentId: Long = department().id): RestUserRequest =
            RestUserRequest(
                username = "username@email.de",
                password = "password",
                departmentId = departmentId,
                role = Roles.USER,
            )

        fun user(): UserEntry =
            UserEntry(
                id = 1L,
                role = Roles.USER,
                department = departmentEntry(),
                userName = "user@email.de",
                passWord = "pass",
            )

        fun restDepartmentWithUserRequest(): RestDepartmentWithUserRequest =
            RestDepartmentWithUserRequest(
                username = "username@email.de",
                departmentName = "department",
                leaderName = "leaderName",
                leaderEMail = "leader@department.de",
                features = setOf(DepartmentFeatures.CHILD_GROUPS, DepartmentFeatures.YOUTH_GROUPS),
            )

        fun restDepartmentRequest(): RestDepartmentRequest =
            RestDepartmentRequest(
                name = "department",
                leaderEMail = "leader@mail.de",
                leaderName = "leader",
                phoneNumber = "",
                shortName = "",
                features = emptySet(),
                headDepartmentName = "",
                paused = false,
            )

        fun event(): RestEventRequest = RestEventRequest("event")
    }
}
