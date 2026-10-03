package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.persistence.entry.BaseAttendeeEntry
import de.kordondev.lagermelder.core.persistence.entry.YouthEntry
import de.kordondev.lagermelder.core.persistence.repository.AttendeeInEventRepository
import de.kordondev.lagermelder.core.persistence.repository.BaseAttendeeRepository
import de.kordondev.lagermelder.core.persistence.repository.YouthsRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.service.helper.TShirtSizeValidator
import de.kordondev.lagermelder.exception.BadRequestException
import de.kordondev.lagermelder.exception.UniqueException
import de.kordondev.lagermelder.exception.WrongTimeException
import de.kordondev.lagermelder.helper.Entities
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.access.AccessDeniedException
import java.util.Optional

class AttendeeServiceTest {
    private lateinit var authorityService: AuthorityService
    private lateinit var settingsService: SettingsService
    private lateinit var tShirtSizeValidator: TShirtSizeValidator
    private lateinit var youthRepository: YouthsRepository
    private lateinit var baseAttendeeRepository: BaseAttendeeRepository
    private lateinit var attendeeInEventRepository: AttendeeInEventRepository
    private lateinit var attendeeService: AttendeeService

    private val attendee = Entities.attendee() as YouthEntry

    @BeforeEach
    fun setUp() {
        authorityService = mock()
        settingsService = mock()
        tShirtSizeValidator = mock()
        youthRepository = mock()
        baseAttendeeRepository = mock()
        attendeeInEventRepository = mock()
        attendeeService =
            AttendeeService(
                authorityService = authorityService,
                settingsService = settingsService,
                tShirtSizeValidator = tShirtSizeValidator,
                youthRepository = youthRepository,
                youthLeaderRepository = mock(),
                childRepository = mock(),
                childLeaderRepository = mock(),
                zKidRepository = mock(),
                helperRepository = mock(),
                baseAttendeeRepository = baseAttendeeRepository,
                eventRepository = attendeeInEventRepository,
            )

        whenever(settingsService.canBeEdited(any())).doReturn(true)
        whenever(youthRepository.save(any<YouthEntry>())).doAnswer { it.arguments[0] as YouthEntry }
    }

    @Test
    fun createAttendeeGeneratesIdAndCode() {
        val created = attendeeService.createAttendee(attendee)

        assertThat(created.id).isNotEqualTo(attendee.id)
        assertThat(created.code).isNotEqualTo(attendee.code).hasSize(8)
        verify(youthRepository).save(any<YouthEntry>())
    }

    @Test
    fun createAttendeeChecksAuthorityForDepartment() {
        whenever(authorityService.hasAuthority(attendee, AuthorityService.LK_KARLSRUHE_ALLOWED))
            .doThrow(AccessDeniedException("denied"))

        assertThatThrownBy { attendeeService.createAttendee(attendee) }.isInstanceOf(AccessDeniedException::class.java)
        verify(youthRepository, never()).save(any<YouthEntry>())
    }

    @Test
    fun createAttendeeFailsAfterRegistrationEnd() {
        whenever(settingsService.canBeEdited(any())).doReturn(false)

        assertThatThrownBy { attendeeService.createAttendee(attendee) }.isInstanceOf(WrongTimeException::class.java)
    }

    @Test
    fun createAttendeeRequiresUniqueNamePerDepartment() {
        whenever(
            baseAttendeeRepository.findByDepartmentAndFirstNameAndLastName(attendee.department, attendee.firstName, attendee.lastName),
        ).doReturn(BaseAttendeeEntry.of(attendee))

        assertThatThrownBy { attendeeService.createAttendee(attendee) }.isInstanceOf(UniqueException::class.java)
    }

    @Test
    fun saveAttendeeAllowsKeepingOwnName() {
        whenever(
            baseAttendeeRepository.findByDepartmentAndFirstNameAndLastName(attendee.department, attendee.firstName, attendee.lastName),
        ).doReturn(BaseAttendeeEntry.of(attendee))
        whenever(baseAttendeeRepository.findById(attendee.id)).doReturn(Optional.of(BaseAttendeeEntry.of(attendee)))
        whenever(youthRepository.findById(attendee.id)).doReturn(Optional.of(attendee))
        whenever(authorityService.hasAuthority(attendee, AuthorityService.LK_KARLSRUHE_ALLOWED)).doReturn(attendee)
        whenever(authorityService.hasAuthority(attendee, AuthorityService.SPECIALIZED_FIELD_DIRECTOR_ALLOWED)).doReturn(attendee)

        val saved = attendeeService.saveAttendee(attendee.id, attendee.copy(additionalInformation = "changed"))

        assertThat(saved.additionalInformation).isEqualTo("changed")
        assertThat(saved.code).isEqualTo(attendee.code)
    }

    @Test
    fun createAttendeeValidatesTShirtSize() {
        whenever(tShirtSizeValidator.validate(attendee.tShirtSize)).doThrow(BadRequestException("invalid"))

        assertThatThrownBy { attendeeService.createAttendee(attendee) }.isInstanceOf(BadRequestException::class.java)
    }
}
