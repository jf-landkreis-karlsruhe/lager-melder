package de.kordondev.attendee.core.security

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.persistence.entry.Roles
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service("authorityService")
class AuthorityService {

    fun hasAuthorityFilter(department: Department): Boolean {
        return SecurityContextHolder.
                getContext()
                .authentication
                .authorities
                .map { it.authority }
                .any { it == department.id.toString() || it == Roles.ADMIN.toString() }
    }

    fun hasAuthorityFilter(attendee: Attendee): Boolean {
        return hasAuthorityFilter(attendee.department);
    }


    fun hasAuthority(department: Department): Department {
        if (hasAuthorityFilter(department)) {
            return department
        }
        throw AccessDeniedException("You are not allowed to access department with other department id")
    }

    fun hasAuthority(attendee: Attendee): Attendee {
        if (hasAuthorityFilter(attendee)) {
            return attendee
        }
        throw AccessDeniedException("You are not allowed to access attendee from other department id")
    }
    fun hasAuthority(attendee: NewAttendee): NewAttendee {
        if (hasAuthorityFilter(attendee.department)) {
            return attendee
        }
        throw AccessDeniedException("You are not allowed to change attendees from other departments")
    }

    fun isAdminFilter(): Boolean {
        return SecurityContextHolder
                .getContext()
                .authentication
                .authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch { it == Roles.ADMIN.toString() }
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
                .anyMatch { it == Roles.SPECIALIZED_FIELD_DIRECTOR.toString() || it == Roles.ADMIN.toString() }
    }

    fun isSpecializedFieldDirector() {
        if (!isSpecializedFieldDirectorFilter()) {
            throw AccessDeniedException("You need to have the role specialized field director")
        }

    }
}