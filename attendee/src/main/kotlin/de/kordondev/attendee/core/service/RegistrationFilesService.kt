package de.kordondev.attendee.core.service

import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import java.io.InputStream
import java.net.MalformedURLException
import java.nio.file.Path
import java.nio.file.Paths


@Service
class RegistrationFilesService(
        val resourceLoader: ResourceLoader
) {

    fun getYouthPlan(id: Long) {
        val resource: Resource = resourceLoader.getResource("classpath:data.txt")
        val inputStream: InputStream = resource.inputStream
        print("Hallo")
    }
}