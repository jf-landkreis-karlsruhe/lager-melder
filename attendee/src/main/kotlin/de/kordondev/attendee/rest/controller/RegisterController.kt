package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.persistence.repository.DepartmentRepository
import de.kordondev.attendee.core.persistence.repository.UserRepository
import de.kordondev.attendee.exception.ResourceAlreadyExistsException
import de.kordondev.attendee.rest.model.DepartmentWithUser
import de.kordondev.attendee.rest.model.RestAttendee
import de.kordondev.attendee.rest.model.request.RestAttendeeRequest
import de.kordondev.attendee.rest.model.request.RestDepartmentWithUserRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class RegisterController(
    private val departmentRepository: DepartmentRepository,
    private val userRepository: UserRepository
) {


    @PostMapping("/register")
    fun addAttendee(@RequestBody(required = true) @Valid departmentWithUser: RestDepartmentWithUserRequest)  {


        // Check both for existance than create department and then user and return


        if (departmentRepository.findOneByNameOrNull(departmentWithUser.departmentName) != null ||
            userRepository.findOneByUserName(departmentWithUser.username) != null) {
            throw ResourceAlreadyExistsException("Department with name ${departmentWithUser.departmentName} or user with name ${departmentWithUser.username} already exists")
            // throw
        }




    }
}