package de.kordondev.lagermelder.rest.model

import de.kordondev.lagermelder.core.persistence.entry.TShirtSizeEntry

data class RestTShirtSize(
    val size: String
) {
    companion object {
        fun from(tShirtSize: TShirtSizeEntry) = RestTShirtSize(
            size = tShirtSize.size
        )
    }
}