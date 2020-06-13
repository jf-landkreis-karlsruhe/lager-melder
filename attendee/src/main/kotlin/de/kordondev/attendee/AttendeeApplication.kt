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
		val departmentLA = DepartmentEntry(
				name = "LA",
				leaderName = "Brian",
				leaderEMail = "brian@email.com"
		)
        val department2 = DepartmentEntry(
						name = "ET",
						leaderName = "Jim",
						leaderEMail = "jim@mail.ka"
				)
		departmentRepository.save(department2)
		departmentRepository.save(departmentLA)

		attendeeRepository.saveAll(listOf(
				AttendeeEntry(
						firstName = "Iris",
						lastName = "Muller",
                		department = departmentLA,
						birthday = "1992-12-07",
						food = Food.ALLERGY,
						tShirtSize = TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
						additionalInformation = "Youth, 164, Allergy",
						role = AttendeeRole.YOUTH
				), AttendeeEntry(
						firstName = "Karl",
						lastName = "Smith",
						department = departmentLA,
						birthday = "1993-12-07",
						food = Food.NONE,
						tShirtSize = TShirtSize.M,
						additionalInformation = "Youth, M, No food",
						role = AttendeeRole.YOUTH
				)
		))

		userRepository.saveAll(listOf(
				UserEntry(
						role = Roles.USER,
						userName = "user",
						passWord = BCryptPasswordEncoder().encode("pass"),
						department = departmentLA
				),
				UserEntry(
						role = Roles.ADMIN,
						userName = "admin",
						passWord = BCryptPasswordEncoder().encode(adminPassword),
						department = department2
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
