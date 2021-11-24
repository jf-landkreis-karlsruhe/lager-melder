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

    fun hasAuthorityFilter(department: Department, allowedRoles: List<String>): Boolean {
        return SecurityContextHolder
            .getContext()
            .authentication
            .authorities
            .map { it.authority }
            .any { role -> role == department.id.toString() || allowedRoles.any { it.toString() == role } }
    }

    fun hasAuthorityFilter(attendee: Attendee, allowedRoles: List<String>): Boolean {
        return hasAuthorityFilter(attendee.department, allowedRoles);
    }


    fun hasAuthority(department: Department, allowedRoles: List<String>): Department {
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

    fun hasAuthority(attendee: NewAttendee, allowedRoles: List<String>): NewAttendee {
        if (hasAuthorityFilter(attendee.department, allowedRoles)) {
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
            .anyMatch { it == Roles.SPECIALIZED_FIELD_DIRECTOR || it == Roles.ADMIN }
    }

    fun isSpecializedFieldDirector() {
        if (!isSpecializedFieldDirectorFilter()) {
            throw AccessDeniedException("You need to have the role specialized field director")
        }

    }
}