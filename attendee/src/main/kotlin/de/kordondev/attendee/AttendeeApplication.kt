package de.kordondev.attendee

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.core.persistence.repository.DepartmentRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class AttendeeApplication {

	@Bean
	fun init(attendeeRepository: AttendeeRepository, departmentRepository: DepartmentRepository) = ApplicationRunner {
		val departmentLA = DepartmentEntry(
				name = "LA",
				leaderName = "Brian",
				leaderEMail = "brian@email.com"
		)
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
	}
}

fun main(args: Array<String>) {
	runApplication<AttendeeApplication>(*args)
}
