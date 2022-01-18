package de.kordondev.attendee.core.security

import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator

class PasswordGenerator() {
    companion object {
        private val alphabeticalRule: CharacterRule = CharacterRule(EnglishCharacterData.Alphabetical)
        private val digitRule: CharacterRule = CharacterRule(EnglishCharacterData.Digit)
        private const val passwordLength = 12
        private const val codeLength = 8

        private var passwordGenerator = PasswordGenerator()
        fun generatePassword(): String {
            return passwordGenerator.generatePassword(passwordLength, alphabeticalRule, digitRule)
        }

        fun generateCode(): String {
            return passwordGenerator.generatePassword(codeLength, alphabeticalRule, digitRule)
        }

    }
}