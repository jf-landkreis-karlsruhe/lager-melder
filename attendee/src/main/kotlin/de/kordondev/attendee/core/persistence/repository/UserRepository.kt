package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.UserEntry
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntry, Long>  {
    fun findOneByUserName(userName: String): UserEntry?
    fun findByUserNameAndDepartmentIdOrNull(userName: String, departmentId: Long): UserEntry?
}