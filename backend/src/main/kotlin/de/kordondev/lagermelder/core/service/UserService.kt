package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.mail.MailSenderService
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.SettingsEntry
import de.kordondev.lagermelder.core.persistence.entry.UserEntry
import de.kordondev.lagermelder.core.persistence.repository.UserRepository
import de.kordondev.lagermelder.core.security.AuthorityService
import de.kordondev.lagermelder.core.security.PasswordGenerator
import de.kordondev.lagermelder.core.security.SecurityConstants.USER_ID_PREFIX
import de.kordondev.lagermelder.exception.NotFoundException
import de.kordondev.lagermelder.exception.ResourceAlreadyExistsException
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(
    private val userRepository: UserRepository,
    private val authorityService: AuthorityService,
    private val mailSenderService: MailSenderService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val settingsService: SettingsService
) {

    fun getMe(): UserEntry {
        val userIdWithPrefix = authorityService.getUserId()
        if (userIdWithPrefix.isPresent) {
            val userId = userIdWithPrefix.get().replace(USER_ID_PREFIX, "")
            return getUser(userId.toLong())
        } else {
            throw NotFoundException("No userId in token")
        }
    }

    fun getUser(id: Long): UserEntry {
        // is used by reset password, needs to be "public"
        return userRepository.findByIdOrNull(id)
            ?.copy(passWord = "")
            ?: throw NotFoundException("user with id $id not found")
    }

    fun getUserForDepartment(department: DepartmentEntry): UserEntry {
        val user = userRepository.findOneByDepartment(department)
            ?.let {
                authorityService.hasAuthority(it, AuthorityService.LK_KARLSRUHE_ALLOWED)
            }
            ?: throw NotFoundException("user for department id ${department.id} not found")

        return user
    }

    fun getUserByUsername(username: String): UserEntry {
        return userRepository.findOneByUserName(username)?.copy(passWord = "")
            ?: throw NotFoundException("user with username $username not found")
    }

    fun createUser(newUser: UserEntry): UserEntry {
        authorityService.isSpecializedFieldDirector()
        if (newUser.role != Roles.USER) {
            authorityService.isAdmin()
        }
        userRepository
            .findOneByUserName(newUser.userName)
            ?.let { throw ResourceAlreadyExistsException("Username already exists") }
        val password = if (newUser.passWord == "") PasswordGenerator.generatePassword() else newUser.passWord
        val userWithEncryptedPassword = newUser.copy(passWord = bCryptPasswordEncoder.encode(password))
        val settings = settingsService.getSettings()
        return userRepository
            .save(userWithEncryptedPassword)
            .copy(passWord = "")
            .also { sendEmail(it, password, settings) }
    }

    fun saveResetPassword(userToChange: UserEntry): UserEntry {
        return savePassword(userToChange)
    }

    fun saveUpdatePassword(userToChange: UserEntry): UserEntry {
        authorityService.hasAuthority(userToChange, AuthorityService.SPECIALIZED_FIELD_DIRECTOR_ALLOWED)
        return savePassword(userToChange)
    }

    private fun savePassword(userToChange: UserEntry): UserEntry {
        return userRepository.findByIdOrNull(userToChange.id)
            ?.copy(passWord = bCryptPasswordEncoder.encode(userToChange.passWord))
            ?.let { userRepository.save(it) }
            ?.copy(passWord = "")
            ?: throw NotFoundException("user with id ${userToChange.id} not found")
    }

    fun updateRole(userId: Long, role: String): UserEntry {
        authorityService.isSpecializedFieldDirector()
        return userRepository.findByIdOrNull(userId)
            ?.copy(role = role)
            ?.let { userRepository.save(it) }
            ?: throw NotFoundException("user with id $userId not found")
    }

    @Transactional
    fun updatePasswordAndSendEmail(user: UserEntry): UserEntry {
        authorityService.hasAuthority(user, AuthorityService.SPECIALIZED_FIELD_DIRECTOR_ALLOWED)
        val newPassword = PasswordGenerator.generatePassword()
        val settings = settingsService.getSettings()
        return saveUpdatePassword(user.copy(passWord = newPassword))
            .also { sendEmail(it, newPassword, settings) }
    }

    private fun sendEmail(user: UserEntry, password: String, settings: SettingsEntry) {
        authorityService.hasAuthority(user, AuthorityService.SPECIALIZED_FIELD_DIRECTOR_ALLOWED)
        mailSenderService.sendRegistrationMail(
            to = user.department.leaderEMail,
            leaderName = user.department.leaderName,
            username = user.userName,
            password = password,
            settings
        )
    }
}
