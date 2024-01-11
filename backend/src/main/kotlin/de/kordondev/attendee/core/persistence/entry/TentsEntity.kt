package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.core.model.Department
import org.hibernate.Hibernate
import java.util.Objects
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "tents")
data class TentsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @OneToOne
    @JoinColumn(name= "department_id", referencedColumnName = "id")
    val department: DepartmentEntry,

    @Column(name = "sg200")
    val sg200: Int,
    @Column(name = "sg20")
    val sg20: Int,
    @Column(name = "sg30")
    val sg30: Int,
    @Column(name = "sg40")
    val sg40: Int,
    @Column(name = "sg50")
    val sg50: Int
) {

    override fun toString(): String {
        return "$id"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TentsEntity

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}