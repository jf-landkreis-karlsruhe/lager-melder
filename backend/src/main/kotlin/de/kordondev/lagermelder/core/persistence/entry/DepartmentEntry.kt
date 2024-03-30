package de.kordondev.lagermelder.core.persistence.entry

import javax.persistence.*

@Entity
@Table(name = "departments")
data class DepartmentEntry (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "name")
        val name: String,

        @Column(name = "leader_name")
        val leaderName: String,

        @Column(name = "leader_email")
        val leaderEMail: String
)