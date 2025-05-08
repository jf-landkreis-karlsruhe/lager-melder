package de.kordondev.lagermelder.core.service.models

enum class Group(val group: String) {
    PARTICIPANT("teilnehmer"),
    CHILD_GROUP("kindergruppe");

    companion object {
        fun getGroup(group: String): Group {
            return Group.entries.firstOrNull { it.group == group }
                ?: throw IllegalArgumentException("Group $group could not be found")
        }
    }
}
