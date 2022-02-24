package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.repository.PCRTestSeriesRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.exception.BadRequestException
import de.kordondev.attendee.exception.NotFoundException
import de.kordondev.attendee.rest.model.RestPCRTestSeries
import de.kordondev.attendee.rest.model.request.RestPCRTestSeriesRequest
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import javax.transaction.Transactional

@Service
class PCRTestSeriesService(
    private val pcrTestSeriesRepository: PCRTestSeriesRepository,
    private val pcrTestService: PCRTestService,
    private val authorityService: AuthorityService
) {
    @Transactional
    fun createPcrTestSeries(pcrTestSeries: RestPCRTestSeriesRequest): RestPCRTestSeries {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        isStartBeforeEnd(pcrTestSeries.start, pcrTestSeries.end)

        return pcrTestSeriesRepository.findByNameAndTrashedIsTrue(pcrTestSeries.name)
            ?.let { savePcrTestSeries(it.id, RestPCRTestSeries.ofRequest(pcrTestSeries, it.id), true) }
            ?: run {
                pcrTestService.checkExistenceOfCodes(pcrTestSeries.testCodes)
                val pcrTestSeriesEntry = pcrTestSeriesRepository.save(RestPCRTestSeriesRequest.toEntry(pcrTestSeries))
                pcrTestSeriesEntry.tests =
                    pcrTestService.addPcrTestsToSeries(pcrTestSeriesEntry, pcrTestSeries.testCodes)
                return pcrTestSeriesEntry.let { RestPCRTestSeries.ofEntry(it) }
            }
    }

    @Transactional
    fun savePcrTestSeries(
        id: Long,
        pcrTestSeries: RestPCRTestSeries,
        changeTrashed: Boolean = false
    ): RestPCRTestSeries {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        isStartBeforeEnd(pcrTestSeries.start, pcrTestSeries.end)
        return pcrTestSeriesRepository.findByIdAndTrashedIs(id, changeTrashed)
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
                currentPcrTestSeries.trashed = false
                return pcrTestSeriesRepository.save(currentPcrTestSeries)
                    .let { RestPCRTestSeries.ofEntry(it) }
            }
            ?: createPcrTestSeries(RestPCRTestSeriesRequest.of(pcrTestSeries))
    }

    fun getPcrTestSeries(id: Long): RestPCRTestSeries {
        return pcrTestSeriesRepository.findByIdAndTrashedIsFalse(id)
            ?.let { RestPCRTestSeries.ofEntry(it) }
            ?: throw NotFoundException("PCRTestSeries does not exist with id $id")
    }

    fun getAllPcrTestSeries(): List<RestPCRTestSeries> {
        return pcrTestSeriesRepository.findAllByTrashedIsFalse()
            .map { RestPCRTestSeries.ofEntry(it) }
    }

    @Transactional
    fun deletePcrTestSeries(id: Long) {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))

        pcrTestSeriesRepository.findByIdAndTrashedIsFalse(id)
            ?.let {
                pcrTestService.deleteAll(it.tests)
                it.trashed = true
            }
    }

    private fun isStartBeforeEnd(start: ZonedDateTime, end: ZonedDateTime) {
        if (end.isBefore(start)) {
            throw BadRequestException("Start muss vor dem Ende sein.")
        }
    }
}