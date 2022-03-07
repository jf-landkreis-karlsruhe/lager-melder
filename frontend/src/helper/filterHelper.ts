import {
  Attendee,
  AttendeeRole,
  TShirtSize,
  Food,
  AttendeeStatus,
} from "../services/attendee";

export const youthLeaderAttendees = (
  departmentId: string,
  attendees: Attendee[],
  filterInput: string
): Attendee[] => {
  return attendees
    .filter((attendee) => attendee.departmentId === departmentId)
    .filter((attendee) => attendee.role === AttendeeRole.YOUTH_LEADER)
    .filter((attendees) => filterByFilterInput(attendees, filterInput));
};

export const youthAttendees = (
  departmentId: string,
  attendees: Attendee[],
  filterInput: string
): Attendee[] => {
  return attendees
    .filter((attendee) => attendee.departmentId === departmentId)
    .filter((attendee) => attendee.role === AttendeeRole.YOUTH)
    .filter((attendees) => filterByFilterInput(attendees, filterInput));
};

export const filterByFilterInput = (
  attendee: Attendee,
  filterInput: string
) => {
  if (filterInput.length > 0) {
    return (
      attendee.firstName.includes(filterInput) ||
      attendee.lastName.includes(filterInput) ||
      attendee.additionalInformation.includes(filterInput)
    );
  }
  return true;
};

export const FoodSortOrder = [
  Food.MEAT,
  Food.MUSLIM,
  Food.ALLERGY,
  Food.VEGETARIAN,
  Food.VEGAN,
  Food.NONE,
];

export const TShirtSizeSortOrder = [
  TShirtSize.S104110,
  TShirtSize.S116128,
  TShirtSize.S140152,
  TShirtSize.S164,
  TShirtSize.S176,
  TShirtSize.S,
  TShirtSize.M,
  TShirtSize.L,
  TShirtSize.XL,
  TShirtSize.XXL,
  TShirtSize.X3L,
  TShirtSize.X4L,
  TShirtSize.X5L,
];

export const filterEnteredAttendees = (attendee: Attendee): boolean => {
  return attendee.status === AttendeeStatus.ENTERED;
};
