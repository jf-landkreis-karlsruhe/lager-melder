package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.Roles
import de.kordondev.lagermelder.core.security.SecurityConstants
import de.kordondev.lagermelder.helper.Entities
import de.kordondev.lagermelder.helper.WebTestHelper
import de.kordondev.lagermelder.rest.model.RestPCRTestSeries
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.ZonedDateTime

@Transactional
@SpringBootTest
class PCRTestSeriesControllerTest(val context: WebApplicationContext) {
    lateinit var restMockMvc: MockMvc

    @Autowired
    lateinit var webTestHelper: WebTestHelper

    @BeforeEach
    fun setUp() {
        restMockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun addPCRTestSeries() {

        val pcrTestSeriesEntity = Entities.restPCRTestSeriesRequest()

        restMockMvc.perform(webTestHelper.post("/pcr-test-series", pcrTestSeriesEntity))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(pcrTestSeriesEntity.name))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.start").value(webTestHelper.formatDate(pcrTestSeriesEntity.start))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.end").value(webTestHelper.formatDate(pcrTestSeriesEntity.end)))
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$.testCodes",
                    hasSize<String>(pcrTestSeriesEntity.testCodes.size)
                )
            )
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun updatePCRTestSeries() {

        val pcrTestSeries = Entities.restPCRTestSeriesRequest()

        var createdPcrTestSeries = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/pcr-test-series", pcrTestSeries))
                .andExpect(MockMvcResultMatchers.status().isOk), RestPCRTestSeries::class.java
        )

        val newName = "new name"
        createdPcrTestSeries = createdPcrTestSeries.copy(name = newName)
        restMockMvc.perform(webTestHelper.put("/pcr-test-series/${createdPcrTestSeries.id}", createdPcrTestSeries))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newName))

        val newStart = ZonedDateTime.now().minusDays(1)
        createdPcrTestSeries = createdPcrTestSeries.copy(start = newStart)
        restMockMvc.perform(webTestHelper.put("/pcr-test-series/${createdPcrTestSeries.id}", createdPcrTestSeries))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.start").value(webTestHelper.formatDate(newStart)))


        val newEnd = ZonedDateTime.now().plusDays(2)
        createdPcrTestSeries = createdPcrTestSeries.copy(end = newEnd)
        restMockMvc.perform(webTestHelper.put("/pcr-test-series/${createdPcrTestSeries.id}", createdPcrTestSeries))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.end").value(webTestHelper.formatDate(newEnd)))

        val newTests = listOf("1", "2", "3")
        createdPcrTestSeries = createdPcrTestSeries.copy(testCodes = newTests)
        restMockMvc.perform(webTestHelper.put("/pcr-test-series/${createdPcrTestSeries.id}", createdPcrTestSeries))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$.testCodes",
                    hasSize<String>(newTests.size)
                )
            )
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun deletePCRTestSeries() {

        val pcrTestSeries = Entities.restPCRTestSeriesRequest()

        var createdPcrTestSeries = webTestHelper.toObject(
            restMockMvc.perform(webTestHelper.post("/pcr-test-series", pcrTestSeries))
                .andExpect(MockMvcResultMatchers.status().isOk), RestPCRTestSeries::class.java
        )

        restMockMvc.perform(webTestHelper.get("/pcr-test-series/${createdPcrTestSeries.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.delete("/pcr-test-series/${createdPcrTestSeries.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        restMockMvc.perform(webTestHelper.get("/pcr-test-series/${createdPcrTestSeries.id}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = [SecurityConstants.ROLE_PREFIX + Roles.SPECIALIZED_FIELD_DIRECTOR])
    fun startHasToBeBeforeEnd() {

        val pcrTestSeriesTemplate = Entities.restPCRTestSeriesRequest()
        val pcrTestSeries =
            pcrTestSeriesTemplate.copy(start = ZonedDateTime.now(), end = ZonedDateTime.now().minusDays(1))

        restMockMvc.perform(webTestHelper.post("/pcr-test-series", pcrTestSeries))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}