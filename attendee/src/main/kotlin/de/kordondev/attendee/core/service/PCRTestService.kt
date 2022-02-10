package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.PCRTestEntry
import de.kordondev.attendee.core.persistence.entry.PCRTestSeriesEntry
import de.kordondev.attendee.core.persistence.repository.PCRTestRepository
import de.kordondev.attendee.exception.ExistingDependencyException
import de.kordondev.attendee.exception.NotFoundException
import de.kordondev.attendee.exception.WrongTimeException
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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

    fun getPCRTestForCode(code: String): PCRTestEntry {
        return pcrTestRepository.findByCodeAndTrashedIsFalse(code)
            ?: throw NotFoundException("PCRTest not found for code $code")
    }

    fun checkExistenceOfCodes(codes: List<String>) {
        val pcrTestsWithCode = pcrTestRepository.findAllByCodeInAndTrashedIsFalse(codes)
        if (pcrTestsWithCode.isNotEmpty()) {
            throw ExistingDependencyException(
                "Die folgenden Codes sind bereits für einen andere Testreihe registriert: ${
                    pcrTestWithSeries(pcrTestsWithCode)
                }"
            )
        }
    }

    private fun pcrTestWithSeries(tests: Set<PCRTestEntry>): String {
        return tests
            .map { "${it.code} (${it.pcrTestSeries.name}) " }.toList().joinToString(separator = ", ")
    }


    fun deleteAll(pcrTests: Iterable<PCRTestEntry>) {
        pcrTests.map {
            it.trashed = true;
        }
        pcrTestRepository.saveAll(pcrTests)
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

    fun addPcrTestsToSeries(pcrTestSeries: PCRTestSeriesEntry, testCodes: List<String>): MutableSet<PCRTestEntry> {
        val pcrTests = testCodes.map {
            reactivateOrCreatePcrTest(pcrTestSeries, it)
        }
        return pcrTestRepository.saveAll(pcrTests).toMutableSet()
    }

    fun reactivateOrCreatePcrTest(pcrTestSeries: PCRTestSeriesEntry, testCode: String): PCRTestEntry {
        return pcrTestRepository.findByCodeAndTrashedIsTrue(testCode)
            ?: PCRTestEntry(
                id = 0,
                code = testCode,
                testedAttendees = mutableSetOf(),
                pcrTestSeries = pcrTestSeries,
                trashed = false
            )
    }

/*
    // TODO: called by pcrtestSeries create and update
    fun deletePCRTestsForPCRTestSeries(pcrTestSeries: PCRTestSeries) {
        pcrTestRepository.deleteAll(
            pcrTestSeries.tests.map { PCRTestEntry.of(it) }
        )
    } */

    fun addAttendeeToPCRTest(pcrTestCode: String, attendeeCode: String): AttendeeEntry {
        val pcrTest = getPCRTestForCode(pcrTestCode)
        canPcrTestSeriesBeEdited(pcrTest.pcrTestSeries)
        val attendee = attendeeService.getAttendeeByCode(attendeeCode)
        pcrTestRepository.findByTestedAttendeesIdAndPcrTestSeriesId(attendee.id, pcrTest.pcrTestSeries.id)
            ?.let { oldPcrTest ->
                oldPcrTest.testedAttendees = oldPcrTest.testedAttendees.filter { it.id != attendee.id }.toMutableSet()
                pcrTestRepository.save(oldPcrTest)
            }
        pcrTest.testedAttendees.add(AttendeeEntry.of(attendee))
        pcrTestRepository.save(pcrTest)
        return AttendeeEntry.of(attendee)
    }

    fun deleteAttendeeToPCRTest(pcrTestCode: String, attendeeCode: String) {
        val pcrTest = getPCRTestForCode(pcrTestCode)
        val attendee = attendeeService.getAttendeeByCode(attendeeCode)
        val attendeeEntry = AttendeeEntry.of(attendee)
        pcrTest.testedAttendees.remove(attendeeEntry)
        pcrTestRepository.save(pcrTest)
    }

    var germanTimeDate = DateTimeFormatter.ofPattern("mm:HH dd.MM.yyyy", Locale.GERMANY)
    fun canPcrTestSeriesBeEdited(pcrTestSeries: PCRTestSeriesEntry) {
        val start = pcrTestSeries.start
        val end = pcrTestSeries.end
        val now = ZonedDateTime.now()
        if (now.isAfter(end) || now.isBefore(start)) {
            throw WrongTimeException(
                "Pcrtest kann nur zwischen ${start.format(germanTimeDate)} und ${
                    end.format(
                        germanTimeDate
                    )
                } geändert werden."
            )
        }
    }
}