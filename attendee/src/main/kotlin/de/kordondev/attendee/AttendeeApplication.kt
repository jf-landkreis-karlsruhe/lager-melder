package de.kordondev.attendee

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.UserEntry
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.core.persistence.repository.DepartmentRepository
import de.kordondev.attendee.core.persistence.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class AttendeeApplication {

	@Bean
	fun init(attendeeRepository: AttendeeRepository, departmentRepository: DepartmentRepository, userRepository: UserRepository) = ApplicationRunner {
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
                		department = departmentLA
				), AttendeeEntry(
						firstName = "Karl",
						lastName = "Smith",
						department = departmentLA
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
						passWord = BCryptPasswordEncoder().encode("password"),
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
