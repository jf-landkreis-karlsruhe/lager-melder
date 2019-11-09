package de.kordondev.attendee.core.model

data class NewDepartment(
        val name: String,
        val leaderName: String,
        val leaderEMail: String
)

data class Department(
        val id: Long,
        val name: String,
        val leaderName: String,
        val leaderEMail: String
)