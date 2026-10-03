package de.kordondev.lagermelder.core.security

import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator
import org.springframework.stereotype.Service

@Service
class PasswordGenerator {
    companion object {
        private val alphabeticalRule: CharacterRule = CharacterRule(EnglishCharacterData.Alphabetical)
        private val digitRule: CharacterRule = CharacterRule(EnglishCharacterData.Digit)
        private const val PASSWORD_LENGTH = 12
        private const val CODE_LENGTH = 8
        private const val SECURE_TOKEN_LENGTH = 64

        private var passwordGenerator = PasswordGenerator()

        fun generatePassword(): String = passwordGenerator.generatePassword(PASSWORD_LENGTH, alphabeticalRule, digitRule)

        fun generateCode(): String = passwordGenerator.generatePassword(CODE_LENGTH, alphabeticalRule, digitRule)

        fun generateSecureToken(): String = passwordGenerator.generatePassword(SECURE_TOKEN_LENGTH, alphabeticalRule, digitRule)
    }
}
