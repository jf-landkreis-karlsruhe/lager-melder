package de.kordondev.attendee.core.service

import org.apache.commons.io.IOUtils
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.IOException


@Service
class RegistrationFilesService(
        val resourceLoader: ResourceLoader
) {

    @Throws(IOException::class)
    fun getYouthPlan(id: Long): ByteArray {
        val resource: Resource = resourceLoader.getResource("classpath:data/paedagogischerBetreuer.pdf")
        return IOUtils.toByteArray(resource.inputStream)
    }
}