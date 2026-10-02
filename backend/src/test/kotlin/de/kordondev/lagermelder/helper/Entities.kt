package de.kordondev.lagermelder.helper

import de.kordondev.lagermelder.core.persistence.entry.*
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.rest.model.request.*
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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
                "2005-09-20",
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
                birthday = "2005-09-05",
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

        fun eventEntry(type: EventType = EventType.Location): EventEntry =
            EventEntry(id = 1L, name = "event", code = "eventCode", type = type, trashed = false)

        fun restSettingsRequest(): RestSettingsRequest {
            val settings = settings()
            return RestSettingsRequest(
                registrationEnd = settings.registrationEnd,
                hostCity = settings.hostCity,
                eventStart = settings.eventStart,
                eventEnd = settings.eventEnd,
                eventName = settings.eventName,
                eventAddress = settings.eventAddress,
                organizer = settings.organizer,
                organisationAddress = settings.organisationAddress,
                moneyPerYouthLoader = settings.moneyPerYouthLoader,
                startDownloadRegistrationFiles = settings.startDownloadRegistrationFiles,
                childGroupsRegistrationEnd = settings.childGroupsRegistrationEnd,
                helpersRegistrationEnd = settings.helpersRegistrationEnd,
                numberOfDuties = settings.numberOfDuties,
            )
        }

        fun settings(): SettingsEntry {
            val now = Instant.now()
            return SettingsEntry(
                id = 1L,
                registrationEnd = now.plus(10, ChronoUnit.DAYS),
                hostCity = "city",
                eventStart = LocalDate.now().plusDays(60),
                eventEnd = LocalDate.now().plusDays(65),
                eventName = "event",
                eventAddress = "address",
                organizer = "organizer",
                organisationAddress = "organisation",
                moneyPerYouthLoader = "8,99",
                startDownloadRegistrationFiles = now.plus(20, ChronoUnit.DAYS),
                childGroupsRegistrationEnd = now.plus(10, ChronoUnit.DAYS),
                helpersRegistrationEnd = now.plus(10, ChronoUnit.DAYS),
                numberOfDuties = 0,
            )
        }
    }
}
