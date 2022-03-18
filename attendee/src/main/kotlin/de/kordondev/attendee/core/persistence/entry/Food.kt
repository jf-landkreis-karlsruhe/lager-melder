package de.kordondev.attendee.core.persistence.entry

enum class Food(val value: String) {
    MEAT("Fleisch"),
    NONE("Nichts"),
    VEGETARIAN("Vegetarisch"),
    MUSLIM("Muslimisch"),
    SPECIAL("Sonderessen");

    override
    fun toString(): String {
        return value
    }
}