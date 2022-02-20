package de.kordondev.attendee.core.persistence.entry

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import java.util.*
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

    override fun toString(): String {
        return "$id $code"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as PCRTestEntry

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.code)
    }

}
