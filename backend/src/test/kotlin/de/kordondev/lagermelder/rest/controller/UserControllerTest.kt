package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestDepartment
import de.kordondev.lagermelder.rest.model.RestUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import javax.transaction.Transactional


@Transactional
@SpringBootTest
class UserControllerTest(val context: WebApplicationContext) {

    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    lateinit var department: RestDepartment

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()

        department = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/departments", Entities.restDepartmentRequest()))
                .andExpect(MockMvcResultMatchers.status().isOk), RestDepartment::class.java
        )
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addUserToDepartment() {
        val user = Entities.restUserRequest(department.id)

        restMockMvc.perform(
            webTestHelper.post("/users", user)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.username))
            .andExpect(MockMvcResultMatchers.jsonPath("$.departmentId").value(user.departmentId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(Roles.USER));
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun getUserOfDepartment() {
        val user = Entities.restUserRequest(department.id)

        val createdUser = webTestHelper.toObject(
            restMockMvc.perform(
                webTestHelper.post("/users", user)
            )
                .andExpect(MockMvcResultMatchers.status().isOk),
            RestUser::class.java
        )

        restMockMvc.perform(
            webTestHelper.get("/users/department/${department.id}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdUser.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.username))
            .andExpect(MockMvcResultMatchers.jsonPath("$.departmentId").value(user.departmentId))
    }
}

