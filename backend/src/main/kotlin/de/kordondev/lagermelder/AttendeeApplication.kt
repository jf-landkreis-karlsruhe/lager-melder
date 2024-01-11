package de.kordondev.lagermelder

import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.persistence.entry.UserEntry
import de.kordondev.lagermelder.core.persistence.repository.DepartmentRepository
import de.kordondev.lagermelder.core.persistence.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class AttendeeApplication {

    val logger: Logger = LoggerFactory.getLogger(AttendeeApplication::class.java)

    @Bean
    fun init(
        departmentRepository: DepartmentRepository,
        userRepository: UserRepository,
        @Value("\${application.admin.passwordHash}") adminHash: String
    ) = ApplicationRunner {
        logger.info("Initializing database")
        val adminUsername = "admin"
        userRepository.findOneByUserName(adminUsername)
            ?: createAdmin(departmentRepository, userRepository, adminUsername, adminHash)
    }

    fun createAdmin(
        departmentRepository: DepartmentRepository,
        userRepository: UserRepository,
        adminUsername: String,
        adminHash: String
    ) {
        val adminDepartment = DepartmentEntry(
            name = "admin",
            leaderName = "KordonDev",
            leaderEMail = "KordonDev@mail.ka"
        )
        departmentRepository.save(adminDepartment)

        userRepository.saveAll(
            listOf(
                UserEntry(
                    role = Roles.ADMIN,
                    userName = adminUsername,
                    passWord = adminHash,
                    department = adminDepartment
                )
            )
        )
    }


    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

fun main(args: Array<String>) {
    runApplication<AttendeeApplication>(*args)
}
