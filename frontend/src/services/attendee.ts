import { getData, postData, putData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";

export const getAttendees = () =>
  getData<Attendee[]>("attendees", withAuthenticationHeader());

export const getAttendee = (id: string) =>
  getData<Attendee>(`attendees/${id}`, withAuthenticationHeader());

export const createAttendee = (attendee: NewAttendee) =>
  postData<Attendee>("attendees", withAuthenticationHeader(), attendee);

export const updateAttendee = (attendee: Attendee) =>
  putData<Attendee>(`attendees/${attendee.id}`, withAuthenticationHeader(), attendee);

export const deleteAttendee = (id: string) =>
  deleteData(`attendees/${id}`, withAuthenticationHeader());

export enum AttendeeRole {
  YOUTH = "YOUTH",
  YOUTH_LEADER = "YOUTH_LEADER"
}

export enum Food {
  MEAT = "MEAT",
  NONE = "NONE",
  ALLERGY = "ALLERGY",
  VEGETARIAN = "VEGETARIAN",
  VEGAN = "VEGAN",
  MUSLIM = "MUSLIM"
}

export enum TShirtSize {
  ONE_HUNDRED_TWENTY_EIGHT = "128",
  ONE_HUNDRED_FORTY = "140",
  ONE_HUNDRED_FIFTY_TWO = "152",
  ONE_HUNDRED_FIFTY_EIGHT = "158",
  ONE_HUNDRED_SIXTY_FOUR = "164",
  S = "S",
  M = "M",
  L = "L",
  XL = "XL",
  XXL = "XXL",
  XXXL = "XXXL",
  XXXXL = "XXXXL",
  XXXXXL = "XXXXXL"
}

export interface NewAttendee {
  firstName: string;
  lastName: string;
  departmentId: number;
  birthday: string;
  food: Food;
  tShirtSize: TShirtSize;
  additionalInformation: string;
  role: AttendeeRole;
}

export interface Attendee extends NewAttendee {
  id: number;
}
