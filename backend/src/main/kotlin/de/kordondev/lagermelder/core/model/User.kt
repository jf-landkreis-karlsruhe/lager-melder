package de.kordondev.lagermelder.core.model


data class NewUser(
    val userName: String,
    var passWord: String,
    val role: String,
    val department: Department
)
