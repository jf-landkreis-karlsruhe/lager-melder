package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.service.EventDayService
import de.kordondev.lagermelder.rest.model.RestEventDay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EventDayController(
    val eventDayService: EventDayService
) {

    @GetMapping("/event-days")
    fun getEventDays(): List<RestEventDay> {
        return eventDayService.getEventDays()
            .map { RestEventDay.of(it) }
    }
}