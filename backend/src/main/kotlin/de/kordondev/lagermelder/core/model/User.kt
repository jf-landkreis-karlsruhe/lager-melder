package de.kordondev.lagermelder.core.model


data class NewUser(
    val userName: String,
    var passWord: String,
    val role: String,
    val department: Department
)

data class User(
    val id: Long,
    val userName: String,
    val passWord: String,
    val role: String,
    val department: Department
)

