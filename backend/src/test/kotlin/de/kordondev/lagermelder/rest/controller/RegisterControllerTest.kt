package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants.ROLE_PREFIX
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.WebTestHelper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@Transactional
@SpringBootTest
class RegisterControllerTest(val context: WebApplicationContext) {

    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    @WithMockUser(authorities = [ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addDepartmentAndUser() {
        val departmentWithUserRequest = Entities.restDepartmentWithUserRequest()

        restMockMvc.perform(
            post("/register").contentType(WebTestHelper.CONTENT_TYPE_JSON)
                .content(webTestHelper.toJSON(departmentWithUserRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.departmentId").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(Roles.USER))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(departmentWithUserRequest.username))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentName").value(departmentWithUserRequest.departmentName)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.leaderName").value(departmentWithUserRequest.leaderName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.leaderEMail").value(departmentWithUserRequest.leaderEMail))
    }

}