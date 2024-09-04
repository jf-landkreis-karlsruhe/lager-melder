package de.kordondev.lagermelder.core.persistence.entry

enum class DepartmentFeatures(val value: String) {
    ZKIDS("ZKids"),
    HELPER("Helfer"),
    YOUTH_GROUPS("Jugendgruppen"),
    CHILD_GROUPS("Kindergruppen");

    override
    fun toString(): String {
        return value
    }
}