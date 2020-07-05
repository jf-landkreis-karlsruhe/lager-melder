package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.model.NewUser
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.security.PasswordGenerator
import de.kordondev.attendee.core.service.DepartmentService
import de.kordondev.attendee.core.service.UserService
import de.kordondev.attendee.rest.model.RestUser
import de.kordondev.attendee.rest.model.request.RestUserRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class UserController (
        private val userService: UserService,
        private val departmentService: DepartmentService
) {

    @PostMapping("/user")
    fun addUser(@RequestBody(required = true) @Valid user: RestUserRequest): RestUser {
        val department = departmentService.getDepartment(user.departmentId)
        return userService
                .createUser(NewUser(
                        userName = user.username,
                        role = Roles.valueOf(user.role),
                        department = department,
                        passWord = user.password ?: PasswordGenerator.generatePassword()
                ))
                .let { RestUser.of(it) }
    }
}