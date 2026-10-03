package de.kordondev.lagermelder.exception

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.stream.Stream

/**
 * The frontend relies on the error key and HTTP status (see frontend/src/services/errorConstants.ts).
 */
class ExceptionHandlerTest {
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc =
            MockMvcBuilders
                .standaloneSetup(ThrowingController())
                .setControllerAdvice(ExceptionHandler())
                .build()
    }

    @ParameterizedTest(name = "{0} -> {1} {2}")
    @MethodSource("exceptions")
    fun mapsExceptionToStatusAndKey(
        exceptionName: String,
        expectedStatus: HttpStatus,
        expectedKey: String,
    ) {
        mockMvc
            .perform(get("/throw/$exceptionName"))
            .andExpect(status().`is`(expectedStatus.value()))
            .andExpect(jsonPath("$.key").value(expectedKey))
            .andExpect(jsonPath("$.messages[0].message").value("message of $exceptionName"))
    }

    @Test
    fun mapsValidationErrorsWithFieldNames() {
        mockMvc
            .perform(post("/validate").contentType(MediaType.APPLICATION_JSON).content("""{"name": ""}"""))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.key").value(ErrorConstants.VALIDATION_ERROR))
            .andExpect(jsonPath("$.messages[0].fieldName").value("name"))
    }

    data class ValidatedRequest(
        @field:NotBlank
        val name: String,
    )

    @RestController
    class ThrowingController {
        @GetMapping("/throw/{name}")
        fun throwException(
            @PathVariable name: String,
        ): Unit = throw EXCEPTIONS.getValue(name)("message of $name")

        @PostMapping("/validate")
        fun validate(
            @RequestBody @Valid request: ValidatedRequest,
        ) = request
    }

    companion object {
        private val EXCEPTIONS: Map<String, (String) -> RuntimeException> =
            mapOf(
                "BadRequestException" to ::BadRequestException,
                "ChangedRoleException" to ::ChangedRoleException,
                "ExistingDependencyException" to ::ExistingDependencyException,
                "NotDeletableException" to ::NotDeletableException,
                "NotFoundException" to ::NotFoundException,
                "ResourceAlreadyExistsException" to ::ResourceAlreadyExistsException,
                "UnexpectedTypeException" to ::UnexpectedTypeException,
                "UniqueException" to ::UniqueException,
                "WrongTimeException" to ::WrongTimeException,
                "AccessDeniedException" to { message -> AccessDeniedException(message) },
            )

        @JvmStatic
        fun exceptions(): Stream<Arguments> =
            Stream.of(
                Arguments.of("BadRequestException", HttpStatus.BAD_REQUEST, ErrorConstants.BAD_REQUEST_ERROR),
                Arguments.of("ChangedRoleException", HttpStatus.BAD_REQUEST, ErrorConstants.CHANGED_ROLE),
                Arguments.of("ExistingDependencyException", HttpStatus.FORBIDDEN, ErrorConstants.EXISTING_DEPENDENCY_ERROR),
                Arguments.of("NotDeletableException", HttpStatus.BAD_REQUEST, ErrorConstants.NOT_DELETABLE_ERROR),
                Arguments.of("NotFoundException", HttpStatus.NOT_FOUND, ErrorConstants.NOT_FOUND_ERROR),
                Arguments.of("ResourceAlreadyExistsException", HttpStatus.FORBIDDEN, ErrorConstants.RESOURCE_ALREADY_EXISTS_ERROR),
                // key differs from UnexpectedTypeException.key (UNEXPECTED_TYPE)
                Arguments.of("UnexpectedTypeException", HttpStatus.INTERNAL_SERVER_ERROR, ErrorConstants.WRONG_TYPE),
                Arguments.of("UniqueException", HttpStatus.BAD_REQUEST, ErrorConstants.UNIQUE_ERROR),
                Arguments.of("WrongTimeException", HttpStatus.BAD_REQUEST, ErrorConstants.WRONG_TIME_EXCEPTION),
                Arguments.of("AccessDeniedException", HttpStatus.FORBIDDEN, ErrorConstants.ACCESS_DENIED_ERROR),
            )
    }
}
