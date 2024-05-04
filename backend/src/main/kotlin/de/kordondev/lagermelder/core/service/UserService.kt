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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional


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
        return userRepository.findByIdOrNull(id)
            ?.copy(passWord = "")
            ?: throw NotFoundException("user with id $id not found")
    }

    fun getUserForDepartment(department: DepartmentEntry): UserEntry {
        val user = userRepository.findOneByDepartment(department)
            ?: throw NotFoundException("user for department id ${department.id} not found")

        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
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
        val password = PasswordGenerator.generatePassword()
        val userWithEncryptedPassword = newUser.copy(passWord = bCryptPasswordEncoder.encode(password))
        val settings = settingsService.getSettings()
        return userRepository
            .save(userWithEncryptedPassword)
            .copy(passWord = "")
            .also { sendEmail(it, newUser.passWord, settings) }
    }

    fun saveResetPassword(userToChange: UserEntry): UserEntry {
        return savePassword(userToChange)
    }

    fun saveUpdatePassword(userToChange: UserEntry): UserEntry {
        authorityService.hasAuthority(userToChange, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
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
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return userRepository.findByIdOrNull(userId)
            ?.copy(role = role)
            ?.let { userRepository.save(it) }
            ?: throw NotFoundException("user with id $userId not found")
    }

    @Transactional
    fun updatePasswordAndSendEmail(user: UserEntry): UserEntry {
        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        val newPassword = PasswordGenerator.generatePassword()
        val settings = settingsService.getSettings()
        return saveUpdatePassword(user.copy(passWord = newPassword))
            .also { sendEmail(it, newPassword, settings) }
    }

    private fun sendEmail(user: UserEntry, password: String, settings: SettingsEntry) {
        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        mailSenderService.sendRegistrationMail(
            to = user.department.leaderEMail,
            leaderName = user.department.leaderName,
            username = user.userName,
            password = password,
            settings
        )
    }
}
