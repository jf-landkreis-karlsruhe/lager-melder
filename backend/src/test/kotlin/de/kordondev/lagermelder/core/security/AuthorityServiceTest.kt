package de.kordondev.lagermelder.core.security

import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants.DEPARTMENT_ID_PREFIX
import de.kordondev.lagermelder.core.security.SecurityConstants.ROLE_PREFIX
import de.kordondev.lagermelder.core.security.SecurityConstants.USER_ID_PREFIX
import de.kordondev.lagermelder.helper.Entities
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class AuthorityServiceTest {
    private lateinit var authorityService: AuthorityService

    @BeforeEach
    fun setupMock() {
        MockitoAnnotations.initMocks(this)
        val authentication = mock(Authentication::class.java)
        val securityContext = mock(SecurityContext::class.java)
        `when`(securityContext.authentication).thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)
        `when`(SecurityContextHolder.getContext().authentication).thenReturn(authentication)

        authorityService = AuthorityService()
    }


    // Department
    @Test
    fun isUser_has_DepartmentId_department_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val dep = Entities.department()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val dep = Entities.department()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        val dep = Entities.department()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        val dep = Entities.department()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        val dep = Entities.department()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        val dep = Entities.department()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val dep = Entities.department()
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }

    @Test
    fun `isUser_doesn't_has_DepartmentId_department_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val dep = Entities.department()
        Assertions.assertThrows(
            AccessDeniedException::class.java,
        ) { authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    @Test
    fun isSpecializedFieldDirector_has_DepartmentId_department_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        val dep = Entities.department()
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }

    @Test
    fun `isSpecializedFieldDirector_doesn't_has_DepartmentId_department_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        val dep = Entities.department()
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }

    @Test
    fun isAdmin_has_DepartmentId_department_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        val dep = Entities.department()
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_department_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        val dep = Entities.department()
        assertThat(authorityService.hasAuthority(dep, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))).isEqualTo(
            dep
        )
    }


    // Attendee
    @Test
    fun isUser_has_DepartmentId_attendee_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val attendee = Entities.attendee()
        Assertions.assertThrows(
            AccessDeniedException::class.java
        ) { authorityService.hasAuthority(attendee, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    @Test
    fun isSpecializedFieldDirector_has_DepartmentId_attendee_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        val attendee = Entities.attendee()
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        val attendee = Entities.attendee()
        assertThat(
            authorityService.hasAuthority(
                attendee,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(attendee)
    }

    // specializedFieldDirector
    @Test
    fun isUser__isSpecializedFieldDirectorFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        assertThat(authorityService.isSpecializedFieldDirectorFilter()).isEqualTo(false)
    }

    @Test
    fun `isSpecializedFieldDirector__isSpecializedFieldDirectorFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        assertThat(authorityService.isSpecializedFieldDirectorFilter()).isEqualTo(true)
    }

    @Test
    fun `isAdmin__isSpecializedFieldDirectorFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        assertThat(authorityService.isSpecializedFieldDirectorFilter()).isEqualTo(true)
    }

    @Test
    fun `isUser__isSpecializedFieldDirector`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        assertThat(authorityService.isSpecializedFieldDirector()).isEqualTo(Unit)
    }

    @Test
    fun `isAdmin__isSpecializedFieldDirector`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        assertThat(authorityService.isSpecializedFieldDirector()).isEqualTo(Unit)
    }

    // admin
    @Test
    fun `isUser__isAdminFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        assertThat(authorityService.isAdminFilter()).isEqualTo(false)
    }

    @Test
    fun `isSpecializedFieldDirector__isAdminFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        )

        assertThat(authorityService.isAdminFilter()).isEqualTo(false)
    }

    @Test
    fun `isAdmin__isAdminFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        assertThat(authorityService.isAdminFilter()).isEqualTo(true)
    }

    @Test
    fun `isUser__isAdmin`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

    }

    @Test
    fun `isSpecializedFieldDirector__isAdmin`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR)
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
                SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN)
            )
        )

        assertThat(authorityService.isAdmin()).isEqualTo(Unit)
    }


    fun `isUser__hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(USER_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER.toString())
            )
        )

        val user = Entities.user()
        Assertions.assertThrows(
            AccessDeniedException::class.java,
        ) { authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)) }
    }

    @Test
    fun `isUserWithSameUserID__hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(USER_ID_PREFIX + "1"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.USER)
            )
        )

        val user = Entities.user()
        assertThat(
            authorityService.hasAuthority(
                user,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(user)
    }

    @Test
    fun `isSepcializedFieldDirector__hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(USER_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR.toString())
            )
        )

        val user = Entities.user()
        assertThat(
            authorityService.hasAuthority(
                user,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(user)
    }

    @Test
    fun `isAdmin__hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority(USER_ID_PREFIX + "0"),
                SimpleGrantedAuthority(ROLE_PREFIX + Roles.ADMIN.toString())
            )
        )

        val user = Entities.user()
        assertThat(
            authorityService.hasAuthority(
                user,
                listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR)
            )
        ).isEqualTo(user)
    }
}