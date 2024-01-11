package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.mail.MailSenderService
import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.SendTo
import de.kordondev.attendee.core.security.AuthorityService
import org.springframework.stereotype.Service

@Service
class MailService(
    private val authorityService: AuthorityService,
    private val departmentService: DepartmentService,
    private val attendeeService: AttendeeService,
    private val mailSenderService: MailSenderService,
    private val settingsService: SettingsService
) {
    fun sendReminderMail(sendTo: SendTo): Number {
        authorityService.isSpecializedFieldDirector()
        val settings = settingsService.getSettings()
        return departmentService.getDepartments()
            .filter { filterDepartmentsBy(it, sendTo) }
            .map { mailSenderService.sendReminderMail(it.leaderEMail, it.leaderName, settings) }
            .filter { it }
            .count()
    }

    fun sendRegistrationFinishedMail(sendTo: SendTo): Number {
        authorityService.isSpecializedFieldDirector()
        val settings = settingsService.getSettings()
        return departmentService.getDepartments()
            .filter { filterDepartmentsBy(it, sendTo) }
            .map { mailSenderService.sendRegistrationFinishedMail(it.leaderEMail, it.leaderName, settings) }
            .filter { it }
            .count()
    }

    private fun filterDepartmentsBy(department: Department, sendTo: SendTo): Boolean {
        if (sendTo == SendTo.ALL_DEPARTMENTS) {
            return true
        }
        val attendeeList = attendeeService.getAttendeesForDepartment(department)
        if (sendTo == SendTo.DEPARTMENTS_WITH_ATTENDEES) {
            return attendeeList.isNotEmpty()
        }
        if (sendTo == SendTo.DEPARTMENTS_WITHOUT_ATTENDEES) {
            return attendeeList.isEmpty()
        }
        return false
    }
}