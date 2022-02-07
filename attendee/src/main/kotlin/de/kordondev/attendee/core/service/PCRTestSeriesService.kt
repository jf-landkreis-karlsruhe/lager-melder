package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.repository.PCRTestSeriesRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.rest.model.RestPCRTestSeries
import de.kordondev.attendee.rest.model.request.RestPCRTestSeriesRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PCRTestSeriesService(
    private val pcrTestSeriesRepository: PCRTestSeriesRepository,
    private val pcrTestService: PCRTestService,
    private val authorityService: AuthorityService
) {
    fun createPcrTestSeries(pcrTestSeries: RestPCRTestSeriesRequest): RestPCRTestSeries {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        pcrTestService.checkExistenceOfCodes(pcrTestSeries.testCodes)
        val pcrTestSeriesEntry = pcrTestSeriesRepository.save(RestPCRTestSeriesRequest.toEntry(pcrTestSeries))
        pcrTestSeriesEntry.tests = pcrTestService.addPcrTestsToSeries(pcrTestSeriesEntry, pcrTestSeries.testCodes)
        return pcrTestSeriesEntry.let { RestPCRTestSeries.ofEntry(it) }
    }

    fun savePcrTestSeries(id: Long, pcrTestSeries: RestPCRTestSeries): RestPCRTestSeries {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return pcrTestSeriesRepository.findByIdOrNull(id)
            ?.let { currentPcrTestSeries ->
                val currentPcrTestCodes = currentPcrTestSeries.tests.map { it.code }

                val pcrTestCodesToCreate = pcrTestSeries.testCodes.filter { !currentPcrTestCodes.contains(it) }
                val pcrTestsToDelete = currentPcrTestSeries.tests.filter { !pcrTestSeries.testCodes.contains(it.code) }
                val pcrTestCodesToDelete = pcrTestsToDelete.map { it.code }

                currentPcrTestSeries.update(pcrTestSeries)
                currentPcrTestSeries.tests =
                    currentPcrTestSeries.tests.filter { !pcrTestCodesToDelete.contains(it.code) }.toMutableSet()
                currentPcrTestSeries.tests.addAll(
                    pcrTestService.addPcrTestsToSeries(
                        currentPcrTestSeries,
                        pcrTestCodesToCreate
                    )
                )
                pcrTestService.deleteAll(pcrTestsToDelete)
                return pcrTestSeriesRepository.save(currentPcrTestSeries)
                    .let { RestPCRTestSeries.ofEntry(it) }
            }
            ?: createPcrTestSeries(RestPCRTestSeriesRequest.of(pcrTestSeries))
    }
/*
    fun getPcrTestSeries(id: Long): PCRTestSeries {
        return pcrTestSeriesRepository.findByIdOrNull(id)
            ?.let { PCRTestSeriesEntry.to(it) }
            ?: throw NotFoundException("PCRTestSeries does not exist with id $id")
    }

    fun getAllPcrTestSeries(): List<PCRTestSeries> {
        return pcrTestSeriesRepository.findAll()
            .map { PCRTestSeriesEntry.to(it) }
    }

    fun deletePcrTestSeries(id: Long) {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))

        val pcrTestSeries = pcrTestSeriesRepository.findByIdOrNull(id) ?: return;
        if (pcrTestSeries.tests.isNotEmpty()) {
            throw ExistingDependencyException("Tests for pcrTestSeries $id exist. Delete them first.")
        }
        pcrTestSeriesRepository.delete(pcrTestSeries);
    }*/
}