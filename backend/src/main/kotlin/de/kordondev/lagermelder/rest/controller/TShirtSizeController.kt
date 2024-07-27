package de.kordondev.lagermelder.rest.controller

import de.kordondev.lagermelder.core.persistence.entry.TShirtSizeEntry
import de.kordondev.lagermelder.core.service.TShirtSizeService
import de.kordondev.lagermelder.rest.model.RestTShirtSize
import de.kordondev.lagermelder.rest.model.request.RestTShirtSizeRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class TShirtSizeController(
    private val tShirtSizeService: TShirtSizeService
) {

    @GetMapping("/tShirtSizes")
    fun getTShirtSizes(): List<String> {
        return tShirtSizeService.getTShirtSizes()
            .map { it.toString() }
    }

    @PostMapping("/tShirtSizes")
    fun addTShirtSize(@RequestBody(required = true) @Valid tShirtSize: RestTShirtSizeRequest): RestTShirtSize {
        return tShirtSizeService
            .createTShirtSize(TShirtSizeEntry(tShirtSize.size))
            .let { RestTShirtSize.from(it) }
    }

    @DeleteMapping("/tShirtSizes/{size}")
    fun deleteTShirtSize(
        @PathVariable(value = "size") size: String,
        @RequestBody(required = true) @Valid replacementSize: RestTShirtSizeRequest
    ) {
        tShirtSizeService.deleteAndReplaceTShirtSize(size, replacementSize.size)
    }
}