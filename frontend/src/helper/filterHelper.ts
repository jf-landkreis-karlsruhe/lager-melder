import { Attendee, AttendeeRole, TShirtSize, Food } from "../services/attendee";

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
  Food.NONE
];

export const TShirtSizeSortOrder = [
  TShirtSize.ONE_HUNDRED_TWENTY_EIGHT,
  TShirtSize.ONE_HUNDRED_FORTY,
  TShirtSize.ONE_HUNDRED_FIFTY_TWO,
  TShirtSize.ONE_HUNDRED_FIFTY_EIGHT,
  TShirtSize.ONE_HUNDRED_SIXTY_FOUR,
  TShirtSize.S,
  TShirtSize.M,
  TShirtSize.L,
  TShirtSize.XL,
  TShirtSize.XXL,
  TShirtSize.XXXL,
  TShirtSize.XXXXL,
  TShirtSize.XXXXXL
];
