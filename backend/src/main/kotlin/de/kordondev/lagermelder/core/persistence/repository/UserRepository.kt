package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.UserEntry
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntry, Long> {
    fun findOneByUserName(userName: String): UserEntry?
    fun findOneByDepartment(department: DepartmentEntry): UserEntry?
}