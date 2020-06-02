package de.kordondev.attendee.core.security

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
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
    @Before
    fun setupMock() {
        MockitoAnnotations.initMocks(this)
        val authentication = mock(Authentication::class.java);
        val securityContext = mock(SecurityContext::class.java )
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)
        `when`(SecurityContextHolder.getContext().authentication).thenReturn(authentication)

        authorityService = AuthorityService()
    }


    // Department
    @Test
    fun isUser_has_DepartmentId_department_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
            SimpleGrantedAuthority("1"),
            SimpleGrantedAuthority(Roles.USER.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthorityFilter(dep)).isEqualTo(true)
    }

    @Test
    fun `isUser_doesn't_has_DepartmentId_department_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthorityFilter(dep)).isEqualTo(false)
    }

    @Test
    fun isAdmin_has_DepartmentId_department_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthorityFilter(dep)).isEqualTo(true)
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_department_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthorityFilter(dep)).isEqualTo(true)
    }


    @Test
    fun isUser_has_DepartmentId_department_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthority(dep)).isEqualTo(dep)
    }

    @Test(expected = AccessDeniedException::class)
    fun `isUser_doesn't_has_DepartmentId_department_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        authorityService.hasAuthority(dep)
    }

    @Test
    fun isAdmin_has_DepartmentId_department_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthority(dep)).isEqualTo(dep)
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_department_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        assertThat(authorityService.hasAuthority(dep)).isEqualTo(dep)
    }



    // Attendee
    @Test
    fun isUser_has_DepartmentId_attendee_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(10L, "att", "endee", "20-09-2005", Food.MEAT, TShirtSize.ONE_HUNDRED_SIXTY_FOUR, "", AttendeeRole.YOUTH, dep)
        assertThat(authorityService.hasAuthorityFilter(attendee)).isEqualTo(true)
    }

    @Test
    fun `isUser_doesn't_has_DepartmentId_attendee_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(10L, "att", "endee", "20-09-2005", Food.MEAT, TShirtSize.ONE_HUNDRED_SIXTY_FOUR, "", AttendeeRole.YOUTH, dep)
        assertThat(authorityService.hasAuthorityFilter(attendee)).isEqualTo(false)
    }

    @Test
    fun isAdmin_has_DepartmentId_attendee_hasAuthorityFilter() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(10L, "att", "endee", "20-09-2005", Food.MEAT, TShirtSize.ONE_HUNDRED_SIXTY_FOUR, "", AttendeeRole.YOUTH, dep)
        assertThat(authorityService.hasAuthorityFilter(attendee)).isEqualTo(true)
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_attendee_hasAuthorityFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(10L, "att", "endee", "20-09-2005", Food.MEAT, TShirtSize.ONE_HUNDRED_SIXTY_FOUR, "", AttendeeRole.YOUTH, dep)
        assertThat(authorityService.hasAuthorityFilter(attendee)).isEqualTo(true)
    }


    @Test
    fun isUser_has_DepartmentId_attendee_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(10L, "att", "endee", "20-09-2005", Food.MEAT, TShirtSize.ONE_HUNDRED_SIXTY_FOUR, "", AttendeeRole.YOUTH, dep)
        assertThat(authorityService.hasAuthority(attendee)).isEqualTo(attendee)
    }

    @Test(expected = AccessDeniedException::class)
    fun `isUser_doesn't_has_DepartmentId_attendee_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(10L, "att", "endee", "20-09-2005", Food.MEAT, TShirtSize.ONE_HUNDRED_SIXTY_FOUR, "", AttendeeRole.YOUTH, dep)
        authorityService.hasAuthority(attendee)
    }

    @Test
    fun isAdmin_has_DepartmentId_attendee_hasAuthority() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("1"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(10L, "att", "endee", "20-09-2005", Food.MEAT, TShirtSize.ONE_HUNDRED_SIXTY_FOUR, "", AttendeeRole.YOUTH, dep)
        assertThat(authorityService.hasAuthority(attendee)).isEqualTo(attendee)
    }

    @Test
    fun `isAdmin_doesn't_has_DepartmentId_attendee_hasAuthority`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        val dep = Department(id = 1L, name = "Dep", leaderName = "depLeader", leaderEMail = "l@dep.com")
        val attendee = Attendee(10L, "att", "endee", "20-09-2005", Food.MEAT, TShirtSize.ONE_HUNDRED_SIXTY_FOUR, "", AttendeeRole.YOUTH, dep)
        assertThat(authorityService.hasAuthority(attendee)).isEqualTo(attendee)
    }

    // admin
    @Test
    fun `isUser__isAdminFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        assertThat(authorityService.isAdminFilter()).isEqualTo(false)
    }

    @Test
    fun `isAdmin__isAdminFilter`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        assertThat(authorityService.isAdminFilter()).isEqualTo(true)
    }

    @Test(expected = AccessDeniedException::class)
    fun `isUser__isAdmin`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.USER.toString())
        ))

        authorityService.isAdmin()
    }

    @Test
    fun `isAdmin__isAdmin`() {
        `when`(SecurityContextHolder.getContext().authentication.authorities).thenReturn(listOf(
                SimpleGrantedAuthority("0"),
                SimpleGrantedAuthority(Roles.ADMIN.toString())
        ))

        assertThat(authorityService.isAdmin()).isEqualTo(Unit)
    }
}