package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.DepartmentService
import de.kordondev.lagermelder.core.service.SecurityService
import de.kordondev.lagermelder.core.service.UserService
import de.kordondev.lagermelder.rest.model.RestOk
import de.kordondev.lagermelder.rest.model.RestUser
import de.kordondev.lagermelder.rest.model.request.RestPasswordPasswordTokenRequest
import de.kordondev.lagermelder.rest.model.request.RestResetPasswordTokenRequest
import de.kordondev.lagermelder.rest.model.request.RestUserRequest
import de.kordondev.lagermelder.rest.model.request.RestUserRoleRequest
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class UserController(
    private val userService: UserService,
    private val departmentService: DepartmentService,
    private val securityService: SecurityService
) {

    @GetMapping("/users/me")
    fun getMe(): RestUser {
        return userService
            .getMe()
            .let { RestUser.of(it) }
    }

    @GetMapping("/users/department/{id}")
    fun getUserForDepartment(@PathVariable("id") id: Long): RestUser {
        val department = departmentService.getDepartment(id)
        return userService
            .getUserForDepartment(department)
            .let { RestUser.of(it) }
    }

    @PostMapping("/users")
    fun addUser(@RequestBody(required = true) @Valid user: RestUserRequest): RestUser {
        val department = departmentService.getDepartment(user.departmentId)
        return userService
            .createUser(
                RestUserRequest.to(user, 0, department)
            )
            .let { RestUser.of(it) }
    }

    @PutMapping("/users/{id}/password")
    fun changePassword(
        @RequestBody(required = true) @Valid user: RestUserRequest,
        @PathVariable("id") id: Long
    ): RestUser {
        val department = departmentService.getDepartment(user.departmentId)
        return userService
            .saveUpdatePassword(RestUserRequest.to(user, id, department))
            .let { RestUser.of(it) }
    }

    @PostMapping("/users/{id}/sendRegistrationEmail")
    fun sendRegistrationEmail(@PathVariable("id") id: Long): RestUser {
        val user = userService.getUser(id)
        return userService
            .updatePasswordAndSendEmail(user)
            .let { RestUser.of(it) }
    }

    @PutMapping("/users/{id}/role")
    fun updateRole(
        @PathVariable("id") id: Long,
        @RequestBody(required = true) @Valid userRole: RestUserRoleRequest
    ): RestUser {
        return userService
            .updateRole(id, userRole.role)
            .let { RestUser.of(it)}
    }

    @PutMapping("/users/forgotPasswordToken")
    fun sendForgotPasswordEmail(
        @RequestBody(required = true) @Valid passwordPasswordTokenRequest: RestPasswordPasswordTokenRequest
    ): RestOk {
        return securityService.sendResetPasswordLink(
            passwordPasswordTokenRequest.username,
            passwordPasswordTokenRequest.linkAddress
        )
    }

    @PutMapping("/users/resetPasswordWithToken")
    fun resetPasswordWithToken(
        @RequestBody(required = true) @Valid resetPasswordRequest: RestResetPasswordTokenRequest
    ): RestOk {
        return securityService.resetPasswordWithToken(resetPasswordRequest.token, resetPasswordRequest.password)
    }
}