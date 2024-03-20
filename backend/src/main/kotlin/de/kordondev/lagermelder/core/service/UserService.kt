package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.mail.MailSenderService
import de.kordondev.lagermelder.core.model.Department
import de.kordondev.lagermelder.core.model.NewUser
import de.kordondev.lagermelder.core.model.Settings
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
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

    fun getUserForDepartment(department: Department): UserEntry {
        val user = userRepository.findOneByDepartment(DepartmentEntry.of(department))
            ?: throw NotFoundException("user for department id ${department.id} not found")

        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return user
    }

    fun createUser(user: NewUser): UserEntry {
        authorityService.isSpecializedFieldDirector()
        if (user.role != Roles.USER) {
            authorityService.isAdmin()
        }
        userRepository
            .findOneByUserName(user.userName)
            ?.let { throw ResourceAlreadyExistsException("Username already exists") }
        user.passWord = PasswordGenerator.generatePassword()
        val userWithEncryptedPassword = user.copy(passWord = bCryptPasswordEncoder.encode(user.passWord))
        val settings = settingsService.getSettings()
        return userRepository
            .save(UserEntry.of(userWithEncryptedPassword))
            .copy(passWord = "")
            .also { sendEmail(it, user.passWord, settings) }
    }

    fun saveUpdatePassword(userToChange: UserEntry): UserEntry {
        authorityService.hasAuthority(userToChange, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
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

    private fun sendEmail(user: UserEntry, password: String, settings: Settings) {
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