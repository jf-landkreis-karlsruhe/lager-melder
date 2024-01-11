package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.mail.MailSenderService
import de.kordondev.lagermelder.core.model.Department
import de.kordondev.lagermelder.core.model.NewUser
import de.kordondev.lagermelder.core.model.Settings
import de.kordondev.lagermelder.core.model.User
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

    fun getMe(): User {
        val userIdWithPrefix = authorityService.getUserId();
        if (userIdWithPrefix.isPresent) {
            val userId = userIdWithPrefix.get().replace(USER_ID_PREFIX, "")
            return getUser(userId.toLong())
        } else {
            throw NotFoundException("No userId in token")
        }
    }

    fun getUser(id: Long): User {
        return userRepository.findByIdOrNull(id)
            ?.let { it.copy(passWord = "") }
            ?.let { UserEntry.to(it) }
            ?: throw NotFoundException("user with id $id not found")
    }

    fun getUserForDepartment(department: Department): User {
        val user = userRepository.findOneByDepartment(DepartmentEntry.of(department))
            ?.let { UserEntry.to(it) }
            ?: throw NotFoundException("user for department id ${department.id} not found")

        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return user
    }

    fun createUser(user: NewUser): User {
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
            .let { it.copy(passWord = "") }
            .let { savedUser -> UserEntry.to(savedUser) }
            .also { sendEmail(it, user.passWord, settings) }
    }

    fun saveUpdatePassword(userToChange: User): User {
        authorityService.hasAuthority(userToChange, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return userRepository.findByIdOrNull(userToChange.id)
            ?.let { it.copy(passWord = bCryptPasswordEncoder.encode(userToChange.passWord)) }
            ?.let { userRepository.save(it) }
            ?.let { it.copy(passWord = "") }
            ?.let { UserEntry.to(it) }
            ?: throw NotFoundException("user with id ${userToChange.id} not found")
    }

    fun updateRole(userId: Long, role: String): User {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return userRepository.findByIdOrNull(userId)
            ?.let { it.copy(role = role) }
            ?.let { userRepository.save(it) }
            ?.let { UserEntry.to(it) }
            ?: throw NotFoundException("user with id $userId not found")
    }

    @Transactional
    fun updatePasswordAndSendEmail(user: User): User {
        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        val newPassword = PasswordGenerator.generatePassword()
        val settings = settingsService.getSettings()
        return saveUpdatePassword(user.copy(passWord = newPassword))
            .also { sendEmail(it, newPassword, settings) }
    }

    private fun sendEmail(user: User, password: String, settings: Settings) {
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