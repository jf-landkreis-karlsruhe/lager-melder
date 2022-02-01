package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.PCRTest
import javax.persistence.*

@Entity
@Table(name = "pcr_tests")
data class PCRTestEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "code")
    val code: String,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "tested_attendee_id")
    val testedAttendees: Set<AttendeeEntry>,

    @ManyToOne
    @JoinColumn(name = "pcr_test_series_id")
    val pcrTestSeries: PCRTestSeriesEntry
) {
    companion object {
        fun of(pcrTest: PCRTest): PCRTestEntry {
            return PCRTestEntry(
                id = pcrTest.id,
                code = pcrTest.code,
                testedAttendees = pcrTest.testedAttendees.map { AttendeeEntry.of(it) }.toSet(),
                pcrTestSeries = PCRTestSeriesEntry.of(pcrTest.pcrTestSeries)
            )
        }

        fun to(pcrTest: PCRTestEntry): PCRTest {
            return PCRTest(
                id = pcrTest.id,
                code = pcrTest.code,
                testedAttendees = pcrTest.testedAttendees.map { AttendeeEntry.to(it) }.toMutableSet(),
                pcrTestSeries = PCRTestSeriesEntry.to(pcrTest.pcrTestSeries)
            )
        }
    }
}
