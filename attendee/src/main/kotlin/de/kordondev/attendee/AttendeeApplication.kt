package de.kordondev.attendee

import de.kordondev.attendee.core.persistence.entry.*
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.core.persistence.repository.DepartmentRepository
import de.kordondev.attendee.core.persistence.repository.UserRepository
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
			attendeeRepository: AttendeeRepository,
			departmentRepository: DepartmentRepository,
			userRepository: UserRepository,
			@Value("\${application.admin.password}") adminPassword: String
	) = ApplicationRunner {
		logger.info("Initializing database")
        val adminDepartment = DepartmentEntry(
						name = "Admin",
						leaderName = "KordonDev",
						leaderEMail = "KordonDev@mail.ka"
				)
		departmentRepository.save(adminDepartment)

		userRepository.saveAll(listOf(
				UserEntry(
						role = Roles.ADMIN,
						userName = "admin",
						passWord = BCryptPasswordEncoder().encode(adminPassword),
						department = adminDepartment
				))
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
