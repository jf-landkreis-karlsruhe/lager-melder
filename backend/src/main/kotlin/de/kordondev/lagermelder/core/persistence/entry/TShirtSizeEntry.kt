package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.*

@Entity
@Table(name = "t_shirt_sizes")
data class TShirtSizeEntry(
    @Id
    val size: String
) {
    override fun toString(): String {
        return size
    }
}