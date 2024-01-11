package de.kordondev.attendee.core.persistence.entry

import de.kordondev.attendee.exception.BadRequestException

enum class TShirtSize(val size: String) {
    S104110("104/110"),
    S116128("116/128"),
    S140152("140/152"),
    S164("164"),
    S176("176"),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("XXL"),
    X3L("3XL"),
    X4L("4XL"),
    X5L("5XL");

    companion object {
        fun fromString(size: String): TShirtSize {
            return values().find { it.size == size }
                ?: throw BadRequestException("TShirtSize (${size}) ist kein valider Wert")
        }
    }

    override
    fun toString(): String {
        return size
    }
}

