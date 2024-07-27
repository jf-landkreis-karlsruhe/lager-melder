package de.kordondev.lagermelder.core.persistence.repository

import de.kordondev.lagermelder.core.persistence.entry.TShirtSizeEntry
import org.springframework.data.repository.CrudRepository

interface TShirtSizeRepository : CrudRepository<TShirtSizeEntry, String> {
}