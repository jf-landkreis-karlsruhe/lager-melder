package de.kordondev.attendee

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class AttendeeApplication {

	@Bean
	fun init(attendeeRepository: AttendeeRepository) = ApplicationRunner {
		attendeeRepository.saveAll(listOf(
				AttendeeEntry(
					id = 1,
					firstName = "Iris",
					lastName = "Muller"
				), AttendeeEntry(
					id = 2,
					firstName = "Karl",
					lastName = "Smith"
		)) )
	}
}

fun main(args: Array<String>) {
	runApplication<AttendeeApplication>(*args)
}
