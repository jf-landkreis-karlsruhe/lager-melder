package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.NewPCRTestSeries
import de.kordondev.attendee.core.model.PCRTestSeries
import de.kordondev.attendee.core.persistence.entry.PCRTestSeriesEntry
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.repository.PCRTestSeriesRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.exception.ExistingDependencyException
import de.kordondev.attendee.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PCRTestSeriesService(
    private val pcrTestSeriesRepository: PCRTestSeriesRepository,
    private val pcrTestService: PCRTestService,
    private val authorityService: AuthorityService
) {
    fun createPcrTestSeries(pcrTestSeries: NewPCRTestSeries): PCRTestSeries {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return pcrTestSeriesRepository.save(PCRTestSeriesEntry.of(pcrTestSeries))
            .let { PCRTestSeriesEntry.to(it) }
    }

    fun savePcrTestSeries(id: Long, pcrTestSeries: PCRTestSeries): PCRTestSeries {
        authorityService.hasRole(listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        val updatedPCRTestSeries = pcrTestSeries.copy(id = id)
        return pcrTestSeriesRepository.save(PCRTestSeriesEntry.of(updatedPCRTestSeries))
            .let { PCRTestSeriesEntry.to(it) }
    }

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
    }
}