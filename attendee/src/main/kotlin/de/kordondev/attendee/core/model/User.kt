package de.kordondev.attendee.core.model

import de.kordondev.attendee.core.persistence.entry.Roles
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator


data class NewUser (
        val userName: String,
        val passWord: String = generatePassword(),
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

val alphabeticalRule: CharacterRule = CharacterRule(EnglishCharacterData.Alphabetical)
val digitRule: CharacterRule = CharacterRule(EnglishCharacterData.Digit)

var passwordGenerator = PasswordGenerator()
fun generatePassword(): String {
    return passwordGenerator.generatePassword(12, alphabeticalRule, digitRule)
}
