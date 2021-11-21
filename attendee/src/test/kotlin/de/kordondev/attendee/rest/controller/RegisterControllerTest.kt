package de.kordondev.attendee.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.rest.model.request.RestDepartmentWithUserRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.charset.Charset
import javax.transaction.Transactional

@Transactional
@SpringBootTest
class RegisterControllerTest(val context: WebApplicationContext) {

    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper
    val CONTENT_TYPE_JSON = MediaType(
        MediaType.APPLICATION_JSON.type,
        MediaType.APPLICATION_JSON.subtype,
        Charset.forName("utf8")
    )

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    @WithMockUser(authorities = [Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addDepartmentAndUser() {
        val departmentWithUserRequest = RestDepartmentWithUserRequest(
            username = "username",
            departmentName = "department",
            leaderName = "leaderName",
            leaderEMail = "leader@department.de"
        )


        restMockMvc.perform(
            post("/register").contentType(CONTENT_TYPE_JSON).content(toJSON(departmentWithUserRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.departmentId").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(Roles.USER.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(departmentWithUserRequest.username))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentName").value(departmentWithUserRequest.departmentName)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.leaderName").value(departmentWithUserRequest.leaderName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.leaderEMail").value(departmentWithUserRequest.leaderEMail))
    }

    fun toJSON(inputObject: Any?): ByteArray {
        return objectMapper.writeValueAsBytes(inputObject)
    }
}