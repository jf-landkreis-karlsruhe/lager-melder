package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.model.NewUser
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.service.DepartmentService
import de.kordondev.attendee.core.service.UserService
import de.kordondev.attendee.rest.model.RestDepartment
import de.kordondev.attendee.rest.model.RestUser
import de.kordondev.attendee.rest.model.request.RestDepartmentRequest
import de.kordondev.attendee.rest.model.request.RestUserRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController (
        private val userService: UserService,
        private val departmentService: DepartmentService
) {

    @PostMapping("/user")
    fun addUser(@RequestBody(required = true) user: RestUserRequest): RestUser {
        val department = departmentService.getDepartment(user.departmentId)
        return userService
                .createUser(NewUser(
                        userName = user.username,
                        role = Roles.valueOf(user.role),
                        department = department,
                        passWord = user.password!!
                ))
                .let { savedUser -> RestUser.of(savedUser) }
    }
}