package de.kordondev.attendee.core.model

import de.kordondev.attendee.core.persistence.entry.Roles
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator


data class NewUser (
        val userName: String,
        val passWord: String,
        val role: Roles,
        val department: Department
)

data class User (
        val id: Long,
        val userName: String,
        val passWord: String,
        val role: Roles,
        val department: Department
)

