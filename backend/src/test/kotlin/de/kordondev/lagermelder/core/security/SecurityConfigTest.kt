package de.kordondev.lagermelder.core.security

import com.jayway.jsonpath.JsonPath
import de.kordondev.lagermelder.helper.IntegrationTest
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestLoginUser
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * Runs requests through the real security filter chain (login, JWT filter, permitAll rules).
 * The admin user is created on startup by LagermelderApplication; its password in the test config is "password".
 */
@Transactional
@IntegrationTest
class SecurityConfigTest(
    val context: WebApplicationContext,
) {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    @Autowired
    lateinit var createJWTAuthentication: CreateJWTAuthentication

    @BeforeEach
    fun setUp() {
        mockMvc =
            MockMvcBuilders
                .webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(springSecurity())
                // registered as servlet filter by Spring Boot, MockMvc only adds the security filter chain by itself
                .addFilters<DefaultMockMvcBuilder>(createJWTAuthentication)
                .build()
    }

    @Test
    fun protectedEndpointWithoutTokenIsForbidden() {
        mockMvc
            .perform(webTestHelper.get("/attendees"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun healthEndpointIsPublic() {
        mockMvc
            .perform(webTestHelper.get("/actuator/health"))
            .andExpect(status().isOk)
    }

    @Test
    fun publicEndpointsArePublic() {
        mockMvc
            .perform(webTestHelper.get("/public/present-by-executed-role"))
            .andExpect(status().isOk)
    }

    @Test
    fun loginReturnsTokenThatGrantsAccess() {
        val token = login(ADMIN_USERNAME, ADMIN_PASSWORD)

        mockMvc
            .perform(webTestHelper.get("/users/me").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value(ADMIN_USERNAME))
    }

    @Test
    fun loginWithWrongPasswordIsUnauthorized() {
        mockMvc
            .perform(webTestHelper.post("/login", RestLoginUser(ADMIN_USERNAME, "wrong")))
            .andExpect(status().isUnauthorized)
    }

    private fun login(
        username: String,
        password: String,
    ): String {
        val response =
            mockMvc
                .perform(webTestHelper.post("/login", RestLoginUser(username, password)))
                .andExpect(status().isOk)
                .andReturn()
                .response.contentAsString
        // response is {"Authorization": "Bearer <jwt>"}
        return JsonPath.read<String>(response, "$.Authorization").removePrefix("Bearer ")
    }

    companion object {
        private const val ADMIN_USERNAME = "admin@jf-landkreis-karlsruhe.de"
        private const val ADMIN_PASSWORD = "password"
    }
}
