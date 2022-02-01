package de.kordondev.attendee

import de.kordondev.attendee.core.persistence.entry.*
import de.kordondev.attendee.core.persistence.repository.AttendeeRepository
import de.kordondev.attendee.core.persistence.repository.DepartmentRepository
import de.kordondev.attendee.core.persistence.repository.EventRepository
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
        eventRepository: EventRepository,
        @Value("\${application.admin.password}") adminPassword: String
    ) = ApplicationRunner {
        logger.info("Initializing database")
        val adminDepartment = DepartmentEntry(
            name = "Admin",
            leaderName = "KordonDev",
            leaderEMail = "KordonDev@mail.ka"
        )
        departmentRepository.save(adminDepartment)

        userRepository.saveAll(
            listOf(
                UserEntry(
                    role = Roles.ADMIN,
                    userName = "admin",
                    passWord = BCryptPasswordEncoder().encode(adminPassword),
                    department = adminDepartment
                )
            )
        )

        eventRepository.save(EventEntry(name = "Test event 001", code = "event001", trashed = false))
        eventRepository.save(EventEntry(name = "Test event 002", code = "event002", trashed = false))
        attendeeRepository.saveAll(
            listOf(
                AttendeeEntry(
                    firstName = "first1",
                    lastName = "last1",
                    birthday = "20-09-2005",
                    food = Food.MEAT,
                    tShirtSize = TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
                    additionalInformation = "",
                    role = AttendeeRole.YOUTH,
                    department = adminDepartment,
                    code = "att-0001",
                ),
                AttendeeEntry(
                    firstName = "first2",
                    lastName = "last2",
                    birthday = "20-09-2006",
                    food = Food.VEGAN,
                    tShirtSize = TShirtSize.M,
                    additionalInformation = "m",
                    role = AttendeeRole.YOUTH_LEADER,
                    department = adminDepartment,
                    code = "att-0002",
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
