package de.kordondev.attendee.core.persistence.entry

enum class TShirtSize(val size: String) {
    ONE_HUNDRED_TWENTY_EIGHT("128"),
    ONE_HUNDRED_FORTY("140"),
    ONE_HUNDRED_FIFTY_TWO("152"),
    ONE_HUNDRED_FIFTY_EIGHT("158"),
    ONE_HUNDRED_SIXTY_FOUR("164"),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("XXL"),
    XXXL("XXXL"),
    XXXXL("XXXXL"),
    XXXXXL("XXXXXL");
    companion object {
        private val map = TShirtSize.values().associateBy(TShirtSize::size)
        fun fromString(size: String) = map[size]
    }
}

