package de.kordondev.lagermelder.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Enforces the layering described in backend/ARCHITECTURE.md.
 *
 * The KNOWN_VIOLATIONS lists freeze the state at the time the rules were introduced.
 * They may only shrink: remove an entry once the file is fixed (the test fails if an entry no longer violates).
 * Never add new entries.
 */
class ArchitectureTest {
    private val production = Konsist.scopeFromProduction()

    @Test
    fun `core does not depend on rest`() {
        assertLayerRule(
            files = production.files.filter { it.packagee?.name?.startsWith("$BASE.core") == true },
            forbiddenImportPrefix = "$BASE.rest",
            knownViolations = KNOWN_CORE_TO_REST_VIOLATIONS,
        )
    }

    @Test
    fun `persistence does not depend on services, security or rest`() {
        val persistenceFiles = production.files.filter { it.packagee?.name?.startsWith("$BASE.core.persistence") == true }
        listOf("$BASE.core.service", "$BASE.core.security", "$BASE.rest").forEach { forbidden ->
            assertLayerRule(persistenceFiles, forbidden, emptySet())
        }
    }

    @Test
    fun `controllers do not access repositories directly`() {
        assertLayerRule(
            files = production.files.filter { it.packagee?.name == "$BASE.rest.controller" },
            forbiddenImportPrefix = "$BASE.core.persistence.repository",
            knownViolations = KNOWN_CONTROLLER_TO_REPOSITORY_VIOLATIONS,
        )
    }

    @Test
    fun `controllers live in rest controller and are named Controller`() {
        production
            .classes()
            .withAnnotationNamed("RestController")
            .assertTrue { it.resideInPackage("$BASE.rest.controller") && it.name.endsWith("Controller") }
    }

    @Test
    fun `entities live in persistence entry`() {
        production
            .classes()
            .withAnnotationNamed("Entity")
            .assertTrue { it.resideInPackage("$BASE.core.persistence.entry") }
    }

    @Test
    fun `repositories live in persistence repository and are named Repository`() {
        production
            .interfaces()
            .filter { it.parents().any { parent -> parent.name.endsWith("Repository") || parent.name == "CrudRepository" } }
            .assertTrue { it.resideInPackage("$BASE.core.persistence.repository") && it.name.endsWith("Repository") }
    }

    @Test
    fun `integration tests use IntegrationTest instead of SpringBootTest`() {
        Konsist
            .scopeFromTest()
            .classes()
            .filterNot { it.hasAnnotationModifier }
            .assertFalse { it.hasAnnotationWithName("SpringBootTest") }
    }

    @Test
    fun `public functions in test classes are annotated as tests or lifecycle methods`() {
        val testAnnotations =
            setOf("Test", "ParameterizedTest", "BeforeEach", "AfterEach", "BeforeAll", "AfterAll", "Bean")
        Konsist
            .scopeFromTest()
            .classes()
            .filter { it.name.endsWith("Test") }
            .flatMap { testClass ->
                testClass
                    .functions()
                    .filterNot { "${testClass.name}.${it.name}" in KNOWN_FUNCTIONS_WITHOUT_TEST_ANNOTATION }
            }.filter { !it.hasPrivateModifier && !it.hasProtectedModifier && !it.hasInternalModifier }
            .assertTrue { function -> function.annotations.any { it.name in testAnnotations } }
    }

    private fun assertLayerRule(
        files: List<KoFileDeclaration>,
        forbiddenImportPrefix: String,
        knownViolations: Set<String>,
    ) {
        val violating =
            files
                .filter { file -> file.imports.any { it.name.startsWith(forbiddenImportPrefix) } }
                .map { it.nameWithExtension }
                .toSet()

        assertThat(violating - knownViolations)
            .describedAs("New files importing $forbiddenImportPrefix (not allowed, see ARCHITECTURE.md)")
            .isEmpty()
        assertThat(knownViolations - violating)
            .describedAs("Fixed files that must be removed from the known violations list")
            .isEmpty()
    }

    companion object {
        private const val BASE = "de.kordondev.lagermelder"

        private val KNOWN_CORE_TO_REST_VIOLATIONS =
            setOf(
                "JWTAuthorizationFilter.kt",
                "CreateJWTAuthentication.kt",
                "SecurityService.kt",
                "RegistrationFilesService.kt",
                "YouthPlanAttendeeRoleService.kt",
                "EventService.kt",
                "DepartmentService.kt",
            )

        private val KNOWN_CONTROLLER_TO_REPOSITORY_VIOLATIONS = setOf("AuthorizationController.kt")

        private val KNOWN_FUNCTIONS_WITHOUT_TEST_ANNOTATION = emptySet<String>()
    }
}
