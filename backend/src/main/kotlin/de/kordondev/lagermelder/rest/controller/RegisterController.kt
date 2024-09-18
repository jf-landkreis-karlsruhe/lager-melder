package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.repository.DepartmentRepository
import de.kordondev.lagermelder.core.persistence.repository.UserRepository
import de.kordondev.lagermelder.core.service.DepartmentService
import de.kordondev.lagermelder.core.service.UserService
import de.kordondev.lagermelder.exception.ResourceAlreadyExistsException
import de.kordondev.lagermelder.rest.model.RestDepartmentWithUser
import de.kordondev.lagermelder.rest.model.request.RestDepartmentWithUserRequest
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

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

        val newDepartment = RestDepartmentWithUserRequest.toDepartment(departmentWithUser, Random.nextLong())
        val features = newDepartment.features

        val departmentWithoutFeatures = newDepartment.copy(features = emptySet())
        val savedDepartment = departmentService.createDepartment(departmentWithoutFeatures)

        val departmentWithFeatures = savedDepartment.copy(features = features.map { it.copy(departmentId = savedDepartment.id) }.toSet())
        val department = departmentService.saveDepartment(departmentWithFeatures)

        val newUser = RestDepartmentWithUserRequest.toUser(departmentWithUser, department)
        val user = userService.createUser(newUser)

        return RestDepartmentWithUser.from(department, user)
    }
}