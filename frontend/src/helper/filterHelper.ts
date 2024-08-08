import { AttendeeRole, Food, AttendeeStatus } from '../services/attendee'
import type { Attendee } from '../services/attendee'

export const youthLeaderAttendees = (
  departmentId: number,
  attendees: Attendee[],
  filterInput: string
): Attendee[] => {
  return attendees
    .filter((attendee) => attendee.departmentId === departmentId)
    .filter((attendee) => attendee.role === AttendeeRole.YOUTH_LEADER)
    .filter((attendees) => filterByFilterInput(attendees, filterInput))
}

export const youthAttendees = (
  departmentId: number,
  attendees: Attendee[],
  filterInput: string
): Attendee[] => {
  return attendees
    .filter((attendee) => attendee.departmentId === departmentId)
    .filter((attendee) => attendee.role === AttendeeRole.YOUTH)
    .filter((attendees) => filterByFilterInput(attendees, filterInput))
}

export const filterByFilterInput = (attendee: Attendee, filterInput: string) => {
  if (filterInput.length > 0) {
    return (
      attendee.firstName.includes(filterInput) ||
      attendee.lastName.includes(filterInput) ||
      attendee.additionalInformation.includes(filterInput)
    )
  }
  return true
}

export const filterEnteredAttendees = (attendee: Attendee): boolean => {
  return attendee.status === AttendeeStatus.ENTERED
}
