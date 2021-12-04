package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.persistence.repository.DepartmentRepository
import de.kordondev.attendee.core.persistence.repository.UserRepository
import de.kordondev.attendee.core.service.DepartmentService
import de.kordondev.attendee.core.service.UserService
import de.kordondev.attendee.exception.ResourceAlreadyExistsException
import de.kordondev.attendee.rest.model.RestDepartmentWithUser
import de.kordondev.attendee.rest.model.request.RestDepartmentWithUserRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class RegisterController(
    private val departmentRepository: DepartmentRepository,
    private val departmentService: DepartmentService,
    private val userRepository: UserRepository,
    private val userService: UserService
) {

    private val logger: Logger = LoggerFactory.getLogger(RegisterController::class.java)

    @PostMapping("/register")
    fun addDepartmentAndUser(@RequestBody(required = true) @Valid departmentWithUser: RestDepartmentWithUserRequest): RestDepartmentWithUser {
        logger.info("Try to register new user")
        if (departmentRepository.findOneByName(departmentWithUser.departmentName) != null ||
            userRepository.findOneByUserName(departmentWithUser.username) != null
        ) {

            logger.error("Department with name ${departmentWithUser.departmentName} or user with name ${departmentWithUser.username} already exists")
            throw ResourceAlreadyExistsException("Department with name ${departmentWithUser.departmentName} or user with name ${departmentWithUser.username} already exists")
        }

        val newDepartment = RestDepartmentWithUserRequest.toDepartment(departmentWithUser)
        val department = departmentService.createDepartment(newDepartment)

        val newUser = RestDepartmentWithUserRequest.toUser(departmentWithUser, department)
        val user = userService.createUser(newUser)

        return RestDepartmentWithUser.from(department, user)
    }
}