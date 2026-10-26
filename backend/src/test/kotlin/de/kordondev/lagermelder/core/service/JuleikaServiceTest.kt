package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.juleika.JuleikaValidationResult
import de.kordondev.lagermelder.core.juleika.JuleikaValidationService
import de.kordondev.lagermelder.core.persistence.entry.AttendeeRole
import de.kordondev.lagermelder.core.persistence.entry.ChildLeaderEntry
import de.kordondev.lagermelder.core.persistence.entry.Food
import de.kordondev.lagermelder.core.persistence.entry.YouthLeaderEntry
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.helper.Entities
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDate

class JuleikaServiceTest {
    private lateinit var authorityService: AuthorityService
    private lateinit var juleikaValidationService: JuleikaValidationService
    private lateinit var juleikaService: JuleikaService

    private val apiExpireDate = LocalDate.of(2028, 3, 31)
    private val clientExpireDate = LocalDate.of(2099, 1, 1)

    private val leader =
        YouthLeaderEntry(
            firstName = "Lea",
            lastName = "Leiter",
            birthday = "2000-01-01",
            food = Food.MEAT,
            tShirtSize = Entities.TShirtSizeMock.L.size,
            additionalInformation = "",
            code = "",
            role = AttendeeRole.YOUTH_LEADER,
            department = Entities.department(),
            status = null,
            juleikaNumber = "12340",
            juleikaExpireDate = clientExpireDate,
        )

    @BeforeEach
    fun setUp() {
        authorityService = mock()
        juleikaValidationService = mock()
        juleikaService = JuleikaService(authorityService, juleikaValidationService)
        whenever(authorityService.hasRole(AuthorityService.USER_ALLOWED)).doReturn(true)
        whenever(juleikaValidationService.validate("12340", "Leiter")).doReturn(JuleikaValidationResult(true, apiExpireDate))
        whenever(juleikaValidationService.validate("99999", "Leiter")).doReturn(JuleikaValidationResult(false, null))
    }

    @Test
    fun validateReturnsResultOfApi() {
        assertThat(juleikaService.validate("12340", "Leiter")).isEqualTo(JuleikaValidationResult(true, apiExpireDate))
    }

    @Test
    fun validateIsDeniedWithoutRole() {
        whenever(authorityService.hasRole(AuthorityService.USER_ALLOWED)).doReturn(false)

        assertThatThrownBy { juleikaService.validate("12340", "Leiter") }.isInstanceOf(AccessDeniedException::class.java)
        verify(juleikaValidationService, never()).validate(any(), any())
    }

    @Test
    fun replacesExpireDateSentByClientWithCheckedOne() {
        val checked = juleikaService.withCheckedExpireDate(leader) as YouthLeaderEntry

        assertThat(checked.juleikaExpireDate).isEqualTo(apiExpireDate)
    }

    @Test
    fun removesExpireDateOfInvalidJuleika() {
        val checked = juleikaService.withCheckedExpireDate(leader.copy(juleikaNumber = "99999")) as YouthLeaderEntry

        assertThat(checked.juleikaExpireDate).isNull()
    }

    @Test
    fun checksChildLeadersToo() {
        val childLeader =
            ChildLeaderEntry(
                firstName = leader.firstName,
                lastName = leader.lastName,
                birthday = leader.birthday,
                food = leader.food,
                tShirtSize = leader.tShirtSize,
                additionalInformation = "",
                code = "",
                role = AttendeeRole.CHILD_LEADER,
                department = leader.department,
                status = null,
                juleikaNumber = "12340",
                juleikaExpireDate = clientExpireDate,
            )

        val checked = juleikaService.withCheckedExpireDate(childLeader) as ChildLeaderEntry

        assertThat(checked.juleikaExpireDate).isEqualTo(apiExpireDate)
    }

    @Test
    fun keepsStoredExpireDateWhenNumberAndLastNameAreUnchanged() {
        val stored = leader.copy(juleikaExpireDate = LocalDate.of(2027, 6, 1))

        val checked = juleikaService.withCheckedExpireDate(leader, stored) as YouthLeaderEntry

        assertThat(checked.juleikaExpireDate).isEqualTo(stored.juleikaExpireDate)
        verify(juleikaValidationService, never()).validate(any(), any())
    }

    @Test
    fun checksAgainWhenNumberChanged() {
        val stored = leader.copy(juleikaNumber = "11110", juleikaExpireDate = LocalDate.of(2027, 6, 1))

        val checked = juleikaService.withCheckedExpireDate(leader, stored) as YouthLeaderEntry

        assertThat(checked.juleikaExpireDate).isEqualTo(apiExpireDate)
    }

    @Test
    fun leavesOtherAttendeesUnchanged() {
        val youth = Entities.attendee()

        assertThat(juleikaService.withCheckedExpireDate(youth)).isSameAs(youth)
        verify(juleikaValidationService, never()).validate(any(), any())
    }
}
