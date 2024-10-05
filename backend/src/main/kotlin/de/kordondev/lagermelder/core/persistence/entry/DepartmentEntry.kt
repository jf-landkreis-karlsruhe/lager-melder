package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.*

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
        val leaderEMail: String,

        @Column(name = "phone_number")
        val phoneNumber: String = "",

        @Column(name = "short_name")
        val shortName: String = "",

        @OneToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.EAGER)
        @JoinColumn(name = "department_id")
        val features: Set<DepartmentFeatureEntry> = emptySet(),

        @Column(name = "head_department_name")
        val headDepartmentName: String = ""
)