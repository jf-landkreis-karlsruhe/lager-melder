package de.kordondev.attendee.core.security

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class HasAuthorityTest {
    private lateinit var authorityService: AuthorityService;

    @BeforeEach
    fun setupMock() {
        MockitoAnnotations.initMocks(this)
        val authentication = mock(Authentication::class.java);
        val securityContext = mock(SecurityContext::class.java)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)
        `when`(SecurityContextHolder.getContext().authentication).thenReturn(authentication)

        authorityService = AuthorityService()
    }


    // Department
    @Test
    fun isUser_has_DepartmentId_department_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(
            authorityService.hasAuthorityFilter(
                dep,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun `isUser_doesn't_has_DepartmentId_department_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(
            authorityService.hasAuthorityFilter(
                dep,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(false)
    }

    @Test
    fun isSpecializedFieldDirector_has_DepartmentId_department_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(
            authorityService.hasAuthorityFilter(
                dep,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun `isSpecializedFieldDirector_doesn't_has_DepartmentId_department_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(
            authorityService.hasAuthorityFilter(
                dep,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun isAdmin_has_DepartmentId_department_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(
            authorityService.hasAuthorityFilter(
                dep,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_department_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(
            authorityService.hasAuthorityFilter(
                dep,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }


    @Test
    fun isUser_has_DepartmentId_department_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }

    @Test
    fun `isUser_doesn't_has_DepartmentId_department_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        Assertions.assertThrows(
            AccessDeniedException::class.java,
        ) { authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    @Test
    fun isSpecializedFieldDirector_has_DepartmentId_department_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }

    @Test
    fun `isSpecializedFieldDirector_doesn't_has_DepartmentId_department_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }

    @Test
    fun isAdmin_has_DepartmentId_department_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_department_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }


    // Attendee
    @Test
    fun isUser_has_DepartmentId_attendee_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthorityFilter(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun `isUser_doesn't_has_DepartmentId_attendee_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthorityFilter(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(false)
    }

    @Test
    fun isSpecializedFieldDirector_has_DepartmentId_attendee_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthorityFilter(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun `isSpecializedFieldDirector_doesn't_has_DepartmentId_attendee_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthorityFilter(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun isAdmin_has_DepartmentId_attendee_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthorityFilter(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_attendee_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthorityFilter(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(true)
    }

    @Test
    fun isUser_has_DepartmentId_attendee_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthority(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(attendee)
    }

    @Test
    fun `isUser_doesn't_has_DepartmentId_attendee_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        Assertions.assertThrows(
            AccessDeniedException::class.java
        ) { authorityService.hasAuthority(attendee, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    @Test
    fun isSpecializedFieldDirector_has_DepartmentId_attendee_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthority(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(attendee)
    }

    @Test
    fun `isSpecializedFieldDirector_doesn't_has_DepartmentId_attendee_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthority(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(attendee)
    }

    @Test
    fun isAdmin_has_DepartmentId_attendee_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthority(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(attendee)
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_attendee_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(
            10L,
            "att",
            "endee",
            "20-09-2005",
            Food.MEAT,
            TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
            "",
            AttendeeRole.YOUTH,
            dep
        )
        assertThat(
            authorityService.hasAuthority(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(attendee)
    }

    // specializedFieldDirector
    @Test
    fun `isUser__isSpecializedFieldDirectorFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        assertThat(authorityService.isSpecializedFieldDirectorFilter()).isEqualTo(false)
    }

    @Test
    fun `isSpecializedFieldDirector__isSpecializedFieldDirectorFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        assertThat(authorityService.isSpecializedFieldDirectorFilter()).isEqualTo(true)
    }

    @Test
    fun `isAdmin__isSpecializedFieldDirectorFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        assertThat(authorityService.isSpecializedFieldDirectorFilter()).isEqualTo(true)
    }

    @Test
    fun `isUser__isSpecializedFieldDirector`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        Assertions.assertThrows(
            AccessDeniedException::class.java
        ) {
            authorityService.isSpecializedFieldDirector()
        }
    }

    fun `isSpecializedFieldDirector__isSpecializedFieldDirector`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        assertThat(authorityService.isSpecializedFieldDirector()).isEqualTo(Unit)
    }

    @Test
    fun `isAdmin__isSpecializedFieldDirector`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        assertThat(authorityService.isSpecializedFieldDirector()).isEqualTo(Unit)
    }

    // admin
    @Test
    fun `isUser__isAdminFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        assertThat(authorityService.isAdminFilter()).isEqualTo(false)
    }

    @Test
    fun `isSpecializedFieldDirector__isAdminFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        assertThat(authorityService.isAdminFilter()).isEqualTo(false)
    }

    @Test
    fun `isAdmin__isAdminFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        assertThat(authorityService.isAdminFilter()).isEqualTo(true)
    }

    @Test
    fun `isUser__isAdmin`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
            )
        )

        Assertions.assertThrows(
            AccessDeniedException::class.java
        ) { authorityService.isAdmin() }

    }

    @Test
    fun `isSpecializedFieldDirector__isAdmin`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        Assertions.assertThrows(
            AccessDeniedException::class.java
        ) { authorityService.isAdmin() }
    }

    @Test
    fun `isAdmin__isAdmin`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
            )
        )

        assertThat(authorityService.isAdmin()).isEqualTo(Unit)
    }
}