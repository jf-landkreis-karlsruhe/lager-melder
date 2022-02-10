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

    @Column(name = "trashed")
    var trashed: Boolean,

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
}
