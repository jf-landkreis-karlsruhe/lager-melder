package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.EventService
import de.kordondev.lagermelder.rest.model.RestRoleCount
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class PublicController(
    private val eventService: EventService
) {
    private val CACHE_DURATION_SECONDS = 600L
    private val roleCountCache = RestRoleCountCache(CACHE_DURATION_SECONDS)

    @GetMapping("/public/present-by-executed-role")
    fun getPresentByExecutedRoleCount(): RestRoleCount {
        roleCountCache.data?.let { return it }

        return eventService.getGlobalEventSummary()
            .let { RestRoleCount.of(it) }
            .also { roleCountCache.data = it }
    }

    class RestRoleCountCache(
        private val cacheDurationSeconds: Long
    ) {
        private var cachedData: RestRoleCount? = null
        private var expires: LocalDateTime = LocalDateTime.MIN

        var data: RestRoleCount?
            @Synchronized get() {
                val now = LocalDateTime.now()
                if (now.isAfter(expires)) {
                    return null
                }
                return cachedData
            }
            @Synchronized set(value) {
                cachedData = value
                expires = LocalDateTime.now().plusSeconds(cacheDurationSeconds)
            }
    }
}
