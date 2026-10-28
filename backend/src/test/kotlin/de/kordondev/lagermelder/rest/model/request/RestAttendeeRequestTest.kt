package de.kordondev.lagermelder.rest.model.request

import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry
import de.kordondev.lagermelder.helper.Entities
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

class RestAttendeeRequestTest {
    private val department = Entities.department()

    @Test
    fun mapsJuleikaExpireDateOfLeader() {
        val request = Entities.restAttendeeRequest().copy(role = AttendeeRole.YOUTH_LEADER, juleikaExpireDate = "2026-12-08")

        val attendee = RestAttendeeRequest.to(request, department, null, emptySet()) as YouthLeaderEntry

        assertThat(attendee.juleikaExpireDate).isEqualTo(LocalDate.of(2026, 12, 8))
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = ["", " "])
    fun mapsMissingJuleikaExpireDateToNull(date: String?) {
        val request = Entities.restAttendeeRequest().copy(role = AttendeeRole.YOUTH_LEADER, juleikaExpireDate = date)

        val attendee = RestAttendeeRequest.to(request, department, null, emptySet()) as YouthLeaderEntry

        assertThat(attendee.juleikaExpireDate).isNull()
    }
}
