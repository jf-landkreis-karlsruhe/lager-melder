package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.pdf.YouthPlanOverview
import org.apache.commons.io.IOUtils
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


@Service
class RegistrationFilesService(
        val youthPlanOverview: YouthPlanOverview
) {
    private val logger: Logger = LoggerFactory.getLogger(RegistrationFilesService::class.java)

    fun getYouthPlan(id: Long): ByteArray {
        val result = youthPlanOverview.createYouthPlan()
        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

}