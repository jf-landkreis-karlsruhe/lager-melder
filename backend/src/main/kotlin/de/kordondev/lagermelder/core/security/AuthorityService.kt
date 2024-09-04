package de.kordondev.lagermelder.core.security

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.TentsEntity
import de.kordondev.lagermelder.core.persistence.entry.UserEntry
import de.kordondev.lagermelder.core.persistence.entry.interfaces.Attendee
import de.kordondev.lagermelder.core.security.SecurityConstants.DEPARTMENT_ID_PREFIX
import de.kordondev.lagermelder.core.security.SecurityConstants.ROLE_PREFIX
import de.kordondev.lagermelder.core.security.SecurityConstants.USER_ID_PREFIX
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service("authorityService")
class AuthorityService {

    fun hasAuthorityFilter(department: DepartmentEntry, allowedRoles: List<String>): Boolean {
        return SecurityContextHolder
            .getContext()
            .authentication
            .authorities
            .map { it.authority }
            .any { authority -> authority == DEPARTMENT_ID_PREFIX + department.id.toString() || allowedRoles.any { ROLE_PREFIX + it.toString() == authority } }
    }

    fun hasAuthorityFilter(attendee: Attendee, allowedRoles: List<String>): Boolean {
        return hasAuthorityFilter(attendee.department, allowedRoles);
    }

    fun hasAuthority(department: DepartmentEntry, allowedRoles: List<String>): DepartmentEntry {
        if (hasAuthorityFilter(department, allowedRoles)) {
            return department
        }
        throw AccessDeniedException("You are not allowed to access department with other department id")
    }

    fun hasAuthority(attendee: Attendee, allowedRoles: List<String>): Attendee {
        if (hasAuthorityFilter(attendee, allowedRoles)) {
            return attendee
        }
        throw AccessDeniedException("You are not allowed to access attendee from other department id")
    }

    fun hasAuthority(tents: TentsEntity, allowedRoles: List<String>): TentsEntity {
        if (hasAuthorityFilter(tents.department, allowedRoles)) {
            return tents
        }
        throw AccessDeniedException("You are not allowed to change attendees from other departments")
    }

    fun hasAuthority(user: UserEntry, allowedRoles: List<String>): UserEntry {
        if (hasRole(allowedRoles) || hasUserId(user.id)) {
            return user
        }
        throw AccessDeniedException("You are not allowed to change another user")
    }

    fun hasUserId(userId: Long): Boolean {
        val loggedInUserId = getUserId()
        if (loggedInUserId.isEmpty) {
            return false;
        }
        return loggedInUserId.get() == USER_ID_PREFIX + userId.toString()
    }

    fun getUserId(): Optional<String> {
        return SecurityContextHolder
            .getContext()
            .authentication
            .authorities
            .stream()
            .map(GrantedAuthority::getAuthority)
            .filter { it.startsWith(USER_ID_PREFIX) }
            .findFirst()
    }

    fun hasRole(allowedRoles: List<String>): Boolean {
        return SecurityContextHolder
            .getContext()
            .authentication
            .authorities
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch { authority -> allowedRoles.any { authority == ROLE_PREFIX + it } }
    }

    fun isAdminFilter(): Boolean {
        return SecurityContextHolder
            .getContext()
            .authentication
            .authorities
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch { it == ROLE_PREFIX + Roles.ADMIN }
    }

    fun isAdmin() {
        if (!isAdminFilter()) {
            throw AccessDeniedException("You need to have the role admin")
        }
    }

    fun isSpecializedFieldDirectorFilter(): Boolean {
        return SecurityContextHolder
            .getContext()
            .authentication
            .authorities
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch { it == ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR || it == ROLE_PREFIX + Roles.ADMIN }
    }

    fun isSpecializedFieldDirector() {
        if (!isSpecializedFieldDirectorFilter()) {
            throw AccessDeniedException("You need to have the role specialized field director")
        }

    }
}