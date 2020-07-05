package de.kordondev.attendee.core.service

import org.apache.commons.io.IOUtils
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDDocumentCatalog
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
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB

import org.apache.pdfbox.pdmodel.graphics.color.PDColor

import org.apache.pdfbox.cos.COSDictionary

import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary

import org.apache.pdfbox.pdmodel.common.PDRectangle

import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget

import org.apache.pdfbox.pdmodel.interactive.form.PDTextField

import org.apache.pdfbox.cos.COSName

import org.apache.pdfbox.pdmodel.PDResources

import org.apache.pdfbox.pdmodel.font.PDType1Font

import org.apache.pdfbox.pdmodel.font.PDFont

import org.apache.pdfbox.pdmodel.PDPage





@Service
class RegistrationFilesService(
        val resourceLoader: ResourceLoader
) {
    private val logger: Logger = LoggerFactory.getLogger(RegistrationFilesService::class.java)

    /*
        Organisation
        Auswahl{type: PDCheckBox value: COSName{Heimfreizeiten Zeitlager}}
        Blatt{type: PDTextField value: COSString{123}}
     */

    @Throws(IOException::class)
    fun getYouthPlan(id: Long): ByteArray {
        val resource: Resource = resourceLoader.getResource("classpath:data/paedagogischerBetreuer.pdf")
        val result = PDDocument()

        val fields = mutableListOf<PDField>()

        val pdfDocument: PDDocument = PDDocument.load(resource.inputStream)
        val form1 = pdfDocument.documentCatalog.acroForm;

        val field1 = form1.getField("Landesjugendplan_19")
        field1.setValue("WAS")
        field1.partialName = "1Landesjugendplan_19"

        val fieldAuswahl = form1.getField("Auswahl")
        fieldAuswahl?.setValue("Heimfreizeiten Zeitlager")
        fieldAuswahl.partialName = "1Auswahl"

         for (i in 0..155) {
             val testField = form1.getField("Texteingabe$i")
             testField?.setValue(i.toString())
             // testField?.partialName = "1Texteingabe$i"
             if (testField != null ) {
                 fields.add(testField)
             }
         }

        fields.add(field1)
        fields.add(fieldAuswahl)
        result.addPage(pdfDocument.getPage(0))



        // Second document
        //val pdfDocument1: PDDocument = PDDocument.load(resource.inputStream)
        //result.addPage(pdfDocument1.getPage(0))


        val finalForm = PDAcroForm(result)
        result.documentCatalog.acroForm = finalForm
        finalForm.fields = fields

        val test = result.documentCatalog.acroForm.getField("1Landesjugendplan_19")?.valueAsString
        logger.info("test $test")


        val out = ByteArrayOutputStream()
        result.save(out)
        result.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }

    fun testFunction(): ByteArray? {
        val pdDocument: PDDocument? = PDDocument()
        val page: /*@@ohpepy@@*/PDPage? = PDPage(PDRectangle.A4)
        pdDocument?.addPage(page)

        val form: /*@@bbirja@@*/PDAcroForm? = PDAcroForm(pdDocument)
        pdDocument?.documentCatalog?.acroForm = form

        val font: /*@@lkkyjh@@*/PDFont? = PDType1Font.HELVETICA
        val resources: /*@@ftqssz@@*/PDResources? = PDResources()
        resources?.put(COSName.getPDFName("Helv"), font)
        form?.setDefaultResources(resources)

        val textField: /*@@tivgxy@@*/PDTextField? = PDTextField(form)
        textField?.setPartialName("SampleField")

        val defaultAppearance: /*@@josevw@@*/kotlin.String? = "/Helv 12 Tf 0 0 1 rg"
        textField?.setDefaultAppearance(defaultAppearance)

        form?.getFields()?.add(textField)

        val widget: /*@@rknobj@@*/PDAnnotationWidget? = textField?.getWidgets()?.get(0)
        val rect: /*@@znavhi@@*/PDRectangle? = PDRectangle(50F, 750F, 200F, 50F)
        widget?.setRectangle(rect)
        widget?.setPage(page)

        val fieldAppearance: /*@@dvfqiw@@*/PDAppearanceCharacteristicsDictionary? = PDAppearanceCharacteristicsDictionary(COSDictionary())
        fieldAppearance?.setBorderColour(PDColor(floatArrayOf(0f, 1f, 0f), PDDeviceRGB.INSTANCE))
        fieldAppearance?.setBackground(PDColor(floatArrayOf(1f, 1f, 0f), PDDeviceRGB.INSTANCE))
        widget?.setAppearanceCharacteristics(fieldAppearance)

        widget?.setPrinted(true)

        page?.getAnnotations()?.add(widget)

        textField?.setValue("Sample Field 44")

        val out = ByteArrayOutputStream()
        pdDocument?.save(out)
        pdDocument?.close()
        return IOUtils.toByteArray(ByteArrayInputStream(out.toByteArray()))
    }
}