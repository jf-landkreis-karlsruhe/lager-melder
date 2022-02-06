package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.PCRTestEntry
import de.kordondev.attendee.core.persistence.entry.PCRTestSeriesEntry
import de.kordondev.attendee.core.persistence.repository.PCRTestRepository
import de.kordondev.attendee.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class PCRTestService(
    val pcrTestRepository: PCRTestRepository,
    val attendeeService: AttendeeService
) {

    /*fun getPCRTestsForSeries(pcrTestSeriesId: Long): Set<PCRTest> {
        return pcrTestRepository.findAllByPcrTestSeriesId(pcrTestSeriesId)
            .map { PCRTestEntry.to(it) }
            .toSet()
    }*/

    fun getPCRTestForCode2(code: String): PCRTestEntry {
        return pcrTestRepository.findByCode(code)
            ?: throw NotFoundException("PCRTest not found for code $code")
    }

    /* fun getPCRTestForCode(code: String): PCRTest {
         return pcrTestRepository.findByCode(code)
             ?.let { PCRTestEntry.to(it) }
             ?: throw NotFoundException("PCRTest not found for code $code")
     }

     fun getPCRTest(id: Long): PCRTest {
         return pcrTestRepository.findByIdOrNull(id)
             ?.let { PCRTestEntry.to(it) }
             ?: throw NotFoundException("PCRTest not found for id $id")
     }

     fun getPCRTests(): List<PCRTest> {
         return pcrTestRepository.findAll()
             .map { PCRTestEntry.to(it) }
     }

     fun updatePCRTest(pcrTest: PCRTest): PCRTest {
         return pcrTestRepository.save(PCRTestEntry.of(pcrTest))
             .let { PCRTestEntry.to(it) }
     }*/

    /*fun savePCRTests(pcrTestSeries: PCRTestSeries, pcrTestCodes: List<String>): List<PCRTest> {
        val newPcrTests = pcrTestCodes
            .map {
                PCRTest(
                    id = 0,
                    code = it,
                    testedAttendees = mutableSetOf(),
                    pcrTestSeries = pcrTestSeries
                )
            }
            .map { PCRTestEntry.of(it) }
        return pcrTestRepository.saveAll(newPcrTests)
            .map { PCRTestEntry.to(it) }
    }*/

    fun addPcrTestsToSeries(pcrTestSeries: PCRTestSeriesEntry, testCodes: List<String>) {
        val pcrTests = testCodes.map {
            PCRTestEntry(
                id = 0,
                code = it,
                testedAttendees = mutableSetOf(),
                pcrTestSeries = pcrTestSeries
            )
        }

        pcrTestSeries.tests = pcrTestRepository.saveAll(pcrTests).toSet()
    }

/*
    // TODO: called by pcrtestSeries create and update
    fun deletePCRTestsForPCRTestSeries(pcrTestSeries: PCRTestSeries) {
        pcrTestRepository.deleteAll(
            pcrTestSeries.tests.map { PCRTestEntry.of(it) }
        )
    } */

    fun addAttendeeToPCRTest(pcrTestCode: String, attendeeCode: String): Attendee {
        val pcrTest = getPCRTestForCode2(pcrTestCode)
        val attendee = attendeeService.getAttendeeByCode(attendeeCode)
        pcrTest.testedAttendees.add(AttendeeEntry.of(attendee))
        pcrTestRepository.save(pcrTest)
        return attendee
    }
    /*

     fun deleteAttendeeToPCRTest(pcrTestCode: String, attendeeCode: String) {
         val pcrTest = getPCRTestForCode(pcrTestCode)
         val attendee = attendeeService.getAttendeeByCode(attendeeCode)
         pcrTest.testedAttendees.remove(attendee)
         pcrTestRepository.save(PCRTestEntry.of(pcrTest))
     }*/
}