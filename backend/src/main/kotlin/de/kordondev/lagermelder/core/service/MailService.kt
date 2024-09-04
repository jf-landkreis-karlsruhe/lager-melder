package de.kordondev.lagermelder.core.service

import de.kordondev.lagermelder.core.mail.MailSenderService
import de.kordondev.lagermelder.core.persistence.entry.DepartmentEntry
import de.kordondev.lagermelder.core.persistence.entry.SendTo
import de.kordondev.lagermelder.core.security.AuthorityService
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

    private fun filterDepartmentsBy(department: DepartmentEntry, sendTo: SendTo): Boolean {
        if (sendTo == SendTo.ALL_DEPARTMENTS) {
            return true
        }
        val attendees = attendeeService.getAttendeesForDepartment(department)
        val attendeeList = (attendees.youths + attendees.youthLeaders)
        if (sendTo == SendTo.DEPARTMENTS_WITH_ATTENDEES) {
            return attendeeList.isNotEmpty()
        }
        if (sendTo == SendTo.DEPARTMENTS_WITHOUT_ATTENDEES) {
            return attendeeList.isEmpty()
        }
        return false
    }
}
