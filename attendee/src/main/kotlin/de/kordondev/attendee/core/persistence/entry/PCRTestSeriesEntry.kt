package de.kordondev.attendee.core.persistence.entry

import java.time.ZonedDateTime
import javax.persistence.*

@Entity
@Table(name = "pcr_test_series")
data class PCRTestSeriesEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "name", unique = true)
    val name: String,

    @Column(name = "start")
    val start: ZonedDateTime,

    @Column(name = "end")
    val end: ZonedDateTime,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pcrTestSeries")
    var tests: Set<PCRTestEntry> = setOf()
) {
    override fun hashCode(): Int {
        return id.toInt()
    }

    override fun toString(): String {
        return "$id $name $start $end"
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }


    companion object {
        /*
        fun of(pcrTestSeries: PCRTestSeries): PCRTestSeriesEntry {
            return PCRTestSeriesEntry(
                id = pcrTestSeries.id,
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                tests = pcrTestSeries.tests.map { PCRTestEntry.of(it) }.toSet()
            )
        }

        fun of(pcrTestSeries: NewPCRTestSeries): PCRTestSeriesEntry {
            val pcrTestSeriesEntry = PCRTestSeriesEntry(
                id = 0,
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                tests = mutableSetOf()
            )
            pcrTestSeriesEntry.tests = pcrTestSeries.testCodes.map {
                PCRTest(
                    id = 0,
                    code = it,
                    testedAttendees = mutableSetOf(),
                    pcrTestSeriesId = pcrTestSeriesEntry.id,
                )
            }.map { PCRTestEntry.of(it) }
                .toSet()
            return pcrTestSeriesEntry
        }

        fun to(pcrTestSeries: PCRTestSeriesEntry): PCRTestSeries {
            return PCRTestSeries(
                id = pcrTestSeries.id,
                name = pcrTestSeries.name,
                start = pcrTestSeries.start,
                end = pcrTestSeries.end,
                tests = pcrTestSeries.tests.map { PCRTestEntry.to(it) }
            )
        }*/
    }
}
