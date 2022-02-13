package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.rest.model.RestPCRTestSeries
import java.time.ZonedDateTime
import javax.persistence.*

@Entity
@Table(name = "pcr_test_series")
data class PCRTestSeriesEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "name", unique = true)
    var name: String,

    @Column(name = "start")
    var start: ZonedDateTime,

    @Column(name = "end")
    var end: ZonedDateTime,

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "pcrTestSeries",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE]
    )
    var tests: MutableSet<PCRTestEntry> = mutableSetOf()
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

    fun update(pcrTestSeriesEntry: RestPCRTestSeries) {
        this.name = pcrTestSeriesEntry.name
        this.start = pcrTestSeriesEntry.start
        this.end = pcrTestSeriesEntry.end
    }
}
