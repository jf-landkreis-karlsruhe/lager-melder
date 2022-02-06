package de.kordondev.attendee.core.persistence.entry

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "pcr_tests")
data class PCRTestEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "code", unique = true)
    val code: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "attendee_pcr_test",
        joinColumns = [JoinColumn(name = "attendee_id")],
        inverseJoinColumns = [JoinColumn(name = "pcr_test_id")]
    )
    var testedAttendees: MutableSet<AttendeeEntry>,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnore
    @JoinColumn(name = "pcr_test_series_id")
    val pcrTestSeries: PCRTestSeriesEntry
) {

    override fun hashCode(): Int {
        return id.toInt()
    }

    override fun toString(): String {
        return "$id $code"
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }

    companion object {
        /* fun of(pcrTest: PCRTest): PCRTestEntry {
             return PCRTestEntry(
                 id = pcrTest.id,
                 code = pcrTest.code,
                 testedAttendees = pcrTest.testedAttendees.map { AttendeeEntry.of(it) }.toSet(),
                 pcrTestSeries = pcrTest.pcrTestSeries
             )
         }

         fun to(pcrTest: PCRTestEntry): PCRTest {
             return PCRTest(
                 id = pcrTest.id,
                 code = pcrTest.code,
                 testedAttendees = pcrTest.testedAttendees.map { AttendeeEntry.to(it) }.toMutableSet(),
                 pcrTestSeries = pcrTest.pcrTestSeries,
             )
         }
         */
    }
}
