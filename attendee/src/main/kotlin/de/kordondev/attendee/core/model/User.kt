package de.kordondev.attendee.core.model

import de.kordondev.attendee.core.persistence.entry.Roles

data class NewUser (
        var userName: String,
        var passWord: String,
        var role: Roles,
        var department: Department
)

data class User (
        var id: Long,
        var userName: String,
        var passWord: String,
        var role: Roles,
        var department: Department
)
