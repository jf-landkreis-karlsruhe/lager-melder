package de.kordondev.attendee

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AttendeeApplication

fun main(args: Array<String>) {
	runApplication<AttendeeApplication>(*args)
}
