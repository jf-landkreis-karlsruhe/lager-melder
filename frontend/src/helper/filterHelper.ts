import { Attendee, AttendeeRole } from "../services/attendee";

import { Department } from "../services/department";

export const youthLeaderAttendees = (
  departmentId: string,
  attendees: Attendee[],
  filterInput: string
): Attendee[] => {
  return attendees
    .filter(attendee => attendee.departmentId === departmentId)
    .filter(attendee => attendee.role === AttendeeRole.YOUTH_LEADER)
    .filter(attendees => filterByFilterInput(attendees, filterInput));
};

export const youthAttendees = (
  departmentId: string,
  attendees: Attendee[],
  filterInput: string
): Attendee[] => {
  return attendees
    .filter(attendee => attendee.departmentId === departmentId)
    .filter(attendee => attendee.role === AttendeeRole.YOUTH)
    .filter(attendees => filterByFilterInput(attendees, filterInput));
};

export const filterByFilterInput = (attendee: Attendee, filterInput: string) => {
  if (filterInput.length > 0) {
    return (
      attendee.firstName.includes(filterInput) ||
      attendee.lastName.includes(filterInput) ||
      attendee.additionalInformation.includes(filterInput)
    );
  }
  return true;
}